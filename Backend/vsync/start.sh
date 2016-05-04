#!/bin/bash
# Usage: bash start.sh arg1 arg2 
# arg1 = number of Flask servers
# arg2 = number of Master servers

#clean up everything before launching the system
killall python
killall mono
killall ipy

echo Running start.sh script
echo Process ID: $$

#Start Flask Servers
echo Starting Flask servers
for (( i=1; i<$(($1+1)); i++ ))
do
	python flaskServer.py $i &
done

#Start VSync Servers
echo Starting VSync servers
ipy masterServer.py 1 &
sleep 30 

for (( i=2; i<$(($2+1)); i++ ))
do
	ipy masterServer.py $i &
done
echo yay

