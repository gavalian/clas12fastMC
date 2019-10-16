/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.tests;

import org.jlab.clas12.fastMC.swimmer.ParticleSwimmer;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.physics.Particle;

/**
 *
 * @author gavalian
 */
public class SwimmerTest {
    public static void main(String[] args) {
        System.setProperty("JNP_DATA","/Users/gavalian/Work/DataSpace/JNP_DATA");
        ParticleSwimmer swimmer = new ParticleSwimmer();
        Particle electron = new Particle();
        electron.initParticle(11, 0.0, 0.5, 0.5, 0, 0, 0);
        
        Path3D path = swimmer.getParticlePath(electron);
        
        path.show();
        
        //CompositeField field = MagneticFields.getInstance().getCompositeField();
        /*
        MagneticFields mf = MagneticFields.getInstance();
        try {
            mf.initializeMagneticFields("/Users/gavalian/Work/DataSpace/JNP_DATA/etc/data/magfield", "clas12-fieldmap-torus.dat", "clas12-fieldmap-solenoid.dat");
            Swimmer swim = new Swimmer();
            
            float data[] = new float[3];        
            swim.getProbe().field(0.5f,0.5f,0.5f, data);
            System.out.println(" FILED = " + data[0] + " " + data[1] + "  " + data[2]);
            
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SwimmerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MagneticFieldInitializationException ex) {
            Logger.getLogger(SwimmerTest.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
}
