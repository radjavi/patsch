import sys
import re

# (1,12,10,8,6,8,10,12) [] (1,0)(2,0)(3,0)(4,0)(5,0)(6,0)(7,0)(6,0)(5,0)(4,0)(3,0)(2,0)(1,0)


def processData(file):
    allInstances = 0
    nrOfGreaterThanr = 0
    for line in file:
        if line[0] != "[":
            continue
        allInstances += 1
        line_list = line.split(" ")
        m = re.findall('[0-9]+', line_list[0])
        for i in m:
            if int(i) > int(sys.argv[2]):
                nrOfGreaterThanr += 1
                break

    print(allInstances - nrOfGreaterThanr)


if __name__ == "__main__":
    fileName = sys.argv[1]
    with (open(fileName, "r")) as f:
        processData(f)
