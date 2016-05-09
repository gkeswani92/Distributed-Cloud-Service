import xmlrpclib
import time
import ast
import pickle
services = ["Plumbing", "Gardening", "Taxi", "Baby Sitting"]
storeList = list()
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
            time.sleep(5) #in seconds
            for service in services:
                print "-----------------------------------------------------------------"
                print service
                try:
                    print proxy.getForRecorder(service)
                    print type(proxy.getForRecorder(service))

                    storeList.append({
                        "key": service,
                        "value": proxy.getForRecorder(service)
                    })

                    allusers = ast.literal_eval(proxy.getForRecorder(service))
                    # print allusers
                    # print type(allusers)

                    for user_id in allusers:
                        print user_id
                        print proxy.getForRecorder(user_id)

                        storeList.append({
                                "key": user_id,
                                "value": proxy.getForRecorder(user_id)
                            })
                    print "-----------------------------------------------------------------"
                except:
                    print "no service: %s in dht right now " % service
                    print "-----------------------------------------------------------------"
                    continue
            print storeList
            a = pickle.dumps(storeList)
            print type(a)
            with open('snapshot.txt','w') as file:
                file.write(a)
                file.close()
            break


