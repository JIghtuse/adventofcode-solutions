#!/usr/bin/python3

import re

lines = []
with open("../input") as filep:
    lines = filep.readlines()

orig = 0
for line in lines:
    line = line.strip()
    orig += len(line)

esc = 0
for line in lines:
    line = line.strip()
    line = line[1:-1]
    line = re.sub(r'\\x[0-9a-f]{2}', 'Z', line)
    line = re.sub(r'\\"', '"', line)
    line = re.sub(r'\\\\', 'B', line)
    esc += len(line)

unesc = 0
for line in lines:
    line = line.strip()
    line = re.sub(r'\\', '\\\\\\\\', line)
    line = re.sub(r'"', '\\"', line)
    line = '"' + line + '"'
    unesc += len(line)

print(orig-esc)
print(unesc-orig)

#    print("line: [{}] {}".format(len(line), line))
