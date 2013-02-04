#!/usr/bin/python
from webstat import *
import sys
import hashlib
import random

def dictmd5(bigram):
    h = hashlib.md5()
    h.update(str(bigram.items()))
    return h.hexdigest()

def remove_duplicate():
    
    text_md5 = {}
    while True:
        url = sys.stdin.readline();
        if not url:
            break
        title = sys.stdin.readline()
        if not title:
            break
        text = sys.stdin.readline()
        if not text:
            break
        sys.stdin.readline()
        (count, vocab, bigram) = stat(text)

        bigram = clean_dict(bigram)
        md5 = dictmd5(bigram)
        if md5 not in text_md5:
            text_md5[md5] = url
            print url.strip()
            print title.strip()
            print '\t'.join([ k+':'+str(v) for (k, v) in bigram.items()])

def similarity( set1, set2):
    common = 0
    for k,v in set1.items():
        if k in set2:
            common+=1
    return common*0.1/ (len(set1) + len(set2)-common)
        
def cluster():
    sketchmap = {}
    while True:
        url = sys.stdin.readline();
        if not url:
            break
        title = sys.stdin.readline()
        if not title:
            break
        text = sys.stdin.readline()
        if not text:
            break
        if len(text.strip())<2 or url.find('http:')!=0:
            continue
        url = url.strip()
        text = text.strip()
        bigram = { k:int(v) for k,v in [ item.split(':') for item in text.split('\t')]}
        sketch = generate_sketch (bigram, 10)
        for shingle in sketch:
            insert_shingle( sketchmap, shingle, url)

    #debug
    #print >> sys.stderr, len(sketchmap)
    #printmap( sketchmap)
    find_equility_set( sketchmap, 8)
    #printmap(equility_set) 
    #return union_find( equility_set)

def printmap( mapx):
    for k,v in mapx.items():
        print k, v

def generate_sketch( bigram, randomsize):
    shingles = []
    #bitlong = (1<<32)-1
    bitlong = 32
    for k, v in bigram.items():
        h = hashlib.md5()
        h.update(k)
        shingles.append ( 1 << (hash(h.digest()) % bitlong))
    # random shift to make sketch
    sketch = []
    for x in xrange(0, randomsize):
        sketch.append ( shingles[ int(len(shingles) * random.random())])
    return sketch

def insert_shingle( sketchmap, shingle, url):
    if shingle in sketchmap:
        sketchmap[shingle].add(url)
    else:
        sketchmap[shingle] = {url}

def find_equility_set( sketchmap, overlapnum):
    equility_set = {}
    for k, urlset in sketchmap.items():
        if len(urlset) < 2:
            continue
        for url in urlset:
            if url in equility_set:
                equility_set[url].add(k)
            else:
                equility_set[url] = {k}

    for k1, numset1 in equility_set.items():
        if len(numset1) < overlapnum:
            continue
        for k2, numset2 in equility_set.items():
            if k1==k2 or len(numset2) < overlapnum:
                continue
            if len(numset1.intersection(numset2)) > overlapnum:
                print k1,k2
#    valid_entity = []
#    for k, v in equility_set.items():
#        if v > overlapnum:
#            valid_entity.append(k)

def union_find( equility_set):
    for url_pair in equility_set:
        print url_pair


if __name__ == '__main__':
    import getopt
    if len(sys.argv)==1:
        print ' -d < webfile > url and its signature'
        print ' -c < signature file > clustered result'
        sys.exit(0)
    opts, args = getopt.getopt(sys.argv[1:], 'dc')
    bDuplicate, bCluster = (False, False)
    for o, a in opts:
        if o == '-d':
            bDuplicate = True
        elif o == '-c':
            bCluster = True
        else:
            assert False, "unhandled option"

    if bDuplicate:
        remove_duplicate()
    if bCluster:
        cluster()


