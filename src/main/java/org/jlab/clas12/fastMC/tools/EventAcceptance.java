/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.tools;

import java.util.HashMap;
import java.util.Map;
import org.jlab.jnp.physics.PhysicsEvent;

/**
 *
 * @author gavalian
 */
public class EventAcceptance {
    
    private Map<Integer,ParticleAcceptance>    gemcAcceptance = new HashMap<Integer,ParticleAcceptance>();
    private Map<Integer,ParticleAcceptance>  fastmcAcceptance = new HashMap<Integer,ParticleAcceptance>();
    
    public EventAcceptance(){
        gemcAcceptance.put(11, new ParticleAcceptance());
        gemcAcceptance.put(2212, new ParticleAcceptance());
        gemcAcceptance.put(211, new ParticleAcceptance());
        gemcAcceptance.put(-211, new ParticleAcceptance());
        gemcAcceptance.put(22, new ParticleAcceptance());
        
        fastmcAcceptance.put(11, new ParticleAcceptance());
        fastmcAcceptance.put(2212, new ParticleAcceptance());
        fastmcAcceptance.put(211, new ParticleAcceptance());
        fastmcAcceptance.put(-211, new ParticleAcceptance());
        fastmcAcceptance.put(22, new ParticleAcceptance());        
    }
    
    public void acceptanceGemc(PhysicsEvent genEvent, PhysicsEvent recEvent){
        for(Map.Entry<Integer,ParticleAcceptance> entry : gemcAcceptance.entrySet()){
            entry.getValue().acceptance(entry.getKey(), genEvent, recEvent);
        }
    }
    
    public void acceptanceFastmc(PhysicsEvent genEvent, PhysicsEvent recEvent){
        for(Map.Entry<Integer,ParticleAcceptance> entry : fastmcAcceptance.entrySet()){
            entry.getValue().acceptance(entry.getKey(), genEvent, recEvent);
        }
    }


    public Map<Integer, ParticleAcceptance> getGemcAcceptance() {
        return gemcAcceptance;
    }

    public Map<Integer, ParticleAcceptance> getFastmcAcceptance() {
        return fastmcAcceptance;
    }
}
