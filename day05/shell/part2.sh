#!/bin/sh

egrep "(..).*\1" input | egrep "(.).\1" | wc -l
