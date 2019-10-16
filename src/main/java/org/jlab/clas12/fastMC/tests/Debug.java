package org.jlab.clas12.fastMC.tests;

import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.detectors.DCDetector;
import org.jlab.clas12.fastMC.detectors.ECDetector;
import org.jlab.clas12.fastMC.detectors.FTDetector;
import org.jlab.clas12.fastMC.swimmer.ParticleSwimmer;
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.ParticleList;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.LundReader;

import java.util.List;

public class Debug {

    
    public  Debug(){
        
    }
    
    private static void DCTest() {
        //List<String> dataFiles = FileFinder.getFiles("/media/tylerviducic/Elements/clas12/mcdata/*.dat");
        String dataFile = "";
        System.setProperty("JNP_DATA","/Users/gavalian/Work/DataSpace/JNP_DATA");
        
        H2F hSquare = new H2F("hSquare", "hSquare", 200, -450, 450, 200, -450, 450);
        TCanvas c1 = new TCanvas("c1", 500, 500);

        DCDetector dcDetector = new DCDetector();
        ParticleSwimmer swimmer = new ParticleSwimmer();

        int eventCounter = 0;

        System.out.println(dataFile);
        LundReader reader = new LundReader();
        reader.acceptStatus(1);
        reader.addFile(dataFile);
        reader.open();

        PhysicsEvent event = new PhysicsEvent();

        while (reader.nextEvent(event)) {
            eventCounter++;
            System.out.println(event.toLundString());
            event.setBeamParticle(new Particle(11, 0, 0, 11));
            event.setTargetParticle(new Particle(2212, 0, 0, 0));

            ParticleList particles = event.getParticleList();
            for (int i = 0; i < particles.count(); i++) {
                Path3D particlePath = swimmer.getParticlePath(particles.get(i));
                List<DetectorHit> hits = dcDetector.getHits(particlePath);
                if (hits.size() > 0) {
                    System.out.println("has hits");
                    for (DetectorHit hit : hits) {
                        hSquare.fill(hit.getHitPosition().x(), hit.getHitPosition().y());
                    }
                }
            }
            if (eventCounter > 100000) {
                break;
            }
        }
        c1.draw(hSquare);
    }


    private static void ECTest() {
        String dataFile = "";
        //System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");
        H2F hSquare = new H2F("hSquare", "hSquare",200, -450, 450, 200, -450, 450);
        TCanvas c1 = new TCanvas("c1", 500, 500);

        ECDetector ecDetector = new ECDetector();
        ParticleSwimmer swimmer = new ParticleSwimmer();

        int eventCounter = 0;

            System.out.println(dataFile);
            LundReader reader = new LundReader();
            reader.acceptStatus(1);
            reader.addFile(dataFile);
            reader.open();

            PhysicsEvent event = new PhysicsEvent();

            while (reader.nextEvent(event)) {
                eventCounter++;
                System.out.println(event.toLundString());
                event.setBeamParticle(new Particle(11, 0, 0, 11));
                event.setTargetParticle(new Particle(2212, 0, 0, 0));

                ParticleList particles = event.getParticleList();
                for(int i = 0; i < particles.count(); i++){
                    Path3D particlePath = swimmer.getParticlePath(particles.get(i));
                    List<DetectorHit> hits = ecDetector.getHits(particlePath);
                    if (hits.size() > 0){
                        System.out.println("has hits");
                        for (DetectorHit hit: hits){
                            hSquare.fill(hit.getHitPosition().x(), hit.getHitPosition().y());
                        }
                    }
                }
                if (eventCounter > 100000){
                    break;
                }
        }
        c1.draw(hSquare);
    }
    private static void FToFTest(){

        String dataFile = "";
        H2F hSquare = new H2F("hSquare", "hSquare",200, -450, 450, 200, -450, 450);
        TCanvas c1 = new TCanvas("c1", 500, 500);

        ECDetector ecDetector = new ECDetector();
        ParticleSwimmer swimmer = new ParticleSwimmer();

        int eventCounter = 0;

            System.out.println(dataFile);
            LundReader reader = new LundReader();
            reader.acceptStatus(1);
            reader.addFile(dataFile);
            reader.open();

            PhysicsEvent event = new PhysicsEvent();

            while (reader.nextEvent(event)) {
                eventCounter++;
                System.out.println(event.toLundString());
                event.setBeamParticle(new Particle(11, 0, 0, 11));
                event.setTargetParticle(new Particle(2212, 0, 0, 0));

                ParticleList particles = event.getParticleList();
                for(int i = 0; i < particles.count(); i++){
                    Path3D particlePath = swimmer.getParticlePath(particles.get(i));
                    List<DetectorHit> hits = ecDetector.getHits(particlePath);
                    if (hits.size() > 0){
                        System.out.println("has hits");
                        for (DetectorHit hit: hits){
                            hSquare.fill(hit.getHitPosition().x(), hit.getHitPosition().y());
                        }
                    }
                }
                if (eventCounter > 100000){
                    break;
                }
            }

        c1.draw(hSquare);
    }

    private static void FTTest(){
        String dataFile = "";

        H2F hSquare = new H2F("hSquare", "hSquare",100, -75, 75, 100, -75, 75);
        TCanvas c1 = new TCanvas("c1", 500, 500);

        FTDetector ftDetector = new FTDetector();
        ParticleSwimmer swimmer = new ParticleSwimmer();

        int eventCounter = 0;

            System.out.println(dataFile);
            LundReader reader = new LundReader();
            reader.acceptStatus(1);
            reader.addFile(dataFile);
            reader.open();

            PhysicsEvent event = new PhysicsEvent();

            while (reader.nextEvent(event)) {
                eventCounter++;
                System.out.println(event.toLundString());
                event.setBeamParticle(new Particle(11, 0, 0, 11));
                event.setTargetParticle(new Particle(2212, 0, 0, 0));

                ParticleList particles = event.getParticleList();
                for(int i = 0; i < particles.count(); i++){
                    Path3D particlePath = swimmer.getParticlePath(particles.get(i));
                    List<DetectorHit> hits = ftDetector.getHits(particlePath);
                    if (hits.size() > 0){
                        for (DetectorHit hit: hits){
                            hSquare.fill(hit.getHitPosition().x(), hit.getHitPosition().y());
                        }
                    }
                }
                if (eventCounter > 100000){
                    break;
                }
            }
        c1.draw(hSquare);

    }

    private static void particleSwimmerTest(){
        Particle charged = Particle.createWithMassCharge(1, 1, 0.1, 0.0, 0.1, 0, 0, 0.5);
        Particle uncharged = Particle.createWithMassCharge(1, 0, 0.0, 0.0, 0.1, 0, 0, 0.5);
        Particle negcharged = Particle.createWithMassCharge(1, -1, 0.1, 0.0, 0.1, 0, 0, 0.5);

        System.out.println(negcharged.charge());

        ParticleSwimmer particleSwimmer = new ParticleSwimmer();

        Path3D chargedPath = particleSwimmer.getParticlePath(charged);
        Path3D unchargedPath = particleSwimmer.getParticlePath(uncharged);
        Path3D negchargedPath = particleSwimmer.getParticlePath(negcharged);

        chargedPath.show();
        //unchargedPath.show();
        negchargedPath.show();
    }

    public static void main(String[] args) {
        //System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");
        //particleSwimmerTest();
        
        System.setProperty("JNP_DATA","/Users/gavalian/Work/DataSpace/JNP_DATA");
        ParticleSwimmer swimmer = new ParticleSwimmer();
        
    }

}

