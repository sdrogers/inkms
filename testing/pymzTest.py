import pymzml
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
from numpy import eye

# Read File
run = pymzml.run.Reader('..\\..\\data\\Block2_neg_50.mzML', MS1_Precision=5e-6)
print(run.getSpectrumCount())

spectrum = run[1]
# print(spectrum.peaks)
# spectrum.reduce(mzRange=(100, 200))
# print(spectrum.peaks)
# for mz, i in spectrum.highestPeaks(5):
#   print(mz, i)

print(spectrum.keys())
# print(spectrum['scan start time'] * 60)

print(help(spectrum.xmlTree))

for element in spectrum.xmlTree:
    if element.get('accession') == 'MS:1000129':
        print('-' * 40)
        print(element)
        print(element.get('accession'))
        print(element.tag)
        print(element.items())

    if element.get('accession') == 'MS:1000130':
        print('-' * 40)
        print(element)
        print(element.get('accession'))
        print(element.tag)
        print(element.items())
