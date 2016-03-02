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
public class ActionSelection {
    static public int dataReliablilty = 0;
    static public int []distancesFromSensor = new int[8];
    public static void senseAll(CoveredMap map, MDPRobot robot, String str) {
        String[] sensorData = str.split(",");
         for (int i = 0; i < sensorData.length; i++) {
            int dist = Integer.parseInt(sensorData[i]);
            distancesFromSensor[i] = dist;
        }
        dataReliablilty = distancesFromSensor[6];
        robot.senseTL(map, distancesFromSensor[0]);
        robot.senseTM(map, distancesFromSensor[1]);
        robot.senseTR(map, distancesFromSensor[2]);
        robot.senseFL(map, distancesFromSensor[3]);
        robot.senseFR(map, distancesFromSensor[4]);
        robot.senseLR(map, distancesFromSensor[5]);
        
    }

//    public static int go(CoveredMap Map, MDPRobot Robot) {
//        return Robot.getMovement(Map);
//    }
}
