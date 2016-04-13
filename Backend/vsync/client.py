import xmlrpclib
import sys
import json
from flask import Flask,request

#Proxy server used for RPC communication between the flask process and Master server
proxy = 0
flask_port = 0
app = Flask(__name__)

# setup some global variables and stub data
userDBSR = {'testUserSR': 'testPassword'}
userDBSP = {'testUserSP': 'testPassword'}
serviceID = 1;
serviceData = []
serviceData.append({'id':'1', 'name':'Bob the Gardner', 'type':'Gardening', 'location':'test location', 'radius':'25', 'cost':'60', 'description':'My name is Bob', 'rating': "-1", 'availability':'yes'})
serviceData.append({'id':'2', 'name':'Bob the Plumber', 'type':'Plumbing', 'location':'test location', 'radius':'25', 'cost':'65', 'description':'My name is Bob', 'rating': "4", 'availability':'yes'})
serviceID += 1
serviceID += 1

def initializeRPC(id):
    '''
        Initializes the RPC tunnel between flask and the master server by using
        the XML proxy with address "localhost:(8000+id)"
    '''
    global proxy
    try:
        port = 9000 + int(id)
        address = "http://localhost:{0}/".format(port)
        proxy = xmlrpclib.ServerProxy(address)
        return True
    except Exception as e:
        print("Could not establish RPC tunnel to proxy")
        return False

def startFlaskServer(id):
    '''
        Initializes the RPC tunnel at "localhost:(8000+id)" and the flask server
        at "0.0.0.0:(5000+id)"
    '''
    #Connect RPC tunnel to send requests to the correct address and port
    global flask_port
    rpc_flag = initializeRPC(id)

    if rpc_flag:
        #Flask server starts running at '0.0.0.0:(5000+id)'
        flask_host = '0.0.0.0'
        flask_port = 5000 + int(id)
        app.run(host = flask_host, port = flask_port, debug="True")
        print("Flask server starting up on {0} and {1}".format(flask_host, flask_port))
    else:
        print("Did not start flask server")

@app.route("/")
def hello():
    return "Hello! You are currently connected to flask port: {0}".format(str(flask_port))

@app.route("/testput")
def testput():
    proxy.putDHT("Gaurav","Tanvi")
    return "DHTPut called"

@app.route("/testget")
def testget():
    return "Result from DHTGet: {0} on flask port {1}".format(proxy.getDHT("Gaurav"),flask_port)

# authenticate users - stub created by Andy
@app.route("/authenticate",methods=['GET','POST'])
def authUsers():
    if request.method == 'GET':
        username = request.args.get('username')
        password = request.args.get('password')
        userType = request.args.get('userType')
    elif request.method == 'POST':
        username = request.form.get('username')
        password = request.form.get('password')
        userType = request.form.get('userType')

    if userType == 'sr':
        if username not in userDBSR:
            return json.dumps({'status': 1, 'message':'User Not Exist'})
        elif userDBSR[username] == password:
            return json.dumps({'status': 0, 'message':'Login Success'})
        else:
            return json.dumps({'status': 1, 'message':'Login Invalid'})

    if userType == 'sp':
        if username not in userDBSP:
            return json.dumps({'status': 1, 'message':'User Not Exist'})
        elif userDBSP[username] == password:
            return json.dumps({'status': 0, 'message':'Login Success'})
        else:
            return json.dumps({'status': 1, 'message':'Login Invalid'})

# return two dummy services to the application to test connection - stub created by Andy
@app.route("/getService",methods=['GET'])
def getService():
    reply = {}
    reply["status"] = 0
    reply["data"] = serviceData
    return json.dumps(reply)

# return the details of a particular service - stub created by Andy
@app.route("/getServiceDetails",methods=['GET'])
def getServiceDetails():
    id = request.args.get('serviceID')
    # insert logic here to find the service object given ID
    # for now, I will always return the first service as stub
    reply = {}
    reply["status"] = 0
    reply["data"] = serviceData[0]
    return json.dumps(reply)

# accept post service request and stores it in memory - stub created by Andy
@app.route("/postService",methods=['POST'])
def postService():
    name = request.form.get('name')
    type = request.form.get('type')
    location = request.form.get('location')
    radius = request.form.get('radius')
    cost = request.form.get('cost')
    description = request.form.get('description')
    serviceObj = {}
    serviceObj["id"] = serviceID    
    serviceObj["name"] = name
    serviceObj["type"] = type
    serviceObj["location"] = location
    serviceObj["radius"] = radius
    serviceObj["cost"] = cost
    serviceObj["description"] = description
    serviceObj["availability"] = "yes"
    serviceData.append(serviceObj)
    serviceID += 1
    reply = {}
    reply["status"] = 0
    reply["message"] = "success"
    reply["serviceID"] = serviceID
    return json.dumps(reply)

# remove service from availability list when it is declared off on device - stub created by Andy
@app.route("/deleteService",methods=['POST'])
def deleteService():
    id = request.form.get('serviceID')
    # insert logic here to delete the service object
    reply = {}
    reply["status"] = 0
    reply["message"] = "success"
    return json.dumps(reply)

# /users
# GET: return a list of all users
# POST: required parameters include firstname, lastname, and email. Return user_id
@app.route("/users",methods=['GET','POST'])
def users():
    if request.method == 'GET':
        return "Return a list of users"
    elif request.method == 'POST':
        firstname = request.form.get('firstname')
        lastname = request.form.get('lastname')
        email = request.form.get('email')
        return "user_id"
    else:
        return "invalid input"

# /users/<id>
# GET: return information about the user
# POST: update info of the user
@app.route("/users/<uid>",methods=['GET','POST'])
def user(uid):
    if request.method == 'GET':
        return json.dumps({'firstname': 'first',
                'lastname':'last',
                'email':'email'})
    elif request.method == 'POST':
        firstname = request.form.get('firstname')
        lastname = request.form.get('lastname')
        email = request.form.get('email')
        return json.dumps({'status':0,
                'firstname':firstname,
                'lastname':lastname,
                'email':email})
    else:
        return "invalid input"

# /users/<id>/services
# GET: return all services by this user
# POST: required parameters: service_name, create a new service for this user
@app.route("/users/<uid>/services",methods=['GET','POST'])
def services(uid):
    if request.method == 'GET':
        return json.dumps({'service_id':0,
                'service_name':'service',
                'service_status':'status',
                'service_starttime':'starttime',
                'service_endtime':'endtime'})
    elif request.method == 'POST':
        service_name = request.form.get('service_name')
        return json.dumps({'status':0,
                'service_name':service_name})
    else:
        return "invalid input"

# /users/<uid>/services/<sid>
# GET: return this particular service by this user
# POST: required parameters: service_name, edit the service for this user
@app.route("/users/<uid>/services/<sid>",methods=['GET','POST'])
def service(uid,sid):
    if request.method == 'GET':
        return json.dumps({'service_id':0,
                'service_name':'service',
                'service_status':'status',
                'service_starttime':'starttime',
                'service_endtime':'endtime'})
    elif request.method == 'POST':
        return json.dumps({'service_id':0,
                'service_name':'service',
                'service_status':'status',
                'service_starttime':'starttime',
                'service_endtime':'endtime'})
    else:
        return "invalid input"


if __name__ == "__main__":
    startFlaskServer(sys.argv[1])
