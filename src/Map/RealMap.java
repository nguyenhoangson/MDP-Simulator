package Map;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import Robot.RobotData;
import java.io.IOException;

public class RealMap extends JPanel implements MapData, RobotData {

    // the Robot initially should face
    static public int robDirection = WEST;
    //  Starting position of the Robot X = col  Y = row
    static public int robPosX;
    static public int robPosY;
    static public boolean isChangeStartPos = false;

    private static final long serialVersionUID = 1L;
    protected JPanel[][] entireMap = new JPanel[ROWS][COLS];
    protected Cell[][] newCell = new Cell[ROWS][COLS];
    private int[][] map = new int[ROWS][COLS];

    public RealMap() {
        initiate("src/1.txt");
    }

    protected void initiate(String data) {

        String[] mapData = null;
        try {
            mapData = FileLoader.readTextFile(data);
        } catch (IOException ex) {
            System.out.println("No preset Map load!");
        }

        this.setLayout(new GridLayout(ROWS, COLS));
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {

                if (mapData == null) {
                    newCell[i][j] = new Cell(i, j, this);
                } else {
                    newCell[i][j] = new Cell(i, j, mapData[i].charAt(j), this);
                }

                newCell[i][j].setBorder(BorderFactory.createLineBorder(UNEXPLORED, 1));
                entireMap[i][j] = newCell[i][j];
                this.add(newCell[i][j]);
            }
        }
    }

    public void removeWalls() {
        for (int i = 1; i < ROWS - 1; i++) {
            for (int j = 1; j < COLS - 1; j++) {
                if (!(newCell[i][j].IsGoal() || newCell[i][j].IsVisableZone())) {
                    newCell[i][j].setBackground(FREE);
                    newCell[i][j].setColor(FREE);
                }
            }
        }
    }

    public void loadData() {
        try {

            String fileName = FileLoader.getFileName(false);
            if (fileName == null) {
                return;
            }
            String[] mapData = FileLoader.readTextFile(fileName);
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {

                    newCell[i][j].setBackground(Cell.getColor(mapData[i].charAt(j)));
                    newCell[i][j].setColor(Cell.getColor(mapData[i].charAt(j)));

                }
            }
        } catch (IOException ex) {
            System.out.println("Data load unsuccessful!");
        }

    }

    public JPanel[][] getEntireMap() {
        return entireMap;
    }

    public Cell[][] getNewCell() {
        return newCell;
    }

    // check surrounding input must not add eage coordinate
    public boolean isWalkAble(int i, int j) {
        if (i == 0 || j == 0 || i == ROWS - 1 || j == COLS - 1) {
            return false;
        }
        if (newCell[i][j].getColor() == WALL) {
            return false;  //1
        }
        if (newCell[i + 1][j].getColor() == WALL) {
            return false;//2
        }
        if (newCell[i - 1][j].getColor() == WALL) {
            return false;//3
        }
        if (newCell[i][j + 1].getColor() == WALL) {
            return false;//4
        }
        if (newCell[i][j - 1].getColor() == WALL) {
            return false;//5
        }
        if (newCell[i + 1][j + 1].getColor() == WALL) {
            return false;//6
        }
        if (newCell[i + 1][j - 1].getColor() == WALL) {
            return false;//7
        }
        if (newCell[i - 1][j + 1].getColor() == WALL) {
            return false;//8
        }
        if (newCell[i - 1][j - 1].getColor() == WALL) {
            return false;//9
        }
        return true;
    }

    public void setStartPos(int row, int col) {

        for (int i = robPosY - 1; i < robPosY + 2; i++) {
            for (int j = robPosX - 1; j < robPosX + 2; j++) {
                newCell[i][j].setBackground(FREE);
                newCell[i][j].setColor(FREE);
                newCell[i][j].setExploredACell(false);
            }
        }
        if ((robPosY == 19 && robPosX == 2)) {
            for (int i = robPosY - 1; i < robPosY + 2; i++) {
                for (int j = robPosX - 1; j < robPosX + 2; j++) {
                    newCell[i][j].setBackground(START_GOAL_ZONE);
                    newCell[i][j].setColor(START_GOAL_ZONE);
                }

            }
        }
            for (int i = row - 1; i < row + 2; i++) {
                for (int j = col - 1; j < col + 2; j++) {
                    newCell[i][j].setBackground(START_GOAL_ZONE);
                    newCell[i][j].setColor(START_GOAL_ZONE);
                    newCell[i][j].setExploredACell(true);
                }
            }

            robPosX = col;
            robPosY = row;
            newCell[row][col].setBackground(START);
            newCell[row][col].setColor(START);
            RealMap.isChangeStartPos = false;
        }
    }
