package org.jlab.clas12.fastMC.tests;


import org.jlab.clas12.fastMC.core.Clas12FastMC;
import org.jlab.clas12.fastMC.swimmer.ParticleSwimmer;
import org.jlab.clas12.fastMC.tools.EventAcceptance;
import org.jlab.clas12.fastMC.tools.FileFinder;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.DataManager;

import java.util.List;

public class GEMCComparison {

    public GEMCComparison(){}

    public static void main(String[] args) {

        List<String> dataFiles = FileFinder.getFiles("/home/tylerviducic/research/rho/clas12/data/*");
        System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField");

        EventAcceptance eventAcceptance = new EventAcceptance();
        Clas12FastMC clas12FastMC = new Clas12FastMC();
        int eventCounter = 0;

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
                PhysicsEvent recEvent = DataManager.getPhysicsEvent(10.6, mcParticle);
                PhysicsEvent mcEvent = DataManager.getPhysicsEvent(10.6, mcParticle);
                PhysicsEvent fastmcEvent = clas12FastMC.processEvent(mcEvent);

                eventAcceptance.acceptanceGemc(mcEvent, recEvent);
                eventAcceptance.acceptanceFastmc(mcEvent, fastmcEvent);
            }
        }

        TCanvas c1 = new TCanvas("c1", 1000, 1500);
        c1.divide(2, 3);
        TCanvas c2 = new TCanvas("c2", 1000, 1500);
        c2.divide(2, 3);

        c1.cd(0);
        c1.draw(eventAcceptance.getFastmcAcceptance().get(11).getHistGenerated().getP());
        c1.cd(1);
        c1.draw(eventAcceptance.getFastmcAcceptance().get(11).getHistReconstructed().getP());

        c1.cd(2);
        c1.draw(eventAcceptance.getFastmcAcceptance().get(11).getHistGenerated().getTheta());
        c1.cd(3);
        c1.draw(eventAcceptance.getFastmcAcceptance().get(11).getHistReconstructed().getTheta());

        c1.cd(4);
        c1.draw(eventAcceptance.getFastmcAcceptance().get(11).getHistGenerated().getPhi());
        c1.cd(5);
        c1.draw(eventAcceptance.getFastmcAcceptance().get(11).getHistReconstructed().getPhi());

        c2.cd(0);
        c2.draw(eventAcceptance.getGemcAcceptance().get(11).getHistGenerated().getP());
        c2.cd(1);
        c2.draw(eventAcceptance.getGemcAcceptance().get(11).getHistReconstructed().getP());

        c2.cd(2);
        c2.draw(eventAcceptance.getGemcAcceptance().get(11).getHistGenerated().getTheta());
        c2.cd(3);
        c2.draw(eventAcceptance.getGemcAcceptance().get(11).getHistReconstructed().getTheta());

        c2.cd(4);
        c2.draw(eventAcceptance.getGemcAcceptance().get(11).getHistGenerated().getPhi());
        c2.cd(5);
        c2.draw(eventAcceptance.getGemcAcceptance().get(11).getHistReconstructed().getPhi());


//        c1.cd(0);
//        c1.draw(eventAcceptance.getFastmcAcceptance().get(2212).getHistGenerated().getP());
//        c1.cd(1);
//        c1.draw(eventAcceptance.getFastmcAcceptance().get(2212).getHistReconstructed().getP());
//
//        c1.cd(2);
//        c1.draw(eventAcceptance.getFastmcAcceptance().get(2212).getHistGenerated().getTheta());
//        c1.cd(3);
//        c1.draw(eventAcceptance.getFastmcAcceptance().get(2212).getHistReconstructed().getTheta());
//
//        c1.cd(4);
//        c1.draw(eventAcceptance.getFastmcAcceptance().get(2212).getHistGenerated().getPhi());
//        c1.cd(5);
//        c1.draw(eventAcceptance.getFastmcAcceptance().get(2212).getHistReconstructed().getPhi());
//
//        c2.cd(0);
//        c2.draw(eventAcceptance.getGemcAcceptance().get(2212).getHistGenerated().getP());
//        c2.cd(1);
//        c2.draw(eventAcceptance.getGemcAcceptance().get(2212).getHistReconstructed().getP());
//
//        c2.cd(2);
//        c2.draw(eventAcceptance.getGemcAcceptance().get(2212).getHistGenerated().getTheta());
//        c2.cd(3);
//        c2.draw(eventAcceptance.getGemcAcceptance().get(2212).getHistReconstructed().getTheta());
//
//        c2.cd(4);
//        c2.draw(eventAcceptance.getGemcAcceptance().get(2212).getHistGenerated().getPhi());
//        c2.cd(5);
//        c2.draw(eventAcceptance.getGemcAcceptance().get(2212).getHistReconstructed().getPhi());
    }
}
