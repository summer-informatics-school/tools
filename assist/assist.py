#!/usr/bin/env python3

import sys

limit = 18
file = "assist.txt"
if len(sys.argv) > 1:
  file = sys.argv[1]

#with open('assist-2.0.txt', 'r') as f:
with open(file, 'r') as f:
    lines = f.readlines()

result = []
replic = False
for line in lines:
    words = line.split()
    if not words:
        replic = False
        continue
    replic_now = False
    if words[0][-1] == ':':
        replic = True
        replic_now = True
        if replic:
            name = words[0][0:-1]
    song = replic and name == ''
    pack, length = [[]], 0
    for word in words:
        if length != 0 and length + len(word) > limit:
            pack.append([])
            length = 0
        length += len(word)
        word = word.replace('"', '\\"')
        pack[-1].append(word)
    if replic_now:
        pack[0][0] = "<span style='color: white;'>" + pack[0][0] + "</span>"
    for p in pack:
        if song:
            result.append("<span style='color: pink;'>" + ' '.join(p) + "</span>")
        elif replic:
            result.append(' '.join(p))
        else:
            result.append("<span style='color: lightgreen;'>" + ' '.join(p) + "</span>")

file = 'assist.json'
if len(sys.argv) > 2:
  file = sys.argv[2]
with open(file, 'w') as f:
    print("var lines = [", file=f);
    for line in result:
        print("    \"%s\"," % line, file=f)
    print("];", file=f);

