/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.geom.prim.Point3D;
import org.jlab.jnp.geom.prim.Shape3D;

/**
 *
 * @author gavalian
 * @author viducic
 */
public abstract class Detector {
    
    private DetectorType detectorType;
    private DetectorRegion detectorRegion;
    private double distance;
    private double tilt;
    
    private ArrayList<Shape3D> components = new ArrayList<>();

    public abstract List<DetectorHit> getHits(Path3D path);
    
    public abstract void init();

    public double getDistance() {
        return distance;
    }

    public DetectorRegion getDetectorRegion() {
        return detectorRegion;
    }

    public DetectorType getDetectorType() {
        return detectorType;
    }

    public ArrayList<Shape3D> getComponents(){
        return components;
    }

    public double getTilt() {
        return tilt;
    }

    public void setType(DetectorType detectorType){
        this.detectorType = detectorType;
    }

    public void setDetectorRegion(DetectorRegion detectorRegion){
        this.detectorRegion = detectorRegion;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setTilt(double tilt) {
        this.tilt = tilt;
    }

    public void addComponent(Shape3D shape){
        this.components.add(shape);
    }

    public boolean hasIntersection(Path3D path){
        Iterator<Shape3D> shapes = components.iterator();
        List<Point3D>     points = new ArrayList<Point3D>();
        while(shapes.hasNext()){
            Shape3D shape = shapes.next();
            if(shape.intersection(path, points)>0){
                return true;
            }
        }
        return false;
    }

    public Shape3D getComponent(int sector){
        return this.components.get(sector - 1);
    }
}
