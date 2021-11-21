/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clas12.fastMC;

import java.util.List;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.clas12.fastMC.core.Clas12FastMC;
import org.jlab.clas12.fastMC.detectors.DCDetector;
import org.jlab.clas12.fastMC.detectors.DCDetectorLayers;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.physics.Particle;

/**
 *
 * @author gavalian
 */
public class ProcessMCDC {
    
    
    public static void main(String[] args){
        //Particle p = new Particle();
        
        DCDetectorLayers dc = new DCDetectorLayers();
        DCDetector       det = new DCDetector();
        
        Clas12FastMC clas12FastMC = new Clas12FastMC();
        clas12FastMC.addConfiguration(11, DetectorRegion.FORWARD,   "DC", 6);
        
        for(int k = 0; k < 400; k++){
            Particle p = Particle.random(11, 0.5, 8.5, 
                    Math.toRadians(5), Math.toRadians(45), 
                    0, Math.PI*2.0);
            Path3D path = clas12FastMC.getPath(p);
            //path.show();
            List<DetectorHit> hits = det.getHits(path);
            List<DetectorHit> layers = dc.getHits(path);
            //int sector = dc.getSectorIntersection(path);
            System.out.println(layers.size() + "  " + hits.size() + " >>>> " + p);
            System.out.println(DCDetectorLayers.dataString(layers));
        }
    }
}
