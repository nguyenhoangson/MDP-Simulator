package AStar;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Node implements Comparable<Node>{
    public int preferDirection;
    
	int x;
	int y;
	ArrayList<Node> neighbor = new ArrayList<Node>();
	Node backtrackNode;
	
	boolean obstacle = false;
	boolean visited = false;
	
	public int h;
	public int d;
	public int t;
	
	public void setBacktrackNode(Node n){
		backtrackNode = n;
	}
	public Node backtrack(){
		return backtrackNode;
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	
	public Node(int x, int y, boolean obstacle, int sx, int sy, int ex, int ey){
        preferDirection = 0;
        
        this.x = x;
        this.y = y;
        this.obstacle = obstacle;
        
        d = Integer.MAX_VALUE;
        //h = Math.abs(sx - x) + Math.abs(sy - y);
        h = 0;
        //h = Integer.MAX_VALUE;
        t = d + h;
	}
	
	public boolean isObstacle(){
		return obstacle;
	}
	
	public void addNeighbor(Node node){
		if(!neighbor.contains(node)){
			neighbor.add(node);
			node.addNeighbor(this);
		}
	}

	
    public int compareTo(Node n) {
        if (t < n.t) {
                return -1;
        } else if (t > n.t) {
                return 1;
        } else {
                return 0;
        }
}
}
