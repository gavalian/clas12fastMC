/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.clas12.fastMC.base.Detector;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.jnp.geom.prim.Path3D;

/**
 *
 * @author gavalian
 */
public class Clas12Region {
    
    private DetectorRegion detectorRegion = DetectorRegion.UNDEFINED;
    private final Map<DetectorRegion,List<DetectorRegionConfig>> pidConfigs = 
            new HashMap<DetectorRegion,List<DetectorRegionConfig>>();
    
    private int particleID = 11;
    
    public Clas12Region(int pid){
        particleID = pid;
    }
    
    public int getPid(){
        return particleID;
    }
    
    public void addConfiguration(DetectorRegion region, Detector detector, int hits){
        if(pidConfigs.containsKey(region)==false){
            pidConfigs.put(region, new ArrayList<DetectorRegionConfig>());
        }
        pidConfigs.get(region).add(new DetectorRegionConfig(detector,hits));
    }
    
    private boolean getStatus(Path3D path, List<DetectorRegionConfig> config){
        for(DetectorRegionConfig rc : config){
            if(rc.validate(path)==false) return false;
        }
        return true;
    }
    
    public DetectorRegion getStatus(Path3D path){        
        for(Map.Entry<DetectorRegion,List<DetectorRegionConfig>> entry : pidConfigs.entrySet()){
            boolean status = getStatus(path,entry.getValue());
            if(status==true) return entry.getKey();
        }
        return DetectorRegion.UNDEFINED;
    }
    
    public static class DetectorRegionConfig {
        
        private Detector     detector = null;
        private int      acceptedHits = 0;
        public DetectorRegionConfig(Detector det, int hits){
            detector = det;
            acceptedHits = hits;
        }
        
        public boolean validate(Path3D path){
            List<DetectorHit> detHits = detector.getHits(path);
            if(detHits.size()>=acceptedHits) return true;
            return false;
        }
    }
}
