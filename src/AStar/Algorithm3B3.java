package AStar;
import java.util.LinkedList;
import java.util.List;
import static Map.MapData.COLS;
import static Map.MapData.ROWS;


public class Algorithm3B3 extends Algorithm{
	
	public Algorithm3B3(boolean[][] _map, int _sy, int _sx, int _ey, int _ex){
		super( _map,  _sx,  _sy,  _ex,  _ey);
	}
	
        @Override
	public void initNode(){
		for(int i = 0; i < ROWS-2; i++)
			for(int j = 0; j < COLS-2; j ++){
				Node n;
                                if(map[i][j]==0){
					n = new Node(i,j,true,sx,sy,ex,ey);
				}
                                else{
					n = new Node(i,j,false,sx,sy,ex,ey);
					if(i > 0){
						if(!node[i-1][j].isObstacle()){
							n.addNeighbor(node[i-1][j]);
						}
					}
					if(j > 0){
						if(!node[i][j-1].isObstacle()){
							n.addNeighbor(node[i][j-1]);
						}
					}
					
				}
				node[i][j] = n;
			}
	}

    @Override
    public List<PathFinder.Node> compute() {
        initNode();
        List<PathFinder.Node> path = new LinkedList<PathFinder.Node>();
         Node n = AStar();
        while(n!=null){
            path.add(PathFinder.newNode(n.getY(),n.getX()));
            n=n.backtrack();
        }
            return path;
    }


   
	
	

}