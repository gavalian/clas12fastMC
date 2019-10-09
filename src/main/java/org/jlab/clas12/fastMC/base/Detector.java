/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.base;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.geom.prim.Point3D;
import org.jlab.jnp.geom.prim.Shape3D;
import org.jlab.jnp.physics.Particle;

/**
 *
 * @author gavalian
 * @author viducic
 */
public abstract class Detector {
    
    private DetectorType detectorType;
    private DetectorRegion detectorRegion;
    private double distanceToTarget;
    private double tilt;
    
    private ArrayList<Shape3D> components = new ArrayList<>();

    public abstract List<DetectorHit> getHits(Path3D path);
    public abstract boolean validEvent(Path3D path);
    public abstract void init();

    public double getDistanceToTarget() {
        return distanceToTarget;
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

    public void setDistanceToTarget(double distanceToTarget) {
        this.distanceToTarget = distanceToTarget;
    }

    public void setTilt(double tilt) {
        this.tilt = tilt;
    }

    public void addComponent(Shape3D shape){
        this.components.add(shape);
    }

    public ArrayList<Point3D> intersection(Path3D path){
        Iterator<Shape3D> shapes = components.iterator();
        ArrayList<Point3D>     points = new ArrayList<Point3D>();
        while(shapes.hasNext()){
            Shape3D shape = shapes.next();
            int numIntersections = shape.intersection(path, points);
        }
        return points;
    }

    public Shape3D getComponent(int sector){
        return this.components.get(sector - 1);
    }
}
