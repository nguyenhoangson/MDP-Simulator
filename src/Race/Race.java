/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Race;

import AStar.Algorithm;
import AStar.Algorithm3B3;
import AStar.PathFinder;

import static Race.Client.*;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import Map.CoveredMap;
import Other.MyTimerListener;
import Robot.MDPRobot;

import javax.swing.*;

import static Map.FileLoader.saveExploreTextFile;
import static Map.MapData.COLS;
import static Map.MapData.FREE;
import static Map.MapData.PATH;
import static Map.MapData.ROBOTB;
import static Map.MapData.ROWS;
import static Robot.FastestPath.getMovement;
import static Robot.RobotData.ALIGNMENT;
import static Robot.RobotData.ALIGNMENT_1;
import static Robot.RobotData.ALIGNMENT_2;
import static Robot.RobotData.ALIGNMENT_3;
import static Robot.RobotData.ALIGNMENT_4;
import static Robot.RobotData.EAST;
import static Robot.RobotData.MOVEFORWARD;
import static Robot.RobotData.NORTH;
import static Robot.RobotData.SOUTH;
import static Robot.RobotData.START_FASTER_PATH;
import static Robot.RobotData.TURNLEFT;
import static Robot.RobotData.TURNRIGHT;
import static Robot.RobotData.WEST;

/**
 *
 * @author Ngo Tan Quang
 */

public class Race {

    private MDPRobot robot = null;
    private CoveredMap map = null;

    protected ArrayList<Integer> actionList = new ArrayList<>();
    protected ArrayList<Integer> XCoordinatorList = new ArrayList<>();
    protected ArrayList<Integer> YCoordinatorList = new ArrayList<>();
    protected ArrayList<Integer> fastPath = new ArrayList<>();
    
    private boolean isFastPath = false;
    private boolean isPreparingFastPath = false;
    private boolean isEnding= false;
    private boolean isBashing = true;
    private boolean isTracking = false;

    private int oldRx =-1, oldRy =-1;

    public static int loopCounter;
    boolean neededTobreakLoop = false;
    boolean isfirstRepeatPath = false;
    int infinLoopCounter = 0;

    public static boolean noBT = false;

    int directionToBash = -1;
    boolean directionToBashFound = false;


    public Race(MDPRobot bot, CoveredMap coveredMap, boolean noBluetooth) {
        this.robot = bot;
        this.map = coveredMap;
        noBT = noBluetooth;
         for (int i = robot.getY()- 1; i < robot.getY() + 2; i++) {
            for (int j = robot.getX() - 1; j < robot.getX() + 2; j++) {
                    coveredMap.setExploredMap(robot,j,i,FREE,0);
                }
        }

        // Initialize
        writeToArduino("E");
        System.out.println("Reading from RPI...");
        read();
        actionList.add(TURNRIGHT);
    }

    public static boolean preCali = false;
    public static boolean firstTime = true;
    public static String preAction = "";

    public void mainLoop() throws Exception {

        int ct = 0;
        loopCounter = 0;
        boolean needToCalibrate = false;
        boolean responded = false;
        String stringDecision = null; // String used to decide what will be the next action to take
        boolean readingDone;
        String readAgain = null;
        while (true) {
            if (noBT) break;
            stringDecision = read();
            if (stringDecision.equals("EXPLORE")) break;
        }
        while(true) {
            System.out.println("Loop " + (++ct) + " times");
            // Array for storing distances in order: FR, FL, FM, LS, RS, RL
            if (!isFastPath && !isPreparingFastPath) {
                do {
                    // Get sensor info
                    if (firstTime) {
                        writeToArduino("E");
                        firstTime = false;
                    }
                    if (readAgain != null) {
                        stringDecision = readAgain;
                        readAgain = null;
                    }
                    else
                        stringDecision = isForLocalTesting  ? robot.getSenseData(map) : read();
                    readingDone = (stringDecision.indexOf('D') == -1);
                    System.out.println("Received:" + stringDecision);


                } while (!readingDone);
            }
            // Get the sensor information to choose what will be the next action
            if(!isFastPath)
                ActionSelection.senseAll(map, robot, stringDecision);

            // Update map in PC
            map.repaint();

            // When the robot is at the start position or end position and it has not ended yet
            if (!isEnding && (isFastPath && robot.getX() == 14 && robot.getY() == 2) || (!isFastPath && isNeededToGetBack() && robot.getX() == 2 && robot.getY() == 19)) {
                isEnding = true;
                if (!isFastPath) {
                    saveExploreTextFile("src/explored.txt", robot.getMyExposedMap(), robot.getMyAStarMap());
                    robot.mergeAStarAndExplored();
                    isFastPath = true;
                    isEnding = false;
                }
                while (robot.getDirection() != SOUTH) {
                    turnLeft();
                }
                doAlignment(ALIGNMENT_3);
                while (robot.getDirection() != NORTH) {
                    turnLeft();
                }
                while (true) {
                    if (noBT) break;
                    if (read().equals("RACE")) break;
                }
                isPreparingFastPath = true;
                robot.enableCalibration();
            }

            // Start finding out the shortest path
            else if (isPreparingFastPath && actionList.size() == 0) {
                // doCalibration();
                if(actionList.isEmpty()){

                    if(robot.getX() == 14 && robot.getY() == 2)
                        break;

                    isPreparingFastPath = false;

                    fastPathOptimization(); //Do what?

                    String pS = getFastPathActionList(); //Shortest Path Finding
                    System.out.println("Action list: " + pS);
                    writeToArduino(pS);
                    return;
                    //writeToAndroid("Action list: " + pS);
//                    int dir = robot.getDirection();

                    /* while(!"".equals(pS) && (pS.charAt(0)== 'A' || pS.charAt(0)== 'a' || pS.charAt(0)== 'D' || pS.charAt(0)== 'd')){
                        if(pS.charAt(0)== 'A' || pS.charAt(0)== 'a' ) robot.turnLeft();
                        if(pS.charAt(0)== 'D' || pS.charAt(0)== 'd' ) robot.turnRight();
                        writeToArduino("" + pS.charAt(0));
                        pS = deleteCharAt(pS, 0);
                    }

                    if (robot.getDirection() == NORTH) {
                         writeToArduino("A");
                         writeToArduino("C");
                         writeToArduino("D");

                         char tempC = pS.charAt(0);
                         tempC -= 32;
                         pS = deleteCharAt(pS, 0);
                         pS = tempC + pS;

                    }
                    else if (robot.getDirection() == EAST) {
                         write("D");
                         write("C");
                         write("A");
                    } */
//                    deleteCharAt(pS, 0);

//                    robot.setDirection(dir);
                }
            }

            if (actionList.size() == 0) {
                if(actionList.isEmpty()) {
                    if(neededTobreakLoop){
                        if(infinLoopCounter>10 || isNeededToGetBack()){
                            robot.getPresetWayPt().clear();
                            robot.getWaypoint().clear();
                        }
                        List<PathFinder.Node> path = usePresetWp();
                        if (path == null) {
                            path = useUnexploredWp();
                            // back to start
                            if (path == null) {
                                path = getAStarPath(robot.getStartLoc());
                                if (path == null || path.size() < 2) {
                                    System.out.println("Error path == null || path.size() < 2 " );
                                    // break;
                                }
                            }
                        }
                        ArrayList<Integer> arrL = getMovement(path, robot.getDirection());
                        actionList.add(arrL.get(0));
                    }
                    if (actionList.size() == 0){
                        int action = robot.getMovement(map);
                        actionList.add(action);
                    }
                    robot.updateCalibrationCounter();
                }
            }
            switch (actionList.get(0)) {
                case MOVEFORWARD:
                    moveForward(1);
                    responded = false;
                    break;
                case TURNLEFT:
                    turnLeft();
                    responded = false;
                    break;
                case TURNRIGHT:
                    turnRight();
                    responded = false;
                    break;
                case ALIGNMENT:
                    int temp = robot.getTypeAlignment(map);
                    if (temp != -1) {
                        doAlignment(temp);
                        needToCalibrate = false;
                        responded = false;
                    }
                    else {
                        needToCalibrate = true;
                        responded = true;
                    }
                    break;
                case START_FASTER_PATH:
                    break;
                default:
                    System.out.println("unknown action in actionList");
                    break;
            }

            if (loopCounter % 5 == 0) needToCalibrate = true;
            if (needToCalibrate) {
                System.out.println("Need to calibrate");
                if (!preCali) if ((robot.getTypeAlignment(map) != -1)) {
                    System.out.println("Calibrate " + robot.getTypeAlignment(map));
                    actionList.add(ALIGNMENT);
                }
            }

            if (!responded) {
                readAgain = waitForResponse();
                responded = true;
            }


            actionList.remove(0);

            if (!isFastPath && robot.getX() == 14 && robot.getY() == 2) {
                    isTracking = true;
                    if(!neededTobreakLoop)
                        robot.getPresetWayPt().clear();
            }

            if (isTracking && (oldRx != robot.getX() || oldRy != robot.getY())) {
                oldRx = robot.getX();
                oldRy = robot.getY();
                XCoordinatorList.add(robot.getX());
                YCoordinatorList.add(robot.getY());
            }

        }
        System.out.println("Finish main loop");
    }


    void doCalibration(){
        int calib = robot.checkCalibration(map);

        if (calib == MOVEFORWARD) {
            actionList.add(ALIGNMENT);
        } else if (calib == TURNLEFT) {
            actionList.add(TURNLEFT);
            actionList.add(ALIGNMENT);
            actionList.add(TURNRIGHT);
        } else if (calib == TURNRIGHT) {
            actionList.add(TURNRIGHT);
            actionList.add(ALIGNMENT);
            actionList.add(TURNLEFT);
        }
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
        Algorithm pf =  new Algorithm3B3(robot.getMyAStarMap(), rY, rX, nexY, nexX);
       // PathFinder pf = new PathFinder(Robot.getMyAStarMap(), rY, rX, nexY, nexX);
        List<PathFinder.Node> path = pf.compute();
        if(path.isEmpty()) path = null;
        
        long end = System.currentTimeMillis();

//        System.out.print("A* Algorithm take: " + (end - start) + " Mil");
//        System.out.println("ry: " + rY + "rx: "+ rX + "nexY: " + nexY + "nexX: "+ nexX);
//        if (path == null || path.isEmpty()) {
//            path = null;
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

     private void fastPathOptimization(){
        int i = XCoordinatorList.size() - 1;
        while(i > 1){
            for(int j = 0; j <= i -2; j++){
                if((Math.abs(XCoordinatorList.get(i) - XCoordinatorList.get(j))
                        + Math.abs(YCoordinatorList.get(i) - YCoordinatorList.get(j))) == 1){
                    for(int k = i-1; k>j; k--){
                        XCoordinatorList.remove(k);
                        YCoordinatorList.remove(k);
                    }
                   i =  XCoordinatorList.size() + 1 - i + j;
                }
            }
            i--;
        }
    }

    private String getFastPathActionList() throws Exception{
        int direction = robot.getDirection();
       // for (int i = XCoordinatorList.size() - 1; i > 0; i--)
            //System.out.println(i +" :" + XCoordinatorList.get(i) + "s:" + XCoordinatorList.size());
        
        for (int i = XCoordinatorList.size() - 1; i > 0; i--) {
            int curX = XCoordinatorList.get(i);
            int nexX = XCoordinatorList.get(i - 1);
            int curY = YCoordinatorList.get(i);
            int nexY = YCoordinatorList.get(i - 1);

            if (direction == NORTH) {
                if (nexY - curY == -1) {
                    fastPath.add(MOVEFORWARD);
                } else if (nexX - curX == -1) {
                    fastPath.add(TURNLEFT);
                    fastPath.add(MOVEFORWARD);
                    direction = WEST;
                } else if (nexX - curX == 1) {
                    fastPath.add(TURNRIGHT);
                    fastPath.add(MOVEFORWARD);
                    direction = EAST;
                }else if(nexY - curY == 1){
                    fastPath.add(TURNRIGHT);
                    fastPath.add(TURNRIGHT);
                    fastPath.add(MOVEFORWARD);
                    direction = SOUTH;
                }
            } else if (direction == WEST) {
                if (nexX - curX == -1) {
                    fastPath.add(MOVEFORWARD);
                } else if (nexY - curY == 1) {
                    fastPath.add(TURNLEFT);
                    fastPath.add(MOVEFORWARD);
                    direction = SOUTH;
                } else if (nexY - curY == -1) {
                    fastPath.add(TURNRIGHT);
                    fastPath.add(MOVEFORWARD);
                    direction = NORTH;
                } else if(nexX - curX == 1){
                    fastPath.add(TURNRIGHT);
                    fastPath.add(TURNRIGHT);
                    fastPath.add(MOVEFORWARD);
                    direction = EAST;
                }
            } else if (direction == SOUTH) {
                if (nexY - curY == 1) {
                    fastPath.add(MOVEFORWARD);
                } else if (nexX - curX == 1) {
                    fastPath.add(TURNLEFT);
                    fastPath.add(MOVEFORWARD);
                    direction = EAST;
                } else if (nexX - curX == -1) {
                    fastPath.add(TURNRIGHT);
                    fastPath.add(MOVEFORWARD);
                    direction = WEST;
                } else if(nexY - curY == -1){
                    fastPath.add(TURNRIGHT);
                    fastPath.add(TURNRIGHT);
                    fastPath.add(MOVEFORWARD);
                    direction = NORTH;
                }
            } else if (direction == EAST) {
                if (nexX - curX == 1) {
                    fastPath.add(MOVEFORWARD);
                } else if (nexY - curY == -1) {
                    fastPath.add(TURNLEFT);
                    fastPath.add(MOVEFORWARD);
                    direction = NORTH;
                } else if (nexY - curY == 1) {
                    fastPath.add(TURNRIGHT);
                    fastPath.add(MOVEFORWARD);
                    direction = SOUTH;
                } else if(nexX - curX == -1){
                    fastPath.add(TURNRIGHT);
                    fastPath.add(TURNRIGHT);
                    fastPath.add(MOVEFORWARD);
                    direction = WEST;
                }
            }
        }

        /**
         * TODO:
         * Test this part
         */
        Algorithm pf =  new Algorithm3B3(robot.getMyAStarMap(), 18, 1, 1, 13);
       // PathFinder pf = new PathFinder(Robot.getMyAStarMap(), 18, 1, 1, 13);
        List<PathFinder.Node> path  = pf.compute();
         //List<PathFinder.Node> newPath = directlyToGoal(path);
         //if(newPath != null) path=newPath;
         ArrayList<Integer> arrL = getMovement(path, robot.getDirection());
         
        ArrayList<Integer> bestPath;
        if(arrL.size() < fastPath.size()-3) bestPath = arrL;          
        else bestPath = fastPath;

        //for (int i = 0; i < bestPath.size(); i++)actionList.add(bestPath.get(i));
        return getFasterPathMove(bestPath);
    }

    List<PathFinder.Node>  directlyToGoal(List<PathFinder.Node> path){
        int direction = robot.getDirection();
        
        List<PathFinder.Node> newPath = new LinkedList<PathFinder.Node>();
        int oldX = path.get(0).x; int oldY = path.get(0).y;
        float growRate = 0.1f;
        for (int i = 0; i < path.size(); i++) {
             newPath.add(path.get(i));
             int x = path.get(i).x;
             int y = path.get(i).y;
             
             int vX = 13 - x;
             int vY = 1 - y;
             double d = sqrt(vX*vX + vY*vY);
             double nVX = vX/d;
             double nVY = vY/d;
             
             float newX = (float) (x+0.5), newY = (float) (y-0.5);
             boolean pathFound = true;
             
             if(x-oldX > 0)
                 robot.setDirection(EAST);
             else if(x-oldX < 0)
                 robot.setDirection(WEST);
             else if(y-oldY > 0)
                 robot.setDirection(SOUTH);
             else if(y-oldY < 0)
                 robot.setDirection(NORTH);
                 
             robot.setX(x+1); robot.setY(y+1);
             if(robot.isLeftCalibrationAvailable(map)) growRate = 0.03f;
             else growRate = 0.08f;
            
             
             oldX = x;
             oldY = y;
             while(newX < 15 && newY > 0){
                 for (int k = 0; k < ROWS - 2; k++) {
                     for (int j = 0; j < COLS - 2; j++) {
                          if(robot.getMyAStarMap()[k][j] == false){
                              double disFormStartX = newX-(x+0.5);
                              double disFormStartY = newY-(y-0.5);
                              if(isCollided(newX,newY,j+0.5f,k-0.5f,sqrt(disFormStartX*disFormStartX + disFormStartY*disFormStartY),growRate)){
                                  newX = 15; pathFound = false;
                                  break;}
                          }
                     }
                 }
                 newX+=nVX; newY+=nVY;
             }
             if(pathFound) {
                 directionToBash = (int)((180.0 * asin(nVX/1)) / PI +0.5) ;
                 directionToBashFound = true;
                 System.out.print("go Bash at : " +  directionToBash + "degree");
                 robot.setX(2); robot.setY(19);robot.setDirection(direction);
                 return newPath;
             }
             
             //System.out.print("X : "+ x + " Y : "+ y );
        }
        robot.setX(2); robot.setY(19);robot.setDirection(direction);
        return null;
    }

    boolean isCollided(float x1, float y1, float x2, float y2, double dis, float growRate){
        double xDif = x1 - x2;
        double yDif = y1 - y2;
        double distanceSquared = xDif * xDif + yDif * yDif;
        return distanceSquared < (1.3 + 0.5+dis*growRate+growRate) * (1.3 + 0.5+dis*growRate + growRate);
    }

    boolean isAllForward(ArrayList<Integer>  aList, int s, int e){
        for (int i = s; i <= e; i++)if(aList.get(i) != MOVEFORWARD)return false;
        return true;
    }

    String getFasterPathMove(ArrayList<Integer>  aList){
       String str = "";
       int mSteps,turnWhere, oldRotX= robot.getX(), oldRotY= robot.getY(), oldDir= robot.getDirection();
       for (int i = 0; i < aList.size(); i++){
            char c = 'W';
            mSteps = 0;
            turnWhere=-1;
            boolean isMerged = false;
            switch(aList.get(i)){
                case  MOVEFORWARD:
                    
                 if(!isMerged && i < aList.size()-15 && isAllForward(aList,i,i+15)){
                    c = 'U';  i+=15; mSteps = 16; 
                    
                    robot.moveForward(mSteps);
                    if(robot.isLeftCalibrationAvailable(map))
                        isMerged = true;
                    robot.moveForward(-mSteps);
                        
                    
                 }
                 if(!isMerged && i < aList.size()-14 && isAllForward(aList,i,i+14)){
                      robot.moveForward(15);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-15);
                    if(mSteps == 0 || isMerged){ c = 'F';  i+=14; mSteps = 15;}
                 }
                 if(!isMerged && i < aList.size()-13 && isAllForward(aList,i,i+13)){
                    robot.moveForward(14);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-14);
                    if(mSteps == 0 || isMerged){ c = 'B';  i+=13; mSteps = 14; }
                    
                    
                 }
                 if(!isMerged && i < aList.size()-12 && isAllForward(aList,i,i+12)){
                                         robot.moveForward(13);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-13);
                    if(mSteps == 0 || isMerged){ c = 'V';  i+=12; mSteps = 13; }

                 }
                 if(!isMerged && i < aList.size()-11 && isAllForward(aList,i,i+11)){
                                          robot.moveForward(12);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-12);
                    if(mSteps == 0 || isMerged){   c = 'C';  i+=11; mSteps = 12; }
                  
                 }
                 if(!isMerged && i < aList.size()-10 && isAllForward(aList,i,i+10)){
                                          robot.moveForward(11);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-11);
                    if(mSteps == 0 || isMerged){ c = 'R';  i+=10; mSteps = 11; }
                    
                 }
                 if(!isMerged && i < aList.size()-9 && isAllForward(aList,i,i+9)){
                                          robot.moveForward(10);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-10);
                    if(mSteps == 0 || isMerged){ c = 'E';  i+=9; mSteps = 10; }
                    
                 }
                 if(!isMerged && i < aList.size()-8 && isAllForward(aList,i,i+8)){
                                          robot.moveForward(9);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-9);
                    if(mSteps == 0 || isMerged){ c = 'P';  i+=8; mSteps = 9;}
                     
                 }
                 if(!isMerged && i < aList.size()-7 && isAllForward(aList,i,i+7)){
                                          robot.moveForward(8);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-8);
                    if(mSteps == 0 || isMerged){   c = 'O';  i+=7; mSteps = 8; }

                 }
                 if(!isMerged && i < aList.size()-6 && isAllForward(aList,i,i+6)){
                                          robot.moveForward(7);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-7);
                    if(mSteps == 0 || isMerged){  c = 'L';  i+=6; mSteps = 7; }
                   
                 }
                 if(!isMerged && i < aList.size()-5 && isAllForward(aList,i,i+5)){
                                          robot.moveForward(6);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-6);
                    if(mSteps == 0 || isMerged){ c = 'K';  i+=5; mSteps = 6; }
                    
                 }
                 if(!isMerged && i < aList.size()-4 && isAllForward(aList,i,i+4)){
                                          robot.moveForward(5);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-5);
                    if(mSteps == 0 || isMerged){  c = 'J';  i+=4; mSteps = 5;}
                    
                 }
                 if(!isMerged && i < aList.size()-3 && isAllForward(aList,i,i+3)){
                                          robot.moveForward(4);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-4);
                    if(mSteps == 0 || isMerged){ c = 'H';  i+=3; mSteps = 4; }
                    
                 }
                 if(!isMerged && i < aList.size()-2 && isAllForward(aList,i,i+2)){
                                          robot.moveForward(3);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-3);
                    if(mSteps == 0 || isMerged){ c = 'M';  i+=2; mSteps = 3; }
                    
                 }
                 if(!isMerged && i < aList.size()-1 && isAllForward(aList,i,i+1)){
                                          robot.moveForward(2);
                    if(robot.isLeftCalibrationAvailable(map)){
                        isMerged = true;
                    }
                    robot.moveForward(-2);
                    if(mSteps == 0 || isMerged){  c = 'N';  i+=1; mSteps = 2; }
                   
                 }
                 if(!isMerged && mSteps == 0) {c = 'W';    mSteps = 1;}           break;
                case  TURNLEFT:    c = 'A'; turnWhere =1; break;
                case  TURNRIGHT:   c = 'D'; turnWhere =2; break;
            }
            if (mSteps>0) {
                String tempStr = "";
                if (mSteps > 10) {
                    tempStr = "W" + (10) + "#" + "W" + (mSteps-10) + "#";
                }
                else
                    tempStr = "W" + mSteps + "#";
                str += tempStr;

            }
            else{
                str += (char) (robot.isLeftCalibrationAvailable(map) ? c+32 : c);
                str += "#";
            }

            robot.moveForward(mSteps);
            if(turnWhere ==1) robot.turnLeft(); 
            if(turnWhere ==2) robot.turnRight();
       }
       
       if(directionToBash != -1){
           if(robot.getDirection() == EAST) directionToBash-=90;
           if(robot.getDirection() == SOUTH) directionToBash-=180;
           if(robot.getDirection() == WEST) directionToBash+=90;
       }
       robot.setX(oldRotX); robot.setY(oldRotY); robot.setDirection(oldDir);    
       return str;
   }
   
    private boolean isNeededToGetBack() {
        //return   isTracking || 100 <= robot.getPercentCellCovered(); //percentToCover = 50
        //return robot.getPercentCellCovered() >= 50 || isTracking;
        return isTracking && (90 <= robot.getPercentCellCovered());
    }

    // Perform robot actions
    void turnLeft() {
        preCali = false;
        char c = 'A';
        preAction = "" + c;
        robot.turnLeft();
        writeToArduino("" + c);
        writeToAndroid("" + c);
    }

    void turnRight() {
        preCali = false;
        char c = 'D';
        robot.turnRight();
        preAction = "" + c;
        writeToArduino("" + c);
        writeToAndroid("" + c);
    }

    void moveForward(int nSteps) {
        preCali = false;
        loopCounter += nSteps;
        System.out.println("Loop counter : " + loopCounter);
        for (int i = 1; i<=nSteps; i++) writeToArduino("W");
        for (int i = 1; i<=nSteps; i++) writeToAndroid("W");
        preAction = "";
        for(int i =0; i < nSteps; i++) {
            robot.moveForward();
            preAction += "W";
            if(isFastPath)this.map.setColor(robot.getX(), robot.getY(), PATH);
            else {
                this.map.setColor(robot.getX(), robot.getY(), ROBOTB);
                //if((this.map.getNewCell())[robot.getY()][robot.getX()].getColor() != PATH)


            }
        }
    }

    public static void sendObstacleData(int x, int y, boolean add) {
        if (noBT) return;
        x -= 1;
        y -= 1;
        String toSend = add ? "ADD" : "REMOVE";
        toSend += "OBSTACLE:" + x + "," + y;
        Client.writeToAndroid(toSend);
    }

    void doAlignment(int type) {
        if (type == -1) return;
        char c = 'C';
        if(type == ALIGNMENT_1) c = 'B';
        if(type == ALIGNMENT_2) c = 'V';
        if(type == ALIGNMENT_3) c = 'C';
        if(type == ALIGNMENT_4) c = 'N';
        writeToArduino("" + c);
        preAction = "" + c;
        loopCounter = 1;
        preCali = true;
    }

    // Auxiliary functions: No assumption about robot, and it can be re-used
    private static String deleteCharAt(String strValue, int index) {
        return strValue.substring(0, index) + strValue.substring(index + 1);
    }

    public String waitForResponse() {
        System.out.println("Waiting for responses");
        long startTime = System.currentTimeMillis(), endTime, totalTime;
        while (true) {
            String temp = hasNext();
            if (temp != null) return temp;
            endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            System.out.println("Total time: " + totalTime);
            if (totalTime >= 1000) {
                System.out.println("Write again");
                writeToArduino(preAction);
                startTime = System.currentTimeMillis();
            }
        }
    }
}


