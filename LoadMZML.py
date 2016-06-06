import sys
import pymzml
import numpy as np
import time


class LoadMZML(object):
    def __init__(self, param):
        # Read File
        self.run = pymzml.run.Reader(param.filename, MS1_Precision=5e-6)

        # Maximum peaks in 1d array
        scansTotal = self.run.getSpectrumCount()

        # scansTotal =0
        # for spectrum in run:
        #    if type(spectrum['id']) == int:
        #        scansTotal = scansTotal + 1
        #    else:
        #        print('skip')

        scansPerLine = scansTotal / param.lines  # 6327 , 8 =  790 + 7 remaining
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
        index = 1
        for line in range(0, param.lines):
            data.append([])
            if direction:
                for i in range(0, scansPerLine):
                    data[line].append(index)
                    index = index + 1
            else:
                for i in reversed(range(0, scansPerLine)):
                    data[line].append(index + i)
                index = index + scansPerLine

            index = index + skipPerLine
            direction = not direction
            if remaining >= 0:
                remaining = remaining - 1
                index = index + 1

        self.data = data
        # -----------------------------------------------------------------------

    def getReduceSpec(self, mzRangeLower, mzRangeHighest):

        start = time.clock()
        print(start)

        result = []
        for line in range(len(self.data)):
            row = []
            for column in range(len(self.data[line])):
                index = self.data[line][column]
                spectrum = self.run[index]
                intensity = 0
                for mz, i in spectrum.peaks:
                    if mzRangeLower <= mz <= mzRangeHighest:
                        intensity = intensity + i
                row.append(intensity)
            result.append(row)

        end = time.clock()
        print("%.2fs" % (end - start))
        return np.array(result)

    def getReduceSpecFast(self, mzRangeLower, mzRangeHighest):

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
                searchList = spectrum.peaks
                startIndex = self.binarySearch(searchList, mzRangeLower)

                for j in range(startIndex + 1, len(searchList)):
                    mz, i = searchList[j]
                    if mz > mzRangeHighest:
                        break
                    intensity = intensity + i

                row.append(intensity)
            result.append(row)

        sys.stdout.write("\r100%\n")
        end = time.clock()
        print("%.2fs" % (end - start))
        return np.array(result)

    def binarySearch(self, alist, item):
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
