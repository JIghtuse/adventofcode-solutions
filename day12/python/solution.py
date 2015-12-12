#!/usr/bin/python3

import json

def sum_numbers(data, ignore_tag=""):
    total = 0

    if isinstance(data, dict):
        if ignore_tag in data.keys():
            return 0
        if ignore_tag in data.values():
            return 0
        for k in data.keys():
            total += sum_numbers(data[k], ignore_tag)
    elif isinstance(data, list):
        for item in data:
            total += sum_numbers(item, ignore_tag)
    elif isinstance(data, int):
        total += data
    return total

assert sum_numbers(json.loads('[1,2,3]')) == 6
assert sum_numbers(json.loads('{"a":2,"b":4}')) == 6
assert sum_numbers(json.loads('[[[3]]]')) == 3
assert sum_numbers(json.loads('{"a":{"b":4},"c":-1}')) == 3
assert sum_numbers(json.loads('{"a":[-1,1]} ')) == 0
assert sum_numbers(json.loads('[-1,{"a":1}] ')) == 0
assert sum_numbers(json.loads('[]')) == 0
assert sum_numbers(json.loads('{}')) == 0

data = ""
with open("../input") as filep:
    data = json.load(filep)

print(sum_numbers(data))
print(sum_numbers(data, 'red'))
