import clr
import sys
from System import Action
#Append Python Library Path 
sys.path.append('/System/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7')
import os
#Append the Resources folder that has Vsync.dll
sys.path.append(os.getcwd()+'/../Resources')
clr.AddReference('Vsync.dll')
import Vsync


Vsync.VsyncSystem.Start() # start Vsync system
g = Vsync.Group("group1") # create a Vsync Group


def myfunc(i):
    print('Hello from myfunc with i=' + i.ToString())
    return
def myRfunc(r):
    print('Hello from myRfunc with r=' + r.ToString())
    group.Reply(-1)
    return
def myViewFunc(v):
    print('New view: ' + v.ToString())
    print ('My rank = ' + v.GetMyRank().ToString())
    for a in v.joiners:
        print('  Joining: ' + a.ToString() + ', isMyAddress='+a.isMyAddress().ToString())
    for a in v.leavers:
        print('  Leaving: ' + a.ToString() + ', isMyAddress='+a.isMyAddress().ToString())
    return
 
g.RegisterHandler(0, Action[int](myfunc))
g.RegisterHandler(1, Action[float](myRfunc))
g.RegisterViewHandler(Vsync.ViewHandler(myViewFunc))
g.Join()
g.Send(0,17)
res = []
nr = g.Query(Vsync.Group.ALL,1,98.8,Vsync.EOLMarker(),res)
print('After Query got ' + nr.ToString() + ' results: ', res)