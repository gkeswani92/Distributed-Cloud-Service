#Handy: A P2P Marketplace
##About Handy
Handy will be a trusted community marketplace for people to list, discover, and book unique services around the world - online or from a mobile device. Whether it’s a gardener on a weekend, a plumber for a pipe burst, or a ride to another city, Handy will aim to connect people to unique services at any price point.

For this project, we will build an application that acts as a middleman by connecting people who are looking to provide services such as moving, gardening, plumbing, repairing etc. (service providers) with people who are in need of these services(customers). 
 We plan to use the cloud to store user (both service providers and customer) data and facilitate communication between them.

##Dependencies
###Git
```
sudo apt-get install git
git clone https://github.com/gkeswani92/Peer-To-Peer-Market-Place.git
```

###Flask
```
apt-get install python-pip
pip install flask
```

###Python2.6
We use Python2.6 because its modules are compatible with Iron Python. To install:
```
sudo add-apt-repository ppa:fkrull/deadsnakes
sudo apt-get update
sudo apt-get install python2.6 python2.6-dev
```

###HAProxy
```
sudo apt-get install haproxy
```

###Change HAProxy configuration file to the file under /resources
```
cp /etc/haproxy/haproxy.cfg /etc/haproxy/haproxy.cfg_orig
cat /dev/null > /etc/haproxy/haproxy.cfg
vi /etc/haproxy/haproxy.cfg
```

###Set HAProxy to run by default
```
vi /etc/default/haproxy
Set ENABLED=0 to ENABLED = 1
```

###Mono and Iron Python
```
sudo apt-get install mono-complete
git clone git://github.com/IronLanguages/main.git IronLanguages
cd IronLanguages
xbuild Solutions/IronPython.sln /p:Configuration=Release

Add alias ipy="mono IronLanguages/bin/Release/ipy.exe” to .bashrc
. ~/.bashrc
```

###Run 2 Flask Servers
```
python /home/ubuntu/Peer-To-Peer-Market-Place/Backend/vsync/flaskServer.py 1
python /home/ubuntu/Peer-To-Peer-Market-Place/Backend/vsync/flaskServer.py 2
```

###Run atleast 4 Master Servers
```
mono /home/ubuntu/IronLanguages/Util/IronPython/ipy.exe /home/ubuntu/Peer-To-Peer-Market-Place/Backend/vsync/masterServer.py 1
sleep 20
mono /home/ubuntu/IronLanguages/Util/IronPython/ipy.exe /home/ubuntu/Peer-To-Peer-Market-Place/Backend/vsync/masterServer.py 2
mono /home/ubuntu/IronLanguages/Util/IronPython/ipy.exe /home/ubuntu/Peer-To-Peer-Market-Place/Backend/vsync/masterServer.py 3
mono /home/ubuntu/IronLanguages/Util/IronPython/ipy.exe /home/ubuntu/Peer-To-Peer-Market-Place/Backend/vsync/masterServer.py 4
```

###Ping the server
Ping the EC2 instance/localhost from your browser depending on where the application has been deployed. This will send the request to the load balancer that will route it to the respective flask server and get you the required response.
```
https://localhost:8080
```

###HTTP Load Testing
Install go
```
mkdir $HOME/Go
mkdir -p $HOME/Go/src/github.com/user

export GOPATH=$HOME/Go
export GOROOT=/usr/local/opt/go/libexec
export PATH=$PATH:$GOPATH/bin
export PATH=$PATH:$GOROOT/bin

brew install go

go get golang.org/x/tools/cmd/godoc
go get golang.org/x/tools/cmd/vet
```

Install vegeta 
```
brew update && brew install vegeta
go get -u github.com/tsenart/vegeta
```

Load test:
```
echo "GET http://ec2-54-165-216-78.compute-1.amazonaws.com/testget" | vegeta attack -duration=15s | tee results.bin | vegeta report
cat results.bin | vegeta report -reporter=plot > plot.html
open plot.html
```
