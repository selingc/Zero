import feedparser
import json
import sys

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
for e in entries:
	title = e.get('title', 'n/a')
	description = e.get('description', 'n/a')
	geo_long = e.get('geo_long','n/a')
	geo_lat = e.get('geo_lat','n/a')
	published = e.get('published','n/a')
	if len(e.enclosures):
		image = e.enclosures[0].get('href','n/a')
	else:
		image = 'n/a'
	dic = {'title':title, 'description':description,'longtitude':geo_long, 'latitude':geo_lat, 'image':image}
	s = json.dumps(dic) # dump to json format, in java it's so messy
	file.write(s)
	if e != entries[-1]:
		file.write(',')
file.write(']')
file.close()


