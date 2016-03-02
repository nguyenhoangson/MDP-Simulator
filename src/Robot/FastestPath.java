/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author cailianjiang
 */
package Robot;

import AStar.Algorithm;
import AStar.Algorithm3B3;
import AStar.PathFinder;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ArrayList;

import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import Map.CoveredMap;
import Map.MapData;
import Other.MyTimerListener;

public class FastestPath implements RobotData, MapData {
    
    private MDPRobot robot = null;
    private CoveredMap map = null;
    private JFrame frame;
    private boolean isExporationEnd = false;

    private final double stepsPerSec, percentToCover;
    private final MyTimerListener timeLimit;
    
    public FastestPath(MDPRobot bot, CoveredMap coveredMap, double stepsPerSec, double percentToCover, MyTimerListener listener) {
        this.robot = bot;
        this.map = coveredMap;
        
        this.stepsPerSec = stepsPerSec;
        this.percentToCover = percentToCover;
        this.timeLimit = listener;
        
        for (int i = 0; i < ROWS - 2; i++) {
            for (int j = 0; j < COLS - 2; j++) {
                (robot.getMyAStarMap())[i][j] = map.getNewCell()[i + 1][j + 1].getColor() != WALL;
                map.getNewCell()[i + 1][j + 1].setExploredACell(true);
            }
        }
        robot.getPresetWayPt().clear();
        robot.getWaypoint().clear();
        robot.getPresetWayPt().add(1 * (COLS - 2) + 13); // goal
        
    }

    //start  to display exploration

    public void go() {
        Thread thread;
        thread = new Thread(new Runnable() {
            public void run() {
                frame = new JFrame("Explore");

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
        return percentToCover <= robot.getPercentCellCovered() || timeLimit.getTimer() <= 0;
    }

    //start exploration

    private void startExploration() throws Exception {
        this.robot.senseAll(this.map);
        this.map.repaint();
        
      
        List<PathFinder.Node> path = getAStarPath(robot.getPresetWayPt().get(0));
        ArrayList<Integer> arrL = getMoveMent(path, robot.getDirection());
        System.out.println("arrL size: " + arrL.size());
        do {
            try {
                Thread.sleep((long) (1000 / stepsPerSec));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (arrL.get(0) == MOVEFORWARD) {
                this.robot.moveForward();
                this.robot.senseAll(this.map);
                this.map.repaint();
                this.map.setColor(robot.getX(), robot.getY(), ROBOTB);
                //arrL.remove(0);

                if (isNeededToGetBack()) {
                    robot.getPresetWayPt().clear();
                    robot.getWaypoint().clear();                    
                }
                path = usePresetWp();
                if (path == null) {
                    path = useUnexploredWp();
                    // back to start
                    if (path == null) {
                        path = getAStarPath(1 * (COLS - 2) + 13); // goal
                        if (path == null || path.size() < 2) {
                            break;
                        }
                    }
                }
                arrL = getMoveMent(path, robot.getDirection());
                
            } else if (arrL.get(0) == TURNLEFT) {
                this.robot.turnLeft();
                arrL.remove(0);
            } else if (arrL.get(0) == TURNRIGHT) {
                this.robot.turnRight();
                arrL.remove(0);
            }
            this.robot.senseAll(this.map);
            this.map.repaint();
        } while (!isExporationEnd);
    }
    
    private List<PathFinder.Node> usePresetWp() {
        List<PathFinder.Node> path = null;
        ArrayList<Integer> arrL = robot.getPresetWayPt();
        if (arrL.size() > 0) {
            do {
                int i = getNearestWp(arrL);
                path = getAStarPath(arrL.get(i));
                if (path == null) {
                    arrL.remove(i);
                }
            } while ((path == null || path.size() < 2) && arrL.size() > 0);
        }
        return path;
    }
    
    private List<PathFinder.Node> useUnexploredWp() {
        
        List<PathFinder.Node> path = null;
        ArrayList<Integer> arrL = robot.getWaypoint();
        if (arrL.size() > 0) {
            do {
                int i = getNearestWp(arrL);
                int nexLoc = arrL.get(i);
                path = getAStarPath(nexLoc);
                if (path == null) {
                    robot.setRemoveUnuseWpIndex(nexLoc);
                    arrL.remove(i);
                    addPresetWp(nexLoc);
                    path = usePresetWp();
                }
                
            } while ((path == null || path.size() < 2) && arrL.size() > 0);
        }
        return path;
    }
    
    private int getNearestWp(List<Integer> wp) {
        int index = 0;
        int minDis = Integer.MAX_VALUE;
        
        int wpID;
        int x;
        int y;
        int dis;
        for (int i = 0; i < wp.size(); i++) {
            wpID = wp.get(i);
            x = wpID % (COLS - 2);
            y = wpID / (COLS - 2);
            dis = (Math.abs((robot.getX() - 1) - x) + Math.abs((robot.getY() - 1) - y));
            if (minDis > dis) {
                minDis = dis;
                index = i;
            }
        }
        return index;
    }
    
    private void addPresetWp(int nexLoc) {
        int nexY = nexLoc / (COLS - 2);
        int nexX = nexLoc % (COLS - 2);
//Robot.getMaxSensorRange()
        int d = 1;
        if (nexX + d < COLS - 3 && PathFinder.isPathFree(robot.getMyAStarMap(), nexY, nexX + d)) {
            robot.preSetWayPtArr.add(0, nexLoc + d);
        }
        
        if (nexY + d < ROWS - 3 && PathFinder.isPathFree(robot.getMyAStarMap(), nexY + d, nexX)) {
            robot.preSetWayPtArr.add(0, nexLoc + d * (COLS - 2));
        }
        if (nexX - d > 0 && PathFinder.isPathFree(robot.getMyAStarMap(), nexY, nexX - d)) {
            robot.preSetWayPtArr.add(0, nexLoc - d);
        }
        if (nexY - d > 0 && PathFinder.isPathFree(robot.getMyAStarMap(), nexY - d, nexX)) {
            robot.preSetWayPtArr.add(0, nexLoc - d * (COLS - 2));
        }
        d = 2;
        if (nexX + d < COLS - 3 && nexY + 1 < ROWS - 3
                && PathFinder.isPathFree(robot.getMyAStarMap(), nexY + 1, nexX + d)) {
            robot.preSetWayPtArr.add(0, nexLoc + d + 1 * (COLS - 2));
        }
        if (nexX + d < COLS - 3 && nexY - 1 > 0
                && PathFinder.isPathFree(robot.getMyAStarMap(), nexY - 1, nexX + d)) {
            robot.preSetWayPtArr.add(0, nexLoc + d - 1 * (COLS - 2));
        }
        
        if (nexY + d < ROWS - 3 && nexX - 1 > 0
                && PathFinder.isPathFree(robot.getMyAStarMap(), nexY + d, nexX - 1)) {
            robot.preSetWayPtArr.add(0, nexLoc + d * (COLS - 2) - 1);
        }
        if (nexY + d < ROWS - 3 && nexX + 1 < COLS - 3
                && PathFinder.isPathFree(robot.getMyAStarMap(), nexY + d, nexX + 1)) {
            robot.preSetWayPtArr.add(0, nexLoc + d * (COLS - 2) + 1);
        }
        
        if (nexX - d > 0 && nexY + 1 < ROWS - 3
                && PathFinder.isPathFree(robot.getMyAStarMap(), nexY + 1, nexX - d)) {
            robot.preSetWayPtArr.add(0, nexLoc - d + 1 * (COLS - 2));
        }
        if (nexX - d > 0 && nexY - 1 > 0
                && PathFinder.isPathFree(robot.getMyAStarMap(), nexY - 1, nexX - d)) {
            robot.preSetWayPtArr.add(0, nexLoc - d - 1 * (COLS - 2));
        }
        
        if (nexY - d > 0 && nexX - 1 > 0
                && PathFinder.isPathFree(robot.getMyAStarMap(), nexY - d, nexX - 1)) {
            robot.preSetWayPtArr.add(0, nexLoc - d * (COLS - 2) - 1);
        }
        if (nexY - d > 0 && nexX + 1 < COLS - 3
                && PathFinder.isPathFree(robot.getMyAStarMap(), nexY - d, nexX + 1)) {
            robot.preSetWayPtArr.add(0, nexLoc - d * (COLS - 2) + 1);
        }
        
    }
    
    List< PathFinder.Node> getAStarPath(int nexLoc) {
        long start = System.currentTimeMillis();
        int nexY = nexLoc / (COLS - 2);
        int nexX = nexLoc % (COLS - 2);
        int rY = robot.getY() - 1;
        int rX = robot.getX() - 1;
        if (rY == nexY && rX == nexX) {
            return null;
        }
        //PathFinder pf = new PathFinder(Robot.getMyAStarMap(), rY, rX, nexY, nexX);
        Algorithm pf =  new Algorithm3B3(robot.getMyAStarMap(), rY, rX, nexY, nexX);
        List<PathFinder.Node> path  = pf.compute();
        
        long end = System.currentTimeMillis();
        System.out.println("A* Algorithm take: " + (end - start) + " Mil");

//        if (path == null) {
//            System.out.println("No path");
//        } else {
//            System.out.print("Path = ");
//            for (PathFinder.Node n : path) {
//                System.out.print(n);
//            }
//            System.out.println();
//        }
        return path;
    }
    
   static public ArrayList<Integer> getMoveMent(List<PathFinder.Node> path, int direction) throws Exception {
        //first assume the first action is move forward to get to the second coordinator
        ArrayList<Integer> action = new ArrayList<Integer>();
        
        for (int i = 0; i < path.size() - 1; i++) {
            int moveAction = convert(path.get(i), path.get(i + 1), direction);
            //  System.out.println("action: " + moveAction);
            if (moveAction == 0) {
                System.out.println(" ERROR direction == 0!");
                throw new Exception();
            }
            if (moveAction == TURNLEFT) {
                action.add(TURNLEFT);
                direction = ((direction - 2 + 4) % 4) + 1;
            }
            if (moveAction == TURNRIGHT) {
                action.add(TURNRIGHT);
                direction = (direction % 4) + 1;
            }
            if (moveAction == MOVEBACKWARD) {
                action.add(TURNLEFT);
                direction = ((direction - 2 + 4) % 4) + 1;
                action.add(TURNLEFT);
                direction = ((direction - 2 + 4) % 4) + 1;
            }
            action.add(MOVEFORWARD);
            
        }
        
        return action;
    }
    
    static int convert(PathFinder.Node current, PathFinder.Node next, int direction) throws Exception {
        int action = 0;
        
        int curX = current.x;
        int curY = current.y;
        int nexX = next.x;
        int nexY = next.y;

        if (direction == NORTH) {
            if (nexY - curY == -1) {
                return MOVEFORWARD;
            } else if (nexY - curY == 1) {
                return MOVEBACKWARD;
            } else if (nexX - curX == -1) {
                return TURNLEFT;
            } else if (nexX - curX == 1) {
                return TURNRIGHT;
            }
        } else if (direction == WEST) {
            if (nexX - curX == -1) {
                return MOVEFORWARD;
            } else if (nexX - curX == 1) {
                return MOVEBACKWARD;
            } else if (nexY - curY == 1) {
                return TURNLEFT;
            } else if (nexY - curY == -1) {
                return TURNRIGHT;
            }
        } else if (direction == SOUTH) {
            if (nexY - curY == 1) {
                return MOVEFORWARD;
            } else if (nexY - curY == -1) {
                return MOVEBACKWARD;
            } else if (nexX - curX == 1) {
                return TURNLEFT;
            } else if (nexX - curX == -1) {
                return TURNRIGHT;
            }
        } else if (direction == EAST) {
            if (nexX - curX == 1) {
                return MOVEFORWARD;
            } else if (nexX - curX == -1) {
                return MOVEBACKWARD;
            } else if (nexY - curY == -1) {
                return TURNLEFT;
            } else if (nexY - curY == 1) {
                return TURNRIGHT;
            }
        }

        return action;
    }
    
}
