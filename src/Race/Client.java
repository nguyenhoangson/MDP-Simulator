package Race;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

    public static Socket socket = null;
    public static boolean isForLocalTesting = false;
    public final static String ip = "192.168.19.19";
    public final static int port = 1919;

    public Client() {
    }

    public static void setUp(String hostName, int portNumber) {
        if(isForLocalTesting) return;
        try {
            socket = new Socket(hostName, portNumber);
        } catch (Exception e) {
            System.out.println("Socket error: " + e.getMessage());
        }
    }

    public static void write(String msg) {
        System.out.println("to be sent:" + msg);
        if(isForLocalTesting) return;
        PrintWriter out = null;
        try {
            out = new PrintWriter(socket.getOutputStream());
           // System.out.println("to be sent:" + msg);
            out.print(msg);
            out.flush();
        }catch (Exception e) {
            System.out.println("Write error: " + e.getMessage());
            System.out.println("re -connecting...");
            Client.setUp(ip, port);
            System.out.println("re-connected!");
            write(msg);
        }
    }

    public static String read() {
        //array for storing distances in order: TL, TM, TR, FL, FR, LR 
        if(isForLocalTesting) return "1000,1000,1000,1000,1000,1000";

        String inStr = null;
        Scanner sc;
        try {
            sc = new Scanner(new InputStreamReader(socket.getInputStream()));
            inStr = sc.nextLine();

            while(inStr != "\n"){
                inStr = sc.nextLine();
                System.out.println(inStr);
            }

            return inStr;

        } catch (Exception e) {
            System.out.println("Read error: " + e.getMessage());
            System.out.println("re-connecting...");
            Client.setUp(ip, port);
            System.out.println("re-connected!");
        }
        return inStr;
    }

    public static void main (String... args) {
        Client testClient = new Client();
        testClient.setUp(ip, port);
        testClient.write("Fie, do you copy?");
        System.out.println("Trying to read from RPi...");
        String readFromRPi = testClient.read();
        System.out.println(readFromRPi);
    }
}
