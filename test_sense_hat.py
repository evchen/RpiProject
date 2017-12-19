from sense_hat import SenseHat
import threading,sys

def getVal():
    print("Temperature is: %s" % SenseHat().get_temperature())
    print("Humidity is : %s" % SenseHat().get_humidity())
    threading.Timer(2, getVal).start()

getVal()

