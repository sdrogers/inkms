import pymzml
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
from numpy import eye


def image(Z):
    Z = Z / Z.max() * 255
    Z = Z.astype('uint8')

    img = Image.fromarray(Z)  # monochromatic image
    img.show()
    # img.save('my.png')
    imrgb = Image.merge('RGB', (img, img, img))  # color image
    imrgb.show()


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


lines = 8
width = 7
# Read File
run = pymzml.run.Reader('..\\data\\abcdefgh_1.mzML', MS1_Precision=5e-6)

# -----------------------------------------------------------------------
# Maximum peaks in 1d array
scansTotal = 0

for spectrum in run:
    if type(spectrum['id']) == int:
        scansTotal = scansTotal + 1
    else:
        print('skip')

scansPerLine = scansTotal / lines  # 6328 , 8
# if not scansPerLine.is_integer():
#    raise Exception('Pixels per line not integer value')
scansPerLine = int(scansPerLine)

x = []
t = []

for index in range(1, scansTotal + 1):
    spectrum = run[index]
    spectrum.reduce(mzRange=(350, 360))
    try:
        intensity = getSum(spectrum)
    except:
        intensity = 0
    x.append(0)
    t.append(intensity)

# -----------------------------------------------------------------------

# data[line] = [[[m/z, value],...,[m/z, value]],[[m/z, value],]]
data = []
direction = True  # forward /  backward
for line in range(0, lines):
    data.append([])
    if direction:
        for scan in range(0, scansPerLine):
            data[line].append([x[line * scansPerLine + scan], t[line * scan + scan]])
            # print(line * scansPerLine + scan, "\n")
    else:
        for scan in reversed(range(0, scansPerLine)):
            data[line].append([x[line * scansPerLine + scan], t[line * scan + scan]])
            # print(line * scansPerLine + scan, "\n")
    direction = not direction

# -----------------------------------------------------------------------

np_data = np.array(data)
Z = np_data[:, :, 1]

print("Finished")
