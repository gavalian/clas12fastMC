package org.jlab.clas12.fastMC.tests;

import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.detectors.DCDetector;
import org.jlab.clas12.fastMC.detectors.ECDetector;
import org.jlab.clas12.fastMC.detectors.FTDetector;
import org.jlab.clas12.fastMC.detectors.FToFDetector;
import org.jlab.clas12.fastMC.swimmer.ParticleSwimmer;
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.ParticleList;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.DataManager;
import org.jlab.jnp.reader.LundReader;

import java.util.List;

public class Debug {

    
    public  Debug(){
    }
    
    private static void dcTest() {
        List<String> dataFiles = FileFinder.getFiles("/media/tylerviducic/Elements/clas12/mcdata/rho_mc/*.hipo");
        //String dataFile = "";
        System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");
        
        H2F hSquare = new H2F("hSquare", "hSquare", 200, -450, 450, 200, -450, 450);
        TCanvas c1 = new TCanvas("c1", 500, 500);

        DCDetector dcDetector = new DCDetector();
        ParticleSwimmer swimmer = new ParticleSwimmer();

        int eventCounter = 0;

        for(String dataFile: dataFiles) {
            HipoReader reader = new HipoReader();
            reader.open(dataFile);

            Bank particles = new Bank(reader.getSchemaFactory().getSchema("mc::event"));
            Event event = new Event();

            while (reader.hasNext()) {
                eventCounter++;
                reader.nextEvent(event);
                event.read(particles);

                PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particles);

                ParticleList particleList = physicsEvent.getParticleList();
                for (int i = 0; i < particleList.count(); i++) {
                    Path3D particlePath = swimmer.getParticlePath(particleList.get(i));
                    List<DetectorHit> hits = dcDetector.getHits(particlePath);
                    if (hits.size() > 0) {
                        System.out.println("has hits");
                        for (DetectorHit hit : hits) {
                            hSquare.fill(hit.getHitPosition().x(), hit.getHitPosition().y());
                        }
                    }
                }
//                if (eventCounter > 100000) {
//                    break;
//                }
            }
        }
        c1.draw(hSquare);
    }

    private static void ecTest() {
        List<String> dataFiles = FileFinder.getFiles("/media/tylerviducic/Elements/clas12/mcdata/rho_mc/*.hipo");
        //String dataFile = "";
        System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");

        H2F hSquare = new H2F("hSquare", "hSquare", 200, -450, 450, 200, -450, 450);
        TCanvas c1 = new TCanvas("c1", 500, 500);

        ECDetector ecDetector = new ECDetector();
        ParticleSwimmer swimmer = new ParticleSwimmer();

        int eventCounter = 0;

        for(String dataFile: dataFiles) {
            HipoReader reader = new HipoReader();
            reader.open(dataFile);

            Bank particles = new Bank(reader.getSchemaFactory().getSchema("mc::event"));
            Event event = new Event();

            while (reader.hasNext()) {
                eventCounter++;
                reader.nextEvent(event);
                event.read(particles);

                PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particles);

                ParticleList particleList = physicsEvent.getParticleList();
                for (int i = 0; i < particleList.count(); i++) {
                    Path3D particlePath = swimmer.getParticlePath(particleList.get(i));
                    List<DetectorHit> hits = ecDetector.getHits(particlePath);
                    if (hits.size() > 0) {
                        System.out.println("has hits");
                        for (DetectorHit hit : hits) {
                            hSquare.fill(hit.getHitPosition().x(), hit.getHitPosition().y());
                        }
                    }
                }
//                if (eventCounter > 100000) {
//                    break;
//                }
            }
        }
        c1.draw(hSquare);
    }

    private static void ftofTest() {
        List<String> dataFiles = FileFinder.getFiles("/media/tylerviducic/Elements/clas12/mcdata/rho_mc/*.hipo");
        //String dataFile = "";
        System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");

        H2F hSquare = new H2F("hSquare", "hSquare", 200, -450, 450, 200, -450, 450);
        TCanvas c1 = new TCanvas("c1", 500, 500);

        FToFDetector ftofDetector = new FToFDetector();
        ParticleSwimmer swimmer = new ParticleSwimmer();

        int eventCounter = 0;

        for(String dataFile: dataFiles) {
            HipoReader reader = new HipoReader();
            reader.open(dataFile);

            Bank particles = new Bank(reader.getSchemaFactory().getSchema("mc::event"));
            Event event = new Event();

            while (reader.hasNext()) {
                eventCounter++;
                reader.nextEvent(event);
                event.read(particles);

                PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particles);

                ParticleList particleList = physicsEvent.getParticleList();
                for (int i = 0; i < particleList.count(); i++) {
                    Path3D particlePath = swimmer.getParticlePath(particleList.get(i));
                    List<DetectorHit> hits = ftofDetector.getHits(particlePath);
                    if (hits.size() > 0) {
                        System.out.println("has hits");
                        for (DetectorHit hit : hits) {
                            hSquare.fill(hit.getHitPosition().x(), hit.getHitPosition().y());
                        }
                    }
                }
//                if (eventCounter > 100000) {
//                    break;
//                }
            }
        }
        c1.draw(hSquare);
    }

    private static void ftTest() {
        List<String> dataFiles = FileFinder.getFiles("/media/tylerviducic/Elements/clas12/mcdata/rho_mc/*.hipo");
        //String dataFile = "";
        System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");

        H2F hSquare = new H2F("hSquare", "hSquare", 50, -50, 50, 500, -50, 50);
        TCanvas c1 = new TCanvas("c1", 500, 500);

        FTDetector ftDetector = new FTDetector();
        ParticleSwimmer swimmer = new ParticleSwimmer();

        int eventCounter = 0;

        for(String dataFile: dataFiles) {
            HipoReader reader = new HipoReader();
            reader.open(dataFile);

            Bank particles = new Bank(reader.getSchemaFactory().getSchema("mc::event"));
            Event event = new Event();

            while (reader.hasNext()) {
                eventCounter++;
                reader.nextEvent(event);
                event.read(particles);

                PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particles);

                ParticleList particleList = physicsEvent.getParticleList();
                for (int i = 0; i < particleList.count(); i++) {
                    Path3D particlePath = swimmer.getParticlePath(particleList.get(i));
                    List<DetectorHit> hits = ftDetector.getHits(particlePath);
                    if (hits.size() > 0) {
                        System.out.println("has hits");
                        for (DetectorHit hit : hits) {
                            hSquare.fill(hit.getHitPosition().x(), hit.getHitPosition().y());
                        }
                    }
                }
//                if (eventCounter > 100000) {
//                    break;
//                }
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
        System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");
//        particleSwimmerTest();
        dcTest();
        ecTest();
        ftofTest();;
        ftTest();
    }

}

