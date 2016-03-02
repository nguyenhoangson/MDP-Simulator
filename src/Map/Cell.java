package Map;

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.event.MouseAdapter;

public final class Cell extends JPanel implements MapData {

    private static final long serialVersionUID = 1L;
    private int row;
    private int col;
    private Color color;
    private boolean explored = false;
    private RealMap map;
    public float Explored_From = 9999;

    //this will keep track of the path
    private String pathStatus = "notPassed";

     public Cell(final int row, final int col) {
        this.row = row;
        this.col = col;
     }
    public Cell(final int row, final int col, RealMap map_) {
        this.row = row;
        this.col = col;
        map = map_;
        BuildUpDefaultCell();
        if (color == START) {
            RealMap.robPosX = col;
            RealMap.robPosY = row;
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JFrame frame = null;

                if (RealMap.isChangeStartPos == true && map.isWalkAble(row, col)) {
                    map.setStartPos(row, col);
                } else if (color != START_GOAL_ZONE && color != START) {
                    SetOrRemoveWall();
                } else {
                    if (color == START) {
                        RealMap.isChangeStartPos = true;
                    } else {
                        JOptionPane.showMessageDialog(frame, "Cannot put walls in start or goal area ! ");
                    }

                }
                
                
                
            }
        });
    }

    public Cell(final int row, final int col, char obj, RealMap map_) {
        this.row = row;
        this.col = col;
        map = map_;

        setBackground(getColor(obj));
        this.setColor(getColor(obj));

        if (IsVisableZone()) {
            this.setExploredACell(true);
            if (color == START) {
                RealMap.robPosX = col;
                RealMap.robPosY = row;
            }
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                
                JFrame frame = null;
                 if (RealMap.isChangeStartPos == true && map.isWalkAble(row, col)) {
                    map.setStartPos(row, col);
                } else if (color != START_GOAL_ZONE && color != START) {
                    SetOrRemoveWall();
                } else {
                    if (color == START) {
                        RealMap.isChangeStartPos = true;
                    } else {
                        JOptionPane.showMessageDialog(frame, "Cannot put walls in start or goal area ! ");
                    }

                }
                
                
            }
        });
    }

    private void SetOrRemoveWall() {
        if (getColor() == WALL && !IsBoarder()) {
            setBackground(FREE);
            setColor(FREE);
        } else {
            setBackground(WALL);
            setColor(WALL);
        }
    }

    private void BuildUpDefaultCell() {
        //set up walls
        if (this.IsBoarder()) {
            this.setBackground(WALL);
            this.setColor(WALL);
        } //set up start & goal
        else if (this.IsGoal() || ((19 - 2 < this.row && this.row < 19 + 2)
                && (2 - 2 < this.col && this.col < 2 + 2))) {

            this.setBackground(START_GOAL_ZONE);
            this.setColor(START_GOAL_ZONE);
            if (2 == this.col && 19 == this.row) {
                this.setBackground(START);
                this.setColor(START);
            }

            this.setExploredACell(true);
        } //set up free space
        else {
            this.setBackground(FREE);
            this.setColor(FREE);
        }
    }

    public boolean IsBoarder() {
        if (this.row == 0 || this.row == ROWS - 1 || this.col == COLS - 1 || this.col == 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean IsGoal() {
        return ((0 < this.row && this.row < 4) && (COLS - 5 < this.col && this.col < COLS - 1));
    }

    public boolean IsVisableZone() {
        return color == START || color == START_GOAL_ZONE;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean getExplored() {
        return this.explored;
    }

    public void setExploredACell( boolean trueORFalse) {
        this.explored = trueORFalse;
    }

    public int getXC() {
        return (this.col) * 33 + 15;
    }

    public int getYC() {
        return (this.row) * 33 + 15;
    }

    public String getPathStatus() {
        return this.pathStatus;
    }

    public void setPathStatus(String s) {
        this.pathStatus = s;
    }

    public char getContent() {

        if (color == WALL) {
            return 'w';
        }
        if (color == FREE) {
            return 'f';
        }
        if (color == START_GOAL_ZONE) {
            return 'z';
        }
        if (color == START) {
            return 's';
        }
        if (color == UNEXPLORED) {
            return 'u';
        }
        if (color == ROBOTF) {
            return 'r';
        }
        if (color == ROBOTB) {
            return 'R';
        }
        if (color == PATH) {
            return 'p';
        }
        return 'f';
    }

    public static Color getColor(char c) {

        if (c == 'w') {
            return WALL;
        }
        if (c == 'f') {
            return FREE;
        }
        if (c == 'z') {
            return START_GOAL_ZONE;
        }
        if (c == 's') {
            return START;
        }
        if (c == 'u') {
            return UNEXPLORED;
        }
        if (c == 'r') {
            return ROBOTF;
        }
        if (c == 'R') {
            return ROBOTB;
        }
        if (c == 'p') {
            return PATH;
        }

        return WALL;
    }
}
