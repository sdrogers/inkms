# coding: utf-8
import sys
import pymzml
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
import time
from numpy import eye
from LoadMZML import LoadMZML


# % matplotlib inline


def imageFromArray(Z):
    max = Z.max()
    Z = Z / max * 255
    Z = Z.astype('uint8')

    img = Image.fromarray(Z)  # monochromatic image
    img.save('org.png')
    img.show()

    # imrgb = Image.merge('RGB', (img, img, img))  # color image
    # imrgb.show()


def graphVlines(x_start_mm, x_stop_mm, mzRangeLower, mzRangeHighest):
    start = time.clock()

    x_start = int(x_start_mm / param.widthInMM * len(data[0]))
    x_stop = int(x_stop_mm / param.widthInMM * len(data[0]))

    mz_g = []
    i_g = []

    for line in range(len(data)):
        sys.stdout.write("\r{0}%".format(line / len(data) * 100))
        sys.stdout.flush()
        for x in range(x_start, x_stop):
            index = data[line][x]
            spectrum = run[index]

            for (mz, i) in spectrum.peaks:
                if mzRangeLower <= mz <= mzRangeHighest:
                    mz_g.append(mz)
                    i_g.append(i)

    sys.stdout.write("\r100%\n")
    end = time.clock()
    print("%.2fs" % (end - start))

    fig = plt.figure()
    plt.plot(mz_g, i_g, 'b^')
    plt.vlines(mz_g, [0], i_g)
    # plt.savefig('Vlines_{0}-{1}mm{2}-{3}mz.png'.format(x_start_mm, x_stop_mm, mzRangeLower, mzRangeHighest))
    plt.show()


def graphVlinesV2(x_start_mm, x_stop_mm, mzRangeLower, mzRangeHighest, resolution, n=5):
    start = time.clock()

    x_start = int(x_start_mm / param.widthInMM * len(data[0]))
    x_stop = int(x_stop_mm / param.widthInMM * len(data[0]))

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

            for (mz, i) in spectrum.peaks:
                if mzRangeLower <= mz <= mzRangeHighest:
                    if x_start <= x <= x_stop:
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

    perm = diff_g.argsort()  # permutation that sorts arrays
    print("i1 - i:")
    print(diff_g[perm][0:n])
    print("mz:")
    print(mz_g[perm][0:n])
    print("i:")
    print(i_g[perm][0:n])
    print("i1:")
    print(i_g1[perm][0:n])

    fig = plt.figure()
    plt.plot(mz_g, i_g, 'b^')
    plt.vlines(mz_g, [0], i_g)
    # plt.savefig('Vlines0_{0}-{1}.png'.format(x_start_mm, x_stop_mm))

    fig = plt.figure()
    plt.plot(mz_g, i_g1, 'b^')
    plt.vlines(mz_g, [0], i_g1)
    # plt.savefig('Vlines1_{0}-{1}.png'.format(x_start_mm, x_stop_mm))
    plt.show()

    # np.savetxt('mz{0}-{1}.csv'.format(mzRangeLower, mzRangeHighest), mz_g, delimiter=",")
    # np.savetxt('i_g{0}-{1}.csv'.format(mzRangeLower, mzRangeHighest), i_g, delimiter=",")
    # np.savetxt('i_g1{0}-{1}.csv'.format(mzRangeLower, mzRangeHighest), i_g1, delimiter=",")


def plotImshow(mzRangeLower, mzRangeHighest):
    intensity = loadMZML.getReduceSpecFast(mzRangeLower, mzRangeHighest)
    np.savetxt('Z{0}-{1}.csv'.format(mzRangeLower, mzRangeHighest), intensity, delimiter=",")
    plt.figure()
    plt.imshow(intensity, extent=[0, param.widthInMM, 0, param.heightInMM], interpolation='none', cmap='hot')
    # plt.savefig('imShow{0}-{1}.png'.format(mzRangeLower, mzRangeHighest))
    plt.show()


class Parameters:
    def __init__(self):
        #  self.filename = '/Users/simon/Dropbox/MS_Ink_Data/ALphabet/abcdefgh_1.mzML'
        self.filename = '..\\data\\abcdefgh_1.mzML'
        self.lines = 8
        self.widthInMM = 62
        self.heightInMM = 10
        self.downMotionInMM = 1.25


param = Parameters()
loadMZML = LoadMZML(param)
run = loadMZML.run
data = loadMZML.data

# graphVlines(x_start_mm=30, x_stop_mm=40, mzRangeLower=374, mzRangeHighest=376).
# graphVlines(x_start_mm=40, x_stop_mm=50, mzRangeLower=374, mzRangeHighest=376)
graphVlinesV2(x_start_mm=30, x_stop_mm=40, mzRangeLower=300, mzRangeHighest=500, resolution=200)
# plotImshow(mzRangeLower=374, mzRangeHighest=376)

print("Finished")
