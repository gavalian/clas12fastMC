/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.core;

import java.util.List;
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
    
    public Clas12Region(){
        
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
            if(detHits.size()>acceptedHits) return true;
            return false;
        }
    }
}
