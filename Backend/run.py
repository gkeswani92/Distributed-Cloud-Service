from flask import Flask,request

import json

app = Flask(__name__)

@app.route("/")
def hello():
    return "Hello World!"


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
    app.run(host = '0.0.0.0', port = 5000, debug="True")
