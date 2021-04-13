from ast import literal_eval as make_tuple
import re
import matplotlib.pyplot as plt
import sys
import os
from pathlib import Path


def plotVisualizer(solution, m, instance):
    CoordinatesX = []
    CoordinatesY = []
    tuples = re.findall("\([0-9]+\,[0-9]+\)", solution)
    path = [make_tuple(x) for x in tuples]

    for x in range(len(path)):
        CoordinatesX.append((x, path[x][0]))
        CoordinatesY.append((x, path[x][1]))

    X_zipped = list(zip(*CoordinatesX))
    Y_zipped = list(zip(*CoordinatesY))
    figure = plt.figure()
    plt.plot((X_zipped[0]), (X_zipped[1]))
    plt.plot((Y_zipped[0]), (Y_zipped[1]))
    plt.axis([0, len(path)-1, -1, m+1])

    figure.savefig("figures/m="+str(m) + "/" + instance)
    plt.close()


if __name__ == "__main__":
    fileName = sys.argv[1]
    with (open(fileName, "r")) as f:
        mString = f.readline()
        m = int(mString[2:])
        Path("figures/m="+str(m)+"/").mkdir(parents=True, exist_ok=True)
        for line in f:
            line_split = line.split(" ")
            instance = line_split[0]
            solution = line_split[1]
            plotVisualizer(solution, m, instance)
