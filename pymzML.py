import pymzml
import numpy as np
import matplotlib.pyplot as plt

lines = 8
# Read File
run = pymzml.run.Reader('data\\abcdefgh_1.mzML', MS1_Precision=5e-6)

# -----------------------------------------------------------------------
# Maximum peaks in 1d array
scans = 0
x = []
t = []
for spectrum in run:

    max_mz = spectrum.peaks[0][0]
    max_i = spectrum.peaks[0][1]
    for mz, i in spectrum.peaks:
        if max_i < i:
            max_i = i
            max_mz = mz
            # print(type(mz), type(i))  # float, float
            # print(mz, i)
    x.append(max_mz)
    t.append(max_i)
    scans = scans + 1

# -----------------------------------------------------------------------
scansPerLine = scans / lines  # 6328 , 8
if not scansPerLine.is_integer():
    raise Exception('Pixels per line not integer value')
scansPerLine = int(scansPerLine)

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
