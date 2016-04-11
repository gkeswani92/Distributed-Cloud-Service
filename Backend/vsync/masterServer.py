import clr, sys
from System import Action

sys.path.append('/System/Library/Frameworks/Python.framework/Versions/2.5/lib/python2.6') #Append Python Library Path

import os
from threading import Thread, Lock, Condition
from SimpleXMLRPCServer import SimpleXMLRPCServer,SimpleXMLRPCRequestHandler

sys.path.append(os.getcwd()+'/../Resources') #Append the Resources folder that has Vsync.dll
clr.AddReference('Vsync.dll')
import Vsync

class MasterServer(Thread):
    def __init__(self,id):
        Thread.__init__(self)
        self.id = id
        self.name = "master%s"%id
        self.group_name = "group"

        self.server = SimpleXMLRPCServer(("localhost", 8000 + int(id)))
        self.server.register_introspection_functions()
        self.server.register_function(self.checkPassword, 'checkPassword')
        self.server.register_function(self.welcome_page, 'welcome_page')

        self.group = Vsync.Group(self.group_name)
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
