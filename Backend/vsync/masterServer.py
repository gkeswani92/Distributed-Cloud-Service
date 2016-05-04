import clr, sys
from System import Action,Predicate
from System.Collections.Generic import KeyValuePair

#sys.path.append('/System/Library/Frameworks/Python.framework/Versions/2.5/lib/python2.6') #Append Python Library Path
sys.path.append('/usr/lib/python2.6')
import json
import pickle
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
        self.server.register_function(self.putService)
        self.server.register_function(self.checkPassword)
        self.server.register_function(self.getServiceProvider)
        self.server.register_function(self.getProvidersForServiceTypes)

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

    def getProvidersForServiceTypes(self, service_type):
        '''
            Returns the service id of all providers registered under the service type
        '''
        return self.group.DHTGet[(str,str)](service_type)

    def getServiceProvider(self, service_type, location):
        '''
            Returns a provider to a user if available and turns the providers
            available to False
        '''
        reply = None

        try:
            #Get the providers that are registered under the mentioned service type
            providers = self.getProvidersForServiceTypes(service_type)

            if providers is not None:
                log = "Providers found: {0} ".format(providers)
                providers = pickle.loads(providers)
                log += "Successfully coverted providers to list. "

                #Iterate through the providers to find a provider
                for service_id in providers:
                    log += "Searching for service id {0} in the DHT. ".format(service_id)

                    serviceObj = self.group.DHTGet[(str,str)](service_id)

                    #This check should be redundant unless there has been an issue with the DHT
                    if serviceObj is not None:
                        log += "Found object for service id {0} in the DHT. ".format(service_id)
                        serviceObj = pickle.loads(serviceObj)

                        #If the provider is available and is in the requested location
                        #return to the user
                        if serviceObj["available"] == True and serviceObj["location"] == location:
                            log += "Found matching service provider"
                            reply = { "status" : 0,
                                      "data"   : json.dumps(serviceObj) }
                            return json.dumps(reply)

                reply = {"status"    : 1,
                         "providers" : json.dumps(providers),
                         "message"   : "Could not find any {0} provider in {1}".format(service_type, location),
                         "log"       : log}
            else:
                reply = {"status"  : 1,
                         "message" : "Could not find any providers for the requested service type",
                         "log"     : log}

        except Exception as e:
            reply = { "status" : 1,
                      "error"  : str(e),
                      "log"    : log }

        return json.dumps(reply)

    def putService(self, key, value):
        '''
            Puts the key and value in to the DHT depending on the parameters
        '''
        #If the key is in the list of services, it means this is a call to add
        #a service ID under its service name
        if key in self.services:
            serviceIDs = self.getProvidersForServiceTypes(key)

            #If this is the first time a service is being registered under this type
            #create a new list, else append to the list and put in the DHT
            if serviceIDs is None:
                serviceIDs = [value]
            else:
                serviceIDs = pickle.loads(serviceIDs)
                serviceIDs.append(value)

            #Add the current service id under the list of services for its category
            self.group.DHTPut(key, pickle.dumps(serviceIDs))

            return "Service ID {0} added to list of {1}".format(value, key)

        #The key-value pair passed in is the service id and the corresponding
        #service object
        else:
            self.group.DHTPut(key, value)
            return "Information about Service ID {0} has been stored in the DHT {1}".format(key, value)

    def checkPassword(self,username,password):
        print "RPC call to check username,password"
        print "username: %s" % username
        print "password: %s" % password
        return self.authUser(username,password)


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
