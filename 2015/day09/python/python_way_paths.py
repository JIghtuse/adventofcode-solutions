#!/usr/bin/python3

from collections import defaultdict
from itertools import permutations

places = set()
graph = defaultdict(dict)
for line in open("../input"):
    src, _, dst, _, dist = line.split()
    places.add(src)
    places.add(dst)
    graph[src][dst] = int(dist)
    graph[dst][src] = int(dist)

distances = []
for perm in permutations(places):
    distances.append(sum(map(lambda x, y: graph[x][y], perm[:-1], perm[1:])))

print(min(distances))
print(max(distances))
