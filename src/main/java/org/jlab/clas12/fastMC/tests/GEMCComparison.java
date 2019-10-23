package org.jlab.clas12.fastMC.tests;


import org.jlab.clas12.fastMC.core.Clas12FastMC;
import org.jlab.clas12.fastMC.swimmer.ParticleSwimmer;
import org.jlab.clas12.fastMC.tools.EventAcceptance;
import org.jlab.clas12.fastMC.tools.FileFinder;
import org.jlab.groot.data.H2F;
import org.jlab.groot.data.TDirectory;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.pdg.PhysicsConstants;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.DataManager;

import java.util.List;
import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.physics.Particle;

public class GEMCComparison {

    public GEMCComparison(){}

    public static void main(String[] args) {

//        List<String> dataFiles = FileFinder.getFiles("/home/tylerviducic/research/rho/clas12/data/*");
        //System.setProperty("JNP_DATA","/Users/gavalian/Work/DataSpace/JNP_DATA");
        System.setProperty("JNP_DATA","/home/tylerviducic/research/clas12MagField/");
        //String directory = "/Users/gavalian/Work/DataSpace/clas12/mc";
        String directory = "/media/tylerviducic/Elements/clas12/mcdata/fastMC";
       
        int eventCounter = 0;

       
        HipoChain reader = new HipoChain();
        reader.addDir(directory);
        reader.open();
        

        EventAcceptance eventAcceptance = new EventAcceptance();
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

                
        clas12FastMC.show();
        //clas12FastMC.addConfiguration(2212, "CVT", 3);
        
        EventFilter filter = new EventFilter("11:X+:X-:Xn");

        Event event = new Event();
        Bank mcParticle = new Bank(reader.getSchemaFactory().getSchema("MC::Particle"));
        Bank recParticle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
        
        int protonCounter = 0;
        int protonCounterTrue = 0;
        
        int photonCounter      = 0;
        int photonCounterTrue  = 0;
        
        while (reader.hasNext() && eventCounter < 100000){
            
            reader.nextEvent(event);
            event.read(recParticle);
            event.read(mcParticle);
            
            PhysicsEvent mcEvent = DataManager.getPhysicsEvent(10.6, mcParticle);
            PhysicsEvent recEvent = DataManager.getPhysicsEvent(10.6, recParticle);
            PhysicsEvent fastMCEvent = clas12FastMC.processEvent(mcEvent);
            if(fastMCEvent.countByPid(11) > 0){
//                System.out.println("------------------");
//                System.out.println(fastMCEvent.toLundString());
//                System.out.println(recEvent.toLundString());
                //when fastMC detects e but gemc, plot p, theta, phi
            }
            
            eventCounter++;

            if(filter.isValid(mcEvent)) {
                int count = mcEvent.countByPid(2212);
                for (int p = 0; p < count; p++) {
                    protonCounter++;
                    Particle proton = mcEvent.getParticleByPid(2212, p);
                    //if(proton.theta()*57.29>40){
                    //System.out.println(proton.toLundString());
                    boolean status = clas12FastMC.validHit(proton);
                    if (status == true) protonCounterTrue++;
                    //System.out.printf(" event # %8d , loop = %4d  status = %s\n",eventCounter,p,status);
                    //}
                }

                int countg = mcEvent.countByPid(22);
                //System.out.println(" # photons = " + countg);
                for (int p = 0; p < countg; p++) {
                    photonCounter++;
                    //System.out.println("\t getting photon # " + p);
                    Particle photon = mcEvent.getParticleByPid(22, p);
                    //if(proton.theta()*57.29>40){
                    //System.out.println(proton.toLundString());
                    boolean status = clas12FastMC.validHit(photon);
                    if (status == true) photonCounterTrue++;
                    //System.out.printf(" event # %8d , loop = %4d  status = %s\n",eventCounter,p,status);
                    //}
                }
                eventAcceptance.acceptanceFastmc(mcEvent, fastMCEvent);
                eventAcceptance.acceptanceGemc(mcEvent, recEvent);
            }
        }
        System.out.println("\n\n---------------------------------------------------------------------------------");
        System.out.printf(" EVENT = %8d, PROTONS = %8d, TRUE = %8d, ACCEPTANCE = %8.4f\n", 
                eventCounter, protonCounter, protonCounterTrue, (1.0*protonCounterTrue)/protonCounter);
        System.out.printf(" EVENT = %8d, PHOTONS = %8d, TRUE = %8d, ACCEPTANCE = %8.4f\n", 
                eventCounter, photonCounter, photonCounterTrue, (1.0*photonCounterTrue)/photonCounter);
        System.out.println("---------------------------------------------------------------------------------");


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
//        TCanvas c7 = new TCanvas("fastMC pi+", 1000, 1500);
//        c7.divide(2, 2);
//        TCanvas c8 = new TCanvas("gemc pi+", 1000, 1500);
//        c8.divide(2, 2);
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
//        eventAcceptance.getFastmcAcceptance().get(211).getHistReconstructed().draw(c7);
//        eventAcceptance.getGemcAcceptance().get(211).getHistReconstructed().draw(c8);
        eventAcceptance.getFastmcAcceptance().get(-211).getHistReconstructed().draw(c9);
        eventAcceptance.getGemcAcceptance().get(-211).getHistReconstructed().draw(c10);
    }
}
