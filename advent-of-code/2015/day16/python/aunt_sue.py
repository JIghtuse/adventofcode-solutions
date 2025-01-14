#!/usr/bin/python3

from collections import defaultdict

def equal(a, b):
    return all(item in b and b[item] == a[item] for item in a)

def equal_real(a, b):
    for item in a:
        if item not in b:
            return False
        if item in ["cats", "trees"]:
            if a[item] <= b[item]:
                return False
        elif item in ["pomeranians", "goldfish"]:
            if a[item] >= b[item]:
                return False
        elif b[item] != a[item]:
            return False
    return True

def find_sue(data, key, equal_function):
    for sue in data:
        if equal_function(data[sue], key):
            return sue
    return None

def main():
    data = defaultdict(dict)
    for line in open("input"):
        line = line.split()
        sue = line[1]
        line = line[2:]
        for i in range(len(line)//2):
            item = line[2*i].strip(',').strip(':')
            value = line[2*i+1].strip(',')
            data[sue][item] = int(value)

    key = {"children": 3,
            "cats": 7,
            "samoyeds": 2,
            "pomeranians": 3,
            "akitas": 0,
            "vizslas": 0,
            "goldfish": 5,
            "trees": 3,
            "cars": 2,
            "perfumes": 1}

    print(find_sue(data, key, equal))
    print(find_sue(data, key, equal_real))

if __name__ == "__main__":
    main()
