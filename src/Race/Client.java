package Race;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.InterruptedException;

public class Client {

    public static Socket socket = null;
    public static boolean isForLocalTesting = false;
    public final static String ip = "192.168.19.19";
    public final static int port = 1919;

    public Client() {
    }

    public static boolean setUp(String hostName, int portNumber) {

        boolean isConnected = false;
//        if(isForLocalTesting) return;
        try {
            socket = new Socket(hostName, portNumber);
            isConnected = true;
            return isConnected;
        } catch (Exception e) {
            System.out.println("Socket error: " + e.getMessage());
        }
        return isConnected;
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

    public static void writeToArduino(String msg) {
        String toWrite = "R:" + msg + "\n";
        write(toWrite);
    }

    public static void writeToAndroid(String msg) {
        String toWrite = "N:" + msg + "\n";
        write(toWrite);
    }

    public static String read() {
        //array for storing distances in order: FL, FM, FR, LS, RU, RL
        if(isForLocalTesting) return "1000,1000,1000,1000,1000,1000";

        String inStr = null;
        Scanner sc;
        try {
            sc = new Scanner(new InputStreamReader(socket.getInputStream()));
            inStr = sc.nextLine();
            System.out.println(inStr);
            return inStr;
        } catch (Exception e) {
            System.out.println("Read error: " + e.getMessage());
            System.out.println("re-connecting...");
            Client.setUp(ip, port);
            System.out.println("re-connected!");
            return read();
        }
    }

    public static void main (String... args) throws InterruptedException {
        Client testClient = new Client();

        boolean connection = testClient.setUp(ip,port);

        while(connection == false){
            connection = testClient.setUp(ip, port);
        }

        try{
            Thread.sleep(4000);

            // Only send the message when connection is ready
            for(int i = 0; i < 10; i++){
                testClient.write("WUTTT\n");
            }

            System.out.println("Trying to read from RPi...");
            String readFromRPi = testClient.read();
            System.out.println(readFromRPi);

        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
