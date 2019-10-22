/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.tools;

import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.physics.Particle;

/**
 *
 * @author gavalian
 */
public class ParticleHisto {
    private H1F histP;
    private H1F histTheta;
    private H1F histPhi;
    private H2F thetaPhi;
    
    public ParticleHisto(){
        histP = new H1F("histP",100,0.0,10.0);
        histP.setTitle("Momentum (GeV)");
        histTheta = new H1F("histTheta",100,0.0,180.0);
        histTheta.setTitle("Theta (deg)");
        histPhi = new H1F("histTheta",180,-180.0,180.0);
        histPhi.setTitle("Phi (deg)");
        thetaPhi = new H2F("thetaPhi", 60, 0, 60, 180, -180, 180);
    }
    
    public void fill(Particle p){
        histP.fill(p.p());
        histTheta.fill(Math.toDegrees(p.theta()));
        histPhi.fill(Math.toDegrees(p.phi()));
        thetaPhi.fill(Math.toDegrees(p.theta()), Math.toDegrees(p.phi()));
    }
    
    public H1F getP(){ return histP;}
    public H1F getTheta(){ return histTheta;}
    public H1F getPhi(){ return histPhi;}
    public H2F getThetaPhi() { return thetaPhi; }

    public int draw(TCanvas c1, int startPad) {
        c1.cd(startPad    ).draw(histP);
        c1.cd(startPad + 1).draw(histTheta);
        c1.cd(startPad + 2).draw(histPhi);
        c1.cd(startPad + 3).draw(thetaPhi);
        return startPad + 3;
    }
    public void draw(TCanvas c1) {
        c1.getCanvas().drawNext(histP);
        c1.getCanvas().drawNext(histTheta);
        c1.getCanvas().drawNext(histPhi);
        c1.getCanvas().drawNext(thetaPhi);
    }
}
