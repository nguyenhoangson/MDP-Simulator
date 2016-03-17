package Map;

import static Race.ActionSelection.dataReliablilty;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import Race.Race;
import Robot.MDPRobot;
import Robot.RobotData;
import java.util.ArrayList;
import Race.*;

public class CoveredMap extends JPanel implements MapData, RobotData {

    private static final long serialVersionUID = 1L;

    private Cell[][] newCell = new Cell[ROWS][COLS];
    private Cell[][] originalCell = new Cell[ROWS][COLS];
    private MDPRobot r;

    public CoveredMap(RealMap realMap, MDPRobot r) {
        this.r = r;
        this.initiate(realMap);
    }

    public void setColor(int i, int j, Color color) {
        newCell[j][i].setExploredACell(true);
        
        if(newCell[j][i].getColor() != ROBOTB && newCell[j][i].getColor() != PATH) newCell[j][i].setColor(color);
        else newCell[j][i].setColor(PATH);
    }

    public void resetSensePriory(){
        for(int i =0; i < ROWS; i++)
            for(int j =0; j < COLS; j++)
                newCell[i][j].Explored_From-=1;
    }
    
    public void setExploredMap(MDPRobot robot, int i, int j, Color color, int d) {
        int[][] myExpMap = robot.getMyExposedMap();
        boolean[][] myAStarMap = robot.getMyAStarMap();
        boolean[][] myAStarMap2 = robot.getMyAStarMap2();
        
        ArrayList<Integer> ptArr = robot.getWaypoint();
        ArrayList<Integer> preSetWayPtArr = robot.getPresetWayPt();
        int removeUnuseWpIndex = robot.getRemoveUnuseWpIndex();
        
        float dis = (float) (d - dataReliablilty *0.1);
        if (i > 0 && j > 0 && j < ROWS - 1 && i < COLS - 1 &&  dis < newCell[j][i].Explored_From-0.0001 ) { //&& myExpMap[j - 1][i - 1] == 0
            newCell[j][i].setExploredACell(true);
            newCell[j][i].Explored_From = dis;

             int tempCoord = (j - 1) * (COLS - 2) + i - 1;
            if (removeUnuseWpIndex == tempCoord) {
                preSetWayPtArr.clear();

            }
            for (int k = 0; k < ptArr.size(); k++) {
                if (ptArr.get(k) == tempCoord) {
                    ptArr.remove(k);
                }
            }

            if (newCell[j][i].getColor() != PATH && newCell[j][i].getColor() != ROBOTB && newCell[j][i].getColor() != START_GOAL_ZONE && newCell[j][i].getColor() != START){
                newCell[j][i].setColor(color);
                if (color == WALL && !Client.isForLocalTesting)
                    Race.sendObstacleData(i, j, true);
                if (color == FREE && !Client.isForLocalTesting && newCell[j][i].getColor() == WALL) {
                    Race.sendObstacleData(i, j, false);
                }
            }
            if(newCell[j][i].getColor() == ROBOTB || newCell[j][i].getColor() == PATH)  
                myAStarMap2[j][i] = true; // for walkable
            
            if(myExpMap[j - 1][i - 1] ==0)robot.increNumCellCovered();
               myExpMap[j - 1][i - 1] = 1;
            
            myAStarMap[j - 1][i - 1] = (newCell[j][i].getColor() == WALL || newCell[j][i].getColor() == TO_BE_CONFIRMED) ? false : true;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        int j;
        int i;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(UNEXPLORED);
        g.fillRect(33, 33, 492, 657);
        for (j = 0; j < COLS; j++) {
            g.setColor(UNEXPLORED);
            g.fillRect(30 + j * 33, 0, 3, 723);
        }
        for (i = 0; i < ROWS; i++) {
            g.setColor(UNEXPLORED);
            g.fillRect(0, 30 + i * 33, 558, 3);
        }

        //this is to dye the explored cells
        for (i = 0; i < ROWS; i++) {
            for (j = 0; j < COLS; j++) {
                if (newCell[i][j].getExplored()) {
                    g.setColor(newCell[i][j].getColor());
                    g.fillRect(newCell[i][j].getXC() - 15, newCell[i][j].getYC() - 15, 30, 30);
                }
            }
        }

        //this part will dye the Robot
        g.setColor(ROBOTB);
        g.fillRect(this.r.getXC() - 81, this.r.getYC() - 81, 96, 96);
        switch (this.r.getDirection()) {
            case EAST:
                g.setColor(ROBOTF);
                g.fillRect(this.r.getXC() - 15, this.r.getYC() - 48, 30, 30);
                break;
            case SOUTH:
                g.setColor(ROBOTF);
                g.fillRect(this.r.getXC() - 48, this.r.getYC() - 15, 30, 30);
                break;
            case WEST:
                g.setColor(ROBOTF);
                g.fillRect(this.r.getXC() - 81, this.r.getYC() - 48, 30, 30);
                break;
            case NORTH:
                g.setColor(ROBOTF);
                g.fillRect(this.r.getXC() - 48, this.r.getYC() - 81, 30, 30);
                break;
        }
    }

    protected void initiate(RealMap realMap) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                newCell[i][j] = new Cell(i, j);
                this.newCell[i][j].setColor(realMap.newCell[i][j].getColor());
                this.newCell[i][j].setBackground(realMap.newCell[i][j].getColor());
                this.newCell[i][j].setExploredACell(realMap.newCell[i][j].getExplored());
                
                originalCell[i][j] = new Cell(i, j);
                this.originalCell[i][j].setColor(realMap.newCell[i][j].getColor());
                this.originalCell[i][j].setBackground(realMap.newCell[i][j].getColor());
                this.originalCell[i][j].setExploredACell(realMap.newCell[i][j].getExplored());

            }
        }
    }
    public Cell uncoverACell(int x, int y) {
        return originalCell[y][x];
    }
    public Cell uncoverACell(int x, int y, MDPRobot robot) {
        newCell[y][x].setExploredACell(true);
        int[][] myExpMap = robot.getMyExposedMap();
        boolean[][] myAStarMap = robot.getMyAStarMap();
        ArrayList<Integer> ptArr = robot.getWaypoint();
        ArrayList<Integer> preSetWayPtArr = robot.getPresetWayPt();
        int removeUnuseWpIndex = robot.getRemoveUnuseWpIndex();

        if (x > 0 && y > 0 && y < ROWS - 1 && x < COLS - 1 && myExpMap[y - 1][x - 1] == 0) {

            myExpMap[y - 1][x - 1] = 1;
            robot.increNumCellCovered();
            myAStarMap[y - 1][x - 1] = newCell[y][x].getColor() == WALL ? false : true;

            int tempCoord = (y - 1) * (COLS - 2) + x - 1;
            if (removeUnuseWpIndex == tempCoord) {
                preSetWayPtArr.clear();

            }
            for (int i = 0; i < ptArr.size(); i++) {
                if (ptArr.get(i) == tempCoord) {
                    ptArr.remove(i);
                }
            }
        }

//        System.out.println("Explored Map ");
//        for (int i = 0; i < myExpMap.length; i++) {
//            for (int j = 0; j < myExpMap[0].length; j++) {
//                System.out.print(myExpMap[i][j] + " ");
//            }
//            System.out.println();
//        }
//        System.out.println("A*  Map ");
//        for (int i = 0; i < myAStarMap.length; i++) {
//            for (int j = 0; j < myAStarMap[0].length; j++) {
//                System.out.print(myAStarMap[i][j] + " ");
//            }
//            System.out.println();
//        }
//
//        System.out.print("ptArr = ");
//        for (int n : ptArr) {
//            System.out.print("(" + n / (COLS - 2) + " " + n % (COLS - 2) + ")");
//        }
//        System.out.println();
        return newCell[y][x];
    }

    public Cell[][] getNewCell() {
        return newCell;
    }
}
