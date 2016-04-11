#Starting a flask app
from flask import Flask
app = Flask(__name__)

#The client will serve its request to this proxy
import xmlrpclib
proxy = xmlrpclib.ServerProxy("http://localhost:8000/")

@app.route("/")
def hello():
    return proxy.welcome_page();

if __name__ == "__main__":
    app.run(host = '0.0.0.0', port = 5000, debug="True")