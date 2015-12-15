#!/usr/bin/python3

from collections import defaultdict
from functools import reduce
import operator

data = {}
for line in open("input"):
    line = line.split()
    data[line[0]] = [int(line[i].strip(',')) for i in range(2, 10 + 1, 2)]

MAX = 100

scores = defaultdict(list)

for i in range(1, MAX + 1):
    for j in range(1, MAX - i + 1):
        for k in range(1, MAX - i - j + 1):
            l = MAX - i - j - k
            if l < 0 or i + j + k + l != MAX:
                continue
            props = [i, j, k, l]
            key = ','.join(str(p) for p in props)
            for z in range(5):
                s = sum(props[m] * data[name][z] for m, name in enumerate(data))
                scores[key].append(max(0, s))

def score(properties):
    return reduce(operator.mul, properties[1][:-1], 1)

best = max(scores.items(), key=score)
print(score(best))

def score_500_calories(properties):
    if properties[1][4] != 500:
        return 0
    return reduce(operator.mul, properties[1][:-1], 1)

best_500 = max(scores.items(), key=score_500_calories)
print(score_500_calories(best_500))
