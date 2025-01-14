#!/usr/bin/python3

from collections import Counter


def read_data(fname):
    return list(open(fname).readlines())


def solve_first(fname):
    data = read_data(fname)
    return ''.join((Counter(d).most_common()[0][0]) for d in zip(*data))


def solve_second(fname):
    data = read_data(fname)
    return ''.join((Counter(d).most_common()[-1][0]) for d in zip(*data))


print(solve_first("input.txt"))
print(solve_second("input.txt"))
