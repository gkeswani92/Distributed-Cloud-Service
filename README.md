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

###Change HAProxy configuration file 
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

