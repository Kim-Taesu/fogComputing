#!/usr/bin/env python
# coding=utf8
import csv
import sys

from bs4 import BeautifulSoup

readerTmp = open(sys.argv[1], 'r')
reader = csv.reader(readerTmp, delimiter=",")

svg = open('./data/south_korea.svg', 'rt', encoding='UTF8').read()

senior_count = {}
counts_only = []
min_value = 100;
max_value = 0;
past_header = False

for row in reader:
    if not past_header:
        past_header = True
        continue

    try:
        unique = row[0]
        count = float(row[1].strip())
        senior_count[unique] = count
        counts_only.append(count)
    except:
        pass

soup = BeautifulSoup(svg, 'html.parser')

paths = soup.findAll('path')

colors = ["#8000FF", "#BF00FF", "#FF00BF", "#FF0000", "#FF4000", "#FF8000", "#FFBF00", "#FFFF00", "#BFFF00", "#80FF00",
          "#40FF00", "#088A4B"]

path_style = 'font-size:12px;fill-rule:nonzero;stroke:#FFFFFF;stroke-opacity:1;stroke-width:0;stroke-miterlimit:4;stroke-dasharray:none;stroke-linecap:butt;marker-start:none;stroke-linejoin:bevel;fill:'

for p in paths:
    if p['id']:
        try:
            count = senior_count[p['id']]
        except:
            continue
        if count > 40:
            color_class = 0
        elif count > 30:
            color_class = 1
        elif count > 20:
            color_class = 2
        elif count > 15:
            color_class = 3
        elif count > 10:
            color_class = 4
        elif count > 9:
            color_class = 5
        elif count > 7:
            color_class = 6
        elif count > 5:
            color_class = 7
        elif count > 3:
            color_class = 8
        elif count > 1:
            color_class = 9
        else:
            color_class = 10

        color = colors[color_class]
        p['style'] = path_style + color

# 메모장에 복사 후 *.sgv 파일로 생성
# 한글 인코딩 문제는 'euc-kr' 설정

f = open('./data/test.svg', 'w+', encoding='UTF8')

# print(soup.prettify())

firstLine = True
for i in soup.prettify().split("\n"):
    if firstLine:
        firstLine = False
        continue
    f.write(i + "\n")

f.close()

