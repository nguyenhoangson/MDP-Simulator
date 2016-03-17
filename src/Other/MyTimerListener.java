package Other;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author TQN
 */

public class MyTimerListener implements ActionListener {

    private int count = 0;

    private JLabel label;

    public MyTimerListener(JLabel label) {
        this.label = label;

    }

    public void setTimer(String count) {
        String[] parts = count.split(":");
        this.count = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    public void setTimer(int Seconds) {
        this.count = Seconds;
    }

    public int getTimer() {
        return count;
    }

    public void actionPerformed(ActionEvent e) {
        count--;
        label.setText("Count Down: " + count + "s");
    }
}
