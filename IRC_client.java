
import java.util.*;
import java.io.InputStream;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class IRC_client {
    static ArrayList history_array = new ArrayList();
    static BufferedReader br=null;
    static String nick = "";
    public static void deamon(BufferedReader br, String nick){
        while(true){
            String nickname = "";
            String message = "";
            String resp="";
            try{
                  resp = br.readLine();
            }catch(Exception e){
                 System.out.println("Error, broken pipe or something...");
                 System.exit(-1);
            }
            if (resp!=null){
                if (resp!=""){
                    if (resp.contains("PRIVMSG")){
                           nickname = resp.replace("!", ":").split(":")[1];
                           message = resp.split(":")[2];
                           resp = nickname+":"+message;          
                     }
                     history_array.add(resp);
                     for (int i=0; i<history_array.size(); i++){
                         System.out.println(history_array.get(i));
                     }
                     System.out.print(nick+">> ");
                    
                }
            
            }
        }
    }
    public static String raw_input(String text){
        Scanner scanner = new Scanner( System.in );
        System.out.print(text);
        String irc = scanner.nextLine();
        return irc;

    }
    public static void main(String[] args) {
        // Prints "Hello, World" to the terminal window.
        System.out.println("Welcome to Greg's IRC client.");
        String irc = raw_input("Enter IRC server ip and port:");
        System.out.print("Connecting to "+irc+"... ");
        Socket sock2 = null;

        PrintWriter out=null;
        try{
        
             String host = irc.split(":")[0];
             int port = Integer.parseInt(irc.split(":")[1]);
             sock2 = new Socket(host, port);
             br = new BufferedReader(new InputStreamReader(sock2.getInputStream()));
             out = new PrintWriter(sock2.getOutputStream(), true);
        }catch(Exception e){
            System.out.println("FAILED");
            System.out.println("Cause:");
            System.out.println(e);
            System.exit(-1);
        }
        System.out.println("OK");
        nick = raw_input("Enter your nick:");
        String full_name = raw_input("Enter your full name:");
        out.println("NICK "+nick+"\r\n");
        out.println("USER "+nick+" * * :"+full_name+"\r\n");
        String resp="";
        try{
            
            while(true){
                String line = br.readLine();
                if (line!=null){
                    resp = resp+"\n"+line;
                
                    System.out.println(line);
                
                    if(resp.toString().contains("PING :")){
                       out.println("PONG :"+resp.split("PING :")[1]+"\r\n");
                       break;
                    }
                }
            }
        }catch(Exception e){
            System.err.println(e);
            System.exit(-1);
        }
         Thread t1 = new Thread(new Runnable() {
         public void run()
         {
             deamon(br, nick);
         }});
         t1.start();
         String chatroom = "";
         while(true){
               for (int i=0; i<history_array.size(); i++){
                    System.out.println(history_array.get(i));
               }
               String command = raw_input(nick+">> ");
               command = command.replace("\n", "");
               command = command.replace("/join", "/JOIN");
               if (command.contains("/JOIN")){
                   System.out.println("Joining "+command.split("JOIN")[1].replace(" ","")+"...");
                   chatroom=command.split("JOIN")[1].replace(" ","");
                   command=command.replace("/","");
               }else{
                   if (!(command.contains("/"))){
                       command = "PRIVMSG "+chatroom+" :"+command;
                   }else{
                       command = command.replace("/", "");
                   }
               }
               if (command != "PRIVMSG "){
                     out.println(command +"\r\n");
                     if (command.contains("PRIVMSG")){
                         command = nick+":"+command.split(":")[1];
                     }
                     history_array.add(command);
               
               
               
               }
         
         
         
         
         }
        
        

    }

}
