package Race;

import static Race.Client.*;
import static Race.Client.isForLocalTesting;
import static Race.Client.read;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Robot.MDPRobot;
import java.awt.GridLayout;
//import java.io.IOException;


import Map.CoveredMap;
import Map.RealMap;

final public class RaceMain {
    static public RealMap real;
    private static CoveredMap map;
    public static MDPRobot bot;
    private static final GridLayout panelLayout = new GridLayout(25, 1);
    
    final static int SENSORF = 20;
    final static int SENSORL = 20;
    final static int SENSORR = 20;

    public static void main(String... args) throws Exception {
        
        // Set up connection

        System.out.println("setting up connection...");
        boolean connected;

	    do {
            connected = setUp(ip, port);
        } while (!connected);
        System.out.println("connection created!");
        //String[] robotInfo = read().split(",");
        String[] robotInfo = "14,19,4".split(",");

        // Map creation
        RealMap real = new RealMap();
        JFrame f = new JFrame("Group 19 Race");
        f.setSize(new Dimension(578, 790));
        f.setResizable(false);

        if(isForLocalTesting)
            bot =  new MDPRobot(RealMap.robPosX, RealMap.robPosY, RealMap.robDirection,SENSORF, SENSORL, SENSORR);
        else {
            int rx =Integer.parseInt(robotInfo[0]);
            int ry =Integer.parseInt(robotInfo[1]);
            int rd =Integer.parseInt(robotInfo[2]);
            System.out.println("Robot X: " + rx + " Y: " + ry + " D: " + rd);
            bot = new MDPRobot(rx,ry,rd, SENSORF, SENSORL, SENSORR);
        }

        System.out.println(RealMap.robPosX);
        System.out.println(RealMap.robPosY);
        map = new CoveredMap(real, bot);
        System.out.println("Bot created!");
        System.out.println("Map created!");
        
        Container contentPane = f.getContentPane();
        // add Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(panelLayout);
        contentPane.add(buttonPanel, BorderLayout.EAST);
        contentPane.add(map, BorderLayout.CENTER);
                    
        // Display the window.
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // every 4 steps/ corner/ 3 blocks at front
        // left side can scan to 7 - 8 grids
        // cutoff: 24, 95
        //String[] control = {"w2adw1aaaa", "dw2", "w1a"};
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        /*Race race = new Race(bot, map);
        race.mainLoop();*/

        write("e\n");
        Client.read();
        for (int i = 0; i < 1; i++) {
            write("e\n");
            String readData = read();
            System.out.println("Sensor info: " + readData);
            //Client.write(control[0]);
        }
    }
}
