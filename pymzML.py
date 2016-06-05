# coding: utf-8

import pymzml
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
import time
from numpy import eye
from LoadMZML import LoadMZML


# get_ipython().magic('matplotlib inline')


def imageFromArray(Z):
    max = Z.max()
    Z = Z / max * 255
    Z = Z.astype('uint8')

    img = Image.fromarray(Z)  # monochromatic image
    img.save('org.png')
    img.show()

    # imrgb = Image.merge('RGB', (img, img, img))  # color image
    # imrgb.show()


def graphVlines(x_start_mm, x_stop_mm):
    start = time.clock()
    print(start)

    x_start = int(x_start_mm / param.widthInMM * len(data[0]))
    x_stop = int(x_stop_mm / param.widthInMM * len(data[0]))

    mz_g = []
    i_g = []

    for line in range(len(data)):
        for x in range(x_start, x_stop):
            index = data[line][x]
            spectrum = run[index]

            for (mz, i) in spectrum.peaks:
                if param.mzRangeLower <= mz <= param.mzRangeHighest:
                    mz_g.append(mz)
                    i_g.append(i)

    end = time.clock()
    print(end - start)

    fig = plt.figure()
    plt.plot(mz_g, i_g, 'b^')
    plt.vlines(mz_g, [0], i_g)
    plt.savefig('Vlines{0}-{1}.png'.format(x_start_mm, x_stop_mm))
    plt.show()


def graphVlinesV2(x_start_mm, x_stop_mm):
    start = time.clock()
    print(start)

    x_start = int(x_start_mm / param.widthInMM * len(data[0]))
    x_stop = int(x_stop_mm / param.widthInMM * len(data[0]))

    resolution = 200
    resolutionMZ = param.mzRangeHighest - param.mzRangeLower

    mz_g = np.zeros((resolution,), dtype=np.float)

    c_g = np.zeros((resolution,), dtype=np.int)
    i_g = np.zeros((resolution,), dtype=np.float)

    c_g1 = np.zeros((resolution,), dtype=np.int)
    i_g1 = np.zeros((resolution,), dtype=np.float)

    for line in range(len(data)):
        for x in range(0, len(data[0])):
            index = data[line][x]
            spectrum = run[index]

            for (mz, i) in spectrum.peaks:
                if param.mzRangeLower <= mz <= param.mzRangeHighest:
                    if x_start <= x <= x_stop:
                        indx = (mz - param.mzRangeLower) / resolutionMZ * resolution
                        c_g[indx] += 1
                        i_g[indx] += i
                    else:
                        indx = (mz - param.mzRangeLower) / resolutionMZ * resolution
                        c_g1[indx] += 1
                        i_g1[indx] += i

    for i in range(0, c_g.shape[0]):
        if c_g[i] != 0:
            i_g[i] = i_g[i] / c_g[i]
        if c_g1[i] != 0:
            i_g1[i] = i_g1[i] / c_g1[i]
        mz_g[i] = i * resolutionMZ / resolution + param.mzRangeLower

    end = time.clock()
    print(end - start)

    fig = plt.figure()
    plt.plot(mz_g, i_g, 'b^')
    plt.vlines(mz_g, [0], i_g)
    plt.savefig('Vlines0_{0}-{1}.png'.format(x_start_mm, x_stop_mm))
    plt.show()

    fig = plt.figure()
    plt.plot(mz_g, i_g1, 'b^')
    plt.vlines(mz_g, [0], i_g1)
    plt.savefig('Vlines1_{0}-{1}.png'.format(x_start_mm, x_stop_mm))
    plt.show()


def plotImshow():
    intensity = loadMZML.getReduceSpec(param)
    # np.savetxt('Z{0}-{1}.csv'.format(param.mzRangeLower, param.mzRangeHighest), intensity, delimiter=",")
    plt.figure()
    plt.imshow(intensity, extent=[0, param.widthInMM, 0, param.heightInMM], interpolation='none', cmap='hot')
    plt.savefig('imShow{0}-{1}.png'.format(param.mzRangeLower, param.mzRangeHighest))
    plt.show()


class Parameters:
    def __init__(self):
        #  self.filename = '/Users/simon/Dropbox/MS_Ink_Data/ALphabet/abcdefgh_1.mzML'
        self.filename = '..\\data\\abcdefgh_1.mzML'
        self.mzRangeLower = 300  # 374
        self.mzRangeHighest = 500  # 376
        self.lines = 8
        self.widthInMM = 62
        self.heightInMM = 10
        self.downMotionInMM = 1.25


param = Parameters()
loadMZML = LoadMZML(param)
run = loadMZML.run
data = loadMZML.data

graphVlinesV2(x_start_mm=30, x_stop_mm=40)
# plotImshow()

print("Finished")
