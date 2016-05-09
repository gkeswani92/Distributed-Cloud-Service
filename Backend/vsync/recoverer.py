import xmlrpclib
import time
import ast
import json
import pickle
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
'''
["09bb6987-15e2-11e6-96ce-a45e60b9c6b3", "f4868897-15e5-11e6-aeea-a45e60b9c6b3"]
{"username": "natch", "cost": "1000", "name": "guarav", "availability": 0, "description": "ihopeitworks", "type": "Gardening", "id": "09bb6987-15e2-11e6-96ce-a45e60b9c6b3", "location": "Ithaca"}
'''

obj = '{"username": "natch", "cost": "1000", "name": "guarav", "availability": 0, "description": "ihopeitworks", "type": "Gardening", "id": "09bb6987-15e2-11e6-96ce-a45e60b9c6b3", "location": "Ithaca"}'
obj = json.loads(obj)
obj = json.dumps(obj)


obj2 = '{"username": "natch", "cost": "1000", "name": "tanvi", "availability": 0, "description": "ihopeitworks", "type": "Gardening", "id": "f4868897-15e5-11e6-aeea-a45e60b9c6b3", "location": "Ithaca"}'
obj2 = json.loads(obj2)
obj2 = json.dumps(obj2)
if __name__=='__main__':
    with open('snapshot.txt','r') as file:
        content = file.read()
        kvPairList = pickle.loads(content)
        file.close()
    if(connectMaster()):
        for kvPair in kvPairList:
            if kvPair['key'] is not None and kvPair['value'] is not None:
                proxy.putForRecoverer(kvPair['key'],kvPair['value'])




    # if(connectMaster()):
    #     proxy.putForRecoverer("Gardening",'["09bb6987-15e2-11e6-96ce-a45e60b9c6b3", "f4868897-15e5-11e6-aeea-a45e60b9c6b3"]')
    #     proxy.putForRecoverer('09bb6987-15e2-11e6-96ce-a45e60b9c6b3',obj)
    #     proxy.putForRecoverer("f4868897-15e5-11e6-aeea-a45e60b9c6b3",obj2)



