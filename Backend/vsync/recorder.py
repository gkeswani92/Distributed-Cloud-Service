import xmlrpclib
import time
import ast
services = ["Plumbing", "Gardening", "Taxi", "Baby Sitting"]
proxy=0
text

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
            time.sleep(5) #1 minute
            for service in services:
                print "-----------------------------------------------------------------"
                print service
                try:
                    allusers = ast.literal_eval(proxy.getForRecorder(service))
                    print allusers
                    print type(allusers)
                    for user_id in allusers:
                        print user_id
                        print proxy.getForRecorder(user_id)
                    print "-----------------------------------------------------------------"
                except:
                    print "no service: %s in dht right now " % service
                    print "-----------------------------------------------------------------"
                    continue



