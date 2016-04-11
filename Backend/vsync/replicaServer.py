import clr, sys
from System import Action
sys.path.append('/System/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7') #Append Python Library Path 
import os
from threading import Thread, Lock, Condition
sys.path.append(os.getcwd()+'/../Resources') #Append the Resources folder that has Vsync.dll
clr.AddReference('Vsync.dll')
import Vsync

class ReplicaServer(Thread):
    def __init__(self,id):
        Thread.__init__(self)
        self.id = id
        self.name = "replica%s"%id
        self.group_name = "group%s"%id
        self.group = Vsync.Group(self.group_name)
        self.group.RegisterViewHandler(Vsync.ViewHandler(self.report))
        self.group.RegisterHandler(1, Action[str, str](self.authUser))
        self.group.Join()

    def report(self,v):
        print('New view: ' + v.ToString())
        print('My rank = ' + v.GetMyRank().ToString())
        for a in v.joiners:
            print('  Joining: ' + a.ToString() + ', isMyAddress='+a.isMyAddress().ToString())
        for a in v.leavers:
            print('  Leaving: ' + a.ToString() + ', isMyAddress='+a.isMyAddress().ToString())
        return

    def authUser(self,username,password):
        print "username: %s" % username
        print "password: %s" % password
        return

    def run(self):
        print "===================="
        print "master_id: %s" % self.id
        print "group_name: %s" % self.group_name
        print "===================="



if __name__ == '__main__':
    Vsync.VsyncSystem.Start() # start Vsync system
    print 'Starting a replica server, id = %s' % sys.argv[1]
    replicaServer = ReplicaServer(int(sys.argv[1]))
    replicaServer.start()
    replicaServer.group.Send(1,"Natch","Password")
    Vsync.VsyncSystem.WaitForever()