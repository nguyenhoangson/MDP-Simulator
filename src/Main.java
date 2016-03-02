/**
 *
 * @author Tan Quang Ngo
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Robot.Explore;
import Robot.MDPRobot;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;
import Map.CoveredMap;
import Map.FileLoader;
import Map.RealMap;
import Other.MyTimerListener;
import Robot.ExploreRace;
import Robot.FastestPath;

final public class Main {

    static public RealMap real;
    private static CoveredMap map;
    public static MDPRobot bot;
    private static GridLayout panelLayout = new GridLayout(25, 1);

    public static void main(String... args) {

        final JTextField stepsPerSec, percentToCover, timeLimit;
        // text field for sensor range
        final JTextField sensorFTxt, sensorLTxt, sensorRTxt;

        real = new RealMap();
        System.out.println("Map created!");

        //set up the background
        JFrame f = new JFrame();
        f.setTitle("Group 19 Simulator");
        f.setSize(new Dimension(700, 800));
        // f.setResizable(false);

        Container contentPane = f.getContentPane();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(panelLayout);
        contentPane.add(buttonPanel, BorderLayout.EAST);
        contentPane.add(real, BorderLayout.CENTER);

        JLabel Label = new JLabel("SensorF");
        Label.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(Label);
        sensorFTxt = new JTextField(2);
        sensorFTxt.setText("5");
        buttonPanel.add(sensorFTxt);

        Label = new JLabel("SensorL");
        Label.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(Label);
        sensorLTxt = new JTextField(2);
        sensorLTxt.setText("5");
        buttonPanel.add(sensorLTxt);

        Label = new JLabel("SensorR");
        Label.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(Label);
        sensorRTxt = new JTextField(2);
        sensorRTxt.setText("5");
        buttonPanel.add(sensorRTxt);

        Label = new JLabel("Steps Per Sec");
        Label.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(Label);
        stepsPerSec = new JTextField(2);
        stepsPerSec.setText("5");
        buttonPanel.add(stepsPerSec);

        Label = new JLabel("Percent To Cover (%)");
        Label.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(Label);
        percentToCover = new JTextField(2);
        percentToCover.setText("100");
        buttonPanel.add(percentToCover);

        Label = new JLabel("Time Limit (min:sec)");
        Label.setFont(new Font("Arial", Font.BOLD, 12));
        buttonPanel.add(Label);
        timeLimit = new JTextField(2);
        timeLimit.setText("1:00");
        buttonPanel.add(timeLimit);

        final JLabel Label2 = new JLabel("Count Down:__");
        buttonPanel.add(Label2);
        //pass the label into the MyListener constructor
        final MyTimerListener listener = new MyTimerListener(Label2);
        //the timer fires every 1000 MS (1 second)
        //when it does, it calls the actionPerformed() method of MyListener
        final Timer timer = new Timer(1000, listener);

        JButton coverButton = new JButton("Exploration");
        coverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    listener.setTimer(timeLimit.getText());
                    timer.start();

                    // save Map
                    FileLoader.saveTextFile("src/1.txt", real.getNewCell());
                    bot = new MDPRobot(RealMap.robPosX, RealMap.robPosY, RealMap.robDirection,
                            Integer.parseInt(sensorFTxt.getText()),
                            Integer.parseInt(sensorLTxt.getText()),
                            Integer.parseInt(sensorRTxt.getText()));

                    System.out.println("bot created!");
                    map = new CoveredMap(real, bot);
                    Explore exp = new Explore(bot, map,
                            Double.parseDouble(stepsPerSec.getText()),
                            Double.parseDouble(percentToCover.getText()),
                            listener);
                    System.out.println("Explorer created!");
                    exp.go();

                } catch (IOException ex) {
                    System.out.println("Save Map data Failed!");
                }

            }

        });
        buttonPanel.add(coverButton);
        
        coverButton = new JButton("Exploration_Race");
        coverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    listener.setTimer(timeLimit.getText());
                    timer.start();

                    // save Map
                    FileLoader.saveTextFile("src/1.txt", real.getNewCell());
                    bot = new MDPRobot(RealMap.robPosX, RealMap.robPosY, RealMap.robDirection,
                            Integer.parseInt(sensorFTxt.getText()),
                            Integer.parseInt(sensorLTxt.getText()),
                            Integer.parseInt(sensorRTxt.getText()));

                    System.out.println("bot created!");
                    map = new CoveredMap(real, bot);
                    ExploreRace exp = new ExploreRace(bot, map,
                            Double.parseDouble(stepsPerSec.getText()),
                            Double.parseDouble(percentToCover.getText()),
                            listener);
                    System.out.println("Explorer created!");
                    exp.go();

                } catch (IOException ex) {
                    System.out.println("Save Map data Failed!");
                }

            }

        });
        
        

        buttonPanel.add(coverButton);
        JButton pathFinderButton = new JButton("Fastest Path");
        pathFinderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    listener.setTimer(timeLimit.getText());
                    timer.start();

                    // save Map
                    FileLoader.saveTextFile("src/1.txt", real.getNewCell());
                    bot = new MDPRobot(RealMap.robPosX, RealMap.robPosY, RealMap.robDirection,
                            Integer.parseInt(sensorFTxt.getText()),
                            Integer.parseInt(sensorLTxt.getText()),
                            Integer.parseInt(sensorRTxt.getText()));

                    System.out.println("bot created!");
                    map = new CoveredMap(real, bot);
                    FastestPath exp = new FastestPath(bot, map,
                            Double.parseDouble(stepsPerSec.getText()),
                            Double.parseDouble(percentToCover.getText()),
                            listener);
                    System.out.println("Explorer created!");
                    exp.go();
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        buttonPanel.add(pathFinderButton);

        // Button to create a new Map
        JButton newMapButton = new JButton("Clear");
        newMapButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                real.removeWalls();
            }
        });
        buttonPanel.add(newMapButton);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    FileLoader.saveTextFile(FileLoader.getFileName(true), real.getNewCell());
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        });
        buttonPanel.add(saveButton);

        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                real.loadData();
            }
        });
        buttonPanel.add(loadButton);

        // Display the window.
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
