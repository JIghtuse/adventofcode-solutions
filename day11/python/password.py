#!/usr/bin/python3

import string
ALPHABET = string.ascii_lowercase

def rule1(word):
    for i in range(len(word) - 2):
        if word[i:i+3] in ALPHABET:
            return True
    return False

def rule2(word):
    return all((c not in word for c in 'iol'))

def rule3(word):
    nrep = 0
    skipnext = False
    for i in range(len(word) - 1):
        if skipnext:
            skipnext = False
            continue
        if word[i] == word[i + 1]:
            nrep += 1
            skipnext = True
    return nrep > 1

def shift(s):
    data = list(s)

    for i in range(len(data) - 1, -1, -1):
        data[i] = chr((ord(data[i]) - ord('a') + 1) % 26 + ord('a'))
        if data[i] != 'a':
            break
    return ''.join(data)

def next_password(current_password):
    rules = [ rule1, rule2, rule3 ]
    password = shift(current_password)
    while not all((t(password) for t in rules)):
        password = shift(password)
    return password

password = "hxbxwxba"
password = next_password(password)
print("answer: ", password)
password = next_password(password)
print("answer: ", password)
