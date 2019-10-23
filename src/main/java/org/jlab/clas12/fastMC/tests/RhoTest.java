package org.jlab.clas12.fastMC.tests;

import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.clas12.fastMC.core.Clas12FastMC;
import org.jlab.clas12.fastMC.tools.EventAcceptance;
import org.jlab.clas12.fastMC.tools.ParticleHisto;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.DataManager;

import java.time.Duration;
import java.time.Instant;

public class RhoTest {
    public static void main(String[] args) {
        System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField/");
        String directory = "/media/tylerviducic/Elements/clas12/mcdata/rho_mc";
        int eventCounter = 0;
        int rhoCounter = 0;

        HipoChain reader = new HipoChain();
        reader.addDir(directory);
        reader.open();

        Clas12FastMC clas12FastMC = new Clas12FastMC();
        // Different ways you can detect proton
        clas12FastMC.addConfiguration(2212, DetectorRegion.CENTRAL,  "CVT", 3);
        // This is proton in forward detector
        clas12FastMC.addConfiguration(2212, DetectorRegion.FORWARD,   "DC", 6);
        clas12FastMC.addConfiguration(2212, DetectorRegion.FORWARD, "FTOF", 1);
        // photon is only detected in EC
        clas12FastMC.addConfiguration(22, DetectorRegion.FORWARD,   "ECAL", 1);
        clas12FastMC.addConfiguration(22, DetectorRegion.TAGGER,   "FT", 1);
        //electron different regions
        clas12FastMC.addConfiguration(11, DetectorRegion.TAGGER, "FT", 1);
        clas12FastMC.addConfiguration(11, DetectorRegion.FORWARD, "FTOF", 1);
        clas12FastMC.addConfiguration(11, DetectorRegion.FORWARD, "DC", 6);
        clas12FastMC.addConfiguration(11, DetectorRegion.FORWARD, "ECAL", 1);
        //pions
        clas12FastMC.addConfiguration(211, DetectorRegion.FORWARD,   "DC", 6);
        clas12FastMC.addConfiguration(211, DetectorRegion.FORWARD, "FTOF", 1);
        clas12FastMC.addConfiguration(-211, DetectorRegion.FORWARD,   "DC", 6);
        clas12FastMC.addConfiguration(-211, DetectorRegion.FORWARD, "FTOF", 1);

        EventAcceptance eventAcceptance = new EventAcceptance();
        EventFilter filter = new EventFilter("11:2212:211:-211:22");

        Event event = new Event();
        Bank mcParticle = new Bank(reader.getSchemaFactory().getSchema("mc::event"));

        ParticleHisto pHisto = new ParticleHisto();
        ParticleHisto eHisto = new ParticleHisto();
        ParticleHisto gamHisto = new ParticleHisto();
        ParticleHisto pipHisto = new ParticleHisto();
        ParticleHisto pimHisto = new ParticleHisto();

        Instant start = Instant.now();
        while(reader.hasNext() && eventCounter < 100000){
            eventCounter++;
            if(eventCounter%10000 == 0){
                System.out.println("event count: " + eventCounter);
            }
            reader.nextEvent(event);
            event.read(mcParticle);

            PhysicsEvent mcEvent = DataManager.getPhysicsEvent(10.6, mcParticle);
            PhysicsEvent fastMCEvent = clas12FastMC.processEvent(mcEvent);
            if(filter.isValid(fastMCEvent)){
                rhoCounter++;
                if(rhoCounter%10 == 0) {
                    System.out.println("rho count: " + rhoCounter);
                }
                Particle p = fastMCEvent.getParticle("[2212]");
                Particle e = fastMCEvent.getParticle("[11]");
                Particle pip = fastMCEvent.getParticle("[211]");
                Particle pim = fastMCEvent.getParticle("[-211]");
                Particle gam = fastMCEvent.getParticle("[22]");

                pHisto.fill(p);
                eHisto.fill(e);
                gamHisto.fill(gam);
                pipHisto.fill(pip);
                pimHisto.fill(pim);
            }
        }

        Instant finish = Instant.now();

        TCanvas c1 = new TCanvas("e", 500, 500);
        TCanvas c2 = new TCanvas("p", 500, 500);
        TCanvas c3 = new TCanvas("gam", 500, 500);
        TCanvas c4 = new TCanvas("pip", 500, 500);
        TCanvas c5 = new TCanvas("pim", 500, 500);

        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("time to process 100k events: " + (timeElapsed * 1000) + " seconds");
        System.out.println("Time(ms)/event = " + timeElapsed/100000);
        c1.divide(2,2);
        c2.divide(2,2);
        c3.divide(2,2);
        c4.divide(2,2);
        c5.divide(2,2);

        eHisto.draw(c1);
        pHisto.draw(c2);
        gamHisto.draw(c3);
        pipHisto.draw(c4);
        pimHisto.draw(c5);
    }
}
