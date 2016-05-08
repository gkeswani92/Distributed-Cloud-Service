import xmlrpclib
import time
import ast
services = ["Plumbing", "Gardening", "Taxi", "Baby Sitting"]
proxy=0

def connectMaster():
    global proxy
    try:
        port = 9001
        address = "http://localhost:{0}/".format(port) #address of one of the master nodes
        proxy = xmlrpclib.ServerProxy(address)
        return True
    except Exception as e:
            print("Could not establish RPC tunnel to proxy")
            return False

if __name__=='__main__':
    if(connectMaster()):
        while True:
            proxy.putForRecoverer("","")



