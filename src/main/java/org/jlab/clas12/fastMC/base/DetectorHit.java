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
    
    private double      energy = 0.0; 
    private double        path = 0.0; 
    private double        time = 0.0;
    private int         sector = 0;
    private int          layer = 0;
    private int      component = 0;

    private int                     pIndex = -1;    
    private Vector3D           hitPosition = new Vector3D();
    private DetectorType      detectorType = DetectorType.UNDEFINED;
    private DetectorRegion  detectorRegion = DetectorRegion.UNDEFINED;
    
    public DetectorHit(double x, double y, double z){
        hitPosition.setXYZ(x, y, z);
    }
    
    public DetectorHit(int __s, int __l, int __c){
        sector = __s; layer = __l; component = __c;
    }
    
    public DetectorHit(DetectorType type, DetectorRegion region, double x, double y, double z){
        hitPosition.setXYZ(x, y, z);
        detectorType = type; detectorRegion = region;
    }
    
    
        public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getPath() {
        return path;
    }

    public void setPath(double path) {
        this.path = path;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
    
    public int getSector() {
        return sector;
    }

    public void setSector(int sector) {
        this.sector = sector;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getComponent() {
        return component;
    }

    public void setComponent(int component) {
        this.component = component;
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
                path,energy,time,this.hitPosition.toString());
    }
}
