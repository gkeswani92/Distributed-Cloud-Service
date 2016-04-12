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
        self.server.register_function(self.welcome_page)
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

    def welcome_page(self):
        return "Welcome to Handy"

    def checkPassword(self,username,password):
        print "RPC call to check username,password"
        print "username: %s" % username
        print "password: %s" % password
        return self.authUser(username,password)

    def getDHT(self,key):
        # searchByKey = lambda keyValuePair: keyValuePair.Key == key
        # predicate = Predicate[KeyValuePair[object,object]](searchByKey)
        # return self.dht.Find(predicate).Value
        return self.group.DHTGet[(str,str)](key)

    def putDHT(self,key,value):
        # content = KeyValuePair[object,object](key,value)
        # self.dht.Add(content)
        print "putDHT called key %s value %s" % (key,value)
        self.group.DHTPut(key,value)
        return True

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
