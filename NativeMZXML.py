# coding: utf-8
import xml.etree.ElementTree as ET
import zlib
import base64
import struct
import threading
from queue import Queue
import time
import platform
import multiprocessing
import sys
import numpy as np
import time
from LoadMZML import LoadMZML


class NativeMZXML(object):
    def __init__(self, loadMZML, param, filename):
        namespace = "{http://sashimi.sourceforge.net/schema_revision/mzXML_3.2}"
        tree = ET.parse(filename)
        root = tree.getroot()
        # print(root.tag, "\n")
        # print(root.attrib, "\n") #dictionary

        raw = []
        for root_child in root.getchildren():
            # print(root_child.tag, ':', root_child.attrib)
            if root_child.tag == namespace + "msRun":
                for msRun_children in root_child.getchildren():
                    # print(msRun_children.tag)
                    if msRun_children.tag == namespace + "scan":
                        for scan_children in msRun_children.getchildren():
                            # peak children
                            raw.append(scan_children.text)
        self.raw = raw
        self.loadMZML = loadMZML
        self.param = param

    def getReduceSpec(self, mzRangeLower, mzRangeHighest):

        data = self.loadMZML.getDataStructure()
        start = time.clock()

        result = np.zeros((len(data), len(data[0])), dtype=np.float)

        def do_work(item):
            index = data[item[0]][item[1]]
            spectrum = self.getSpectrumValues(self.raw[index - 1])
            intensity = 0

            for mz, i in LoadMZML.generator(spectrum, mzRangeLower, mzRangeHighest):
                intensity = intensity + i

            result[item[0], item[1]] = intensity

        # The worker thread pulls an item from the queue and processes it
        def worker():
            while True:
                item = q.get()
                do_work(item)
                q.task_done()

        # Create the queue and thread pool.
        q = Queue()
        for i in range(multiprocessing.cpu_count()):
            t = threading.Thread(target=worker)
            t.daemon = True  # thread dies when main thread (only non-daemon thread) exits.
            t.start()

        for line in range(len(data)):
            for column in range(len(data[line])):
                q.put((line, column))

        q.join()  # block until all tasks are done

        end = time.clock()
        print("%.2fs" % (end - start))
        return result

    def getSpectrumValues(self, binary_data):
        data = base64.b64decode(binary_data)
        decompressed_bytes = zlib.decompress(data)
        list = []
        j = 0
        while j < len(decompressed_bytes):
            list.append([struct.unpack('!d', decompressed_bytes[j:j + 8])[0],
                         struct.unpack('!d', decompressed_bytes[j + 8:j + 16])[0]])
            j = j + 16
        return list
