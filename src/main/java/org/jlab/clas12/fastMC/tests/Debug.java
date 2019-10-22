package org.jlab.clas12.fastMC.tests;

import org.jlab.clas12.fastMC.base.Detector;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.detectors.*;
import org.jlab.clas12.fastMC.swimmer.ParticleSwimmer;
import org.jlab.clas12.fastMC.tools.FileFinder;
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

    private static void cvtTest(){
        List<String> dataFiles = FileFinder.getFiles("/media/tylerviducic/Elements/clas12/mcdata/rho_mc/*.hipo");
        //String dataFile = "";
        System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");

            H2F hSquare = new H2F("hSquare", "hSquare", 50, -50, 50, 500, -50, 50);
        TCanvas c1 = new TCanvas("c1", 500, 500);

        CVTDetector cvtDetector = new CVTDetector();
        ParticleSwimmer swimmer = new ParticleSwimmer();

        int eventCounter = 0;

        for(String dataFile: dataFiles) {
            HipoReader reader = new HipoReader();
            reader.open(dataFile);

            Bank particles = new Bank(reader.getSchemaFactory().getSchema("mc::event"));
            Event event = new Event();

            while (reader.hasNext()) {
                eventCounter++;
                System.out.println(eventCounter);
                reader.nextEvent(event);
                event.read(particles);

                PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particles);

                ParticleList particleList = physicsEvent.getParticleList();
                for (int i = 0; i < particleList.count(); i++) {
                    Path3D particlePath = swimmer.getParticlePath(particleList.get(i));
                    List<DetectorHit> hits = cvtDetector.getHits(particlePath);
                    if (hits.size() > 0) {
                        for (DetectorHit hit : hits) {
                            hSquare.fill(hit.getHitPosition().x(), hit.getHitPosition().y());
                        }
                    }
                }
                if (eventCounter > 100000) {
                    break;
                }
            }
        }
        c1.draw(hSquare);
    }

    private static void cvtTest2(){

        CVTDetector cvtDetector = new CVTDetector();
        ParticleSwimmer swimmer = new ParticleSwimmer();
        int hits = 0;

        H2F thetaPhi = new H2F("thetaPhi", 90, 0, 180, 180, -180, 180);
        TCanvas c1 = new TCanvas("c1", 500, 500);

        for(int i = 0; i < 100000; i++){
            //System.out.println(i);
            Particle proton = Particle.random(2212, 1.0, 3.0, Math.toRadians(35.0), Math.toRadians(170.0), Math.toRadians(-180.0), Math.toRadians(180.0));
            Path3D protonPath = swimmer.getParticlePath(proton);
            if(cvtDetector.validHit(protonPath)){
                hits++;
                thetaPhi.fill(Math.toDegrees(proton.theta()), Math.toDegrees(proton.phi()));
            }
        }
        System.out.println("Number of hits: " + hits);
        System.out.println("Percentage of particles detected: " + (((double)hits/100000.0)*100.0));
        c1.draw(thetaPhi);
    }

    public static void main(String[] args) {
        System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");
//        particleSwimmerTest();
//        dcTest();
//        ecTest();
//        ftofTest();;
//        ftTest();
        cvtTest2();
    }
}

