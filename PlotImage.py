import numpy as np
import matplotlib.pyplot as plt
from PIL import Image


class PlotImage(object):
    def __init__(self, loadMZML, param):
        self.loadMZML = loadMZML
        self.param = param

    def plotImshow(self, mzRangeLower, mzRangeHighest):
        intensity = self.loadMZML.getReduceSpec(mzRangeLower, mzRangeHighest)

        plt.figure()
        plt.imshow(intensity, extent=[0, self.param.widthInMM, 0, self.param.heightInMM], interpolation='none',
                   cmap='hot')

        # Save Data
        # np.savetxt('Z{0}-{1}.csv'.format(mzRangeLower, mzRangeHighest), intensity, delimiter=",")
        # plt.savefig('imShow{0}-{1}.png'.format(mzRangeLower, mzRangeHighest))

    def plotImshowII(self, *rangeTuples):
        intensity = self.loadMZML.getReduceSpecII(*rangeTuples)

        plt.figure()
        plt.imshow(intensity, extent=[0, self.param.widthInMM, self.param.heightInMM, 0], interpolation='none',
                   cmap='hot')

        # Save Data
        # np.savetxt('Z{0}.csv'.format(rangeTuples), intensity, delimiter=",")
        # plt.savefig('imShow{0}'.format(rangeTuples))

    def getPillowImage(self, *rangeTuples):
        Z = self.loadMZML.getReduceSpecII(*rangeTuples)
        max = Z.max()
        Z = Z / max * 255
        Z = Z.astype('uint8')

        # imrgb = Image.merge('RGB', (img, img, img))  # color image
        img = Image.fromarray(Z)  # monochromatic image
        return img

    def save(self, filename, *rangeTuples):
        self.getPillowImage(*rangeTuples).save(filename + '.png')

    def printRT(self):
        for line in range(len(self.loadMZML.data)):
            indx = [self.loadMZML.data[line][0], self.loadMZML.data[line][-1]]
            indx.sort()
            print("{0:.2f} - {1:.2f}".format(self.loadMZML.run[indx[0]]['scan start time'] * 60,
                                             self.loadMZML.run[indx[1]]['scan start time'] * 60))
