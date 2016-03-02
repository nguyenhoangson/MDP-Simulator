package Race;

import static Race.Client.isForLocalTesting;
import static Race.Client.read;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Robot.MDPRobot;
import java.awt.GridLayout;
import java.io.IOException;


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

    public static void main(String... args) throws IOException, Exception {
        
        // SET UP SOCKET CONNECTION "WifiP@55"
        System.out.println("setting up connection...");
	    Client.setUp(Client.ip, Client.port);
        System.out.println("connection created!");
        String[] robotInfo = read().split(",");
       
        
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
        System.out.println("bot created!");
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
        
        Race race = new Race(bot, map);
        race.mainLoop();
    }
}
