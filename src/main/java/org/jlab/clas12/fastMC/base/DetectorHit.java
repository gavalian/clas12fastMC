/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.base;

import org.jlab.jnp.geom.prim.Point3D;
import org.jlab.jnp.geom.prim.Vector3D;

import java.util.ArrayList;

/**
 *
 * @author gavalian
 */
public class DetectorHit {
    
    private Vector3D hitPosition = new Vector3D();
    private DetectorType      detectorType = DetectorType.UNDEFINED;
    private DetectorRegion  detectorRegion = DetectorRegion.UNDEFINED;
    
    public DetectorHit(double x, double y, double z){
        hitPosition.setXYZ(x, y, z);
    }
    
    public DetectorHit(DetectorType type, DetectorRegion region, double x, double y, double z){
        hitPosition.setXYZ(x, y, z);
        detectorType = type; detectorRegion = region;
    }
    
    public DetectorHit setDetectorType(DetectorType type){   detectorType = type; return this;}
    public DetectorHit setDetectorRegion(DetectorRegion region){ detectorRegion = region; return this;}

    public Vector3D getHitPosition(){
        return hitPosition;
    }
    
    
}
