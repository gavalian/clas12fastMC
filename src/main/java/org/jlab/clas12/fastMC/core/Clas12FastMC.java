/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.core;

import org.jlab.clas12.fastMC.base.Detector;
import org.jlab.clas12.fastMC.detectors.*;
import org.jlab.clas12.fastMC.swimmer.ParticleSwimmer;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.physics.Particle;

import java.util.ArrayList;
import java.util.Iterator;
import org.jlab.jnp.physics.PhysicsEvent;

/**
 *
 * @author gavalian
 * @author viducic
 */
public class Clas12FastMC {

    private ArrayList<Detector> detectors;
    private ParticleSwimmer particleSwimmer = null;
    
    public Clas12FastMC(){
        detectors = new ArrayList<>();
        this.addDetector(new DCDetector());
        this.addDetector(new ECDetector());
        this.addDetector(new FToFDetector());
        this.addDetector(new FTDetector());
        this.addDetector(new CVTDetector());
        initSwimmer(-1.0,-1.0);
    }
    
    public Clas12FastMC(double torusField, double solenoidField){
        initSwimmer(torusField, solenoidField);
    }
    
    private void initSwimmer(double torusField, double solenoidField){
        particleSwimmer = new ParticleSwimmer(torusField, solenoidField);
    }

    public void addDetector(Detector detector){
        this.detectors.add(detector);
    }

    public boolean validHit(Particle part){
        Iterator<Detector> detectors = this.detectors.iterator();
        while (detectors.hasNext()){
            Detector currentDetector = detectors.next();
            Path3D particlePath = particleSwimmer.getParticlePath(part);
            if(!currentDetector.validHit(particlePath)){
                return false;
            }
        }
        return true;
    }

    public boolean validHitByPid(Particle particle){
        Path3D particlePath = particleSwimmer.getParticlePath(particle);
        boolean validHit = false;
        switch (particle.pid()){
            case 11:
                if ((detectors.get(0).validHit(particlePath) && detectors.get(2).validHit(particlePath)) || detectors.get(3).validHit(particlePath) || detectors.get(4).validHit(particlePath)){
//                if (detectors.get(3).validHit(particlePath)){
                    validHit = true;
                }
                break;
            case 22:
                if(detectors.get(1).validHit(particlePath) && particle.p() >=1 && particle.p() <= 9 && Math.toDegrees(particle.theta())>= 10 && Math.toDegrees(particle.theta())<= 35){
                    validHit = true;
                }
            default:
                if (detectors.get(0).validHit(particlePath) || detectors.get(4).validHit(particlePath)){
                    validHit = true;
                }
                break;
        }
        return validHit;
    }
    
    public PhysicsEvent processEvent(PhysicsEvent physEvent){
        PhysicsEvent fastMCEvent = new PhysicsEvent();
        int count = physEvent.count();
        for(int i = 0; i < count; i++){
            Particle p = physEvent.getParticle(i);
            if(this.validHitByPid(p)){
                fastMCEvent.addParticle(p);
            }
        }
        return fastMCEvent;
    }
}
