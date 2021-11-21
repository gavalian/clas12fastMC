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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.clas12.fastMC.base.DetectorType;
import org.jlab.clas12.fastMC.core.Clas12Region.DetectorRegionConfig;
import org.jlab.clas12.fastMC.resolution.ElectronResolution;
import org.jlab.clas12.fastMC.resolution.EventResolution;
import org.jlab.clas12.fastMC.resolution.ProtonResolution;

import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;

/**
 *
 * @author gavalian
 * @author viducic
 */
public class Clas12FastMC {

    //private ArrayList<Detector> detectors;
    private ParticleSwimmer particleSwimmer = null;
    private Map<Integer,Clas12Region>  detectorConfigs = new HashMap<>();
    private Map<DetectorType,Detector>    detectors = new HashMap<>();
    private EventResolution               resolutions = new EventResolution();
    private boolean                  applyResolutions = false;
    private List<DetectorHit>       eventDetectorHits = new ArrayList<>();
    
    public Clas12FastMC(){
        //detectors = new ArrayList<>();
/*        this.addDetector(new DCDetector());
        this.addDetector(new ECDetector());
        this.addDetector(new FToFDetector());
        this.addDetector(new FTDetector());
        this.addDetector(new CVTDetector());*/
        detectors.put(DetectorType.DC, new DCDetector());
        detectors.put(DetectorType.ECAL, new ECDetector());
        detectors.put(DetectorType.FTOF, new FToFDetector());
        detectors.put(DetectorType.FT, new FTDetector());
        detectors.put(DetectorType.CVT, new CVTDetector());
        initSwimmer(-1.0,-1.0);
    }
    
    public void setResolution(boolean flag){
        this.applyResolutions = flag;
    }
    
    public EventResolution getEventResolution(){
        return this.resolutions;
    }
    
    public void initResolutions(){
        resolutions.addResolution(  11, DetectorRegion.FORWARD, new ElectronResolution());
        resolutions.addResolution( 211, DetectorRegion.FORWARD, new ElectronResolution());
        resolutions.addResolution(-211, DetectorRegion.FORWARD, new ElectronResolution());
        resolutions.addResolution(2212, DetectorRegion.FORWARD, new ProtonResolution());
    }
    
    public void addConfiguration(int pid, DetectorRegion region, Detector detector, int hits){
        if(detectorConfigs.containsKey(pid)==false){
            detectorConfigs.put(pid, new Clas12Region(pid));
        }
        
        detectorConfigs.get(pid).addConfiguration(region, detector, hits);
        //System.out.println("");
    }
    
    public void addConfiguration(int pid, DetectorRegion region, String detector, int hits){
        DetectorType type =  DetectorType.getType(detector);
        System.out.println(">>>>>>>>> CLAS12 FAST MC : " + type);
        addConfiguration(pid,region,detectors.get(type),hits);
    }
    
    /*public void addConfiguration(int pid, String config){
        String[] tokens = config.split(":");
        for(int i = 0; i < tokens.length; i+=2){
            addConfiguration(pid,tokens[i],Integer.parseInt(tokens[i+1]));
        }
    }*/

    public Clas12FastMC(double torusField, double solenoidField){
        initSwimmer(torusField, solenoidField);
    }
    
    private void initSwimmer(double torusField, double solenoidField){
        particleSwimmer = new ParticleSwimmer(torusField, solenoidField);
    }

   /* public void addDetector(Detector detector){
        this.detectors.add(detector);
    }*/
    
    public void show(){
        System.out.printf(">>>>>>>>>>>>>>>>>>>> %s <<<<<<<<<<<<<<<<<<<<\n","FAST MC CONFIGURATION BEGIN");
        Collection<Clas12Region> configs = detectorConfigs.values();
        for(Clas12Region config: configs){
            System.out.println(config.toString());
        }
        System.out.printf(">>>>>>>>>>>>>>>>>>>> %s <<<<<<<<<<<<<<<<<<<<\n","FAST MC CONFIGURATION  END ");
    }
    
    public DetectorRegion getRegion(Particle part){
        
        int pid = part.pid();
        
        if(detectorConfigs.containsKey(pid)==false) return DetectorRegion.UNDEFINED;

        Path3D path = particleSwimmer.getParticlePath(part);
        //System.out.println(" passed this part for pid = " + pid);
        //path.show();
        
        Clas12Region region = detectorConfigs.get(pid);
        return region.getStatus(path);
    }
    
    public DetectorRegion getRegion(Path3D path, int pid){
        if(detectorConfigs.containsKey(pid)==false) return DetectorRegion.UNDEFINED;
        Clas12Region region = detectorConfigs.get(pid);
        return region.getStatus(path);
    }
    
    public Path3D getPath(Particle part){
        if(detectorConfigs.containsKey(part.pid())==false) return new Path3D();
        return particleSwimmer.getParticlePath(part);
    }
    
    public List<DetectorHit> getDetectorHits(){return this.eventDetectorHits;};
    
    public PhysicsEvent processEvent(PhysicsEvent event){
        PhysicsEvent fastMCevent = new PhysicsEvent();
        fastMCevent.beamParticle().copy(event.beamParticle());
        fastMCevent.targetParticle().copy(event.targetParticle());
        
        eventDetectorHits.clear();
        
        int count = event.count();
        int accepted = 0;
        
        for(int i = 0; i < count; i++){
            Particle part = event.getParticle(i);
            Path3D   path = this.getPath(part);
            DetectorRegion region = this.getRegion(path, part.pid());
            //gg-DetectorRegion region = getRegion(part);
            //System.out.println(part.toLundString());
            //System.out.println("region = " + region);
            if(region!=DetectorRegion.UNDEFINED){
                Particle pnew = Particle.copyFrom(part);
                Clas12Region rconfig = detectorConfigs.get(part.pid());
                List<DetectorHit> hits = rconfig.getHits(path);
                DetectorHit.setIndex(hits, accepted);accepted++;
                //System.out.printf("particle : %d\n",pnew.pid());
                //for(DetectorHit h : hits){
                //    System.out.println(h);
                //}
                this.eventDetectorHits.addAll(hits);
                if(applyResolutions==true){
                    resolutions.applyResolution(pnew, region);
                }
                fastMCevent.addParticle(pnew);
            }
        }
        return fastMCevent;
    }
    
    public void processEvent(PhysicsEvent event, FastMCResponse response){
        response.physicsEvent.clear();

        response.physicsEvent.beamParticle().copy(event.beamParticle());
        response.physicsEvent.targetParticle().copy(event.targetParticle());
        
        response.detectorHits.clear();
        
        int count = event.count();
        int accepted = 0;
        
        for(int i = 0; i < count; i++){
            Particle part = event.getParticle(i);
            Path3D   path = this.getPath(part);
            DetectorRegion region = this.getRegion(path, part.pid());
            //gg-DetectorRegion region = getRegion(part);
            //System.out.println(part.toLundString());
            //System.out.println("region = " + region);
            if(region!=DetectorRegion.UNDEFINED){
                Particle pnew = Particle.copyFrom(part);
                Clas12Region rconfig = detectorConfigs.get(part.pid());
                List<DetectorHit> hits = rconfig.getHits(path);
                DetectorHit.setIndex(hits, accepted);accepted++;
                //System.out.printf("particle : %d\n",pnew.pid());
                //for(DetectorHit h : hits){
                //    System.out.println(h);
                //}
                response.detectorHits.addAll(hits);
                if(applyResolutions==true){
                    resolutions.applyResolution(pnew, region);
                }
                response.physicsEvent.addParticle(pnew);
            }
        }
    }
    
    
    public boolean validHit(Particle part){        
        DetectorRegion region = getRegion(part);
        if(region == DetectorRegion.UNDEFINED) return false;
        return true;
    }

    public Path3D  swim(Particle part){
        return this.particleSwimmer.getParticlePath(part);
    }
    
    public boolean validHitByPid(Particle particle){
        /*
        Path3D particlePath = particleSwimmer.getParticlePath(particle);
        
        
        
        boolean status = validHit(particle);
        
        
        
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
                    if(detectors.get(4).validHit(particlePath)){
                        validHit = false;
                    }
                }
            default:
                if (detectors.get(0).validHit(particlePath) || detectors.get(4).validHit(particlePath)){
                    validHit = true;
                }
                break;
        }
        return validHit;*/
        return true;
    }
    
    public static class FastMCResponse {
        public List<DetectorHit> detectorHits = new ArrayList<>();
        public PhysicsEvent      physicsEvent = new PhysicsEvent();
    }
       
}
