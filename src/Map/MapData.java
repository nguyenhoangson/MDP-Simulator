package Map;

import java.awt.Color;

public interface MapData {

    // size of the arena
    int COLS = 17;
    int ROWS = 22;

    //color of the wall
    Color WALL = Color.BLACK;

    //color of the free area
    Color FREE = Color.WHITE;
    
    Color TO_BE_CONFIRMED = Color.LIGHT_GRAY;

    //color of the start & goal
    Color START_GOAL_ZONE =  Color.YELLOW;
    Color START =  Color.ORANGE;

    //color of the unexplored area
    Color UNEXPLORED = Color.GRAY;

    //color of the Robot front
    Color ROBOTF = Color.BLUE;

    //color of the Robot back
    Color ROBOTB = Color.GREEN;

    //color of the path
    Color PATH = Color.BLUE;
}
