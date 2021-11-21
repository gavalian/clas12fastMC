/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jlab.clas12.fastMC.detectors;

import java.util.ArrayList;
import java.util.List;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.jnp.geom.prim.Face3D;
import org.jlab.jnp.geom.prim.Line3D;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.geom.prim.Point3D;
import org.jlab.jnp.geom.prim.Shape3D;
import org.jlab.jnp.geom.prim.Triangle3D;

/**
 *
 * @author gavalian
 */
public class DCDetectorLayers {
    
    
    public DCDetectorLayers(){
        DCGeometry.init();
    }
    
    public void getOrigin(Point3D p,int layer, int component){        
        p.set(DCGeometry.points[layer][component][0], 
                DCGeometry.points[layer][component][1],
                DCGeometry.points[layer][component][2]
        );
    }
    
    public void getOrigin(Point3D p,int sector, int layer, int component){        
        this.getOrigin(p, layer, component);        
        p.rotateZ(Math.toRadians(sector*60));
    }
    
    public void getEnd(Point3D p,int layer, int component){        
        p.set(DCGeometry.points[layer][component][3], 
                DCGeometry.points[layer][component][4],
                DCGeometry.points[layer][component][5]
        );
    }
    
    public void getEnd(Point3D p, int sector, int layer, int component){        
        this.getEnd(p, layer, component);
        p.rotateZ(Math.toRadians(sector*60));
    }
    
    public void getLine(Line3D line, int layer, int component){
        this.getOrigin(line.origin(), layer, component);
        this.getEnd(line.end(), layer, component);
    }
    
    public void getLine(Line3D line, int sector, int layer, int component){
        getLine(line,layer,component);
        line.rotateZ(Math.toRadians(sector*60));
    }
    
    
    public int  getSectorIntersection(Path3D path){
        Shape3D b = new Shape3D();
        b.addFace(new Triangle3D(0.,0.,0.,0.,0.,0.,0.,0.,0.));
        b.addFace(new Triangle3D(0.,0.,0.,0.,0.,0.,0.,0.,0.));        
        Point3D point = new Point3D();
        int    nlines = path.getNumLines();
        Line3D   line = new Line3D();
        for(int i = 0; i < 6; i++){
            for(int l = 0; l < nlines; l++){
                path.getLine(line, l);
                this.getBoundary(b, i, 0);
                if(b.hasIntersectionSegment(line)==true) return i;
            }
        }
        return -1;
    }
    
    public int getComponent(Path3D path, int sector, int layer){
        Line3D      line = new Line3D();
        Line3D      wire = new Line3D();
        
        double  distance = 100.0;
        int    component = -1;
        
        for(int w = 0; w < 112; w++){
            this.getLine(wire, sector, layer, w);
            for(int p = 0; p < path.getNumLines(); p++){
                path.getLine(line, p);
                Line3D inter = line.distanceSegments(wire);
                if(inter.length()<distance){
                    distance = inter.length();
                    component = w;
                }
            }
        }
        return component;
    }
    
    public List<DetectorHit> getHits(Path3D path){
        int sector = this.getSectorIntersection(path);
        List<DetectorHit>  hits = new ArrayList<>();
        Shape3D           shape = new Shape3D();
        Line3D             line = new Line3D();
        
        shape.addFace(new Triangle3D(0.,0.,0.,0.,0.,0.,0.,0.,0.));
        shape.addFace(new Triangle3D(0.,0.,0.,0.,0.,0.,0.,0.,0.)); 
        if(sector>=0){
            for(int layer = 0; layer < 36; layer++ ){
                getBoundary(shape, sector, layer);
                boolean flag = false;
                for(int p = 0; p < path.getNumLines(); p++){
                    path.getLine(line, p);
                    if(shape.hasIntersectionSegment(line)==true) flag = true;
                }
                if(flag==true){
                    int component = this.getComponent(path, sector, layer);
                    DetectorHit hit = new DetectorHit(sector,layer,component);
                    hits.add(hit);
                }
            }
        }
        return hits;
    }
    
    public static String dataString(List<DetectorHit> hits){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < hits.size(); i++){
            str.append(String.format("%d,%3d,%4d,",
                    hits.get(i).getSector(),
                    hits.get(i).getLayer(),
                    hits.get(i).getComponent()
                    ));
        }
        return str.toString();
    }
    
    public void getBoundary(Shape3D s, int sector, int layer){
        this.getOrigin(s.face(0).point(0), sector, layer,0);
        this.getEnd(s.face(0).point(1), sector, layer,0);
        this.getOrigin(s.face(0).point(2), sector, layer,111);
        
        this.getEnd(s.face(1).point(0), sector, layer,0);
        this.getEnd(s.face(1).point(1), sector, layer,111);
        this.getOrigin(s.face(1).point(2), sector, layer,111);
    }
}
