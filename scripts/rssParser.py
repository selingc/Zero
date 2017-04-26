import feedparser
import json
import sys
from pygeocoder import Geocoder
import time


#parse with link here
source = sys.argv[1]
filename = sys.argv[2]

#open file
file = open(filename,'w')

#parse feed
parser = feedparser.parse(source)

#entries are <item> tags
entries = parser.entries

# [ ] for json file
file.write('[')
i = 0
for e in entries:
	if i%5 == 0:
		time.sleep(1)
	title = e.get('title', 'n/a')
	description = e.get('description', 'n/a')
	geo_long = e.get('geo_long','n/a')
	geo_lat = e.get('geo_lat','n/a')
	loc = 'n/a'
	if geo_long!='n/a' and geo_lat!='n/a':
		loc = Geocoder.reverse_geocode(float(geo_lat), float(geo_long))
		print(loc)
	published = e.get('published','n/a')
	if len(e.enclosures):
		image = e.enclosures[0].get('href','n/a')
	else:
		image = 'n/a'
	dic = {'title':title, 'description':description,'longtitude':geo_long, 'latitude':geo_lat, 'image':image, 'location': str(loc)}
	s = json.dumps(dic)
	file.write(s)
	if e != entries[-1]:
		file.write(',')
	i+=1
file.write(']')
file.close()


