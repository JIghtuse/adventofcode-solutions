#!/usr/bin/python3


def read_data(fname):
    data = []
    for line in open(fname).readlines():
        d, checksum = line[:-8], line[-7:-2]
        d = d.split('-')
        d, sector_id = d[:-1], d[-1]

        data.append((d, int(sector_id), checksum))
    return data


def solve_first(fname):
    data = read_data(fname)
    s = 0
    for names, sector_id, checksum in data:
        names = ''.join(names)
        names = list(set([(-names.count(n), n) for n in names]))
        names.sort()
        checksum_actual = ''.join(n[1] for n in names[:5])
        if checksum_actual == checksum:
            s += sector_id
    return s


def caeser_shift(s, rotation):
    return ''.join(chr(((ord(c) - ord('a') + rotation) % 26) + ord('a')) for c in s)


def solve_second(fname):
    data = read_data(fname)
    for names, sector_id, _ in data:
        names = [caeser_shift(s, sector_id % 26) for s in names]
        names = ' '.join([''.join(n) for n in names])
        if names.startswith('northpole'):
            return names, sector_id


print(solve_first("input.txt"))
print(solve_second("input.txt"))
