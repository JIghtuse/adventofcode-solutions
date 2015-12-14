#!/usr/bin/python3

from collections import defaultdict
import operator

data = {}
for line in open("../input"):
    name, _, _, speed, _, _, speed_for, *_, rest, _ = line.split()
    data[name] = (int(speed), int(speed_for), int(rest))

time = 2503
runs = defaultdict(int)
for name in data:
    speed, speed_for, rest = data[name]
    cycle_time = speed_for + rest
    nruns = time // cycle_time
    rem = time - nruns * cycle_time
    runs[name] = speed * (nruns * speed_for + min(rem, speed_for))

print(sorted(runs.items(), key=operator.itemgetter(1)))

runs = defaultdict(int)
scores = defaultdict(int)
for t in range(time):
    for name in data:
        speed, speed_for, rest = data[name]
        cycle_time = speed_for + rest
        normalized_time = t % cycle_time
        if normalized_time < speed_for:
            runs[name] += speed

    runs_cur = sorted(runs.items(), key=operator.itemgetter(1))
    best_distance = runs_cur[-1][1]
    for (dame, distance) in runs_cur[::-1]:
        if distance == best_distance:
            scores[dame] += 1

print(sorted(scores.items(), key=operator.itemgetter(1)))
