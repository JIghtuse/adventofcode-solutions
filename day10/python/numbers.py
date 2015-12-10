#!/usr/bin/python3

from itertools import groupby

def iterate(inp):
    s = ""
    for k, g in groupby(inp):
        s += str(len(list(g))) + str(k)
    return s

def length_niter(inp, n):
    for _ in range(n):
        inp = iterate(inp)
    return len(inp)

inp = '1113122113'

print(length_niter(inp, 40))
print(length_niter(inp, 50))
