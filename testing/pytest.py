import pymzml
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
from numpy import eye

lines = 8
# Read File
run = pymzml.run.Reader('..\\data\\abcdefgh_1.mzML', MS1_Precision=5e-6)

# -----------------------------------------------------------------------
# Maximum peaks in 1d array
scans = 0
x = []
t = []

for spectrum in run:
    print(spectrum.peaks)
    spectrum.reduce(mzRange=(100, 200))
    print(spectrum.peaks)
    for mz, i in spectrum.highestPeaks(5):
        print(mz, i)
    break
