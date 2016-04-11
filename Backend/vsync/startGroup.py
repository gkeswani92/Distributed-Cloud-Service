import sys
# import subprocess
from System.Diagnostics import Process

def startSystem(numGroup,numServPerGroup):
    for i in range(1,numGroup+1):
        p = Process()
        p.StartInfo.UseShellExecute = False
        p.StartInfo.RedirectStandardOutput = True
        dir(p)
        p.StartInfo.FileName = 'ipy'
        p.StartInfo.Arguments = './startGroup.py ' + str(i)
        p.Start()

        #subprocess.Popen(['ipy','startGroup.py',str(i)])

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print "Usage: ipy start.py NUMBER_OF_GROUP NUMBER_OF_SERVER_PER_GROUP"
    else:
        startSystem(int(sys.argv[1]),int(sys.argv[2]))
