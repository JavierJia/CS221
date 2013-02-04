#!/usr/bin/python

import sys,re
from sys import stdin, stdout

splitRE = re.compile('\\W')
fullnumber = re.compile('^[0-9]+$')

def insert( mydict, word, value=1):
    if word in mydict:
        mydict[word] = mydict[word]+value
    else:
        mydict[word] = value

def merge( xdict, ydict):
    for k, v in ydict.items():
        insert (xdict, k, v)
    return xdict

def clean_dict(vocab):
    import operator
    from sets import Set
    stopword = Set([ w.strip().lower() for w in [ line for line in open('stopword.txt').readlines()]])
    sorted_x = sorted(vocab.iteritems(), key=operator.itemgetter(1), reverse=True)
    x = {}
    for (k,v) in sorted_x:
        if len(k)>1 and sum([ w in stopword for w in k.split()])==0 :
            x [k] = v
    return x
    
def stat (text):
    # stuff need to return
    # count, vocab, bigram
    (count, vocab, bigram) = (0, {}, {})

    preword = ''
    for word in splitRE.split(text):
        if len(word) < 1 or len(fullnumber.findall(word))>0 :
            preword = ''
            continue
        count +=1
        word = word.lower()
        insert( vocab, word)
        if len(preword) > 0:
          insert( bigram, preword + ' ' + word)
        preword = word
    return (count, vocab, bigram)

def dumpDict( mydict):
    cleandict = clean_dict(mydict)
    for (k,v) in cleandict:
        print k + '\t' + str(v)
        

if __name__ == '__main__':
    longest = 0
    (gcount, gvocab, gbigram) = (0, {}, {})
    while True:
        url = stdin.readline()
        if not url:
            break
        title = stdin.readline()
        if not title:
            break
        text = stdin.readline()
        if not text:
            break
        stdin.readline()
        
        (count, vocab, bigram) = stat(text)
        if count > longest:
            longgestpage = url
            longest = count
        gcount += count
        (gvocab, gbigram)  = ( merge(gvocab, vocab), merge(gbigram, bigram))
        
    print gcount, 'longgest:', longgestpage, longest
    dumpDict(gvocab)
    print
    dumpDict(gbigram)

