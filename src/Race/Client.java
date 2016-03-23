package Race;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.InterruptedException;
import Race.Race;

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
        } catch (Exception e) {
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
        if (Race.noBT) return;
        String toWrite = "N:" + msg + "\n";
        write(toWrite);
    }

    public static boolean hasNext() {
        try {
            Scanner sc = new Scanner(new InputStreamReader(socket.getInputStream()));
            return sc.hasNext();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static String read() {
        //array for storing distances in order: FL, FM, FR, LS, RS, RL
        if(isForLocalTesting) return "1000,1000,1000,1000,1000,1000";
        System.out.println("Trying to read: ");
        String inStr = null;
        Scanner sc;
        try {
            sc = new Scanner(new InputStreamReader(socket.getInputStream()));
            inStr = sc.nextLine();
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

        boolean connection = setUp(ip,port);

        while(!connection){
            connection = setUp(ip, port);
        }

        try {
            Thread.sleep(1000);

            writeToArduino("W6#D#W4#A#W11#D#W9#");

        } catch (Exception e){
            Thread.currentThread().interrupt();
        }

    }
}
