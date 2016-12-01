#!usr/bin/env python3

from collections import namedtuple

Command = namedtuple('RotationCommand', ['rotation', 'length'])

DIRECTIONS = [[0, -1], [1, 0], [0, 1], [-1, 0]]


def read_data(fname):
    data = []
    with open(fname) as input_file:
        for line in input_file.readlines():
            for command in line.split(','):
                command = command.strip()
                rotation = command[0]
                length = int(command[1:])
                data.append(Command(rotation, length))
    return data


def distance_from_origin(pos):
    return abs(pos[0]) + abs(pos[1])


def rotate(rotate_direction, direction_idx):
    if rotate_direction == 'L':
        direction_idx -= 1
    else:
        direction_idx += 1
    return direction_idx % len(DIRECTIONS)


def get_first_coordinates():
    direction_idx = 0
    xy = (0, 0)

    for command in read_data("input.txt"):
        direction_idx = rotate(command.rotation, direction_idx)
        direction = DIRECTIONS[direction_idx]

        # xy[0] = xy[0] + direction[0] * command.length
        # xy[1] = xy[1] + direction[1] * command.length
        xy = [k[0] + command.length * k[1] for k in zip(xy, direction)]
    return xy


def get_second_coordinates():
    direction_idx = 0
    xy = (0, 0)
    visited_locations = set(xy)

    for command in read_data("input.txt"):
        direction_idx = rotate(command.rotation, direction_idx)
        direction = DIRECTIONS[direction_idx]
        for i in range(command.length):
            xy = xy[0] + direction[0], xy[1] + direction[1]
            if xy in visited_locations:
                return xy
            visited_locations.add(xy)


print(distance_from_origin(get_first_coordinates()))
print(distance_from_origin(get_second_coordinates()))
