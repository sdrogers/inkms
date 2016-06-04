import numpy as np
from PIL import Image

im = Image.open('..\\..\\data\\unspecified.png')
print(im.getpixel((23, 0)))  # 238,246,255
#  When translating a color image to black and white (mode "L"),
#  the library uses the ITU-R 601-2 luma transform::
#  L = R * 299/1000 + G * 587/1000 + B * 114/1000

gray = im.convert('L')
print(gray.getpixel((23, 0)))

# Let numpy do the heavy lifting for converting pixels to pure black or white
bw = np.asarray(gray).copy()

# Pixel range is 0...255, 256/2 = 128
bw[bw < 128] = 0  # Black
bw[bw >= 128] = 255  # White

# Now we put it back in Pillow/PIL land
imfile = Image.fromarray(bw)
imfile.save("unspecified_bw.png")
imfile.show()
