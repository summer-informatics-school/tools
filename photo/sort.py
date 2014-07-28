#!/usr/bin/python3
# -*- coding: utf-8 -*-

import csv
import shutil

def parse_head (rows):
  head = 0
  res = []
  for r in rows:
    if head == 0:
      head = r
      continue
    x = {}
    for i in range (len (head)):
      x[head[i]] = r[i]
    res.append (x)
  return res

if __name__ == '__main__':
  users = parse_head (csv.reader (open ('current.csv')))
  for u in users:
    if u['Номер фото'] == '':
      continue
    name = "selected/" + u['Параллель'] + ' ' + u['Имя'] + ' ' + u['Фамилия'] + ".jpg"
    photo = "photos/DSC_" + u['Номер фото'] + ".JPG"
    print (photo + " -> " + name)
    shutil.copyfile (photo, name)
