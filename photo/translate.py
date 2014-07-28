#!/usr/bin/python3
# -*- coding: utf-8 -*-

import csv

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
  print ("<html>")
  print ("<head>")
  print ("  <title>ЛКШ.2014.Июль.Фото</title>\n")
  print ("  <link rel=\"stylesheet\" type=\"text/css\" " + \
    "href=\"photo.css\" />")
  print ("<meta http-equiv=\"content-type\" " + \
    "content=\"text/html; charset=UTF-8\" />")
  print ("<meta http-equiv=\"content-language\" content=\"ru-ru\" />")
  print ("<meta http-equiv=\"expires\" content=\"0\" />")
  print ("</head>")
  print ("<body>")
  print ("<table>")
  for u in users:
    print ("<tr><td>" + u['Параллель'] + "</td>")
    print ("<td>" + u['Имя'] + ' ' + u['Фамилия'] + "</td>")
    if u['Номер фото'] != '':
      print ("<td><a href=\"photos/DSC_" + u['Номер фото'] + \
        ".JPG\">фото</a></td>")
    else:
      print ("<td><b>ещё нет фото</b></td>")
    print ("</td>")
    print ("</tr>")
  print ("</table>")
  print ("</body>")
  print ("</html>")
