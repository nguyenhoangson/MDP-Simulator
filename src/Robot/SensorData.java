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
    
    int TLLB = 3;
    int TLUB = 35;
    int TMLB = 3;
    int TMUB = 35;
    int TRLB = 3;
    int TRUB = 35;
    int FLLB = 3;
    int FLUB = 35;
    int FRLB = 3;
    int FRUB = 45;
    int LRLB = 3;
    int LRUB = 35;
}
