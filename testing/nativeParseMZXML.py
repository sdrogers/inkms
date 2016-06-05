import xml.etree.ElementTree as ET
import zlib
import base64
import struct

namespace = "{http://sashimi.sourceforge.net/schema_revision/mzXML_3.2}"
tree = ET.parse('data\\abcdefgh_1.mzXML')
root = tree.getroot()

# print(root.tag, "\n")
# print(root.attrib, "\n") #dictionary


for root_child in root.getchildren():
    # print(root_child.tag, ':', root_child.attrib)
    if root_child.tag == namespace + "msRun":
        for msRun_children in root_child.getchildren():
            # print(msRun_children.tag)
            if msRun_children.tag == namespace + "scan":
                for scan_children in msRun_children.getchildren():
                    # peak children
                    # print(scan_children.tag, ':', scan_children.attrib)
                    # print(scan_children.text)
                    # print(help(scan_children))
                    data = base64.b64decode(scan_children.text)
                    decompressed_bytes = zlib.decompress(data)
                    barray = bytearray()
                    k=0
                    j=0
                    for i in decompressed_bytes:
                        barray.append(i)
                        j=j+1
                        if j == 8:
                            k=k+1
                            print(struct.unpack('!d', barray)[0]) #! network order , 64 bits
                            #print(type(struct.unpack('!d', ba)[0]))
                            barray = bytearray()
                            j = 0
                    print(k)

print("Finished")
