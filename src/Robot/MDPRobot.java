package Robot;

import java.util.ArrayList;
import Map.Cell;
import Map.CoveredMap;
import Map.MapData;
import static Robot.SensorData.DATA_VARITATION;
import static Robot.SensorData.LSLB;
import static Robot.SensorData.LSUB;
import static Robot.SensorData.RSLB;
import static Robot.SensorData.RSUB;
import static Robot.SensorData.RLLB;
import static Robot.SensorData.RLUB;
import static Robot.SensorData.FLLB;
import static Robot.SensorData.FLUB;
import static Robot.SensorData.FMLB;
import static Robot.SensorData.FMUB;
import static Robot.SensorData.FRLB;
import static Robot.SensorData.FRUB;
import static Robot.SensorData.VARITATION;

public class MDPRobot implements RobotData, MapData {

    protected int[][] myExposedMap = new int[ROWS - 2][COLS - 2];
    protected boolean[][] myAStarMap = new boolean[ROWS - 2][COLS - 2];
    protected ArrayList<Integer> ptArr = new ArrayList<>(); // keep track of uncovered area, use as waypoint pts
    public ArrayList<Integer> preSetWayPtArr = new ArrayList<>(); // preset waypoint pts

    protected boolean[][] myAStarMap2 = new boolean[ROWS - 2][COLS - 2];
    
    // the Robot initially should face east
    private int direction;
    private int previousAction = TURNRIGHT;
    //  position of the Robot X = col  Y = row
    private int x;
    private int y;

    // the max range of sensors on the Robot
    private int sensorF;
    private int sensorL;
    private int sensorR;

    private int x_axis_calibration_counter = 0;
    private int y_axis_calibration_counter = 0;

    private int removeUnuseWpIndex = -1;

    // the memory of Robot
    private boolean previousLeftWall = true;
    private int startLoc;

    private int numCellCovered;
    private int percentCellCovered = 0;
    
    public boolean isRightChanged = false;

    public  void mergeAStarAndExplored(){
       for (int i = 0; i < ROWS - 2; i++) {
            for (int j = 0; j < COLS - 2; j++) {
                if(myExposedMap[i][j] ==0){ // if unexplored
                    myAStarMap[i][j] = false; // for walkable
                     myAStarMap2[i][j] = false; // for walkable
                }
            }
        }
    }

    public MDPRobot(int startX, int startY, int direction_,
            int sensorF, int sensorL, int sensorR) {

        this.sensorF = sensorF;
        this.sensorL = sensorL;
        this.sensorR = sensorR;

        this.x = startX;
        this.y = startY;
        this.direction = direction_;

        for (int i = 0; i < ROWS - 2; i++) {
            for (int j = 0; j < COLS - 2; j++) {
                myExposedMap[i][j] = 0;
                myAStarMap[i][j] = true; // for walkable
                myAStarMap2[i][j] = true; // for walkable
                ptArr.add(i * (COLS - 2) + j);
            }
        }

       // preSetWayPtArr.add(1 * (COLS - 2) + 1); // top left conner
        preSetWayPtArr.add(1 * (COLS - 2) + 13); // goal
        // preSetWayPtArr.add( (startY-1)*(COLS - 2) + startX-1); // start

        // start
        startLoc = (18) * (COLS - 2) + 1;
        for (int i = startY - 2; i < startY + 1; i++) {
            for (int j = startX - 2; j < startX + 1; j++) {
                myExposedMap[i][j] = 1;
                int tempCoord = i * (COLS - 2) + j;
                for (int k = 0; k < ptArr.size(); k++) {
                    if (ptArr.get(k) == tempCoord) {
                        ptArr.remove(k);
                    }
                }

            }

        }
        numCellCovered = 9;
        enableCalibration();

    }

    // to perform some basic movements
    public void moveForward(int steps) {
        switch (this.direction) {
            case EAST:
                this.x+=steps;
                break;
            case SOUTH:
                this.y+=steps;
                break;
            case WEST:
                this.x-=steps;
                break;
            case NORTH:
                this.y-=steps;
                break;
        }
    }

    // to perform some basic movements
    public void moveForward() {
        switch (this.direction) {
            case EAST:
                this.x++;
                break;
            case SOUTH:
                this.y++;
                break;
            case WEST:
                this.x--;
                break;
            case NORTH:
                this.y--;
                break;
        }
    }

    // to perform some basic movements
    public void turnLeft() {
        switch (this.direction) {
            case EAST:
                this.direction = NORTH;
                break;
            case SOUTH:
                this.direction = EAST;
                break;
            case WEST:
                this.direction = SOUTH;
                break;
            case NORTH:
                this.direction = WEST;
                break;
        }
    }

    // to perform some basic movements
    public void turnRight() {
        switch (this.direction) {
            case EAST:
                this.direction = SOUTH;
                break;
            case SOUTH:
                this.direction = WEST;
                break;
            case WEST:
                this.direction = NORTH;
                break;
            case NORTH:
                this.direction = EAST;
                break;
        }
    }

    // to perform some basic movements
    public void turnBack() {
        this.turnLeft();
        this.turnLeft();
    }

    int addVariation(float n, float v) {
        return  (int) (n+ n * (-v + (Math.random()*v*2)));
    }
    public String getSenseData(CoveredMap map) {
        float v =  DATA_VARITATION; 
        String str = "";
        Cell uncovered1 = null;
        Cell uncovered2 = null;
        Cell uncovered3 = null;
        int i;
   
        for (i = 0; i <= this.sensorF + 2; i++) { // FL
            switch (this.direction) {
                case EAST:uncovered3 = map.uncoverACell(this.x + i, this.y - 1);break;
                case SOUTH:uncovered3 = map.uncoverACell(this.x + 1, this.y + i);break;
                case WEST:uncovered3 = map.uncoverACell(this.x - i, this.y + 1); break;
                case NORTH:uncovered3 = map.uncoverACell(this.x - 1, this.y - i);break;
            }
            if (uncovered3.getColor() == WALL) {break;}
        }
        str +=  addVariation(i*10-10+2,v) +   ",";
       
        for (i = 0; i <= this.sensorF + 2; i++) { // FM
            switch (this.direction) {
                case EAST:uncovered1 = map.uncoverACell(this.x + i, this.y);break;
                case SOUTH:uncovered1 = map.uncoverACell(this.x, this.y + i);break;
                case WEST:uncovered1 = map.uncoverACell(this.x - i, this.y); break;
                case NORTH:uncovered1 = map.uncoverACell(this.x, this.y - i); break;
            }
            if (uncovered1.getColor() == WALL) { break;}
        }

        str +=addVariation(i*10-10+2,v) +",";
    
        for (i = 0; i <= this.sensorF + 2; i++) { // FR
            switch (this.direction) {
                case EAST:uncovered2 = map.uncoverACell(this.x + i, this.y + 1);break;
                case SOUTH:uncovered2 = map.uncoverACell(this.x - 1, this.y + i);break;
                case WEST:uncovered2 = map.uncoverACell(this.x - i, this.y - 1);break;
                case NORTH:uncovered2 = map.uncoverACell(this.x + 1, this.y - i);break;
            }
            if (uncovered2.getColor() == WALL) {break;}
        }

        str +=addVariation(i*10-10+2,v) +",";

        for (i = 0; i <= this.sensorL + 2; i++) { // LS
            switch (this.direction) {
                case EAST:uncovered1 = map.uncoverACell(this.x, this.y - i, this);break;
                case SOUTH:uncovered1 = map.uncoverACell(this.x + i, this.y, this);break;
                case WEST:uncovered1 = map.uncoverACell(this.x, this.y + i, this); break;
                case NORTH:uncovered1 = map.uncoverACell(this.x - i, this.y, this);break;
            }
            if (uncovered1.getColor() == WALL) {break; }
        }

        str +=addVariation(i*10-10,v) + ",";

        for (i = 0; i <= this.sensorR + 2; i++) { // RS
            switch (this.direction) {
                case EAST:uncovered1 = map.uncoverACell(this.x + 1, this.y + i, this);break;
                case SOUTH:uncovered1 = map.uncoverACell(this.x - i, this.y + 1, this);break;
                case WEST: uncovered1 = map.uncoverACell(this.x - 1, this.y - i, this);break;
                case NORTH:uncovered1 = map.uncoverACell(this.x + i, this.y - 1, this);break;
            }
            if (uncovered1.getColor() == WALL) break;
        }
        str += addVariation(i*10-10 + 8,v)+   ",";
        for (i = 0; i<= this.sensorR + 2; i++) { // RL
            switch (this.direction) {
                case EAST:uncovered2 = map.uncoverACell(this.x - 1, this.y + i, this);break;
                case SOUTH:uncovered2 = map.uncoverACell(this.x - i, this.y - 1, this);break;
                case WEST: uncovered2 = map.uncoverACell(this.x + 1, this.y - i, this);break;
                case NORTH:uncovered2 = map.uncoverACell(this.x + i, this.y + 1, this);break;
            }
            if (uncovered2.getColor() == WALL) break;
        }
        str +=addVariation(i*10-10,v) + ",";
        
 
        str +=  addVariation(2,1) ; // dataReliablilty
        return str;
    }

    // sense the Map, discover the covered cells, very tricky here, depending on
    // how the sensors are placed
    void senseFront(CoveredMap map) {
        Cell uncovered1 = null;
        Cell uncovered2 = null;
        Cell uncovered3 = null;
        int i;

        for (i = 0; i <= this.sensorF + 2; i++) {
            switch (this.direction) {
                case EAST:uncovered1 = map.uncoverACell(this.x + i, this.y, this);break;
                case SOUTH:uncovered1 = map.uncoverACell(this.x, this.y + i, this);break;
                case WEST:uncovered1 = map.uncoverACell(this.x - i, this.y, this);break;
                case NORTH:uncovered1 = map.uncoverACell(this.x, this.y - i, this);break;
            }
            if (uncovered1.getColor() == WALL) {break;}
        }
    
        for (i = 0; i <= this.sensorF + 2; i++) {
            switch (this.direction) {
                case EAST:
                    uncovered2 = map.uncoverACell(this.x + i, this.y - 1, this); break;
                case SOUTH:uncovered2 = map.uncoverACell(this.x + 1, this.y + i, this); break;
                case WEST: uncovered2 = map.uncoverACell(this.x - i, this.y + 1, this); break;
                case NORTH:uncovered2 = map.uncoverACell(this.x - 1, this.y - i, this);break;
            }
            if (uncovered2.getColor() == WALL) {break;}
        }
       
        for (i = 0; i <= this.sensorF + 2; i++) {
            switch (this.direction) {
                case EAST: uncovered3 = map.uncoverACell(this.x + i, this.y + 1, this);break;
                case SOUTH:uncovered3 = map.uncoverACell(this.x - 1, this.y + i, this);break;
                case WEST:uncovered3 = map.uncoverACell(this.x - i, this.y - 1, this); break;
                case NORTH:uncovered3 = map.uncoverACell(this.x + 1, this.y - i, this);break;
            }
            if (uncovered3.getColor() == WALL) {break;}
        }
    }

    void senseLeft(CoveredMap map) {
        Cell uncovered1 = null;
        //Cell uncovered2 = null;
        //Cell uncovered3 = null;
        int i;
        for (i = 0; i <= this.sensorL + 2; i++) {
            switch (this.direction) {
                case EAST:uncovered1 = map.uncoverACell(this.x, this.y - i, this);break;
                case SOUTH:uncovered1 = map.uncoverACell(this.x + i, this.y, this);break;
                case WEST:uncovered1 = map.uncoverACell(this.x, this.y + i, this); break;
                case NORTH:uncovered1 = map.uncoverACell(this.x - i, this.y, this);break;
            }
            if (uncovered1.getColor() == WALL) {break; }
        }
    }

    void senseRight(CoveredMap map) {
        Cell uncovered1 = null;
        Cell uncovered2 = null;
        int i;
        for (i = 0; i <= this.sensorR + 2; i++) {
            switch (this.direction) {
                case EAST:uncovered1 = map.uncoverACell(this.x + 1, this.y + i, this);break;
                case SOUTH:uncovered1 = map.uncoverACell(this.x - i, this.y + 1, this);break;
                case WEST: uncovered1 = map.uncoverACell(this.x - 1, this.y - i, this);break;
                case NORTH:uncovered1 = map.uncoverACell(this.x + i, this.y - 1, this);break;
            }
            if (uncovered1.getColor() == WALL) break;
        }

        for (i = 0; i<= this.sensorR + 2; i++) {
            switch (this.direction) {
                case EAST:uncovered2 = map.uncoverACell(this.x - 1, this.y + i, this);break;
                case SOUTH:uncovered2 = map.uncoverACell(this.x - i, this.y - 1, this);break;
                case WEST: uncovered2 = map.uncoverACell(this.x + 1, this.y - i, this);break;
                case NORTH:uncovered2 = map.uncoverACell(this.x + i, this.y + 1, this);break;
            }
            if (uncovered2.getColor() == WALL) break;
        }

    }


    /**
     * int checkObstacle
     * @param distance: distance in cm
     * @param type: type of sensor
     *            1: Front IR
     *            2: Right IR
     *            3: Left US
     * @return how many grids away the obstacle is
     */
    int checkObstacle(int distance, int type) {
        switch (type) {
            case 1:
                if (distance < 0) return 100;
                else if (distance <= 10) return 1;
                else if (15 <= distance && distance <= 21) return 2;
                //else if (distance <= 32) return 3; */
                else return 100;

            case 2:
                if (distance < 0) return 200;
                else if (distance <= 10) return 1;
                else if (16 <= distance && distance <= 20) return 2;
                //else if (distance <= 30) return 3; */
                else return 200;
        }
        return -1;
    }

    public void senseFL(CoveredMap map, int distance) {
        int d = checkObstacle(distance, 1);
        if (d == -1) return;
        if (d == 100) {
            paintFLFree(map, 3);
        }
        else {
            paintFLWall(map, d);
            paintFLFree(map, d);
        }
    }

    public void senseFM(CoveredMap map, int distance) {
        int d = checkObstacle(distance, 1);
        if (d == -1) return;
        if (d == 100) {
            paintFMFree(map, 3);
        }
        else {
            paintFMWall(map, d);
            paintFMFree(map, d);
        }
    }

    public void senseFR(CoveredMap map, int distance) {
        int d = checkObstacle(distance, 1);
        if (d == -1) return;
        if (d == 100) {
            paintFRFree(map, 3);
        }
        else {
            paintFRWall(map, d);
            paintFRFree(map, d);
        }
    }

    public void senseLS(CoveredMap map, int distance) {
        int d = checkObstacle(distance, 2);
        if (d == -1) return;
        if (d == 200) {
            paintLSFree(map, 3);
        }
        else {
            paintLSWall(map, d);
            paintLSFree(map, d);
        }
    }

    public void senseRS(CoveredMap map, int distance) {
        int d = checkObstacle(distance, 2);
        if (d == -1) return;
        if (d == 200) {
            paintRSFree(map, 3);
        }
        else {
            paintRSWall(map, d);
            paintRSFree(map, d);
        }
    }

    void paintFLWall(CoveredMap map, int distance) {
        switch (this.direction) {
            case EAST:map.setExploredMap(this, this.x + distance + 1, this.y - 1, WALL, distance);break;
            case SOUTH:map.setExploredMap(this, this.x + 1, this.y + distance + 1, WALL, distance);break;
            case WEST:map.setExploredMap(this, this.x - distance - 1, this.y + 1, WALL, distance);break;
            case NORTH:map.setExploredMap(this, this.x - 1, this.y - distance - 1, WALL, distance);break;
        }
    }

    void paintFMWall(CoveredMap map, int distance) {
        switch (this.direction) {
            case EAST:map.setExploredMap(this, this.x + distance + 1, this.y, WALL, distance);break;
            case SOUTH:map.setExploredMap(this, this.x, this.y + distance + 1, WALL, distance);break;
            case WEST:map.setExploredMap(this, this.x - distance - 1, this.y, WALL, distance);break;
            case NORTH:map.setExploredMap(this, this.x, this.y - distance - 1, WALL, distance);break;
        }
    }

    void paintFRWall(CoveredMap map, int distance) {
        switch (this.direction) {
            case EAST:map.setExploredMap(this, this.x + distance + 1, this.y + 1, WALL, distance);break;
            case SOUTH:map.setExploredMap(this, this.x - 1, this.y + distance + 1, WALL, distance); break;
            case WEST: map.setExploredMap(this, this.x - distance - 1, this.y - 1, WALL, distance);break;
            case NORTH:map.setExploredMap(this, this.x + 1, this.y - distance - 1, WALL, distance);break;
        }
    }

    void paintLSWall(CoveredMap map, int distance) {
        switch (this.direction) {
            case EAST:map.setExploredMap(this, this.x, this.y - distance - 1, WALL, distance);break;
            case SOUTH: map.setExploredMap(this, this.x + distance + 1, this.y, WALL, distance);break;
            case WEST:map.setExploredMap(this, this.x, this.y + distance + 1, WALL, distance);break;
            case NORTH:map.setExploredMap(this, this.x - distance - 1, this.y, WALL, distance);break;
        }
    }

    void paintRSWall(CoveredMap map, int distance) {
        switch (this.direction) {
            case EAST:map.setExploredMap(this, this.x, this.y + distance + 1, WALL, distance);break;
            case SOUTH:map.setExploredMap(this, this.x - distance - 1, this.y, WALL, distance); break;
            case WEST: map.setExploredMap(this, this.x, this.y - distance - 1, WALL, distance);break;
            case NORTH:map.setExploredMap(this, this.x + distance + 1, this.y, WALL, distance); break;
        }
    }

    void paintRLWall(CoveredMap map, int distance) {
        switch (this.direction) {
            case EAST:map.setExploredMap(this, this.x - 1, this.y + distance + 1, WALL, distance);break;
            case SOUTH:map.setExploredMap(this, this.x - distance - 1, this.y - 1, WALL, distance);break;
            case WEST:map.setExploredMap(this, this.x + 1, this.y - distance - 1, WALL, distance);break;
            case NORTH:map.setExploredMap(this, this.x + distance + 1, this.y + 1, WALL, distance);break;
        }
    }

    void paintFLFree(CoveredMap map, int distance) {
        for (int i = 1; i < distance; i++) {
            switch (this.direction) {
                case EAST:
                    if (x + i + 1 < COLS - 1 && y - 1 > 0) {
                        map.setExploredMap(this, this.x + i + 1, this.y - 1, FREE, i);
                    }
                    break;
                case SOUTH:
                    if (y + i + 1 < ROWS - 1 && x + 1 < COLS - 1) {
                        map.setExploredMap(this, this.x + 1, this.y + i + 1, FREE, i);
                    }
                    break;
                case WEST:
                    if (x - i - 1 > 0 && y + 1 < ROWS - 1) {
                        map.setExploredMap(this, this.x - i - 1, this.y + 1, FREE, i);
                    }
                    break;
                case NORTH:
                    if (y - i - 1 > 0 && x - 1 > 0) {
                        map.setExploredMap(this, this.x - 1, this.y - i - 1, FREE, i);
                    }
                    break;
            }
        }
    }

    void paintFMFree(CoveredMap map, int distance) {
        for (int i = 1; i < distance; i++) {
            switch (this.direction) {
                case EAST:
                    if (x + i + 1 < COLS - 1) 
                        map.setExploredMap(this, this.x + i + 1, this.y, FREE, i);
                    break;
                case SOUTH: //this.x, this.y + distance + 1
                    if (y + i + 1 < ROWS - 1) 
                        map.setExploredMap(this, this.x, this.y + i + 1, FREE, i);
                    break;
                case WEST:
                    if (x - i - 1 > 0) 
                        map.setExploredMap(this, this.x - i - 1, this.y, FREE, i);
                    break;
                case NORTH:
                    if (y - i - 1 > 0) 
                        map.setExploredMap(this, this.x, this.y - i - 1, FREE, i);
                    break;
            }
        }
    }

    void paintFRFree(CoveredMap map, int distance) {
        //Map.setColor()
        for (int i = 1; i < distance; i++) {
            switch (this.direction) {
                case EAST:
                    if (x + i + 1 < COLS - 1 && y + 1 < ROWS - 1) 
                        map.setExploredMap(this, this.x + i + 1, this.y + 1, FREE, i);
                    break;
                case SOUTH:
                    if (x - 1 > 0 && y + i + 1 < ROWS - 1) 
                        map.setExploredMap(this, this.x - 1, this.y + i + 1, FREE, i);
                    break;
                case WEST:
                    if (x - i - 1 > 0 && y - 1 > 0) 
                        map.setExploredMap(this, this.x - i - 1, this.y - 1, FREE, i);
                    break;
                case NORTH:
                    if (x + 1 < COLS - 1 && y - i - 1 > 0) 
                        map.setExploredMap(this, this.x + 1, this.y - i - 1, FREE, i);
                    break;
            }
        }
    }

    void paintLSFree(CoveredMap map, int distance) {
        for (int i = 1; i < distance; i++) {
            switch (this.direction) {
                case EAST:
                    if (x + 1 < COLS - 1 && y - i - 1 > 0) 
                        map.setExploredMap(this, this.x, this.y - i - 1, FREE, i);
                    break;
                case SOUTH:
                    if (x + i + 1 < COLS - 1 && y + 1 < ROWS - 1)
                        map.setExploredMap(this, this.x + i + 1, this.y, FREE, i);
                    break;
                case WEST:
                    if (x - 1 > 0 && y + i + 1 < ROWS - 1) 
                        map.setExploredMap(this, this.x, this.y + i + 1, FREE, i);
                    break;
                case NORTH:
                    if (x - i - 1 > 0 && y - 1 > 0)
                        map.setExploredMap(this, this.x - i - 1, this.y, FREE, i);
                    break;
            }
        }
    }

    void paintRSFree(CoveredMap map, int distance) {
        
        for (int i = 1; i < distance-1; i++) {
            switch (this.direction) {
                case EAST:
                    if (x + 1 < COLS - 1 && y + i + 1 < ROWS - 1) {
                         if (((map.getNewCell())[this.y + 1 + 1][this.x]).getColor() == WALL)
                            isRightChanged = true;
                        map.setExploredMap(this, this.x, this.y + i + 1, FREE, i);
                    }
                    break;
                case SOUTH:
                    if (x - i - 1 > 0 && y + 1 < ROWS - 1) {
                     if (((map.getNewCell())[this.y][this.x -1- 1]).getColor() == WALL)
                            isRightChanged = true;
                        map.setExploredMap(this, this.x - i - 1, this.y, FREE, i);
                    }
                    break;
                case WEST:
                    if (x - 1 > 0 && y - i - 1 > 0)
                       if (((map.getNewCell())[this.y -1- 1][this.x]).getColor() == WALL)
                            isRightChanged = true;
                        map.setExploredMap(this, this.x, this.y - i - 1, FREE, i);
                    break;
                case NORTH:
                    if (x + i + 1 < COLS - 1 && y - 1 > 0){
                        if (((map.getNewCell())[this.y][this.x + 1+ 1]).getColor() == WALL)
                            isRightChanged = true;
                        map.setExploredMap(this, this.x + i + 1, this.y, FREE, i);
                    }
                    break;
            }
        }
    }

    void paintRLFree(CoveredMap map, int distance) {
        for (int i = 1; i < distance; i++) {
            switch (this.direction) {
                case EAST:
                    if (x - 1 > 0 && y - i - 1 > 0)
                        map.setExploredMap(this, this.x - 1, this.y + i + 1, FREE, i);
                    break;
                case SOUTH:
                    if (x + i + 1 < COLS - 1 && y - 1 > 0)
                        map.setExploredMap(this, this.x - i - 1, this.y - 1, FREE, i);
                    break;
                case WEST:
                    if (x + 1 < COLS - 1 && y + i + 1 < ROWS - 1)
                        map.setExploredMap(this, this.x + 1, this.y - i - 1, FREE, i);
                    break;
                case NORTH:
                    if (y + 1 < ROWS - 1 && x - i - 1 > 0)
                        map.setExploredMap(this, this.x + i + 1, this.y + 1, FREE, i);
                    break;
            }
        }
    }

    public void setPreviousAction(int action){
        previousAction = action;
    }

    // Get the next move??
    public int getMovement(CoveredMap map) {
        switch (direction) {
            case NORTH:
                if (previousAction != TURNRIGHT
                        && map.getNewCell()[y - 1][x + 2].getColor() != WALL
                        && map.getNewCell()[y][x + 2].getColor() != WALL
                        && map.getNewCell()[y + 1][x + 2].getColor() != WALL) {
                    previousAction = TURNRIGHT;
                    return TURNRIGHT;
                } else if (map.getNewCell()[y - 2][x - 1].getColor() != WALL
                        && map.getNewCell()[y - 2][x].getColor() != WALL
                        && map.getNewCell()[y - 2][x + 1].getColor() != WALL) {
                    previousAction = MOVEFORWARD;
                    return MOVEFORWARD;
                } else {
                    previousAction = TURNLEFT;
                    return TURNLEFT;
                }

            case EAST:
                if (previousAction != TURNRIGHT
                        && map.getNewCell()[y + 2][x - 1].getColor() != WALL
                        && map.getNewCell()[y + 2][x].getColor() != WALL
                        && map.getNewCell()[y + 2][x + 1].getColor() != WALL) {
                    previousAction = TURNRIGHT;
                    return TURNRIGHT;
                } else if (map.getNewCell()[y - 1][x + 2].getColor() != WALL
                        && map.getNewCell()[y][x + 2].getColor() != WALL
                        && map.getNewCell()[y + 1][x + 2].getColor() != WALL) {
                    previousAction = MOVEFORWARD;
                    return MOVEFORWARD;
                } else {
                    previousAction = TURNLEFT;
                    return TURNLEFT;
                }

            case SOUTH:
                if (previousAction != TURNRIGHT
                        && map.getNewCell()[y - 1][x - 2].getColor() != WALL
                        && map.getNewCell()[y][x - 2].getColor() != WALL
                        && map.getNewCell()[y + 1][x - 2].getColor() != WALL) {
                    previousAction = TURNRIGHT;
                    return TURNRIGHT;
                } else if (map.getNewCell()[y + 2][x - 1].getColor() != WALL
                        && map.getNewCell()[y + 2][x].getColor() != WALL
                        && map.getNewCell()[y + 2][x + 1].getColor() != WALL) {
                    previousAction = MOVEFORWARD;
                    return MOVEFORWARD;
                } else {
                    previousAction = TURNLEFT;
                    return TURNLEFT;
                }

            case WEST:
                if (previousAction != TURNRIGHT
                        && map.getNewCell()[y - 2][x - 1].getColor() != WALL
                        && map.getNewCell()[y - 2][x].getColor() != WALL
                        && map.getNewCell()[y - 2][x + 1].getColor() != WALL) {
                    previousAction = TURNRIGHT;
                    return TURNRIGHT;
                } else if (map.getNewCell()[y - 1][x - 2].getColor() != WALL
                        && map.getNewCell()[y][x - 2].getColor() != WALL
                        && map.getNewCell()[y + 1][x - 2].getColor() != WALL) {
                    previousAction = MOVEFORWARD;
                    return MOVEFORWARD;
                } else {
                    previousAction = TURNLEFT;
                    return TURNLEFT;
                }
        }

        return -1;
    }

    public boolean wallInFront(CoveredMap map) {
        Cell [][]cells = map.getNewCell();
        switch (this.direction) {
            case NORTH:
                if (cells[y - 2][x - 1].getColor() == WALL && cells[y - 2][x].getColor() == WALL
                        && cells[y - 2][x + 1].getColor() == WALL)
                    return true;
                break;
            case SOUTH:
                if (cells[y + 2][x - 1].getColor() == WALL && cells[y + 2][x].getColor() == WALL
                        && cells[y + 2][x + 1].getColor() == WALL)
                    return true;
                break;
            case EAST:
                if (cells[y - 1][x + 2].getColor() == WALL && cells[y][x + 2].getColor() == WALL
                        && cells[y + 1][x + 2].getColor() == WALL)
                    return true;
                break;
            case WEST:
                if (cells[y - 1][x - 2].getColor() == WALL && cells[y][x - 2].getColor() == WALL
                        && cells[y + 1][x - 2].getColor() == WALL)
                    return true;
                break;
        }
        return false;
    }

    public boolean wallLeft(CoveredMap map) {
        Cell [][]cells = map.getNewCell();
        switch (this.direction) {
            case NORTH:
                if (cells[y - 1][x - 2].getColor() == WALL && cells[y][x - 2].getColor() == WALL
                        && cells[y + 1][x - 2].getColor() == WALL)
                    return true;
                break;
            case SOUTH:
                if (cells[y - 1][x + 2].getColor() == WALL && cells[y][x + 2].getColor() == WALL
                        && cells[y + 1][x + 2].getColor() == WALL)
                    return true;
                break;
            case EAST:
                if (cells[y - 2][x - 1].getColor() == WALL && cells[y - 2][x].getColor() == WALL
                        && cells[y - 2][x + 1].getColor() == WALL)
                    return true;
                break;
            case WEST:
                if (cells[y + 2][x - 1].getColor() == WALL && cells[y + 2][x].getColor() == WALL
                        && cells[y + 2][x + 1].getColor() == WALL)
                    return true;
                break;
        }
        return false;
    }

    public boolean wallRight(CoveredMap map) {
        Cell [][]cells = map.getNewCell();
        switch (this.direction) {
            case NORTH:
                if (cells[y - 1][x + 2].getColor() == WALL && cells[y][x + 2].getColor() == WALL
                        && cells[y + 1][x + 2].getColor() == WALL)
                    return true;
                break;
            case SOUTH:
                if (cells[y - 1][x - 2].getColor() == WALL && cells[y][x - 2].getColor() == WALL
                        && cells[y + 1][x - 2].getColor() == WALL)
                    return true;
                break;
            case EAST:
                if (cells[y + 2][x - 1].getColor() == WALL && cells[y + 2][x].getColor() == WALL
                        && cells[y + 2][x + 1].getColor() == WALL)
                    return true;
                break;
            case WEST:
                if (cells[y - 2][x - 1].getColor() == WALL && cells[y - 2][x].getColor() == WALL
                        && cells[y - 2][x + 1].getColor() == WALL)
                    return true;
                break;
        }
        return false;
    }

    public int getTypeAlignment(CoveredMap map) {

        Cell [][]cells = map.getNewCell();
        if (wallInFront(map) && wallRight(map))
            return ALIGNMENT_3;
        switch (this.direction) {
            case NORTH:
                if (cells[y - 2][x - 1].getColor() == WALL && cells[y - 2][x + 1].getColor() == WALL && cells[y - 2][x].getColor() == WALL) return ALIGNMENT_1;
                else if (cells[y - 1][x + 2].getColor() == WALL && cells[y +1][x + 2].getColor() == WALL && cells[y][x + 2].getColor() == WALL) return ALIGNMENT_2;
                break;
            case EAST:
                if (cells[y - 1][x + 2].getColor() == WALL && cells[y + 1][x + 2].getColor() == WALL && cells[y][x + 2].getColor() == WALL) return ALIGNMENT_1;
                else if (cells[y + 2][x - 1].getColor() == WALL && cells[y + 2][x + 1].getColor() == WALL && cells[y + 2][x].getColor() == WALL) return ALIGNMENT_2;
                break;
            case SOUTH:
                if (cells[y + 2][x - 1].getColor() == WALL && cells[y + 2][x + 1].getColor() == WALL && cells[y + 2][x].getColor() == WALL) return ALIGNMENT_1;
                else if (cells[y + 1][x - 2].getColor() == WALL && cells[y - 1][x - 2].getColor() == WALL && cells[y][x - 2].getColor() == WALL) return ALIGNMENT_2;
                break;
            case WEST:
                if (cells[y - 1][x - 2].getColor() == WALL && cells[y + 1][x - 2].getColor() == WALL && cells[y][x - 2].getColor() == WALL) return ALIGNMENT_1;
                else if (cells[y - 2][x - 1].getColor() == WALL && cells[y - 2][x + 1].getColor() == WALL && cells[y - 2][x].getColor() == WALL) return ALIGNMENT_2;
                break;
        }
        return -1;
    }
    
    public boolean isLeftCalibrationAvailable(CoveredMap map) {

        Cell [][]cells = map.getNewCell();
        switch (this.direction) {
            case NORTH:
                if (cells[y - 2][x + 2].getColor() == WALL && cells[y + 2][x + 2].getColor() == WALL && cells[y - 1][x + 2].getColor() == WALL && cells[y + 1][x + 2].getColor() == WALL && cells[y][x + 2].getColor() == WALL) return true;
                break;
            case EAST:
                if (cells[y + 2][x - 2].getColor() == WALL && cells[y + 2][x + 2].getColor() == WALL && cells[y + 2][x - 1].getColor() == WALL && cells[y + 2][x].getColor() == WALL && cells[y + 2][x + 1].getColor() == WALL) return true;
                break;
            case SOUTH:
                if (cells[y - 2][x - 2].getColor() == WALL && cells[y + 2][x - 2].getColor() == WALL && cells[y - 1][x - 2].getColor() == WALL && cells[y + 1][x - 2].getColor() == WALL && cells[y ][x - 2].getColor() == WALL) return true;
                break;
            case WEST:
                if (cells[y - 2][x - 2].getColor() == WALL && cells[y - 2][x + 2].getColor() == WALL && cells[y - 2][x - 1].getColor() == WALL && cells[y - 2][x + 1].getColor() == WALL && cells[y - 2][x].getColor() == WALL) return true;
                break;
        }
        return false;
    }
        
    public void enableCalibration(){
        x_axis_calibration_counter = y_axis_calibration_counter = 999;
    }

    public void updateCalibrationCounter(){
        x_axis_calibration_counter++;
        y_axis_calibration_counter++;
    }

    public int checkCalibration(CoveredMap map) {
        if (x_axis_calibration_counter >= 3
                && ((map.getNewCell()[y - 2][x + 2].getColor() == WALL && map.getNewCell()[y + 2][x + 2].getColor() == WALL &&
                    map.getNewCell()[y][x + 2].getColor() == WALL && map.getNewCell()[y - 1][x + 2].getColor() == WALL && 
                    map.getNewCell()[y + 1][x + 2].getColor() == WALL) 
                    || (map.getNewCell()[y + 2][x + 2].getColor() == WALL &&
                    map.getNewCell()[y][x + 2].getColor() == WALL && map.getNewCell()[y - 1][x + 2].getColor() == WALL && 
                    map.getNewCell()[y + 1][x + 2].getColor() == WALL)
                    || (map.getNewCell()[y - 2][x + 2].getColor() == WALL &&
                         map.getNewCell()[y][x + 2].getColor() == WALL && map.getNewCell()[y - 1][x + 2].getColor() == WALL && 
                         map.getNewCell()[y + 1][x + 2].getColor() == WALL)
                )) {
            x_axis_calibration_counter = 0;
            switch (this.direction) {
                case NORTH:return TURNRIGHT;
                case EAST:return MOVEFORWARD;
                case SOUTH:return TURNLEFT;
                case WEST:return MOVEBACKWARD;
            }
        } else if (x_axis_calibration_counter >= 3
                && ((map.getNewCell()[y - 2][x - 2].getColor() == WALL && map.getNewCell()[y + 2][x - 2].getColor() == WALL &&
                    map.getNewCell()[y][x - 2].getColor() == WALL && map.getNewCell()[y - 1][x - 2].getColor() == WALL && 
                    map.getNewCell()[y + 1][x - 2].getColor() == WALL)
                || (map.getNewCell()[y - 2][x - 2].getColor() == WALL &&
                    map.getNewCell()[y][x - 2].getColor() == WALL && map.getNewCell()[y - 1][x - 2].getColor() == WALL && 
                    map.getNewCell()[y + 1][x - 2].getColor() == WALL)
                || ( map.getNewCell()[y + 2][x - 2].getColor() == WALL &&
                    map.getNewCell()[y][x - 2].getColor() == WALL && map.getNewCell()[y - 1][x - 2].getColor() == WALL && 
                    map.getNewCell()[y + 1][x - 2].getColor() == WALL)
                )){
            x_axis_calibration_counter = 0;
            switch (this.direction) {
                case NORTH:return TURNLEFT;
                case EAST:return MOVEBACKWARD;
                case SOUTH:return TURNRIGHT;
                case WEST:return MOVEFORWARD;
            }
        } else if (y_axis_calibration_counter >= 3
                && ((map.getNewCell()[y - 2][x - 2].getColor() == WALL && map.getNewCell()[y - 2][x + 2].getColor() == WALL &&
                    map.getNewCell()[y - 2][x].getColor() == WALL && map.getNewCell()[y - 2][x - 1].getColor() == WALL &&
                    map.getNewCell()[y - 2][x + 1].getColor() == WALL)
                || ( map.getNewCell()[y - 2][x + 2].getColor() == WALL &&
                    map.getNewCell()[y - 2][x].getColor() == WALL && map.getNewCell()[y - 2][x - 1].getColor() == WALL &&
                    map.getNewCell()[y - 2][x + 1].getColor() == WALL)
                ||(map.getNewCell()[y - 2][x - 2].getColor() == WALL &&
                    map.getNewCell()[y - 2][x].getColor() == WALL && map.getNewCell()[y - 2][x - 1].getColor() == WALL &&
                    map.getNewCell()[y - 2][x + 1].getColor() == WALL)
                
                )) {
            y_axis_calibration_counter = 0;
            switch (this.direction) {
                case NORTH:return MOVEFORWARD;
                case EAST: return TURNLEFT;
                case SOUTH:return MOVEBACKWARD;
                case WEST:return TURNRIGHT;
            }
        } else if (y_axis_calibration_counter >= 3
                 && ((map.getNewCell()[y + 2][x - 2].getColor() == WALL && map.getNewCell()[y + 2][x + 2].getColor() == WALL &&
                    map.getNewCell()[y + 2][x].getColor() == WALL && map.getNewCell()[y + 2][x - 1].getColor() == WALL &&
                    map.getNewCell()[y + 2][x + 1].getColor() == WALL)
                || ( map.getNewCell()[y + 2][x + 2].getColor() == WALL &&
                    map.getNewCell()[y + 2][x].getColor() == WALL && map.getNewCell()[y + 2][x - 1].getColor() == WALL &&
                    map.getNewCell()[y + 2][x + 1].getColor() == WALL)
                || (map.getNewCell()[y + 2][x - 2].getColor() == WALL &&
                    map.getNewCell()[y + 2][x].getColor() == WALL && map.getNewCell()[y + 2][x - 1].getColor() == WALL &&
                    map.getNewCell()[y + 2][x + 1].getColor() == WALL)
                
                )){
            y_axis_calibration_counter = 0;
            switch (this.direction) {
                case NORTH:return MOVEBACKWARD;
                case EAST:return TURNRIGHT;
                case SOUTH:return MOVEFORWARD;
                case WEST:return TURNLEFT;
            }
        }
        return -1;
    }

    void senseAll(CoveredMap map) {
        this.senseFront(map);
        this.senseLeft(map);
        this.senseRight(map);
    }

    public int getDirection() {
        return this.direction;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    public int setDirection(int d) {
        return this.direction = d;
    }

    public boolean getPreviousLeftWall() {
        return previousLeftWall;
    }

    public int getXC() {
        return (this.x) * 33 + 15 + 33;
    }
    public int getYC() {
        return (this.y) * 33 + 15 + 33;
    }

    public void setPreviousLeftWall(boolean b) {
        this.previousLeftWall = b;
    }

    public String getDirectionString() {
        switch (this.direction) {
            case EAST:
                return "east";
            case SOUTH:
                return "south";
            case WEST:
                return "west";
            case NORTH:
                return "north";
            default:
                return null;
        }
    }

    public int[][] getMyExposedMap() {
        return myExposedMap;
    }

    public boolean[][] getMyAStarMap() {
        return myAStarMap;
    }

    public boolean[][] getMyAStarMap2() {
        return myAStarMap2;
    }

    public ArrayList<Integer> getWaypoint() {
        return ptArr;
    }

    public ArrayList<Integer> getPresetWayPt() {
        return preSetWayPtArr;
    }

    public int getMaxSensorRange() {
        int max1 = sensorF > sensorR ? sensorF : sensorR;
        return max1 > sensorL ? max1 : sensorL;
    }

    public void setRemoveUnuseWpIndex(int index) {
        removeUnuseWpIndex = index;
    }

    public int getRemoveUnuseWpIndex() {
        return removeUnuseWpIndex;
    }

    public int getStartLoc() {
        return startLoc;
    }

    public int getPercentCellCovered() {
        return percentCellCovered;
    }

    public void increNumCellCovered() {
        numCellCovered++;
        percentCellCovered = (int) (numCellCovered / 300.0 * 100);
    }
}