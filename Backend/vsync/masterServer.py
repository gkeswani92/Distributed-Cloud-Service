import clr, sys
from System import Action,Predicate
from System.Collections.Generic import KeyValuePair

clr.AddReference('System.Web.Extensions')
from System.Web.Script.Serialization import JavaScriptSerializer

sys.path.append('/System/Library/Frameworks/Python.framework/Versions/2.5/lib/python2.6') #Append Python Library Path
#sys.path.append('/usr/lib/python2.6')
import json
import json
import os
from threading import Thread, Lock, Condition
from SimpleXMLRPCServer import SimpleXMLRPCServer,SimpleXMLRPCRequestHandler

#Append the Resources folder that has Vsync.dll
sys.path.append(os.getcwd()+'/../../Resources')
clr.AddReference('Vsync.dll')
import Vsync
someData = 100
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
        self.server.register_function(self.registerUser)
        self.server.register_function(self.registerUserToken)
        self.server.register_function(self.changeServiceAvailability)
        self.server.register_function(self.updateServiceDetails)
        self.server.register_function(self.deleteService)

        #Initializing/joining a vsync group and its DHT
        self.group = Vsync.Group(self.group_name)
        self.dht = self.group.DHT()

        #First param should be a factor of the tast 2
        #Params are replication factor, expected group size, min group size
        self.group.DHTEnable(2,4,4)

        #Registering functions that can be called on the VSync group
        self.group.RegisterViewHandler(Vsync.ViewHandler(self.report))
        self.group.RegisterHandler(1, Action[str, str, str](self.authUser))


        #Registering snapshot handlers
        #self.group.RegisterMakeChkpt(Vsync.ChkptMaker(self.save))
        #self.group.RegisterLoadChkpt(Action[int](self.load))
        #self.group.Persistent(os.getcwd()+"/Checkpoint/snapshot")
        self.group.Join()

        #Defining the types of services that are provided
        self.services = ["Plumbing", "Gardening", "Taxi", "Baby Sitting"]

    # def save(self,v):
    #     print "save() called"
    #     self.group.SendChkpt(someData)
    #     self.group.EndOfChkpt()
    #
    # def load(self,v):
    #     print "load() got called"
    #     print v

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
        reply = {}
        available_providers = []

        try:
            #Get the providers that are registered under the mentioned service type
            providers = self.getProvidersForServiceTypes(service_type)

            if providers is not None:
                print("Providers found: {0} ".format(providers))
                providers = JavaScriptSerializer().DeserializeObject(providers)

                #Iterate through the providers to find a provider
                for service_id in providers:
                    print("Searching for service id {0} in the DHT. ".format(service_id))

                    serviceObj = self.group.DHTGet[(str,str)](service_id)

                    #This check should be redundant unless there has been an issue with the DHT
                    if serviceObj is not None:
                        print("Found object for service id {0} in the DHT. ".format(service_id))
                        serviceObj = JavaScriptSerializer().DeserializeObject(serviceObj)

                        #If the provider is available and is in the requested location
                        #return to the user
                        if serviceObj["availability"] == 0 and serviceObj["location"] == location:
                            available_providers.append(dict(serviceObj))
                            print("Found matching service provider with name {0}".format(dict(serviceObj).get("name")))
                            print(json.dumps(available_providers))

            #Return dump of providers if they were found, else return with status 0
            if len(available_providers) > 0:
                reply = { "status" : 0,
                          "data"   : json.dumps(available_providers) }
            else:
                print("Could not find any providers for the service type {0} and location {1}".format(service_type, location))
                reply = { "status"  : 1,
                          "message" : "Could not find any providers for the requested service type"}

        except Exception as e:
            reply = { "status" : 1,
                      "error"  : str(e) }

        return json.dumps(reply, indent=4, separators=(',', ': '))

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
                print("First entry for {0} is service id: {1}".format(key, value))
                serviceIDs = [value]
            else:
                serviceIDs = JavaScriptSerializer().DeserializeObject(serviceIDs)
                serviceIDs = list(serviceIDs)
                serviceIDs.append(value)
                print("Appending entry {0} in service type: {1}".format(value, key))

            #Add the current service id under the list of services for its category
            self.group.DHTPut(key, json.dumps(serviceIDs))

            return "Service ID {0} added to list of {1}".format(value, key)

        #The key-value pair passed in is the service id and the corresponding
        #service object
        else:
            self.group.DHTPut(key, value)
            print("Information about Service ID {0} has been stored in the DHT {1}".format(key, value))
            return "Information about Service ID {0} has been stored in the DHT {1}".format(key, value)

    def checkPassword(self, username, password, userType):
        '''
            Receives the RPC call from Flask server to authenticate a user
        '''
        print "RPC call to for authenticating user credentials"
        return self.authUser(username, password, userType)

    def authUser(self, username, password, userType):
        '''
            Receives a call from the VSync method to verify user credentials in
            the DHT
        '''
        print "VSync call to DHT to verify user credentials {0} {1} {2}".format(username, password, userType)

        userObj = self.group.DHTGet[(str,str)](username)
        if userObj is not None:
            userObj = dict(JavaScriptSerializer().DeserializeObject(userObj))
            if userObj.get("password") == password and userObj.get("userType") == userType:
                return json.dumps({'status': 0, 'message':'Login Success'})
            else:
                return json.dumps({'status': 1, 'message':'Login Invalid'})
        else:
            return json.dumps({'status': 1, 'message':'User Not Exist'})

    def registerUser(self, username, password, userType):
        '''
            Registers a new user
        '''
        print "VSync call to DHT to insert user credentials {0} {1} {2}".format(username, password, userType)
        userObj = self.group.DHTGet[(str,str)](username)
        if userObj is None:
            self.group.DHTPut(username, json.dumps({"password" : password, "userType" : userType}))
            return json.dumps({'status': 0, 'message':'Success'})
        else:
            return json.dumps({'status': 1, 'message':'User Already Exists'})

    def registerUserToken(self, username, userType, token):
        '''
            Adds a new or replaces a token if the user exists, else returns a status 1
        '''
        userObj = self.group.DHTGet[(str,str)](username)
        if userObj is not None:
            userObj = dict(JavaScriptSerializer().DeserializeObject(userObj))
            userObj["token"] = token
            self.group.DHTPut(username, json.dumps(userObj))
            return json.dumps({'status': 0, 'message':'Added latest token for user'})
        else:
            return json.dumps({'status': 1, 'message':'Username does not exist'})

    def changeServiceAvailability(self, id):
        '''
            Toggles the availability of the service
        '''
        serviceObj = self.group.DHTGet[(str,str)](service_id)
        if serviceObj is not None:
            print("Found object for service id {0} in the DHT. ".format(service_id))
            serviceObj = dict(JavaScriptSerializer().DeserializeObject(serviceObj))
            availability = serviceObj.get("availability")
            serviceObj["availability"] = 0 if availability == 1 else 1
            print("Toggled the availability")
            self.group.DHTPut(id, json.dumps(serviceObj))
            return json.dumps({'status': 0, 'message':'Availability has been changed'})
        else:
            return json.dumps({'status': 1, 'message':'Service ID does not exist'})

    def updateServiceDetails(self, key, value):
        '''
            Updates the details of the service in the DHT
        '''
        try:
            self.group.DHTPut(key, value)
            return json.dumps({'status': 0, 'message':'Updated details'})
        except Exception as e:
            return json.dumps({'status':1, 'message':str(e)})

    def deleteService(self, serviceID):
        '''
            Deletes the service details of the given service id
        '''
        try:
            serviceObj = self.group.DHTGet[(str,str)](serviceID)

            #If there is a corresponding service object for this id
            if serviceObj is not None:
                serviceObj = dict(JavaScriptSerializer().DeserializeObject(serviceObj))
                serviceType = serviceObj.get("type")
                print("Service Type {0}".format(serviceType))

                #If there are any services registered for this type
                services = self.group.DHTGet[(str,str)](serviceType)
                if services is not None:
                    services = list(JavaScriptSerializer().DeserializeObject(services))
                    print("List of services under {0} : {1}".format(serviceType, json.dumps(services)))

                    #If the service id is registered under the type of service
                    if serviceID in services:
                        services.remove(serviceID)
                        self.group.DHTPut(serviceType, json.dumps(services))
                        return json.dumps({'status': 0, 'message':'Deleted details'})
                    else:
                        return json.dumps({'status': 1, 'message':'Service ID not in service list'})
                else:
                    return json.dumps({'status': 1, 'message':'No services found for this service type'})
            else:
                return json.dumps({'status': 1, 'message':'No service object for given service id'})

        except Exception as e:
            return json.dumps({'status':1, 'message':str(e)})

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
