Group work
Jianfeng Jia
Pei Li

1. About Crawler
We skipped all the url with "[?=]+' 
While crawling, we found that it trap in ftp.ics.uci.edu and fano.ics.uci.edu 
so we restarted by skip the afterward url start by those two site

2. About Data
We store the text into one single file. we keep it url/title/text by 3 lines each page
Then we run the 'webstat.py' to calculate the statistical result.

3. About Extra Credit
We did a simplified shingle-sketch algorithm 
For simplicity, we used 2gram as shingle, map to 32bit value, and make sketch random permutation of 10 times instead of 200,
Then we evaluated the similarity of two page 80%, thus the threshold is set to 8

The framework is written in python. 
cluster.py
union_find.py
