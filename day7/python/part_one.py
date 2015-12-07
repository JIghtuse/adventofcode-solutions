#!/usr/bin/python3

from collections import defaultdict
import operator

def read_graph(fname):
    graph = defaultdict(list)
    with open(fname) as filep:
        for line in filep.readlines():
            split = line.split()
            if len(split) == 3:
                graph[split[-1]] = ("EQ", split[0])
            elif len(split) == 4:
                graph[split[-1]] = (split[0], split[1])
            else:
                graph[split[-1]] = (split[1], split[0], split[2])
    return graph

def op_not(value):
    return ~value & 0xffff + 1

OPERATIONS = {"AND": operator.iand,
              "OR": operator.ior,
              "RSHIFT": operator.rshift,
              "LSHIFT": operator.lshift}

def memoize(f):
    memo = {}
    def helper(graph, key):
        if key not in memo:
            result = f(graph, key)
            if isinstance(result, int):
                memo[key] = result
            else:
                return result
        return memo[key]
    return helper

@memoize
def find_key(graph, key):
    try:
        key = int(key)
        return key
    except ValueError:
        pass
    value = graph[key]
    op = value[0]

    result = None
    if len(value) == 2:
        if op == "EQ":
            result = find_key(graph, value[1])
        if op == "NOT":
            result = op_not(find_key(graph, value[1]))
    else:
        result = OPERATIONS[op](find_key(graph, value[1]), find_key(graph, value[2]))
    result &= 0xffff
    print(key, result)
    return result


def main():
    graph = read_graph("../input")
    print(find_key(graph, 'a'))

if __name__ == "__main__":
    main()
