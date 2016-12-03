#!/usr/bin/python3


def read_data(fname):
    data = []
    for line in open(fname).readlines():
        a, b, c = line.split()
        data.append((int(a), int(b), int(c)))
    return data


def read_data_second(fname):
    data = []
    lines = list(open(fname).readlines())
    for line in range(0, len(lines), 3):
        aline = lines[line + 0].split()
        bline = lines[line + 1].split()
        cline = lines[line + 2].split()
        for i in range(3):
            a = aline[i]
            b = bline[i]
            c = cline[i]
            data.append((int(a), int(b), int(c)))
    return data


def number_of_possible_triangles(read_data):
    data = read_data("input.txt")

    return len([True for (a, b, c) in data
                if a + b > c and a + c > b and b + c > a])


print(number_of_possible_triangles(read_data))
print(number_of_possible_triangles(read_data_second))
