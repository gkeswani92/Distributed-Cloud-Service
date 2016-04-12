import sys
from System.Diagnostics import Process

def startSystem(numGroup,numServPerGroup):
    for i in range(1,numServPerGroup+1):
        p = Process()
        p.StartInfo.UseShellExecute = False
        p.StartInfo.RedirectStandardOutput = True
        dir(p)

        #Starting master server using an iron python process
        p.StartInfo.FileName = 'ipy' #'mono'
        p.StartInfo.Arguments = 'masterServer.py ' + str(i) #'/home/ubuntu/IronLanguages/Util/IronPython/ipy.exe masterServer.py ' + str(i)
        p.Start()

        #Starting the flask server using a python process
    p.StartInfo.FileName = 'python'
    p.StartInfo.Arguments = 'client.py ' + str(1)
    p.Start()

    p.StartInfo.FileName = 'python'
    p.StartInfo.Arguments = 'client.py ' + str(2)
    p.Start()

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print "Usage: ipy start.py NUMBER_OF_GROUP NUMBER_OF_SERVER_PER_GROUP"
    else:
        startSystem(int(sys.argv[1]),int(sys.argv[2]))
