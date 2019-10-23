/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.tests;

import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.clas12.fastMC.core.Clas12FastMC;
import org.jlab.clas12.fastMC.tools.EventAcceptance;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.DataManager;

/**
 *
 * @author gavalian
 */
public class GEMCAcceptance {
     public static void main(String[] args) {

//        List<String> dataFiles = FileFinder.getFiles("/home/tylerviducic/research/rho/clas12/data/*");
        System.setProperty("JNP_DATA","/Users/gavalian/Work/DataSpace/JNP_DATA");
        String directory = "/Users/gavalian/Work/DataSpace/clas12/mc";
       
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
        
        while (reader.hasNext()){
            
            reader.nextEvent(event);
            event.read(recParticle);
            event.read(mcParticle);
            
            PhysicsEvent mcEvent = DataManager.getPhysicsEvent(10.6, mcParticle);
            PhysicsEvent recEvent = DataManager.getPhysicsEvent(10.6, recParticle);
            
            eventCounter++;
            
            int count = mcEvent.countByPid(2212);
            for(int p = 0; p < count; p++){
                protonCounter++;
                Particle proton = mcEvent.getParticleByPid(2212, p);
                //if(proton.theta()*57.29>40){
                    //System.out.println(proton.toLundString());
                    boolean status = clas12FastMC.validHit(proton);
                    if(status==true) protonCounterTrue++;
                    System.out.printf(" event # %8d , loop = %4d  status = %s\n",eventCounter,p,status);
                //}
            }
            
            int countg = mcEvent.countByPid(11);
            //System.out.println(" # photons = " + countg);
            for(int p = 0; p < countg; p++){
                photonCounter++;
                //System.out.println("\t getting photon # " + p);
                Particle photon = mcEvent.getParticleByPid(11, p);
               
                //if(proton.theta()*57.29>40){
                    //System.out.println(proton.toLundString());
                    boolean status = clas12FastMC.validHit(photon);
                    if(status==true) photonCounterTrue++;
                    System.out.printf(" event # %8d , loop = %4d  status = %s\n",eventCounter,p,status);
                //}
            }
  
        }
        System.out.println("\n\n---------------------------------------------------------------------------------");
        System.out.printf(" EVENT = %8d, PROTONS = %8d, TRUE = %8d, ACCEPTANCE = %8.4f\n", 
                eventCounter, protonCounter, protonCounterTrue, (1.0*protonCounterTrue)/protonCounter);
        System.out.printf(" EVENT = %8d, PHOTONS = %8d, TRUE = %8d, ACCEPTANCE = %8.4f\n", 
                eventCounter, photonCounter, photonCounterTrue, (1.0*photonCounterTrue)/photonCounter);
        System.out.println("---------------------------------------------------------------------------------");
        
        TCanvas c1 = new TCanvas("c1",800,800);
        
        
    }
}
