#!/usr/bin/python3

from collections import defaultdict
from itertools import combinations

def main():
    containers = []
    for line in open("input"):
        containers.append(int(line.strip()))

    nliters = 150

    count = 0
    for j in range(len(containers)):
        for i in combinations(containers, j):
            if sum(i) == nliters:
                count += 1
    print(count)

    count = defaultdict(int)
    for j in range(len(containers)):
        for i in combinations(containers, j):
            if sum(i) == nliters:
                count[len(i)] += 1
    print(count[min(count.keys())])

if __name__ == "__main__":
    main()
