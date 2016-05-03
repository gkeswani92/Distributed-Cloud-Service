import clr, sys
from System import Action,Predicate
from System.Collections.Generic import KeyValuePair

#sys.path.append('/System/Library/Frameworks/Python.framework/Versions/2.5/lib/python2.6') #Append Python Library Path
sys.path.append('/usr/lib/python2.6')
import os
from threading import Thread, Lock, Condition
from SimpleXMLRPCServer import SimpleXMLRPCServer,SimpleXMLRPCRequestHandler

#Append the Resources folder that has Vsync.dll
sys.path.append(os.getcwd()+'/../../Resources')
clr.AddReference('Vsync.dll')
import Vsync

class MasterServer(Thread):

    def __init__(self,id):
        Thread.__init__(self)
        self.id = id
        self.name = "master%s"%id
        self.group_name = "group"

        print "Running RPC Server on port %s" % (9000+id)
        self.server = SimpleXMLRPCServer(("localhost", 9000 + id))
        self.server.register_introspection_functions()

        #Registering functions to the RPC tunnel
        self.server.register_function(self.putDHT)
        self.server.register_function(self.checkPassword)
        self.server.register_function(self.getDHT)

        #Initializing/joining a vsync group and its DHT
        self.group = Vsync.Group(self.group_name)
        self.dht = self.group.DHT()

        #First param should be a factor of the tast 2
        #Params are replication factor, expected group size, min group size
        self.group.DHTEnable(2,4,4)

        #Registering functions that can be called on the VSync group
        self.group.RegisterViewHandler(Vsync.ViewHandler(self.report))
        self.group.RegisterHandler(1, Action[str, str](self.authUser))
        self.group.Join()

        #Defining the types of services that are provided
        self.services = ["Plumbing", "Gardening", "Taxi", "Baby Sitting"]

    def checkPassword(self,username,password):
        print "RPC call to check username,password"
        print "username: %s" % username
        print "password: %s" % password
        return self.authUser(username,password)

    def getDHT(self, key):
        return self.group.DHTGet[(str,str)](key)

    def putDHT(self, key, value):
        '''
            Puts the key and value in to the DHT depending on the parameters
        '''
        #If the key is in the list of services, it means this is a call to add
        #a service ID under its service name
        if key in self.services:
            serviceIDs = self.getDHT(key)
            if serviceIDs is None:
                serviceIDs = [value]
                self.group.DHTPut(key, json.dumps(serviceIDs))
            else:
                serviceIDs = json.loads(serviceIDs)
                serviceIDs.append(value)

            #Add the current service id under the list of services for its category
            self.group.DHTPut(key, json.dumps(serviceIDs))

            return "Service ID {0} added to list of {1}".format(value, key)

        #The key-value pair passed in is the service id and the corresponding
        #service object
        else:
            self.group.DHTPut(key, value)
            return "Information about Service ID has been stored in the DHT".format(key, value)

    def authUser(self,username,password):
        #This should involve a call to the VSync DHT
        print "Asking my group."
        return username == "Natch" and password == "Password"

    def report(self,v):
        print('New view: ' + v.ToString())
        print('My rank = ' + v.GetMyRank().ToString())
        for a in v.joiners:
            print('  Joining: ' + a.ToString() + ', isMyAddress='+a.isMyAddress().ToString())
        for a in v.leavers:
            print('  Leaving: ' + a.ToString() + ', isMyAddress='+a.isMyAddress().ToString())
        return

    def run(self):
        print "===================="
        print "master_id: %s" % self.id
        print "group_name: %s" % self.group_name
        print "===================="

if __name__ == '__main__':
    Vsync.VsyncSystem.Start() # start Vsync system
    print 'Starting a master server, id = %s' % sys.argv[1]
    masterServer = MasterServer(int(sys.argv[1]))
    masterServer.start()
    masterServer.server.serve_forever()
    Vsync.VsyncSystem.WaitForever()
