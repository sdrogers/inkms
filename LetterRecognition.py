# coding: utf-8
import numpy as np
import PIL
from PIL import Image
from PlotImage import PlotImage


class LetterRecognition(object):
    def __init__(self, loadMZML, param):
        self.loadMZML = loadMZML
        self.param = param
        self.height = len(loadMZML.data)
        self.width = len(loadMZML.data[0])

    def RGBtoBW(self, template_path):
        # http://stackoverflow.com/questions/18777873/convert-rgb-to-black-or-white
        im = Image.open(template_path)

        #  When translating a color image to black and white (mode "L"),
        #  the library uses the ITU-R 601-2 luma transform::
        #  L = R * 299/1000 + G * 587/1000 + B * 114/1000
        gray = im.convert('L')

        # Let numpy do the heavy lifting for converting pixels to pure black or white
        bw = np.asarray(gray).copy()

        # Pixel range is 0...255, 256/2 = 128
        bw[bw < 128] = 0  # Black
        bw[bw >= 128] = 255  # White

        # Now we put it back in Pillow/PIL land
        imfile = Image.fromarray(bw)
        return imfile

    def alignment(self, template, original_b, original_e, template_b, template_e, *rangeTuples):
        ratio = (original_e - original_b) / (template_e - template_b)

        org = PlotImage(self.loadMZML, self.param).getPillowImage(*rangeTuples)
        template = template.resize((int(template.size[0] * ratio), self.height), PIL.Image.ANTIALIAS)

        offsetX = int(original_b - (template_b * ratio))
        org = org.crop((offsetX, 0, self.width, self.height))

        return {'org': org, 'template': template}
