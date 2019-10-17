/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.tools;

import org.jlab.groot.data.H1F;
import org.jlab.jnp.physics.Particle;

/**
 *
 * @author gavalian
 */
public class ParticleHisto {
    private H1F histP;
    private H1F histTheta;
    private H1F histPhi;
    
    public ParticleHisto(){
        histP = new H1F("histP",100,0.0,10.0);
        histTheta = new H1F("histTheta",100,0.0,180.0);
        histPhi = new H1F("histTheta",180,-180.0,180.0);
    }
    
    public void fill(Particle p){
        histP.fill(p.p());
        histTheta.fill(Math.toDegrees(p.theta()));
        histPhi.fill(Math.toDegrees(p.phi()));
    }
    
    public H1F getP(){ return histP;}
    public H1F getTheta(){ return histTheta;}
    public H1F getPhi(){ return histPhi;}
}
