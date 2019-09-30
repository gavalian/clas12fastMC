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
    private String name;
    double distance;
    private double tilt;
    private ArrayList<Shape3D> components = new ArrayList<>();


    public abstract List<DetectorHit> getHits(Path3D path);
    public abstract void init();

    public double getDistance() {
        return distance;
    }

    public double getTilt() {
        return tilt;
    }

    public void setName(String name){
        this.name = name;
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
        while(shapes.hasNext()){
            Shape3D shape = shapes.next();
            if(shape.hasIntersection(path)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Point3D> intersection(Path3D path, ArrayList<Point3D> points){
        Iterator<Shape3D> shapes = components.iterator();
        while(shapes.hasNext()){
            Shape3D shape = shapes.next();
            if(shape.hasIntersection(path)){
                shape.intersection(path, points);
            }
        }
        return points;
    }

    public int getIntersectionComponent(Path3D path){
        Iterator<Shape3D> shapes = components.iterator();
        while(shapes.hasNext()){
            Shape3D shape = shapes.next();
            if(shape.hasIntersection(path)){
                return (this.components.indexOf(shape) + 1);
            }
        }
        return -1;
    }

    public Shape3D getComponent(int sector){
        return this.components.get(sector + 1);
    }
}
