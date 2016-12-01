#!/usr/bin/python3

from itertools import combinations
import copy

class Person:
    def __init__(self, hp, damage, armor):
        self.hp = hp
        self.damage = damage
        self.armor = armor

    def hit(self, opponent):
        self.hp -= max(1, opponent.damage - self.armor)


def fight(person1, person2):
    turn = 0
    while person1.hp >= 0 and person2.hp >= 0:
        if turn % 2 == 0:
            person2.hit(person1)
        else:
            person1.hit(person2)
        turn += 1
    return person1.hp > 0, person2.hp > 0


def shop():
    weapons = [(8, 4, 0),
               (10, 5, 0),
               (25, 6, 0),
               (40, 7, 0),
               (74, 8, 0)]

    armors = [None,
             (13, 0, 1),
             (31, 0, 2),
             (53, 0, 3),
             (75, 0, 4),
             (102, 0, 5)]

    rings = [None,
             None,
             (25, 1, 0),
             (50, 2, 0),
             (100, 3, 0),
             (20, 0, 1),
             (40, 0, 2),
             (80, 0, 3)]

    for w in weapons:
        for a in armors:
            for ring1, ring2 in combinations(rings, 2):
                cost = 0
                damage = 0
                armor = 0

                items = [w, a, ring1, ring2]
                for item in items:
                    if item is not None:
                        cost += item[0]
                        damage += item[1]
                        armor += item[2]
                yield cost, damage, armor


def main():
    enemy = Person(0, 0, 0)

    with open("input") as filep:
        for line in filep:
            line = line.split()
            if line[0].startswith("Hit"):
                enemy.hp = int(line[2])
            elif line[0].startswith("Damage"):
                enemy.damage = int(line[1])
            elif line[0].startswith("Armor"):
                enemy.armor = int(line[1])

    def player_wins(fight, p, enemy):
        return (True, False) == fight(p, enemy)

    mincost = float("inf")
    for cost, damage, armor in shop():
        e = copy.deepcopy(enemy)
        p = Person(100, damage, armor)

        if player_wins(fight, p, e) and cost < mincost:
            mincost = cost
    print(mincost)

    maxcost = float("-inf")
    for cost, damage, armor in shop():
        e = copy.deepcopy(enemy)
        p = Person(100, damage, armor)

        if not player_wins(fight, p, e) and cost > maxcost:
            maxcost = cost
    print(maxcost)

if __name__ == "__main__":
    main()
