import sys
import numpy as np

# (8,8,8,8,8) feasible 0.170919 0
feasibleInstancesET = []
feasibleInstancesnrOfPaths = []

infeasibleInstancesET = []
infeasibleInstancesnrOfPaths = []


def processData(file):
    for line in file:
        line_list = line.split(" ")
        if line_list[1] == "feasible":
            feasibleInstancesET.append(float(line_list[2]))
            feasibleInstancesnrOfPaths.append(int(line_list[3]))
        else:
            infeasibleInstancesET.append(float(line_list[2]))
            infeasibleInstancesnrOfPaths.append(int(line_list[3]))

    print("\n---Feasible---")
    print("sample from", len(feasibleInstancesET), "instances")
    print("mean ET: %.2f" % np.mean(feasibleInstancesET), "ms")
    print("median ET: %.2f" %
          np.median(feasibleInstancesET), "ms")
    print("min ET: %.2f" %
          np.min(feasibleInstancesET), "ms")
    print("max ET: %.2f" %
          np.max(feasibleInstancesET), "ms")
    print("std ET: %.2f" % np.std(feasibleInstancesET), "ms")
    print("mean nr of paths: %.2f" %
          np.mean(feasibleInstancesnrOfPaths), "paths")
    print("median nr of paths:",
          np.median(feasibleInstancesnrOfPaths), "paths")
    print("min nr of paths:",
          np.min(feasibleInstancesnrOfPaths), "paths")
    print("max nr of paths:",
          np.max(feasibleInstancesnrOfPaths), "paths")
    print("std nrPaths: %.2f" % np.std(feasibleInstancesnrOfPaths), "paths")

    print("\n---Infeasible---")
    print("sample from", len(infeasibleInstancesET), "instances")
    print("mean ET: %.2f" %
          np.mean(infeasibleInstancesET), "ms")
    print("median ET: %.2f" %
          np.median(infeasibleInstancesET), "ms")
    print("min ET: %.2f" %
          np.min(infeasibleInstancesET), "ms")
    print("max ET: %.2f" %
          np.max(infeasibleInstancesET), "ms")
    print("std ET: %.2f" % np.std(infeasibleInstancesET), "ms")

    print("mean nr of paths: %.2f" %
          np.mean(infeasibleInstancesnrOfPaths), "paths")
    print("median nr of paths:",
          np.median(infeasibleInstancesnrOfPaths), "paths")
    print("min nr of paths:",
          np.min(infeasibleInstancesnrOfPaths), "paths")
    print("max nr of paths:",
          np.max(infeasibleInstancesnrOfPaths), "paths")
    print("std nrPaths: %.2f" % np.std(infeasibleInstancesnrOfPaths), "paths")


if __name__ == "__main__":
    fileName = sys.argv[1]
    with (open(fileName, "r")) as f:
        processData(f)
