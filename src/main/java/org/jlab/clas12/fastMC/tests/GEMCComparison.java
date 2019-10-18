package org.jlab.clas12.fastMC.tests;


import org.jlab.clas12.fastMC.core.Clas12FastMC;
import org.jlab.clas12.fastMC.swimmer.ParticleSwimmer;
import org.jlab.clas12.fastMC.tools.EventAcceptance;
import org.jlab.clas12.fastMC.tools.FileFinder;
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.DataManager;

import java.util.List;

public class GEMCComparison {

    public GEMCComparison(){}

    public static void main(String[] args) {

        H2F ThetaPhi = new H2F("thetaphi", 90, 0, 180, 180, -180, 180);

        List<String> dataFiles = FileFinder.getFiles("/home/tylerviducic/research/rho/clas12/data/*");
        System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");

        EventAcceptance eventAcceptance = new EventAcceptance();
        Clas12FastMC clas12FastMC = new Clas12FastMC();
        int eventCounter = 0;
        EventFilter filter = new EventFilter("11:X+:X-:Xn");

        for(String dataFile: dataFiles){
            System.out.println(dataFile);
            HipoReader reader = new HipoReader();
            reader.open(dataFile);

            Event event = new Event();
            Bank mcParticle = new Bank(reader.getSchemaFactory().getSchema("MC::Particle"));
            Bank recParticle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

            while (reader.hasNext()){
                reader.nextEvent(event);
                event.read(recParticle);
                event.read(mcParticle);

                eventCounter++;
                System.out.println(eventCounter);
                PhysicsEvent mcEvent = DataManager.getPhysicsEvent(10.6, mcParticle);
                PhysicsEvent recEvent = DataManager.getPhysicsEvent(10.6, recParticle);
                PhysicsEvent fastmcEvent = clas12FastMC.processEvent(mcEvent);

                System.out.println("-----------------------------------------------------------------------");
                System.out.println(mcEvent.toLundString());
                System.out.println(recEvent.toLundString());
                System.out.println(fastmcEvent.toLundString());
                if(filter.isValid(recEvent) && filter.isValid(fastmcEvent)) {
                    eventAcceptance.acceptanceGemc(mcEvent, recEvent);
                    eventAcceptance.acceptanceFastmc(mcEvent, fastmcEvent);
                }
            }
        }

        TCanvas c1 = new TCanvas("fastMC electron", 1000, 1500);
        c1.divide(2, 2);
        TCanvas c2 = new TCanvas("gemc electron", 1000, 1500);
        c2.divide(2, 2);

        TCanvas c3 = new TCanvas("fastMC photon", 1000, 1500);
        c3.divide(2, 2);
        TCanvas c4 = new TCanvas("gemc photon", 1000, 1500);
        c4.divide(2, 2);

        TCanvas c5 = new TCanvas("fastMC proton", 1000, 1500);
        c5.divide(2, 2);
        TCanvas c6 = new TCanvas("gemc proton", 1000, 1500);
        c6.divide(2, 2);

        TCanvas c7 = new TCanvas("fastMC pi+", 1000, 1500);
        c7.divide(2, 2);
        TCanvas c8 = new TCanvas("gemc pi+", 1000, 1500);
        c8.divide(2, 2);

        TCanvas c9 = new TCanvas("fastMC pi-", 1000, 1500);
        c9.divide(2, 2);
        TCanvas c10 = new TCanvas("gemc pi-", 1000, 1500);
        c10.divide(2, 2);

        eventAcceptance.getFastmcAcceptance().get(11).getHistReconstructed().draw(c1);
        eventAcceptance.getGemcAcceptance().get(11).getHistReconstructed().draw(c2);
        eventAcceptance.getFastmcAcceptance().get(22).getHistReconstructed().draw(c3);
        eventAcceptance.getGemcAcceptance().get(22).getHistReconstructed().draw(c4);
        eventAcceptance.getFastmcAcceptance().get(2212).getHistReconstructed().draw(c5);
        eventAcceptance.getGemcAcceptance().get(2212).getHistReconstructed().draw(c6);
        eventAcceptance.getFastmcAcceptance().get(211).getHistReconstructed().draw(c7);
        eventAcceptance.getGemcAcceptance().get(211).getHistReconstructed().draw(c8);
        eventAcceptance.getFastmcAcceptance().get(-211).getHistReconstructed().draw(c9);
        eventAcceptance.getGemcAcceptance().get(-211).getHistReconstructed().draw(c10);

    }
}
