import clr
import sys
#Append Python Library Path 
sys.path.append('/System/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7')
import os
#Append the Resources folder that has Vsync.dll
sys.path.append(os.getcwd()+'/../Resources')
clr.AddReference('Vsync.dll')
import Vsync

#create a Vsync Group
group = Vsync.Group("group1")