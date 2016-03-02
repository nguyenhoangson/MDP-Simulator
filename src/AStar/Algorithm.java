package AStar;

import java.util.ArrayList;
import java.util.List;
import static Map.MapData.COLS;
import static Map.MapData.ROWS;

public abstract class Algorithm {
//	MapDescriptor mapDescriptor;

    protected Node[][] node = new Node[ROWS][COLS];

    int sx, sy, ex, ey;
    protected int[][] map;

    public Algorithm(boolean[][] map, int _sy, int _sx, int _ey, int _ex) {
        int[][] map_ = new int[ROWS - 2][COLS - 2];

        sx = _sx;
        sy = _sy;
        ex = _ex;
        ey = _ey;

        for (int i = 0; i < ROWS - 2; i++) {
            for (int j = 0; j < COLS - 2; j++) {
                map_[i][j] = isPathFree(map, i, j) ? 1 : 0; // 0 means free
            }
        }
        this.map = map_;
    }

    // check surrounding input must not add eage coordinate
    static public boolean isPathFree(boolean[][] map, int i, int j) {
        if (i == 0 || j == 0 || i == ROWS - 3 || j == COLS - 3) {
            return false;
        }
        return map[i][j] && map[i + 1][j] && map[i - 1][j] && map[i][j + 1] && map[i][j - 1] && map[i + 1][j + 1] && map[i + 1][j - 1] && map[i - 1][j + 1] && map[i - 1][j - 1];

    }

    public void setGoal(int x, int y) {
        ex = x;
        ey = y;
    }

    public void setStart(int x, int y) {
        sx = x;
        sy = y;
    }

    public abstract void initNode();

    public abstract List<PathFinder.Node> compute();

    public Node AStar() {
        ArrayList<Node> closedList = new ArrayList<Node>();
        SortedNodeList openList = new SortedNodeList();

        node[ex][ey].d = 0;
        node[ex][ey].t = node[ex][ey].d + node[ex][ey].t;
        openList.add(node[ex][ey]);

        while (openList.size() != 0) {
            Node cur = openList.getFirst();
            if (cur.x == sx && cur.y == sy) {
                return cur;
            }

	    	 //System.out.println("way : " + cur.x + " " + cur.y + " " + cur.d + " " + cur.h);
            openList.remove(cur);
            closedList.add(cur);

            for (Node neighbor : cur.neighbor) {
                boolean isBetter = false;
                int neighborDist = cur.d + 1;
                
                if (closedList.contains(neighbor)) {
                    continue;
                }
                if (!openList.contains(neighbor)) {
                    openList.add(neighbor);
                    isBetter = true;
                } else if (neighborDist < neighbor.d) {
                    isBetter = true;
                } else {
                    isBetter = false;
                }

                if (isBetter) {
                    if(neighbor.getX() != cur.getX()) neighbor.preferDirection = 1;
                    if(neighbor.getY() != cur.getY()) neighbor.preferDirection = 2;
                    if(neighbor.preferDirection != cur.preferDirection)
                        neighborDist++;
                    
                    neighbor.setBacktrackNode(cur);
                    neighbor.d = neighborDist;
                    neighbor.t = neighbor.d + neighbor.h;
                }
            }
        }
        return null;
    }

    /*	public Node AStar(){
     ArrayList<Node> closedList =  new ArrayList<Node>();
     ArrayList<Node> openList = new ArrayList<Node>();
     int lastMoveX=0;
     int lastMoveY=0;
     int lastMoveX2=0;
     int lastMoveY2=0;
     int lastMoveX3=0;
     int lastMoveY3=0;
		 
     node[ex][ey].d = 0;
     node[ex][ey].t = node[ex][ey].d + node[ex][ey].t;
     openList.add(node[ex][ey]);
	     
     while(openList.size() != 0) {
     Collections.sort(openList);
     Node cur = null;
     int shortest = openList.get(0).t;
     for(int i = 1; i< openList.size(); i++){
     if(openList.get(i).backtrackNode != null && openList.get(i).backtrackNode.backtrackNode != null){
     lastMoveX = openList.get(i).backtrackNode.backtrackNode.x;
     lastMoveY = openList.get(i).backtrackNode.backtrackNode.y;
     }
     if(openList.get(i).backtrackNode != null){
     lastMoveX2 = openList.get(i).backtrackNode.x;
     lastMoveY2 = openList.get(i).backtrackNode.y;
     }
     lastMoveX3 = openList.get(i).x;
     lastMoveY3 = openList.get(i).y;
	    		 
     // if(openList.get(i).t <= shortest){
     if(lastMoveX!=0 && lastMoveX2 !=0 && lastMoveX3 != 0){
     if(lastMoveY3 == lastMoveY && lastMoveY == lastMoveY2){
     cur = openList.remove(i);
     break;
     }
     else if(lastMoveX3 == lastMoveX && lastMoveX == lastMoveX2){
     cur = openList.remove(i);
     break;
     }
     }
     // }
     }
     if(cur == null){
     for(int i = 1; i< openList.size(); i++){
     if(openList.get(i).backtrackNode != null && openList.get(i).backtrackNode.backtrackNode != null){
     lastMoveX = openList.get(i).backtrackNode.backtrackNode.x;
     lastMoveY = openList.get(i).backtrackNode.backtrackNode.y;
     }
     if(openList.get(i).backtrackNode != null){
     lastMoveX2 = openList.get(i).backtrackNode.x;
     lastMoveY2 = openList.get(i).backtrackNode.y;
     }
     lastMoveX3 = openList.get(i).x;
     lastMoveY3 = openList.get(i).y;
		    		 
     // if(openList.get(i).t <= shortest){
     if(lastMoveX!=0 && lastMoveX2 !=0 && lastMoveX3 != 0){
     if(lastMoveY2 == lastMoveY3 && lastMoveX2 == lastMoveX){
     cur = openList.remove(i);
     break;
     }else if(lastMoveX2 == lastMoveX3 && lastMoveY2 == lastMoveY){
     cur = openList.remove(i);
     break;
     }
     }
     // }
     }
     }
			 
     if(cur == null)
     cur = openList.remove(0);
	    	 
     if(cur.x == sx && cur.y == sy) {
     return cur ;
     }
	    	 
     closedList.add(cur);
           
     for(Node neighbor : cur.neighbor) {
     boolean isBetter = false;
     int neighborDist = cur.d + 1;
     if (closedList.contains(neighbor))
     continue;
     if(!openList.contains(neighbor)) {
     openList.add(neighbor);
     isBetter = true;
     } else if(neighborDist < neighbor.d) {
     isBetter = true;
     } else {
     isBetter = false;
     }
	    		 
     if (isBetter){
     neighbor.setBacktrackNode(cur);
     neighbor.d = neighborDist;
     neighbor.t = neighbor.d + neighbor.h;
     }
     }
     }
     return null;
     }*/
}
