import pymzml
import time

start = time.clock()

# Read File
run = pymzml.run.Reader('..\\..\\data\\abcdefgh_1.mzML', MS1_Precision=5e-6)

scansTotal = run.getSpectrumCount()
for index in range(1, scansTotal):
    spectrum = run[index]
    for mz, i in spectrum.peaks:
        # print(mz, i)
        pass

end = time.clock()
print("%.2fs" % (end - start))