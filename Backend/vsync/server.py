#Need to do this since SimpleXMLRPCServer works with Python 2.6 when compiled with Iron Python
import sys
sys.path.append('/System/Library/Frameworks/Python.framework/Versions/2.5/lib/python2.6')
from SimpleXMLRPCServer import SimpleXMLRPCServer

def welcome_page():
    print("Server: The welcome page has been requested for by the client")
    return "Welcome to Handy!"

#A simple server which listens on port 8000
server = SimpleXMLRPCServer(("localhost", 8000))
print("Listening on port 8000")
server.register_function(welcome_page, 'welcome_page')
server.serve_forever()
