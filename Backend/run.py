#!flask/bin/python
from services import app
app.run(host='localhost',port=5000,debug=True)
