#!/usr/bin/python3

from itertools import permutations
from collections import defaultdict

graph = defaultdict(dict)
for line in open("../input"):
    person, _, action, amount, _, _, _, _, _, _, neighbour = line.split()
    amount = int(amount)
    neighbour = neighbour[:-1]
    if action == "lose":
        amount *= -1
    graph[person][neighbour] = amount

persons = graph.keys()

def best_arrangement(graph):
    maxchange = 0
    maxperm = ()

    for perm in permutations(persons):
        change = 0
        perm = list(perm) + [ perm[0] ]
        for (i, person) in enumerate(perm[:-1]):
            right_neighbour = perm[i + 1]
            change += graph[right_neighbour][person]
            change += graph[person][right_neighbour]
        if change > maxchange:
            maxchange = change
            maxperm = perm

    return maxchange, maxperm

print(best_arrangement(graph))

graph['Me'] = {person: 0 for person in persons}
for person in graph:
    graph[person]['Me'] = 0

print(best_arrangement(graph))
