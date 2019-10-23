/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.resolution;

import java.util.HashMap;
import java.util.Map;
import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.ParticleModifier;

/**
 *
 * @author gavalian
 */
public class EventResolution {
    
    private Map<Integer,EventResolutionConfig> eventRes = new HashMap<>();
    
    public EventResolution(){
        
    }
        
    public void addResolution(int pid, DetectorRegion region, ParticleModifier modifier){
        if(eventRes.containsKey(pid)==false){
            eventRes.put(pid, new EventResolutionConfig());
        }
        eventRes.get(pid).addConfig(region, modifier);
    }
    
    public void applyResolution(Particle p, DetectorRegion region){
        int pid = p.pid();
        //System.out.println(" applying resolution " + pid + " --> " + eventRes.containsKey(pid));
        if(eventRes.containsKey(pid)==true){
            EventResolutionConfig config = eventRes.get(pid);
            if(config.hasRegion(region)==true){
                config.getModifier(region).modify(p);
            }
        }
    }
    
    public static class EventResolutionConfig {
        
        Map<DetectorRegion,ParticleModifier>  resolutions = new HashMap<>();
        
        public EventResolutionConfig(){
            
        }
        
        public void addConfig(DetectorRegion region, ParticleModifier modifier){
            resolutions.put(region, modifier);
        }
        
        public boolean hasRegion(DetectorRegion region){
            return resolutions.containsKey(region);
        }
        
        public ParticleModifier getModifier(DetectorRegion region){
            return resolutions.get(region);
        }
    }
}
