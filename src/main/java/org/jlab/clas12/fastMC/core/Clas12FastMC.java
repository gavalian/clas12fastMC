/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.core;

import org.jlab.clas12.fastMC.swimmer.ParticleSwimmer;

/**
 *
 * @author gavalian
 */
public class Clas12FastMC {
    
    private ParticleSwimmer particleSwimmer = null;
    
    public Clas12FastMC(){
        initSwimmer(-1.0,1.0);
    }
    
    public Clas12FastMC(double torusField, double solenoidField){
        initSwimmer(torusField, solenoidField);
    }
    
    private void initSwimmer(double torusField, double solenoidField){
        particleSwimmer = new ParticleSwimmer(torusField, solenoidField);
        
    }
    
    
}
