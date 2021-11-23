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
public class ProtonResolution implements ParticleModifier {
    
    private int pid = 2212;
    
    public static ProtonResolution forPid(int __id){
        ProtonResolution er = new ProtonResolution();
        er.pid = __id;
        return er;
    }
    
    @Override
    public void modify(Particle part){
        if(part.pid()==pid){
            double   pres = getP(part);
            double   tres = getTheta(part);
            double phires = getPhi(part);
            PhysicsFactory.applyResolution(part, pres, phires, phires);
        }
    }
    
    public int getPid(){
        return pid;
    }
    
    public double getP(Particle p){
        return p.p()*0.007;
    }
    
    public double getTheta(Particle p){
        return 0.02;
    }
    
    public double getPhi(Particle p){
        return 0.025;
    }
}
