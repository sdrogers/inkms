# coding: utf-8
import sys
import pymzml
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
import time
from numpy import eye
from LoadMZML import LoadMZML


class OptimalMz(object):
    def __init__(self, loadMZML, mzRangeLower, mzRangeHighest, resolution, isLetterFunction):
        run = loadMZML.run
        data = loadMZML.data

        start = time.clock()

        resolutionMZ = mzRangeHighest - mzRangeLower

        mz_g = np.zeros((resolution,), dtype=np.float)
        diff_g = np.zeros((resolution,), dtype=np.float)

        c_g = np.zeros((resolution,), dtype=np.int)
        i_g = np.zeros((resolution,), dtype=np.float)

        c_g1 = np.zeros((resolution,), dtype=np.int)
        i_g1 = np.zeros((resolution,), dtype=np.float)

        for line in range(len(data)):
            sys.stdout.write("\r{0}%".format(line / len(data) * 100))
            sys.stdout.flush()
            for x in range(0, len(data[0])):
                index = data[line][x]
                spectrum = run[index]
                isLetter = isLetterFunction(x, line)

                # for (mz, i) in spectrum.peaks:
                #    if mzRangeLower <= mz <= mzRangeHighest:
                for mz, i in LoadMZML.generator(spectrum.peaks, mzRangeLower, mzRangeHighest):
                    if isLetter:  # letter
                        indx = (mz - mzRangeLower) / resolutionMZ * resolution
                        c_g[indx] += 1
                        i_g[indx] += i
                    else:
                        indx = (mz - mzRangeLower) / resolutionMZ * resolution
                        c_g1[indx] += 1
                        i_g1[indx] += i

        for i in range(0, c_g.shape[0]):
            if c_g[i] != 0:
                i_g[i] = i_g[i] / c_g[i]
            if c_g1[i] != 0:
                i_g1[i] = i_g1[i] / c_g1[i]
            mz_g[i] = i * resolutionMZ / resolution + mzRangeLower
            diff_g[i] = i_g1[i] - i_g[i]

        sys.stdout.write("\r100%\n")
        end = time.clock()
        print("%.2fs" % (end - start))

        self.mz_g = mz_g
        self.diff_g = diff_g
        self.i_g = i_g
        self.i_g1 = i_g1
        self.mzRangeLower = mzRangeLower
        self.mzRangeHighest = mzRangeHighest
        self.resolution = resolution

    @classmethod
    def V1(cls, loadMZML, x_start_mm, x_stop_mm, mzRangeLower, mzRangeHighest, resolution):
        param = loadMZML.param
        data = loadMZML.data
        x_start = int(x_start_mm / param.widthInMM * len(data[0]))
        x_stop = int(x_stop_mm / param.widthInMM * len(data[0]))
        isLetterFunction = lambda x, line: x_start <= x <= x_stop
        return cls(loadMZML, mzRangeLower, mzRangeHighest, resolution, isLetterFunction)

    @classmethod
    def V2(cls, loadMZML, mzRangeLower, mzRangeHighest, resolution, letterRecognition):
        isLetterFunction = lambda x, line: letterRecognition.checkIfLetter(x, line)
        return cls(loadMZML, mzRangeLower, mzRangeHighest, resolution, isLetterFunction)

    def printN(self, n=5):
        perm = self.diff_g.argsort()  # permutation that sorts arrays
        print("i1 - i:")
        print(self.diff_g[perm][0:n])
        print("mz:")
        print(self.mz_g[perm][0:n])
        print("i:")
        print(self.i_g[perm][0:n])
        print("i1:")
        print(self.i_g1[perm][0:n])

    def plot(self):
        fig = plt.figure()
        plt.plot(self.mz_g, self.i_g, 'b^')
        plt.vlines(self.mz_g, [0], self.i_g)

        fig = plt.figure()
        plt.plot(self.mz_g, self.i_g1, 'b^')
        plt.vlines(self.mz_g, [0], self.i_g1)

        # Save Data
        # plt.savefig('Vlines0_{0}-{1}mm{2}-{3}mz.png'.format(x_start_mm, x_stop_mm,mzRangeLower, mzRangeHighest))
        # plt.savefig('Vlines1_{0}-{1}mm{2}-{3}mz.png'.format(x_start_mm, x_stop_mm,mzRangeLower, mzRangeHighest))
        # np.savetxt(
        #    'mz{0}-{1}-{2}.csv'.format(self.mzRangeLower, self.mzRangeHighest, self.resolution),
        #    self.mz_g, delimiter=",")
        # np.savetxt(
        #    'i_g0_{0}-{1}mm{2}-{3}.csv'.format(self.x_start_mm, self.x_stop_mm, self.mzRangeLower, self.mzRangeHighest),
        #    self.i_g,
        #    delimiter=",")
        # np.savetxt(
        #    'i_g1_{0}-{1}mm{2}-{3}.csv'.format(self.x_start_mm, self.x_stop_mm, self.mzRangeLower, self.mzRangeHighest),
        #    self.i_g1,
        #    delimiter=",")
