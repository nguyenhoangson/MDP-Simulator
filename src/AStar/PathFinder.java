/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AStar;

/**
 *
 * @author Tan Quang Ngo
 */
import java.util.*;
import static Map.MapData.COLS;
import static Map.MapData.ROWS;
import Map.RealMap;

import static Robot.RobotData.EAST;
import static Robot.RobotData.WEST;
import static Robot.RobotData.NORTH;
import static Robot.RobotData.SOUTH;

/*
 * Example.
 */
public class PathFinder extends A_Star<PathFinder.Node> {

    private int[][] map;

    public static class Node {

        public int x;
        public int y;

        public int preferDirection;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
            preferDirection = 0;
        }

        Node(int x, int y, int dir) {
            this.x = x;
            this.y = y;
            preferDirection = dir;
        }

        public String toString() {
            return "(" + y + ", " + x + ") ";
        }
    }
    public static Node newNode(int x, int y){
        return new Node(x,y);
    }
    public PathFinder(boolean[][] map, int sy, int sx, int ey, int ex) {
        int[][] map_ = new int[ROWS - 2][COLS - 2];
        _start = new Node(sx, sy);
        _goal = new Node(ex, ey);

        for (int i = 0; i < ROWS - 2; i++) {
            for (int j = 0; j < COLS - 2; j++) {
                map_[i][j] = isPathFree(map, i, j) ? 1 : 0; // 0 means free
            }
        }
        this.map = map_;
//        for (int i = 0; i < Map.length; i++) {
//            for (int j = 0; j < Map[0].length; j++) {
//                System.out.print(map_[i][j] + " ");
//            }
//            System.out.println();
     //   }
    }

    public PathFinder(RealMap realMap, int sy, int sx, int ey, int ex) {
        int[][] map = new int[ROWS][COLS];
        _start = new Node(sx, sy);
        _goal = new Node(ex, ey);

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                map[i][j] = realMap.isWalkAble(i, j) ? 1 : 0; // 0 means free
            }
        }
        this.map = map;

//        for (int i = 0; i < Map.length; i++) {
//            for (int j = 0; j < Map[0].length; j++) {
//                System.out.print(Map[i][j] + " ");
//            }
//            System.out.println();
//        }
    }

    // check surrounding input must not add eage coordinate
    static public boolean isPathFree(boolean[][] map, int i, int j) {
        if (i == 0 || j == 0 || i == ROWS - 3 || j == COLS - 3) {
            return false;
        }
        return map[i][j] && map[i + 1][j] && map[i - 1][j] && map[i][j + 1] && map[i][j - 1] && map[i + 1][j + 1] && map[i + 1][j - 1] && map[i - 1][j + 1] && map[i - 1][j - 1];

    }

    protected boolean isGoal(Node node) {
        return _goal.x == node.x && _goal.y == node.y;
    }

    protected Double g(Node from, Node to) {

        if (from.x == to.x && from.y == to.y) {
            return 0.0;
        }

        if (map[to.y][to.x] == 1) {
            if (from.y > to.y && from.preferDirection == NORTH) {
                return 1.0;
            }
            if (from.y < to.y && from.preferDirection == SOUTH) {
                return 1.0;
            }

            if (from.x > to.x && from.preferDirection == WEST) {
                return 1.0;
            }
            if (from.x < to.x && from.preferDirection == EAST) {
                return 1.0;
            }

            return 5.0;
        }

        return Double.MAX_VALUE;
    }

    protected Double h(Node from, Node to) {
        /* Use the Manhattan distance heuristic.  */
        return new Double(Math.abs(_goal.x - to.x) + Math.abs(_goal.y - to.y));
    }

    protected List<Node> generateSuccessors(Node node) {
        List<Node> ret = new LinkedList<Node>();
        int x = node.x;
        int y = node.y;
        
       if (y > 0 && map[y - 1][x] == 1) {
            ret.add(new Node(x, y - 1, SOUTH));
        }
        if (x > 0 && map[y][x - 1] == 1) {
            ret.add(new Node(x - 1, y, WEST));
        }
       if (x < map[0].length - 1 && map[y][x + 1] == 1) {
            ret.add(new Node(x + 1, y, EAST));
        }
        if (y < map.length - 1 && map[y + 1][x] == 1) {
            ret.add(new Node(x, y + 1, NORTH));
        }
    

        return ret;
    }

}
