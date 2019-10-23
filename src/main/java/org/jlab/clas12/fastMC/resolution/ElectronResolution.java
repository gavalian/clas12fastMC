/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.resolution;

import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.ParticleModifier;
import org.jlab.jnp.physics.PhysicsFactory;

/**
 *
 * @author gavalian
 */
public class ElectronResolution implements ParticleModifier {
    
    private final int pid = 11;
    
    @Override
    public void modify(Particle part){
        if(part.pid()==pid){
            double   pres = getP(part);
            double   tres = getTheta(part);
            double phires = getPhi(part);
            //System.out.printf(" electron resolution = %8.5f %8.5f %8.5f\n",pres,tres,phires);
            //System.out.println("BEFORE = " + part.toLundString());
            PhysicsFactory.applyResolution(part, pres, phires, phires);
            //System.out.println("AFTER  =  " + part.toLundString());
        }
    }
    
    public int getPid(){
        return pid;
    }
    
    public double getP(Particle p){
        return p.p()*0.05;
    }
    
    public double getTheta(Particle p){
        return 0.01;
    }
    
    public double getPhi(Particle p){
        return 0.015;
    }
    
}
