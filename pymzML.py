# coding: utf-8
import sys
import numpy as np
import matplotlib.pyplot as plt
from Cython.Tempita._tempita import _TemplateBreak
from PIL import Image
import time
from numpy import eye
from LoadMZML import LoadMZML
from OptimalMz import OptimalMz
from OptimalMzII import OptimalMzII
from PlotImage import PlotImage
from LetterRecognition import LetterRecognition


# % matplotlib inline

def graphVlines(loadMZML, x_start_mm, x_stop_mm, mzRangeLower, mzRangeHighest):
    start = time.clock()

    data = loadMZML.data
    run = loadMZML.run
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

            # for (mz, i) in spectrum.peaks:
            #   if mzRangeLower <= mz <= mzRangeHighest:
            for mz, i in LoadMZML.generator(spectrum.peaks, mzRangeLower, mzRangeHighest):
                mz_g.append(mz)
                i_g.append(i)

    sys.stdout.write("\r100%\n")
    end = time.clock()
    print("%.2fs" % (end - start))

    fig = plt.figure()
    plt.plot(mz_g, i_g, 'b^')
    plt.vlines(mz_g, [0], i_g)

    # Save Data
    # np.savetxt('mz_{0}-{1}mm{2}-{3}.csv'.format(x_start_mm, x_stop_mm, mzRangeLower, mzRangeHighest), mz_g,
    # delimiter = ",")
    # np.savetxt('i_g_{0}-{1}mm{2}-{3}.csv'.format(x_start_mm, x_stop_mm, mzRangeLower, mzRangeHighest), i_g,
    # delimiter = ",")
    # plt.savefig('Vlines_{0}-{1}mm{2}-{3}mz.png'.format(x_start_mm, x_stop_mm, mzRangeLower, mzRangeHighest))


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

# graphVlines(loadMZML, x_start_mm=30, x_stop_mm=40, mzRangeLower=374, mzRangeHighest=376)
# graphVlines(loadMZML, x_start_mm=40, x_stop_mm=50, mzRangeLower=374, mzRangeHighest=376)

# optimalMz = OptimalMz(loadMZML, x_start_mm=30, x_stop_mm=40, mzRangeLower=300, mzRangeHighest=500, resolution=200)
# optimalMz.printN()
# optimalMz.plot()

# plotImage = PlotImage(loadMZML, param)
# plotImage.plotImshow(mzRangeLower=374, mzRangeHighest=376)
# plotImage.plotImshowII((324, 326),(374, 376))
# plotImage.printRT()
# plotImage.save()

template_path = '..\\data\\unspecified.png'
generated_b = 137
generated_e = 462
template_b = 343
template_e = 1180
letterRecognition = LetterRecognition(loadMZML, param)

im = Image.open(template_path)
plt.figure()
plt.imshow(np.asarray(im))

template = letterRecognition.RGBtoBW(template_path)
plt.figure()
plt.imshow(np.asarray(template))

template = letterRecognition.alignTemplate(generated_b, generated_e, template_b, template_e, template)
generated = letterRecognition.alignGenerated(generated_b, generated_e, template_b, template_e, (374, 376))
template, generated = letterRecognition.alignment(template, generated)

plt.figure()

plt.imshow(np.asarray(generated), extent=[0, param.widthInMM, 0, param.heightInMM], interpolation='none', cmap='hot')

plt.imshow(np.asarray(template), extent=[0, param.widthInMM, 0, param.heightInMM], interpolation='none', cmap='hot',
           alpha=0.15)  # 0 fully transparent

optimalMzII = OptimalMzII(loadMZML, x_start_mm=30, x_stop_mm=40, mzRangeLower=300, mzRangeHighest=500, resolution=200,
                          letterRecognition=letterRecognition)
optimalMzII.printN()
# optimalMzII.plot()

plt.show()
print("Finished")
