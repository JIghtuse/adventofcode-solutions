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

def op_eq(value):
    return value

def op_not(value):
    return ~value & 0xffff

OPERATIONS = {"EQ": op_eq,
              "NOT": op_not,
              "AND": operator.iand,
              "OR": operator.ior,
              "RSHIFT": operator.rshift,
              "LSHIFT": operator.lshift}

def memoize(f):
    memo = {}
    def helper(graph, key):
        if key not in memo:
            memo[key] = f(graph, key)
        return memo[key]
    return helper

@memoize
def find_key(graph, key):
    try:
        return int(key)
    except ValueError:
        pass
    value = graph[key]
    op = value[0]

    if len(value) == 2:
        return OPERATIONS[op](find_key(graph, value[1]))
    else:
        return OPERATIONS[op](find_key(graph, value[1]), find_key(graph, value[2]))

def main():
    graph = read_graph("../input")
    print(find_key(graph, 'a'))

if __name__ == "__main__":
    main()
