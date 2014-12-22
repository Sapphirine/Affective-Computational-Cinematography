import urllib
import re
import csv
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
from BeautifulSoup import BeautifulSoup
html_src = urllib.urlopen('http://movie.douban.com/subject/4881607/reviews').read()
parser = BeautifulSoup(html_src)
k = parser.findAll('a', attrs={'class':'review-hd-avatar'})
num = 0
csvfile = open('output1.csv', 'w')
spamwriter = csv.writer(csvfile,dialect='excel')
for link in k:
    url = link.get('href') + "/collect"
    print link.get('title')
    print "---------"
    html_src = urllib.urlopen(url).read()
    parser = BeautifulSoup(html_src)
    movies = parser.findAll('div', attrs={'class':'item'})
#    movies1 = parser.findAll('span', attrs={'class':re.compile(r"rating(\s)?")})
    for movie in movies:
        a = []
        titles = movie.find('a', attrs={'class':'nbg'})
        ratings = movie.find('span', attrs={'class':re.compile(r"rating(\s)?")})
        if ratings == None:
            continue
        else:
            rating = ratings.get('class')
#            title = titles.get('title')
            print titles.get('title')
            title = titles.get('title')
#            print title
            a.append(str(num))
            a.append(title)
 #           print a
            a.append(rating[6:7])
            spamwriter.writerow(a)
    num = num + 1
    print "--------"
