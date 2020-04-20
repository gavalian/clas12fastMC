/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.an.abs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.clas12.an.base.DetectorEvent;
import org.jlab.clas12.an.base.EventModifier;
import org.jlab.jnp.detector.base.Detector;
import org.jlab.jnp.geom.prim.Mesh3D;
import org.jlab.jnp.geom.prim.Path3D;

/**
 *
 * @author gavalian
 */
public class DetectorFiducialCuts implements EventModifier {
    
    private Mesh3D detectorMesh = null;
    private int    particleID   = 11;
    private Map<Integer,ParticleFiducialConfig> configs = new HashMap<>();
    
    public DetectorFiducialCuts(){
        
    }

    @Override
    public boolean apply(DetectorEvent detEvent) {
        int     count = detEvent.count();
        Path3D   path = new Path3D();        
        for(int i = 0; i < count; i++){
            int pid = detEvent.getPid(i);
            //System.out.println(" pid = " + pid + " has it : " + configs.containsKey(pid));
            if(configs.containsKey(pid)==true){
                detEvent.getPath(path, i);
                //System.out.println("PASS : " + configs.get(pid).pass(path));
                if(configs.get(pid).pass(path)==true){
                    detEvent.setStatus(i, 1);
                } else {
                    detEvent.setStatus(i, -1);
                }
            }
        }
        return true;
    }
    
    public DetectorFiducialCuts addConfig(ParticleFiducialConfig config){
        configs.put(config.getPid(), config); return this;
    }
    
    public static class ParticleFiducialConfig {
        
        private int particleID = 11;
        private List<Detector>  detectors = new ArrayList<>();

        public ParticleFiducialConfig(int pid ){
            this.particleID = pid;
        }
        
        public int getPid(){ return this.particleID;}
        
        public void addDetector(Detector d){
            detectors.add(d);
        }
        
        public boolean pass(Path3D path){
            for(Detector d : detectors){
                if(d.hasLayerHits(path)==false) return false;
            }
            return true;
        }
    }
}
