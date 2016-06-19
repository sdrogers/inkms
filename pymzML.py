# coding: utf-8
import sys
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image
import time
from numpy import eye
from LoadMZML import LoadMZML
from OptimalMz import OptimalMz
from PlotImage import PlotImage
from TemplateOverlay import TemplateOverlay


# % matplotlib inline

def graphVlines(loadMZML, x_start_mm, x_stop_mm, mzRangeLower, mzRangeHighest, ymax):
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

    plt.figure()
    plt.ylim(0, ymax)
    plt.plot(mz_g, i_g, 'b^')
    plt.vlines(mz_g, [0], i_g)

    # Save Data
    # np.savetxt('mz_{0}-{1}mm{2}-{3}.csv'.format(x_start_mm, x_stop_mm, mzRangeLower, mzRangeHighest), mz_g,
    # delimiter = ",")
    # np.savetxt('i_g_{0}-{1}mm{2}-{3}.csv'.format(x_start_mm, x_stop_mm, mzRangeLower, mzRangeHighest), i_g,
    # delimiter = ",")
    # plt.savefig('Vlines_{0}-{1}mm{2}-{3}mz.png'.format(x_start_mm, x_stop_mm, mzRangeLower, mzRangeHighest))


def imagePlotN():
    plotImage = PlotImage(loadMZML, param)
    # plotImage.plotImshow(mzRangeLower=374, mzRangeHighest=376)
    plotImage.plotImshowII((324, 326), (374, 376))
    # plotImage.printRT()
    # plotImage.save('plotImage_324-326_374-376', (324, 326), (374, 376))


def graphVlinesN():
    graphVlines(loadMZML, x_start_mm=30, x_stop_mm=40, mzRangeLower=300, mzRangeHighest=500, ymax=80000)
    graphVlines(loadMZML, x_start_mm=40, x_stop_mm=50, mzRangeLower=300, mzRangeHighest=500, ymax=80000)


def OptimalMzN():
    optimalMz = OptimalMz.V1(loadMZML, x_start_mm=30, x_stop_mm=40, y_start_mm=0, y_stop_mm=10, mzRangeLower=300,
                             mzRangeHighest=500,
                             resolution=200)
    optimalMz.printN()
    optimalMz.plot(2500)
    # optimalMz.save()


def TemplateOverlayN():
    # Parameters
    template_path = '..\\data\\unspecified.png'
    generated_b = 137
    generated_e = 462
    template_b = 343
    template_e = 1180
    plotValues = (374, 376)

    #PlotImage(loadMZML, param).save("abcdef_gen", plotValues)
    templateOverlay = TemplateOverlay(loadMZML, param)

    # Original Image
    im = Image.open(template_path)
    plt.figure()
    plt.imshow(np.asarray(im))

    # Black and White
    template = templateOverlay.RGBtoBW(template_path)
    plt.figure()
    plt.imshow(np.asarray(template), cmap='Greys_r')

    # Overlay template and generated
    template = templateOverlay.alignTemplate(generated_b, generated_e, template_b, template_e, template)
    generated = templateOverlay.alignGenerated(generated_b, generated_e, template_b, template_e, plotValues)
    template, generated = templateOverlay.alignment(template, generated)

    plt.figure()
    plt.imshow(np.asarray(generated), extent=[0, param.widthInMM, 0, param.heightInMM], interpolation='none',
               cmap='hot')
    plt.imshow(np.asarray(template), extent=[0, param.widthInMM, 0, param.heightInMM], interpolation='none', cmap='hot',
               alpha=0.15)  # 0 fully transparent

    # Calculate optimal mass based on template
    optimalMz = OptimalMz.V2(loadMZML, mzRangeLower=300, mzRangeHighest=500, resolution=200,
                             templateClass=templateOverlay)
    optimalMz.printN()
    optimalMz.plot()


def AminoAcidN():
    plotImage = PlotImage(loadMZML, param)

    # cell 1
    # optimalMz = OptimalMz.V1(loadMZML, x_start_mm=0, x_stop_mm=6.25, y_start_mm=0, y_stop_mm=4,
    #                         mzRangeLower=50, mzRangeHighest=200, resolution=600)
    # optimalMz.printN()
    # for i, j in optimalMz.getN(5):
    #    plotImage.plotImshowII((i, j))

    # Cell 20
    optimalMz = OptimalMz.V1(loadMZML, x_start_mm=18.75, x_stop_mm=22, y_start_mm=12, y_stop_mm=15,
                             mzRangeLower=50, mzRangeHighest=200, resolution=600)
    optimalMz.printN()
    for i, j in optimalMz.getN():
        plotImage.plotImshowII((i, j))


class Parameters1:
    def __init__(self):
        #  self.filename = '/Users/simon/Dropbox/MS_Ink_Data/ALphabet/abcdefgh_1.mzML'
        self.filename = '..\\data\\abcdefgh_1.mzML'
        self.lines = 8
        self.widthInMM = 62
        self.heightInMM = 10
        self.downMotionInMM = 1.25


class Parameters2:
    def __init__(self):
        #  self.filename = '/Users/simon/Dropbox/MS_Ink_Data/ALphabet/abcdefgh_1.mzML'
        self.filename = '..\\data\\AA_Array_2.mzML'
        self.lines = 6
        self.widthInMM = 50
        self.heightInMM = 15
        self.downMotionInMM = 3


# param = Parameters1()
# loadMZML = LoadMZML(param)
# imagePlotN()
# graphVlinesN()
# OptimalMzN()
# TemplateOverlayN()

param = Parameters2()
loadMZML = LoadMZML(param, 'positive')
AminoAcidN()

plt.show()
print("Finished")
