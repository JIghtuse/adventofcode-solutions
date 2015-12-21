#!/usr/bin/python3

from functools import reduce
import math

def prime_factors(n):
    factors = []

    counter = 0
    while n % 2 == 0:
        n //= 2
        counter += 1

    if counter:
        factors.append((2, counter))

    for number in range(3, int(math.sqrt(n) + 1), 2):
        if number >= n:
            break

        counter = 0
        while n % number == 0:
            n //= number
            counter += 1

        if counter:
            factors.append((number, counter))

    if n > 1:
        factors.append((n, 1))

    return factors

def npresents_for_house(n):
    result = 1

    for n, counter in prime_factors(n):
        result *= (n**(counter + 1) - 1) // (n - 1)

    return 10 * result

def main():
    npresents = 29000000
    npres = 0
    house = 0
    while npres < npresents:
        house += 1
        npres = npresents_for_house(house)
    print(house, npres)

if __name__ == "__main__":
    main()
