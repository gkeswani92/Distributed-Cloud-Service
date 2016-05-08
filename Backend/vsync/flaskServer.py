import xmlrpclib
import sys
import json
import uuid

from flask import Flask,request
from werkzeug.contrib.cache import SimpleCache
from gcm import GCM

#Proxy server used for RPC communication between the flask process and Master server
proxy = 0
flask_port = 0

#Initializing the flask application and its local cache
app = Flask(__name__)
cache = SimpleCache()

#Time a key value pair will be stored in the local cache
timeout = 2 * 60

#Google cloud messaging tokens for push notifications
gcm = GCM("AIzaSyAY0H3hUW5jHoUZQgqyNrQvScRqrNOmqhk")
counter = 0 # this counter is only used to generate different messages everytime
device_token = {}

# setup some global variables and stub data
userDBSR = {'testUserSR': 'testPassword'}
userDBSP = {'testUserSP': 'testPassword'}


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
def index():
    return "Currently connected to flask port: {0}".format(str(flask_port))

@app.route("/postService", methods=['POST'])
def postService():
    '''
        Accept post service request and stores it in cache and in the DHT
    '''
    #Retrieving the details of the service posted
    name = request.form.get('name')
    service_type = request.form.get('type')
    location = request.form.get('location')
    cost = request.form.get('cost')
    description = request.form.get('description')
    username = request.form.get('username')
    availability = request.form.get('availability')

    #Every posted service will have a unique UUID as its service id
    serviceID = str(uuid.uuid1())

    if name and service_type and location and cost and description and username and availability:
        serviceObj = {  "id"            : serviceID,
                        "name"          : name,
                        "username"      : username,
                        "type"          : service_type,
                        "location"      : location,
                        "cost"          : cost,
                        "description"   : description,
                        "availability"  : int(availability) }

        try:
            #Storing the service id under its service_type for first lookup
            message1 = proxy.putService(service_type, serviceID)

            #Storing the complete service details keyed by the service id
            message2 = proxy.putService(serviceID, json.dumps(serviceObj))

            reply = { "status"    : 0,
                      "message"   : "Success. {0} . {1}".format(message1, message2),
                      "serviceID" : serviceID }

        except Exception as e:
            reply = { "status"    : 1,
                      "message"   : "Failure",
                      "error"     : str(e) }

    else:
        reply = { "status"    : 1,
                  "message"   : "Did not receive all parameters" }

    return json.dumps(reply, indent=4, separators=(',', ': '))

@app.route("/getService",methods=['GET'])
def getServiceProvider():
    '''
        Gets the service, if any, of the requested type and at the request location
    '''
    #Retrieving the details of the service posted
    location = request.args.get('location')
    service_type = request.args.get('type')

    #Getting the service providers for the requested service type
    provider = proxy.getServiceProvider(service_type, location)

    if provider is not None:
        provider = json.loads(provider)
        if provider.get("status") == 0:
            reply = { "status" : 0,
                      "data"   : provider,
                      "error"  : ""}
        else:
            reply = { "status" : 1,
                      "data"   : provider,
                      "error"  : "No service provider found" }

    else:
        reply = { "status" : 1,
                "error"  : "No data retrieved from VSync" }

    return json.dumps(reply, indent=4, separators=(',', ': '))

@app.route("/createUser",methods=['POST'])
def createUser():
    username = request.form.get("username")
    password = request.form.get("password")
    userType = request.form.get("userType")

    try:
        if username and password and userType:
            return proxy.registerUser(username, password, userType)
        else:
            return json.dumps({'status':1, 'message':'Did not receive all params'})
    except Exception as e:
        return json.dumps({'status':1, 'message':str(e)})

@app.route("/authenticate",methods=['GET','POST'])
def authUsers():
    username = request.args.get('username') or request.form.get('username')
    password = request.args.get('password') or request.form.get('password')
    userType = request.args.get('userType') or request.form.get('userType')

    try:
        if username and password and userType:
            return proxy.checkPassword(username, password, userType)
        else:
            return json.dumps({'status':1, 'message':'Did not receive all params'})
    except Exception as e:
        return json.dumps({'status':1, 'message':str(e)})

@app.route("/registerAndroidDeviceForGCMPush",methods=['POST'])
def registerAndroidDeviceForGCMPush():
    global device_token
    username = request.form.get("username")
    userType = request.form.get("userType")
    token = request.form.get("new_push_device_token")

    try:
        if username and userType and token:
            return proxy.registerUserToken(username, userType, token)
        else:
            return json.dumps({'status':1, 'message':'Did not receive all params'})

    except Exception as e:
        return json.dumps({'status':1, 'message':str(e)})

@app.route("/changeServiceAvailability",methods=['POST'])
def changeServiceAvailability():
    serviceID = request.form.get('serviceID')
    try:
        if serviceID is not None:
            return proxy.changeServiceAvailability(serviceID)
        else:
            return json.dumps({"status":1, "message":"All params were not passed"})
    except Exception as e:
        return json.dumps({'status':1, 'message':str(e)})

@app.route("/updateService",methods=['POST'])
def updateService():
    serviceID = request.form.get('serviceID')
    name = request.form.get('name')
    serviceType = request.form.get('type')
    location = request.form.get('location')
    cost = request.form.get('cost')
    description = request.form.get('description')

    try:
        if serviceID and name and serviceType and location and cost and description:
            serviceObj = {  "id"            : serviceID,
                            "name"          : name,
                            "type"          : serviceType,
                            "location"      : location,
                            "cost"          : cost,
                            "description"   : description,
                            "availability"  : 0 }
            return proxy.updateServiceDetails(serviceID, json.dumps(serviceObj))
        else:
            return json.dumps({"status":1, "message":"All params were not passed"})
    except Exception as e:
        return json.dumps({'status':1, 'message':str(e)})

@app.route("/deleteService",methods=['POST'])
def deleteService():
    serviceID = request.form.get('serviceID')
    try:
        if serviceID is not None:
            return proxy.deleteService(serviceID)
        else:
            return json.dumps({"status":1, "message":"All params were not passed"})
    except Exception as e:
        return json.dumps({'status':1, 'message':str(e)})

@app.route("/requestService",methods=['GET','POST'])
def requestService():
    serviceID = request.form.get("serviceID")
    address = request.form.get("address")
    requestorUsername = request.form.get("username")

    #Using the service ID to figure out the token of the service provider and sending
    #a push notification
    serviceProviderDetails = proxy.getServiceProviderTokenFromServiceID(serviceID)
    if getServiceProviderTokenFromServiceID["status"] == 0:
        push_data = { 'messageTitle'      : 'Service Requested',
                      'address'           : address,
                      'requestorUsername' : requestorUsername,
                      'data'              : 'Someone has requested for your service! Open the app to see more details!'}

        tokens = {'temp':serviceProviderDetails.get("token")}
        sendTestPush(tokens, push_data)
        return json.dumps({"status":0})
    else:
        return json.dumps({"status":1})

@app.route("/replyRequest",methods=['GET','POST'])
def replyRequest():
    decision = request.form.get("decision")
    requestorUsername = request.form.get("requestorUsername")

    #Deciding the contents of the push message
    messageTitle = 'Service Request Accepted!' if decision == 'yes' else 'Service Request Declined!'
    data = 'Your service request has been accepted! The ETA is ' + request.form.get("eta") + 'mins' if decision == 'yes' else 'Your service request has been declined! We hope you can find another similar service on Handy'

    push_data = { 'messageTitle' : messageTitle,
                  'data'         : data }

    #Finding the users token to send the push message
    tokens = proxy.getUserToken(requestorUsername)
    if tokens is not None and tokens["status"] == 0:
        sendTestPush(tokens, push_data)
        return json.dumps({"status":0})
    else:
        return json.dumps({"status":1})

def sendTestPush(tokens, data):
    response = gcm.json_request(registration_ids=tokens.values(), data=data)
    return 0

############################ TESTING METHODS####################################

@app.route("/balancing_get",methods=['GET'])
def balancing_get():
    location = request.args.get('location')
    return "Currently connected to flask port: {0} Location: {1}".format(str(flask_port), location)

@app.route("/balancing_post",methods=['POST'])
def balancing_post():
    location = request.form.get('location')
    return "Currently connected to flask port: {0} Location: {1}".format(str(flask_port), location)

@app.route("/testcache",methods=['POST'])
def testCache():
    '''
        Receives a key value pair via a POST request and stores the pair into the
        DHT and the local cache
    '''
    global timeout
    if request.method == 'POST':
        key = request.args.get('key')
        value = request.args.get('value')
        cache.set(key, value, timeout=timeout)
        cached_entry = cache.get(key)
        return "Cached entry: {0}".format(cached_entry)

@app.route("/testput",methods=['POST'])
def testput():
    '''
        Receives a key value pair via a POST request and stores the pair into the
        DHT and the local cache
    '''
    global timeout
    if request.method == 'POST':
        key = request.args.get('key')
        value = request.args.get('value')

        if key is not None and value is not None:
            #Storing the key value pair in the DHT
            proxy.putDHT(key,value)

            #Storing the key value pair in the cache for 5 mins
            cache.set(key, value, timeout=timeout)

            return json.dumps({'status':0,'message':'Flask port %s: Key %s and Value %s have been stored in the DHT and cache' %(flask_port,key,value)})
        else:
            return "Key: {0} Value: {1} Invalid paramaters".format(key, value)


@app.route("/testget",methods=['GET'])
def testget():
    '''
        Reads the key from the call and first checks it the corresponding value is in
        the cache. If found, it returns the value. If not, it uses RPC to call its
        master server with a DHT_GET call
    '''
    display_message = None

    #IF a key was passed in through the URL, we try to look it up in the cache or DHT
    if request.method == 'GET':
        key = request.args.get('key')
        if key is not None:
            cached_entry = cache.get(key)

            #If the key value pair was in the cache, return it
            #Else, look in the DHT
            if cached_entry is not None:
                display_message = "Result from Cache: {0} on flask port {1}".format(cached_entry, flask_port)
            else:
                dht_entry = proxy.getDHT(key)
                if dht_entry is not None:
                    display_message = "Result from DHTGet: {0} on flask port {1}".format(dht_entry, flask_port)
                else:
                    display_message = "Result could not be found in the cache or the DHT on flask port {0}".format(flask_port)
        else:
            display_message = "No key was passed to the testGet method"

    return display_message

if __name__ == "__main__":
    startFlaskServer(sys.argv[1])
