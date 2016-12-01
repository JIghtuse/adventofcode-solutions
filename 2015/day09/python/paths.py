#!/usr/bin/python3

from collections import defaultdict

graph = defaultdict(dict)
for line in open("../input"):
    src, _, dst, _, distance = line.split()
    graph[src][dst] = int(distance)
    graph[dst][src] = int(distance)

cities = list(graph.keys())
total_lengths = []

def print_all(graph, start, visited, total_path=0):
    if all(city in visited for city in graph[start]):
        total_lengths.append(total_path)
        return

    for city in graph[start]:
        if city not in visited:
            print_all(graph, city, [start] + visited, total_path + graph[start][city])

def calc_all(graph):
    for start in cities:
        print_all(graph, start, [])

calc_all(graph)
print(min(total_lengths))
print(max(total_lengths))
