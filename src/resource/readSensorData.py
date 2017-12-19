from sense_hat import SenseHat
import sys

def temp():
    print("%s" %SenseHat().get_temperature())
    return

def humidity():
    print("%s" % SenseHat().get_humidity())
    return

map = {
        "/3303/0/5700" : temp,
        "/3304/0/5700" : humidity,
        }

val = sys.argv[1]
map[val]()
