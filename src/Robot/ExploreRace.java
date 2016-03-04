package Robot;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import Map.CoveredMap;
import static Map.FileLoader.saveExploreTextFile;
import Map.MapData;
import Other.MyTimerListener;

public class ExploreRace implements RobotData, MapData {

    private MDPRobot robot = null;
    private CoveredMap map = null;
    private JFrame frame;
    private boolean isExporationEnd = false;
    private boolean isFastPath = false;
    private boolean isPreparingFastPath = false;
    private boolean isBashing = true;
    private boolean isEnding= false;

    //private boolean started = false;
    private final double stepsPerSec, percentToCover;
    private final MyTimerListener timeLimit;

    protected ArrayList<Integer> actionList = new ArrayList<>();

    public ExploreRace(MDPRobot bot, CoveredMap coveredMap, double stepsPerSec, double percentToCover, MyTimerListener listener) {
        this.robot = bot;
        this.map = coveredMap;

        this.stepsPerSec = stepsPerSec;
        this.percentToCover = percentToCover;
        this.timeLimit = listener;

    }

    //start  to display exploration
    public void go() {
        Thread thread;
        thread = new Thread(new Runnable() {
            public void run() {
                frame = new JFrame("Explore");
                // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.setResizable(false);
                frame.setSize(578, 790);
                frame.setLocationByPlatform(true);

                frame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        isExporationEnd = true;

                    }
                });
                Container contentPane = frame.getContentPane();
                contentPane.add(BorderLayout.CENTER, map);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                try {
                    startExploration();
                } catch (Exception ex) {
                    Logger.getLogger(Explore.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();

    }

    private boolean isNeededToGetBack() {
        //percentToCover = 50
        return percentToCover <= robot.getPercentCellCovered() || timeLimit.getTimer() <= 30;
    }

    //start exploration
    private void startExploration() throws Exception {
        this.robot.senseAll(this.map);
        this.map.repaint();

        do {
            try {
                Thread.sleep((long) (1000 / stepsPerSec));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (isBashing) {
                actionList.add(robot.getMovement(map));
                robot.setPreviousAction(TURNRIGHT);
                if (actionList.get(0) == TURNLEFT) {
                    isBashing = false;
                    actionList.remove(0);
                }
            } else if (isPreparingFastPath && actionList.size() == 0) {
                int calib = robot.checkCalibration(map);
                if (calib == MOVEFORWARD) {
                    actionList.add(ALIGHMENT);
                } else if (calib == TURNLEFT) {
                    actionList.add(TURNLEFT);
                    actionList.add(ALIGHMENT);
                    actionList.add(TURNRIGHT);
                } else if (calib == TURNRIGHT) {
                    actionList.add(TURNRIGHT);
                    actionList.add(ALIGHMENT);
                    actionList.add(TURNLEFT);
                } else {
                    if(robot.getX() == 14 && robot.getY() == 2) break;
                    isPreparingFastPath = false;
                    
                    try {
                        Thread.sleep((long) 10000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } 
            }

            if (actionList.size() == 0) {
                int calib = robot.checkCalibration(map);
                if (calib == MOVEFORWARD) {
                    actionList.add(ALIGHMENT);
                } else if (calib == TURNLEFT) {
                    actionList.add(TURNLEFT);
                    actionList.add(ALIGHMENT);
                    actionList.add(TURNRIGHT);
                } else if (calib == TURNRIGHT) {
                    actionList.add(TURNRIGHT);
                    actionList.add(ALIGHMENT);
                    actionList.add(TURNLEFT);
                } else {
                    actionList.add(robot.getMovement(map));
                }
            }
            switch (actionList.get(0)) {
                case MOVEFORWARD:
                    robot.moveForward();
                    break;
                case TURNLEFT:
                    robot.turnLeft();
                    break;
                case TURNRIGHT:
                    robot.turnRight();
                    break;
                case ALIGHMENT:
                    System.out.println("ALIGHMENT TYPE: " + robot.getTypeAlignment(map));
                    break;
                default:
                    System.out.println("unknown action in actionList");
                    break;
            }
            actionList.remove(0);
            this.robot.senseAll(this.map);
            this.map.repaint();

            if (!isEnding && (isFastPath && robot.getX() == 14 && robot.getY() == 2) || (!isFastPath && isNeededToGetBack() && robot.getX() == 2 && robot.getY() == 19)) {
                isEnding = true;
                if (!isFastPath) {
                    saveExploreTextFile("src/explored.txt", robot.getMyExposedMap(), robot.getMyAStarMap());
                    isFastPath = true;
                    isEnding = false;
                }

                isPreparingFastPath = true;
                robot.enableCalibration();
            }

        } while (!isExporationEnd);
    }

}
