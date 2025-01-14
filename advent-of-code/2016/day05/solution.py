#!/usr/bin/python3

import hashlib

DOOR_ID = "abbhdwsy"


def solve_first():
    index = 0
    digest = ""
    password = ""
    while len(password) != 8:
        while not digest.startswith("00000"):
            digest = hashlib.md5((DOOR_ID + str(index)).encode("utf-8")).hexdigest()
            index += 1
        password += digest[5]
        print(password)
        digest = ""
    return password


def solve_second():
    index = 0
    digest = ""
    password = " " * 8
    while password.find(" ") != -1:
        while not digest.startswith("00000"):
            s = DOOR_ID + str(index)
            digest = hashlib.md5(s.encode("utf-8")).hexdigest()
            index += 1
        try:
            pos = int(digest[5])
            if pos < 8 and password[pos] == ' ':
                password = password[:pos] + digest[6] + password[pos+1:]
                print("'{}'".format(password))
        except ValueError:
            continue
        finally:
            digest = ""
            index += 1
    return password


print(solve_first())
print(solve_second())
