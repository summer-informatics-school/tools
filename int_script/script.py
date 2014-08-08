import sys, os

lines = [x.strip() for x in sys.stdin.readlines() if len(x.strip()) != 0]
#print lines
teams = 0
qs = 0
tlist = []
qlist = []
for l in lines:
    if l[0] == '#':
        continue
    s = l.split()
    if s[0] == 'q': # q qn price
        qs += 1
        qlist.append(s[1])
    elif s[0] == 't':
        tlist.append(' '.join(s[1:]))
        teams += 1
res = [['' for i in xrange(qs)] for j in xrange(teams)]
total = [0] * teams
#print res
cq = -1
coef = 0
for l in lines:
    if l[0] == '#':
        continue
    s = [x for x in l.split() if x != '']
    if s[0] == 'q':
        cq += 1
        coef = int(s[2])
    elif s[0] != 't':
        t = int(s[0])
        r = int(s[1])
        res[t][cq] = str(r)
        total[t] += coef * r

print ';'.join(["\"\""] + ['"' + qlist[i] + '"' for i in xrange(qs)])

for i in xrange(len(res)):
    print ';'.join(['"' + tlist[i] + '"'] + ['"' + x + '"' for x in res[i]] + ['"' + str(total[i]) + '"'])
