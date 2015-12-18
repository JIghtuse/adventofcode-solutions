#!/usr/bin/python3

from collections import defaultdict
from itertools import combinations
import copy

def on_corners(grid, x2, y2):
    x1, y1 = 0, 0
    for x,y in [(x1,y1),
                (x1,y2),
                (x2,y1),
                (x2,y2)]:
        grid[x][y] = '#'

def memoize(f):
    memo = {}
    def helper(i, j, xm, ym):
        if (i, j, xm, ym) not in memo:
            memo[(i, j, xm, ym)] = f(i, j, xm, ym)
        return memo[(i, j, xm, ym)]
    return helper

@memoize
def adj(i, j, xmax, ymax):
    res = []
    for x in range(i-1,i+2):
        for y in range(j-1,j+2):
            if x == i and y == j:
                continue
            if x >= 0 and y >= 0 and x <= xmax and y <= ymax:
                res.append((x,y))
    return res

assert sum(1 for a in adj(0, 0, 3, 3)) == 3
assert sum(1 for a in adj(0, 1, 3, 3)) == 5
assert sum(1 for a in adj(0, 1, 1, 1)) == 3

def new_grid(grid, stuck):
    new_grid = copy.deepcopy(grid)

    xmax, ymax = len(grid) - 1, len(grid[0]) - 1
    if stuck:
        on_corners(grid, xmax, ymax)

    for i, line in enumerate(new_grid):
        for j, item in enumerate(line):
            neighbours_on = sum(grid[x][y] == '#' for x,y in adj(i, j, xmax, ymax))
            if grid[i][j] == '#':
                if neighbours_on not in [2,3]:
                    new_grid[i][j] = '.'
            else:
                if neighbours_on == 3:
                    new_grid[i][j] = '#'
    if stuck:
        on_corners(new_grid, xmax, ymax)
    return new_grid

def count_on(data):
    return sum(item == '#' for line in data for item in line)

def main():
    data = []
    for line in open("input"):
        data.append([item for item in line.strip()])
    original = copy.deepcopy(data)

    print(count_on(data))
    for _ in range(100):
        data = new_grid(data, False)
    print(count_on(data))

    for _ in range(100):
        original = new_grid(original, True)
    print(count_on(original))

if __name__ == "__main__":
    main()
