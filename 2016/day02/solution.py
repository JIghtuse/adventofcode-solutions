#!/usr/bin/python3


DIRECTIONS = {'U': (-1, 0),
              'D': (1, 0),
              'R': (0, 1),
              'L': (0, -1)}


def read_data(fname):
    data = []
    for line in open(fname).readlines():
        data.append(line.strip())
    return data


def move(coordinate, command, xmax, ymax):
    candidate = coordinate.copy()
    candidate[0] += DIRECTIONS[command][0]
    candidate[1] += DIRECTIONS[command][1]
    if 0 <= candidate[0] < xmax and 0 <= candidate[1] < ymax:
        return candidate
    return coordinate


def get_first_bathroom_key():
    keypad = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
    coordinate = [1, 1]

    data = read_data("input.txt")
    key = ''

    for button_commands in data:
        for command in button_commands:
            coordinate = move(coordinate, command, len(keypad[0]), len(keypad))
        key += str(keypad[coordinate[0]][coordinate[1]])
    return key


def get_second_bathroom_key():
    data = read_data("input_simple")
    coordinate = [2, 0]
    keypad = [[None, None, 1, None, None],
              [None, 2, 3, 4, None],
              [5, 6, 7, 8, 9],
              [None, 'A', 'B', 'C', None],
              [None, None, 'D', None, None]]
    key = ''

    for button_commands in data:
        for command in button_commands:
            candidate = move(coordinate, command, len(keypad[0]), len(keypad))
            if keypad[candidate[0]][candidate[1]] is not None:
                coordinate = candidate
        key += str(keypad[coordinate[0]][coordinate[1]])
    return key

print(get_first_bathroom_key())
print(get_second_bathroom_key())
