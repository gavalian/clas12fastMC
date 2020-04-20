/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.an.abs;

import org.jlab.jnp.detector.base.Detector;
import org.jlab.jnp.detector.base.DetectorManager;
import org.jlab.jnp.detector.base.DetectorType;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.geom.prim.Plane3D;
import org.jlab.jnp.geom.prim.Point3D;

/**
 *
 * @author gavalian
 */
public class DetectorDebug {
    public static void main(String[] args){
        DetectorManager.getInstance().initFiducial();
        Detector ecal = DetectorManager.getInstance().getDetector(DetectorType.ECAL);
        
        Path3D path   = new Path3D();
        Plane3D plane = new Plane3D();
        plane.set(0, 0, 700, 0, 0, -1.0);
        Point3D intersect = new Point3D();
        
        for(int i = 0; i < 1000000; i++){
            path.generateRandom(0.0, 0.0, 0.0, Math.toRadians(3.0), Math.toRadians(45), 
                    Math.toRadians(0.0), Math.toRadians(360.0), 900, 8);
            boolean status = ecal.hasLayerHits(path);
            int statusWord = 0;
            if(status==true) statusWord = 1;
            plane.intersection(path.getLine(0), intersect);
            System.out.printf("%3d %12.5f %12.5f %12.5f\n", statusWord,
                    intersect.x(),
                    intersect.y(),
                    intersect.z()
                    );
        }
    }
}
