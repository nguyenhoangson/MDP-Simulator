/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Robot;


/**
 *
 * @author TQN
 */

public interface SensorData {

    float DATA_VARITATION = 0.25f; // 0.2 means (-20% to +20%)
    int VARITATION = 3;           // 3 mean when obstacle at 10, the accepting range is (7 to 13)
    
    int FLLB = 3;
    int FLUB = 35;
    int FMLB = 3;
    int FMUB = 35;
    int FRLB = 3;
    int FRUB = 35;
    int LSLB = 3;
    int LSUB = 35;
    int RSLB = 3;
    int RSUB = 45;
    int RLLB = 3;
    int RLUB = 35;
}
