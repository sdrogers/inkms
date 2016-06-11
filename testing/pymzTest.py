import pymzml
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
from numpy import eye

# Read File
run = pymzml.run.Reader('..\\..\\data\\abcdefgh_1.mzML', MS1_Precision=5e-6)
print(run.getSpectrumCount())

for spectrum in run:
    # print(spectrum.peaks)
    # spectrum.reduce(mzRange=(100, 200))
    # print(spectrum.peaks)
    # for mz, i in spectrum.highestPeaks(5):
    #   print(mz, i)

    print(spectrum.keys())
    print(spectrum['scan start time'] * 60)

    for element in spectrum.xmlTree:
        if element.get('accession') == 'MS:1000130':
            print('-' * 40)
            print(element)
            print(element.get('accession'))
            print(element.tag)
            print(element.items())
