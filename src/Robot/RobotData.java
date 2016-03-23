package Robot;

public interface RobotData {
	//the 4 directions that the Robot could face
	int EAST = 1;
	int SOUTH = 2;
	int WEST = 3;
	int NORTH = 4;
        
    int MOVEFORWARD = 1;
    int TURNLEFT = 2;
    int TURNRIGHT = 3;
    int MOVEBACKWARD = 4;
    int ALIGNMENT = 5;
    int START_FASTER_PATH = 6;
        
    int DIRECTION_TO_BASH_FOUND = 7;
        
    int ALIGNMENT_1 = 1; // front alignment
    int ALIGNMENT_2 = 2; // right alignment
    int ALIGNMENT_3 = 3; // corner alignment
    int ALIGNMENT_4 = 4; // left alighnment
        

}
