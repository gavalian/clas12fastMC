/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.base;

import java.util.List;
import org.jlab.jnp.geom.prim.Vector3D;

/**
 *
 * @author gavalian
 */
public class DetectorHit {
    private double               hitEnergy = 0.0; 
    private double                 hitPath = 0.0; 
    private double                 hitTime = 0.0;
    private int                     pIndex = -1;
    
    private Vector3D           hitPosition = new Vector3D();
    private DetectorType      detectorType = DetectorType.UNDEFINED;
    private DetectorRegion  detectorRegion = DetectorRegion.UNDEFINED;
    
    public DetectorHit(double x, double y, double z){
        hitPosition.setXYZ(x, y, z);
    }
    
    public DetectorHit(DetectorType type, DetectorRegion region, double x, double y, double z){
        hitPosition.setXYZ(x, y, z);
        detectorType = type; detectorRegion = region;
    }
    
    public DetectorHit setIndex(int pind){pIndex = pind; return this;}
    public int         getIndex(){return pIndex;}
    public static void setIndex(List<DetectorHit> hits, int pind){
        for(DetectorHit h : hits) h.setIndex(pind);
    }
    public DetectorHit setDetectorType(DetectorType type){   detectorType = type; return this;}
    public DetectorHit setDetectorRegion(DetectorRegion region){ detectorRegion = region; return this;}
    public DetectorType getDetectorType(){ return this.detectorType;}
    public Vector3D getHitPosition(){
        return hitPosition;
    }

    @Override
    public String toString(){
        return String.format(">>> pindex [%3d] type = %8s,path,e,t (%8.5f %8.5f %8.5f), position %s", 
                pIndex,detectorType.getName(),
                hitPath,hitEnergy,hitTime,this.hitPosition.toString());
    }
}
