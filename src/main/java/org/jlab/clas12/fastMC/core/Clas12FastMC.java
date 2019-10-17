/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.core;

import org.jlab.clas12.fastMC.base.Detector;
import org.jlab.clas12.fastMC.detectors.DCDetector;
import org.jlab.clas12.fastMC.detectors.ECDetector;
import org.jlab.clas12.fastMC.detectors.FTDetector;
import org.jlab.clas12.fastMC.detectors.FToFDetector;
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
        initSwimmer(-1.0,1.0);
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
    
    public PhysicsEvent processEvent(PhysicsEvent physEvent){
        PhysicsEvent fastMCEvent = new PhysicsEvent();
        int count = physEvent.count();
        for(int i = 0; i < count; i++){
            Particle p = physEvent.getParticle(i);
            if(this.validHit(p)==true){
                fastMCEvent.addParticle(p);
            }
        }
        return fastMCEvent;
    }
}
