from sense_hat import SenseHat
import sys

def temp():
    print("%s" %SenseHat().get_temperature())
    return

def humidity():
    print("%s" % SenseHat().get_humidity())
    return

map = {
        "temp" : temp,
        "humidity" : humidity,
        }

val = sys.argv[1]
map[val]()
