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

        start = time.clock()
        print(start)

        x_ids = []
        t_inten = []

        for index in range(1, scansTotal + 1):
            spectrum = self.run[index]
            tmp_peaks = [(mz, i) for mz, i in spectrum.peaks if param.mzRangeLower <= mz <= param.mzRangeHighest]
            intensity = 0
            for mz, i in tmp_peaks:
                intensity = intensity + i
            x_ids.append(index)
            t_inten.append(intensity)

        end = time.clock()
        print(end - start)

        # -----------------------------------------------------------------------

        # data[line] = [[[m/z, value],...,[m/z, value]],[[m/z, value],]]
        data = []
        direction = True  # forward /  backward
        index = 0
        for line in range(0, param.lines):
            data.append([])
            if direction:
                for i in range(0, scansPerLine):
                    data[line].append([x_ids[index], t_inten[index]])
                    index = index + 1
            else:
                for i in reversed(range(0, scansPerLine)):
                    data[line].append([x_ids[index + i], t_inten[index + i]])
                index = index + scansPerLine

            index = index + skipPerLine
            direction = not direction
            if remaining >= 0:
                remaining = remaining - 1
                index = index + 1

        # -----------------------------------------------------------------------

        self.np_data = np.array(data)

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
