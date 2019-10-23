/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.tools;

import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;

/**
 *
 * @author gavalian
 */
public class ParticleAcceptance {
    
    private ParticleHisto histGenerated = new ParticleHisto();
    private ParticleHisto histReconstructed = new ParticleHisto();
    private double        matchCut          = 0.9995;
    
    public ParticleAcceptance(){
        
    }

    public ParticleHisto getHistGenerated() {
        return histGenerated;
    }

    public ParticleHisto getHistReconstructed() {
        return histReconstructed;
    }

    public void setMatchCut(double cut){ matchCut = cut; }
    
    public void acceptance(int pid, PhysicsEvent genEvent, PhysicsEvent recEvent){
        int count = genEvent.countByPid(pid);
        
        for(int i = 0; i < count; i++){
            Particle p = genEvent.getParticleByPid(pid, i);
            if(pid != 211 && pid != -211 && p.p() >= 0.5 ){
            histGenerated.fill(p);
            this.acceptance(p, recEvent);
            }
            else if(p.p() >= 1.0) {
                histGenerated.fill(p);
                this.acceptance(p, recEvent);
            }
        }
    }
    
    public void acceptance(Particle p , PhysicsEvent recEvent){
        Particle match = recEvent.getBestMatch(p, p.charge());
        if(match.cosTheta(p)>=matchCut && match.getStatus() > 0){
            histReconstructed.fill(match);
        }
    }
}
