import requests
import csv
output = list()
numPost = 0
while(numPost < 10): 
    r = requests.post('http://localhost:5001/postService',data={
        "name":"name%s"%numPost,
        "location":"location%s"%numPost,
        "type":"Plumbing",
        "cost":"1000",
        "description":"description%s"%numPost,
        "username":"%s"%numPost,
        "availability":"0"
    })
    numPost = numPost+1
    output.append([r.elapsed.microseconds])

with open('testPost.csv','w') as fp:
    a = csv.writer(fp,delimiter=',')
    a.writerows(output)
    fp.close()





