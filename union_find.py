#!/usr/bin/python
import sys

parent={}
children={}

def track_root(node):
    while parent[node] != 0:
        node = parent[node]
    return node

while True:
    line = sys.stdin.readline()
    if not line:
        break;
    page1, page2 = line.strip().split()
    if page1 not in parent and page2 not in parent:
        parent[page1] = 0
        parent[page2] = page1
        children[page1] = {page2}
        children[page2] = {0}
    if page1 not in parent and page2 in parent:
        parent[page1] = page2
        children[page1]={0}
        children[page2].add(page1)
    if page2 not in parent and page1 in parent:
        parent[page2] = page1
        children[page2]={0}
        children[page1].add(page2)
    if page1 in parent and page2 in parent:
        root1 = track_root(page1)
        root2 = track_root(page2)
        if root1 != root2:
            parent[root1] = root2
            children[root2].add(root1)

for k,v in parent.items():
    if v == 0:
        print v

    
       

