#########################################################################################
# Script based on:                                                                      #
#                                                                                       #
# https://gis.stackexchange.com/questions/59339/generate-random-world-point-geometries  #
# https://www.shanelynn.ie/batch-geocoding-in-python-with-google-geocoding-api/         #
#                                                                                       #
#########################################################################################
from random import uniform
import sys

def newpoint():
   return uniform(-51,-45), uniform(-19, -15)

DEBUG__ = False

file = sys.argv[1]
file_out = "ratings_with_latlng.csv"
count = 0

f_out = open(file_out, 'w')
f = open(file, 'r')

lines = f.readlines()
positions = dict()

for line in lines:
    line_str = line.rstrip('\n')
    usr_id = line.split(",")[0]

    if count == 0:
        line_str += ",lat,lng"
    else:
        loc = newpoint()
        if usr_id in positions:
            loc = positions[usr_id]
        else:
            positions[usr_id] = loc
        line_str += "," + str(loc[0])  + "," + str(loc[1])

    count = 1
    f_out.write(line_str+"\n")
    if DEBUG__:
        print(line_str)

f_out.close()
f.close()
