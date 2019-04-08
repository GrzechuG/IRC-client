import socket
import sys
import threading
history_array = []
for i in range(0, 42):
    history_array.append("")
def deamon(sock2, nick):
    global history_array
    while True:
        resp = sock2.recv(2048)
        if not resp=="":
             if "PRIVMSG" in resp:
                 nickname = resp.replace("!", ":").split(":")[1]
                 message = resp.split(":")[2]
                 resp = nickname+":"+message
             history_array.append(resp)
             for line in history_array:
                print line
             sys.stdout.write(nick+">> ")
             sys.stdout.flush()
print "Welcome to Greg's IRC client."
irc = str(raw_input( "Enter IRC server ip and port:"))
sys.stdout.write("Connecting to "+irc+"... ")
sys.stdout.flush()


try:
    host = irc.split(":")[0]
    port = irc.split(":")[1]
    sock2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                
# Connect the socket to the port where the server is listening
    server_address2 = (host.replace("\n","").replace("\n", ""), int(port))
    sock2.connect(server_address2)

except Exception as e:
    print "FAILED"
    print str(e)
    quit()
print "OK"
#print 'connecting to %s port %s' % server_address2
#print str(server_address2)

nick = str(raw_input("Enter your nick:"))
full_name = str(raw_input("Enter your full name:"))
sock2.sendall("NICK "+nick+"\r\n")
sock2.sendall("USER "+nick+" * * :"+full_name+"\r\n")
resp = sock2.recv(2048)
print resp
chatroom = ""
pingPong = sock2.recv(512)
print "recv 2", pingPong
pingPong = pingPong[5:]
sock2.sendall('PONG %s\r\n' % pingPong)

t1 = threading.Thread(target=deamon, args=[sock2, nick])
t1.start()

while True:
    for line in history_array:
                print line
    command = str(raw_input(nick+">> "))
    command = command.replace("\n", "")
    command = command.replace("/join", "/JOIN")
    if "/JOIN" in command:
        print "Joining "+command.split("JOIN")[1].replace(" ","")+"..."
        chatroom=command.split("JOIN")[1].replace(" ","")
        command=command.replace("/","")
    else:
        print "chatroom:"+chatroom
        if not "/" in command:
            command = "PRIVMSG "+chatroom+" :"+command
            print "Command:"+command
            #history_array.append(command)
        else:
            command = command.replace("/", "")
    if not "PRIVMSG " == command:
         
        sock2.sendall(command +"\r\n")
        if "PRIVMSG" in command:
            command = nick+":"+command.split(":")[1]
        history_array.append(command)
    #resp = sock2.recv(2048)
    #print resp

