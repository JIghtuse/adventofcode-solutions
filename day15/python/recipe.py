#!/usr/bin/python3

from collections import defaultdict
from functools import reduce
import operator

NPROPERTIES = 5

def mixtures(n, total):
    start = total if n == 1 else 0

    for i in range(start, total+1):
        left = total - i
        if n-1:
            for y in mixtures(n-1, left):
                yield [i] + y
        else:
            yield [i]

def gather_scores(data, nspoons):
    scores = defaultdict(list)
    for mix in mixtures(len(data), nspoons):
        key = ','.join(str(p) for p in mix)
        for z in range(NPROPERTIES):
            s = sum(mix[m] * data[name][z] for m, name in enumerate(data))
            scores[key].append(max(0, s))
    return scores

def score(properties):
    return reduce(operator.mul, properties[1][:-1], 1)

def score_500_calories(properties):
    if properties[1][NPROPERTIES - 1] != 500:
        return 0
    return score(properties)

def main():
    data = {}
    for line in open("input"):
        line = line.split()
        data[line[0]] = [int(line[i].strip(',')) for i in range(2, 10 + 1, 2)]

    scores = gather_scores(data, 100)

    best = max(scores.items(), key=score)
    print(score(best))

    best_500 = max(scores.items(), key=score_500_calories)
    print(score_500_calories(best_500))

if __name__ == "__main__":
    main()
