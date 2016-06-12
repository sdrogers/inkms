# coding: utf-8
import numpy as np
import PIL
from PIL import Image
from PlotImage import PlotImage


class LetterRecognition(object):
    def __init__(self, loadMZML, param):
        self.loadMZML = loadMZML
        self.param = param

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

    def alignTemplate(self, generated_b, generated_e, template_b, template_e, template):
        genHeight = len(self.loadMZML.data)
        genWidth = len(self.loadMZML.data[0])
        ratio = (generated_e - generated_b) / (template_e - template_b)
        self.offsetX = int(generated_b - (template_b * ratio))

        self.template = template.resize((int(template.size[0] * ratio), genHeight), PIL.Image.ANTIALIAS)

        # print(self.template.size)
        self.tempHeight = self.template.size[1]
        self.tempWidth = self.template.size[0]
        return self.template

    def alignGenerated(self, generated_b, generated_e, template_b, template_e, *rangeTuples):
        genHeight = len(self.loadMZML.data)
        genWidth = len(self.loadMZML.data[0])

        generated = PlotImage(self.loadMZML, self.param).getPillowImage(*rangeTuples)
        generated = generated.crop((self.offsetX, 0, genWidth, genHeight))

        return generated

    def alignment(self, template, generated):
        # template.save('template.png')
        # generated.save('generated.png')
        # print(template.size)
        # print(generated.size)

        if template.size[0] < generated.size[0]:
            generated = generated.crop((0, 0, template.size[0], template.size[1]))
        else:
            template = template.crop((0, 0, generated.size[0], generated.size[1]))

        return template, generated

    def checkIfLetter(self, x, line):
        x = x - self.offsetX
        if x >= 0 and x < self.tempWidth and line >= 0 and line < self.tempHeight:
            if self.template.getpixel((x, line)) < 128:  # black
                return True
            else:
                return False
        else:
            return False
