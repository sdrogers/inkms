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


class Parameters:
    def __init__(self):
        #  self.filename = '/Users/simon/Dropbox/MS_Ink_Data/ALphabet/abcdefgh_1.mzML'
        self.filename = '..\\data\\abcdefgh_1.mzML'
        self.mzRangeLower = 374
        self.mzRangeHighest = 376
        self.lines = 8
        self.widthInMM = 62
        self.heightInMM = 10
        self.downMotionInMM = 1.25


param = Parameters()
loadMZML = LoadMZML(param)
run = loadMZML.run
intensity = loadMZML.getReduceSpec(param)

# s = pymzml.spec.Spectrum(measuredPrecision=5e-6)

# for x in range(np_data.shape[0]):
#    for y in range(np_data.shape[1]):
#        index = np_data(x, y)
#        spectrum = run[index]
#        s += spectrum


np.savetxt('Z{0}-{1}.csv'.format(param.mzRangeLower, param.mzRangeHighest), intensity, delimiter=",")
# imageFromArray(Z)

plt.figure()
plt.imshow(intensity, extent=[0, param.widthInMM, 0, param.heightInMM], interpolation='none', cmap='hot')
plt.savefig('fig{0}-{1}.png'.format(param.mzRangeLower, param.mzRangeHighest))
plt.show()

print("Finished")
