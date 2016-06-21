# coding: utf-8
import sys
import pymzml
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
import time
from numpy import eye
from LoadMZML import LoadMZML


class OptimalMzV2(object):
    def __init__(self, loadMZML, mzRangeLower, mzRangeHighest, resolution, isLetterFunction):
        run = loadMZML.run
        data = loadMZML.data

        start = time.clock()

        resolutionMZ = mzRangeHighest - mzRangeLower

        mz_g = np.zeros((resolution,), dtype=np.float)
        diff_g = np.zeros((resolution,), dtype=np.float)

        c_g = np.zeros((resolution,), dtype=np.int)
        i_g = np.zeros((resolution,), dtype=np.float)

        noLettersCount = 0
        w_g1 = np.zeros((resolution,), dtype=np.int)
        c_g1 = np.zeros((resolution,), dtype=np.int)
        i_g1 = np.zeros((resolution,), dtype=np.float)

        for line in range(len(data)):
            sys.stdout.write("\r{0:.2f}%".format(line / len(data) * 100))
            sys.stdout.flush()
            for x in range(0, len(data[0])):
                index = data[line][x]
                spectrum = run[index]
                isLetter = isLetterFunction(x, line)

                if not isLetter:
                    continue

                # for (mz, i) in spectrum.peaks:
                #    if mzRangeLower <= mz <= mzRangeHighest:
                for mz, i in LoadMZML.generator(spectrum.peaks, mzRangeLower, mzRangeHighest):
                    indx = (mz - mzRangeLower) / resolutionMZ * resolution
                    c_g[indx] += 1
                    i_g[indx] += i

        for i in range(0, resolution):
            if c_g[i] != 0:
                i_g[i] = i_g[i] / c_g[i]

        for line in range(len(data)):
            sys.stdout.write("\r{0:.2f}%".format(line / len(data) * 100))
            sys.stdout.flush()
            for x in range(0, len(data[0])):
                index = data[line][x]
                spectrum = run[index]
                isLetter = isLetterFunction(x, line)

                if isLetter:
                    continue

                noLettersCount = noLettersCount + 1
                sumi = np.zeros((resolution,), dtype=np.float)
                ci = np.zeros((resolution,), dtype=np.int)
                # for (mz, i) in spectrum.peaks:
                #    if mzRangeLower <= mz <= mzRangeHighest:
                for mz, i in LoadMZML.generator(spectrum.peaks, mzRangeLower, mzRangeHighest):
                    indx = (mz - mzRangeLower) / resolutionMZ * resolution

                    sumi[indx] = sumi[indx] + i
                    ci[indx] = ci[indx] + 1

                    c_g1[indx] += 1
                    i_g1[indx] += i

                for k in range(0, resolution):
                    if (sumi[k] / (ci[k] + 1)) < i_g[k]:
                        w_g1[k] = w_g1[k] + 1

        for i in range(0, resolution):
            # diff_g[i] = -(i_g[i] - i_g1[i]) * (c_g[i]/ (c_g1[i]  + 1))
            mz_g[i] = i * resolutionMZ / resolution + mzRangeLower
            if c_g1[i] != 0:
                i_g1[i] = i_g1[i] / c_g1[i]
            # if i_g[i] < 1000:
            #    diff_g[i] = 0
            # else:
            diff_g[i] = -(i_g[i] - i_g1[i]) * (w_g1[indx] / noLettersCount)

        sys.stdout.write("\r100%\n")
        end = time.clock()
        print("%.2fs" % (end - start))

        perm = diff_g.argsort()  # permutation that sorts arrays

        self.noLettersCount = noLettersCount
        self.w_g1 = w_g1[perm]

        self.mz_g = mz_g[perm]
        self.diff_g = diff_g[perm]
        self.i_g = i_g[perm]
        self.c_g = c_g[perm]
        self.i_g1 = i_g1[perm]
        self.c_g1 = c_g1[perm]
        self.mzRangeLower = mzRangeLower
        self.mzRangeHighest = mzRangeHighest
        self.resolution = resolution
        self.resolutionMZ = resolutionMZ

    @classmethod
    def V1(cls, loadMZML, x_start_mm, x_stop_mm, y_start_mm, y_stop_mm, mzRangeLower, mzRangeHighest, resolution):
        param = loadMZML.param
        data = loadMZML.data
        x_start = int(x_start_mm / param.widthInMM * len(data[0]))
        x_stop = int(x_stop_mm / param.widthInMM * len(data[0]))
        y_start = int(y_start_mm / param.heightInMM * len(data))
        y_stop = int(y_stop_mm / param.heightInMM * len(data))
        isLetterFunction = lambda x, line: x_start <= x and x <= x_stop and y_start <= line and line <= y_stop
        return cls(loadMZML, mzRangeLower, mzRangeHighest, resolution, isLetterFunction)

    @classmethod
    def V2(cls, loadMZML, mzRangeLower, mzRangeHighest, resolution, templateClass):
        isLetterFunction = lambda x, line: templateClass.checkIfLetter(x, line)
        return cls(loadMZML, mzRangeLower, mzRangeHighest, resolution, isLetterFunction)

    def getMZandIndex(self, n=5):
        # skip zero intensity values
        indexes = []
        result = []

        for i in range(0, self.mz_g.shape[0]):
            if self.i_g[i] != 0 and self.i_g1[i] != 0:
                indexes.append(i)
                result.append((self.mz_g[i], self.mz_g[i] + self.resolutionMZ / self.resolution))
            if len(indexes) == n:
                break

        return result, indexes

    def getN(self, n=5):
        return self.getMZandIndex(n)[0]

    def printN(self, n=5):
        result, indexes = self.getMZandIndex(n)
        print("i1 - i:")
        print(self.diff_g[indexes])
        print("")
        print("mz:")
        print(result)
        print("")
        print("Not letters {0} below are {1} of {2}%".format(self.noLettersCount, self.w_g1[indexes],
                                                             self.w_g1[indexes] / self.noLettersCount * 100))
        print("")
        print("i:")
        print(self.i_g[indexes])
        print("i1:")
        print(self.i_g1[indexes])
        print("c:")
        print(self.c_g[indexes])
        print("c1:")
        print(self.c_g1[indexes])
        print("range:")
        print(self.resolutionMZ / self.resolution)

    def plot(self, ymax):
        fig = plt.figure()
        plt.ylim(0, ymax)
        plt.plot(self.mz_g, self.i_g, 'b^')
        plt.vlines(self.mz_g, [0], self.i_g)

        fig = plt.figure()
        plt.ylim(0, ymax)
        plt.plot(self.mz_g, self.i_g1, 'b^')
        plt.vlines(self.mz_g, [0], self.i_g1)

    def save(self):
        # Save Data
        # plt.savefig('Vlines0_{0}-{1}mm{2}-{3}mz.png'.format(x_start_mm, x_stop_mm,mzRangeLower, mzRangeHighest))
        # plt.savefig('Vlines1_{0}-{1}mm{2}-{3}mz.png'.format(x_start_mm, x_stop_mm,mzRangeLower, mzRangeHighest))
        np.savetxt(
            'mz{0}-{1}-{2}.csv'.format(self.mzRangeLower, self.mzRangeHighest, self.resolution),
            self.mz_g, delimiter=",")
        np.savetxt(
            'i_g0_{0}-{1}.csv'.format(self.mzRangeLower, self.mzRangeHighest),
            self.i_g,
            delimiter=",")
        np.savetxt(
            'i_g1_{0}-{1}.csv'.format(self.mzRangeLower, self.mzRangeHighest),
            self.i_g1,
            delimiter=",")
