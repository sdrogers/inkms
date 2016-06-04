# coding: utf-8

# In[1]:

import pymzml
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
import time
from numpy import eye

# # Parameters

# In[2]:

# get_ipython().magic('matplotlib inline')
# filename = '/Users/simon/Dropbox/MS_Ink_Data/ALphabet/abcdefgh_1.mzML'
filename = '..\\data\\abcdefgh_1.mzML'
mzRangeLower = 374
mzRangeHighest = 376
lines = 8
widthInMM = 62
heightInMM = 10
downMotionInMM = 1.25


# In[3]:

def imageFromArray(Z):
    max = Z.max()
    Z = Z / max * 255
    Z = Z.astype('uint8')

    img = Image.fromarray(Z)  # monochromatic image
    img.save('org.png')
    img.show()

    # imrgb = Image.merge('RGB', (img, img, img))  # color image
    # imrgb.show()


# -----------------------------------------------------------------------
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


# getSum(spectrum)
def getSum(spectrum):
    intensity = 0
    for mz, i in spectrum.peaks:
        intensity = intensity + i
    return intensity


# -----------------------------------------------------------------------

# Read File
run = pymzml.run.Reader(filename, MS1_Precision=5e-6)

# Maximum peaks in 1d array
scansTotal = run.getSpectrumCount()

# scansTotal =0
# for spectrum in run:
#    if type(spectrum['id']) == int:
#        scansTotal = scansTotal + 1
#    else:
#        print('skip')

scansPerLine = scansTotal / lines  # 6327 , 8 =  790 + 7 remaining
# if not scansPerLine.is_integer():
#    raise Exception('Pixels per line not integer value')
scansPerLine = scansPerLine * (widthInMM - downMotionInMM) / widthInMM
scansPerLine = int(scansPerLine)
skip = scansTotal - lines * scansPerLine
skipPerLine = int(skip / lines)
remaining = skip - skipPerLine * lines

start = time.clock()
print(start)

x = []
t = []

for index in range(1, scansTotal + 1):
    spectrum = run[index]
    spectrum.reduce(mzRange=(mzRangeLower, mzRangeHighest))
    try:
        intensity = getSum(spectrum)
    except:
        intensity = 0
    x.append(0)
    t.append(intensity)

end = time.clock()
print(end - start)

# -----------------------------------------------------------------------

# data[line] = [[[m/z, value],...,[m/z, value]],[[m/z, value],]]
data = []
direction = True  # forward /  backward
index = 0
for line in range(0, lines):
    data.append([])
    if direction:
        for i in range(0, scansPerLine):
            data[line].append([x[index], t[index]])
            index = index + 1
    else:
        for i in reversed(range(0, scansPerLine)):
            data[line].append([x[index + i], t[index + i]])
        index = index + scansPerLine

    index = index + skipPerLine
    direction = not direction
    if remaining >= 0:
        remaining = remaining - 1
        index = index + 1

# -----------------------------------------------------------------------

np_data = np.array(data)
Z = np_data[:, :, 1]
# np.savetxt('Z{0}-{1}.csv'.format(mzRangeLower, mzRangeHighest), Z, delimiter=",")
# imageFromArray(Z)

# In[10]:

plt.figure()
plt.imshow(Z, extent=[0, widthInMM, 0, heightInMM], interpolation='none', cmap='hot')
plt.savefig('fig{0}-{1}.png'.format(mzRangeLower, mzRangeHighest))
plt.show()

print("Finished")


# In[ ]:
