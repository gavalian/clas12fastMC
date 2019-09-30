/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.detectors;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import org.jlab.clas12.fastMC.base.Detector;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.geom.prim.Shape3D;
import org.jlab.jnp.geom.prim.Triangle3D;

/**
 *
 * @author gavalian
 * @authon viducic
 */
public class ECDetector extends Detector {

    public ECDetector(){
        this.setName("ECal");
        this.setDistance(721.7);
        this.setTilt(25.0);
        init();
    }

    @Override
    public List<DetectorHit> getHits(Path3D path) {
        List<DetectorHit> hits = new ArrayList<DetectorHit>();
        return hits;
    }

    @Override
    public void init(){
        for(int i = 0; i < 6; i++){
            Triangle3D tri = createSector();
            tri.translateXYZ(0.0,0.0,distance);
            tri.rotateY(Math.toRadians(tilt));
            tri.rotateZ(Math.toRadians(60*i));
            Shape3D  shape = new Shape3D();
            shape.addFace(tri);
            this.addComponent(shape);
        }
    }

    private Triangle3D createSector(){
        double a = 86.179;
        double b = 305.013;
        return new Triangle3D(
                a,   -394.2/2, 0.0,
                a,    394.2/2, 0.0,
                -b,    0.0,  0.0);
    }
}
