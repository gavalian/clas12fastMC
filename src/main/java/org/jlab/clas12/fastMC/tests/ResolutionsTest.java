/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.tests;

import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.clas12.fastMC.core.Clas12FastMC;
import org.jlab.clas12.fastMC.resolution.ElectronResolution;
import org.jlab.clas12.fastMC.resolution.ProtonResolution;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.DataManager;

/**
 *
 * @author gavalian
 */
public class ResolutionsTest {
    public static void main(String[] args){
        System.setProperty("JNP_DATA","/Users/gavalian/Work/DataSpace/JNP_DATA");
        String directory = "/Users/gavalian/Work/DataSpace/clas12/mc";
       
        

       
        HipoChain reader = new HipoChain();
        reader.addDir(directory);
        reader.open();
        
        Clas12FastMC clas12FastMC = new Clas12FastMC();
        
        // Different ways you can detect proton
        clas12FastMC.addConfiguration(2212, DetectorRegion.CENTRAL,  "CVT", 3);
        // This is proton in forward detector
        clas12FastMC.addConfiguration(2212, DetectorRegion.FORWARD,   "DC", 6);
        clas12FastMC.addConfiguration(2212, DetectorRegion.FORWARD, "FTOF", 1);
        // electron detection
        clas12FastMC.addConfiguration(11, DetectorRegion.FORWARD,     "DC", 6);
        clas12FastMC.addConfiguration(11, DetectorRegion.FORWARD,   "ECAL", 1);
        clas12FastMC.addConfiguration(11, DetectorRegion.FORWARD,   "FTOF", 1);
        
        clas12FastMC.show();
        // add resolution classes, have to be added for each region separately
        
        clas12FastMC.getEventResolution().addResolution(11, DetectorRegion.FORWARD, new ElectronResolution());
        clas12FastMC.getEventResolution().addResolution(2212, DetectorRegion.FORWARD, new ProtonResolution());
        
        EventFilter filter = new EventFilter("11:X+:X-:Xn");

        Event event = new Event();
        
        Bank mcParticle = new Bank(reader.getSchemaFactory().getSchema("MC::Particle"));
        Bank recParticle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
        
        int eventCounter = 0;
        
        while (reader.hasNext()){
            eventCounter++;
            reader.nextEvent(event);
            event.read(recParticle);
            event.read(mcParticle);
            
            PhysicsEvent mcEvent = DataManager.getPhysicsEvent( 10.6, mcParticle);
            clas12FastMC.setResolution(false);
            PhysicsEvent fastEvent = clas12FastMC.processEvent(mcEvent);
            clas12FastMC.setResolution(true);
            PhysicsEvent fastEventRes = clas12FastMC.processEvent(mcEvent);
            System.out.println(" event # " + eventCounter);
            System.out.println("---------- NO RES");
            System.out.println(fastEvent.toLundString());
            System.out.println("---------- RES");
            System.out.println(fastEventRes.toLundString());
            //PhysicsEvent recEvent = DataManager.getPhysicsEvent(10.6, recParticle);
        }
    }
}
