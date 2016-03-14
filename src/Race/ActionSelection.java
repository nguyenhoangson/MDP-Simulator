/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Race;

import Map.CoveredMap;
import Robot.MDPRobot;

/**
 *
 * @author TQN
 */

// TODO: Make the mapping more proper

public class ActionSelection {
    static public int dataReliablilty = 1;
    static public int []distancesFromSensor = new int[5];

    public static void senseAll(CoveredMap map, MDPRobot robot, String str) {
        String[] sensorData = str.split(",");
        for (int i = 0; i < sensorData.length; i++) {
            int dist = Integer.parseInt(sensorData[i]);
            distancesFromSensor[i] = dist;
        }
        //dataReliablilty = distancesFromSensor[6];
        robot.senseFR(map, distancesFromSensor[0]);
        robot.senseFL(map, distancesFromSensor[1]);
        robot.senseFM(map, distancesFromSensor[2]);
        robot.senseLS(map, distancesFromSensor[3]);
        robot.senseRU(map, distancesFromSensor[4]);
        //robot.senseRL(map, distancesFromSensor[5]);
        
    }

}
