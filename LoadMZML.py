import sys
import pymzml
import numpy as np
import time
import math


class LoadMZML(object):
    # type: normal, positive, negative
    # positive scans have odd number id 1,3,5
    # negative scans have even number id 0,2,4
    def __init__(self, param, type="normal"):

        # Read File
        self.param = param
        self.run = pymzml.run.Reader(param.filename, MS1_Precision=5e-6)

        self.scansTotal = self.run.getSpectrumCount()
        if type == "positive":
            self.scansTotal = math.ceil(self.scansTotal / 2)
            self.startIndex = 1
            self.step = 2
        elif type == 'negative':
            self.scansTotal = math.floor(self.scansTotal / 2)
            self.startIndex = 2
            self.step = 2
        elif type == 'normal':
            self.startIndex = 1
            self.step = 1
        else:
            raise Exception('Invalid Type: normal, positive or negative')

        # scansTotal =0
        # for spectrum in run:
        #    if type(spectrum['id']) == int:
        #        scansTotal = scansTotal + 1
        #    else:
        #        print('skip')

        self.data = self.getDataStructure()
        # -----------------------------------------------------------------------

    def getReduceSpec(self, mzRangeLower, mzRangeHighest):

        start = time.clock()

        result = []
        for line in range(len(self.data)):
            sys.stdout.write("\r{0}%".format(line / len(self.data) * 100))
            sys.stdout.flush()
            row = []
            for column in range(len(self.data[line])):
                index = self.data[line][column]
                spectrum = self.run[index]
                intensity = 0

                # for mz, i in spectrum.peaks:
                #    if mzRangeLower <= mz <= mzRangeHighest:
                for mz, i in LoadMZML.generator(spectrum.peaks, mzRangeLower, mzRangeHighest):
                    intensity = intensity + i

                row.append(intensity)
            result.append(row)

        sys.stdout.write("\r100%\n")
        end = time.clock()
        print("%.2fs" % (end - start))
        return np.array(result)

    def getReduceSpecII(self, *rangeTuples):

        start = time.clock()

        result = []
        for line in range(len(self.data)):
            sys.stdout.write("\r{0:.2f}%".format(line / len(self.data) * 100))
            sys.stdout.flush()
            row = []
            for column in range(len(self.data[line])):
                index = self.data[line][column]
                spectrum = self.run[index]
                intensity = 0

                for rangeTuple in rangeTuples:
                    # for mz, i in spectrum.peaks:
                    #    if mzRangeLower <= mz <= mzRangeHighest:
                    for mz, i in LoadMZML.generator(spectrum.peaks, rangeTuple[0], rangeTuple[1]):
                        intensity = intensity + i

                row.append(intensity)
            result.append(row)

        sys.stdout.write("\r100%\n")
        end = time.clock()
        print("%.2fs" % (end - start))
        return np.array(result)

    def getDataStructure(self):
        param = self.param
        scansTotal = self.scansTotal

        scansPerLine = scansTotal / param.lines
        # if not scansPerLine.is_integer():
        #    raise Exception('Pixels per line not integer value')
        scansPerLine = scansPerLine * (param.widthInMM - param.downMotionInMM) / param.widthInMM
        scansPerLine = int(scansPerLine)
        skip = scansTotal - param.lines * scansPerLine
        skipPerLine = int(skip / param.lines)
        remaining = skip - skipPerLine * param.lines

        # -----------------------------------------------------------------------

        data = []
        direction = True  # forward /  backward
        index = self.startIndex
        for line in range(0, param.lines):
            data.append([])
            if direction:
                for i in range(0, scansPerLine):
                    data[line].append(index)
                    index = index + self.step
            else:
                for i in reversed(range(0, scansPerLine)):
                    data[line].append(index + (i * self.step))
                index = index + (scansPerLine * self.step)

            index = index + (skipPerLine * self.step)
            direction = not direction
            if remaining >= 0:
                remaining = remaining - 1
                index = index + self.step
        return data

    @staticmethod
    def generator(peaks, mzRangeLower, mzRangeHighest):
        startIndex = LoadMZML.binarySearch(peaks, mzRangeLower)

        for j in range(startIndex + 1, len(peaks)):
            mz, i = peaks[j]
            if mz > mzRangeHighest:
                break
            yield mz, i

    @staticmethod
    def binarySearch(alist, item):
        first = 0
        last = len(alist) - 1

        while first < last:
            midpoint = (first + last) // 2
            if alist[midpoint][0] < item and alist[midpoint + 1][0] >= item:
                return midpoint
            elif midpoint == 0 and alist[midpoint][0] >= item:
                return -1
            else:
                if item < alist[midpoint][0]:
                    last = midpoint - 1
                else:
                    first = midpoint + 1
        return last

    # max_mz, max_i = getPeak(spectrum)
    def getPeak(spectrum):
        max_mz = spectrum.peaks[0][0]
        max_i = spectrum.peaks[0][1]
        for mz, i in spectrum.peaks:
            if max_i < i:
                max_i = i
                max_mz = mz
                # print(type(mz), type(i))  # float, float
                # print(mz, i)
        return [max_mz, max_i]

    #     spectrum.reduce(mzRange=(mzRangeLower, mzRangeHighest))
    def getSumSpectrum(spectrum):
        intensity = 0
        try:
            for mz, i in spectrum.peaks:
                intensity = intensity + i
        except:
            intensity = 0
        return intensity
