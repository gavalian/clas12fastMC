/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.an.abs;

import org.jlab.clas12.an.abs.DetectorFiducialCuts.ParticleFiducialConfig;
import org.jlab.jnp.detector.base.DetectorManager;
import org.jlab.jnp.detector.base.DetectorType;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.hipo4.io.HipoReader;

/**
 *
 * @author gavalian
 */
public class Clas12FiducialCuts {
    
    public static void configure(DetectorFiducialCuts fiducial){

        ParticleFiducialConfig   proton = new ParticleFiducialConfig(2212);        
        ParticleFiducialConfig   photon = new ParticleFiducialConfig(22);
        ParticleFiducialConfig electron = new ParticleFiducialConfig(11);

        proton.addDetector(DetectorManager.getInstance().getDetector(DetectorType.FTOF));
        photon.addDetector(DetectorManager.getInstance().getDetector(DetectorType.ECAL));
        
        electron.addDetector(DetectorManager.getInstance().getDetector(DetectorType.FTOF));
        electron.addDetector(DetectorManager.getInstance().getDetector(DetectorType.ECAL));
        
        fiducial.addConfig(photon).addConfig(proton).addConfig(electron);
    }
    
    public static void main(String[] args){
        String filename = "myfile.hipo";
        
        DetectorManager.getInstance().initFiducial();
        
        HipoChain chain = new HipoChain();
        chain.addFile(filename);
        chain.open();
        
        Clas12Event clas12Event = new Clas12Event(chain);
        
        DetectorFiducialCuts fiducial = new DetectorFiducialCuts();
        
        Clas12FiducialCuts.configure(fiducial);
        
        while(clas12Event.readNext()==true){
            
            fiducial.apply(clas12Event);
            
        }
    }
}
