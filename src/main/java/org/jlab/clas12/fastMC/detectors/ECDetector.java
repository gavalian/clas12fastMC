/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.detectors;

import java.util.ArrayList;
import java.util.List;
import org.jlab.clas12.fastMC.base.Detector;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.clas12.fastMC.base.DetectorType;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.geom.prim.Point3D;
import org.jlab.jnp.geom.prim.Shape3D;
import org.jlab.jnp.geom.prim.Triangle3D;

/**
 *
 * @author gavalian
 * @author viducic
 */
public class ECDetector extends Detector {

    public ECDetector() {
        this.setType(DetectorType.ECAL);
        this.setDetectorRegion(DetectorRegion.FORWARD);
        this.setDistanceToTarget(721.7);
        this.setTilt(25.0);
        init();
    }

    @Override
    public List<DetectorHit> getHits(Path3D path) {
        List<DetectorHit> hits = new ArrayList<DetectorHit>();
        ArrayList<Point3D> intersectionPoints;
        intersectionPoints = this.intersection(path);
        hits = points2Hits(intersectionPoints);
        return hits;
    }

    @Override
    public boolean validEvent(Path3D path) {
        return getHits(path).size() > 0;
    }

    @Override
    public void init() {
        for (int i = 0; i < 6; i++) {
            Triangle3D tri = createSector();
            tri.translateXYZ(0.0, 0.0, this.getDistanceToTarget());
            tri.rotateY(Math.toRadians(this.getTilt()));
            tri.rotateZ(Math.toRadians(60 * i));
            Shape3D shape = new Shape3D();
            shape.addFace(tri);
            this.addComponent(shape);
        }
    }

    private Triangle3D createSector() {
        double a = 86.179;
        double b = 305.013;
        return new Triangle3D(
                a, -394.2 / 2, 0.0,
                a, 394.2 / 2, 0.0,
                -b, 0.0, 0.0);
    }


    public static void main(String[] args) {
        ECDetector ecDetector = new ECDetector();
        System.out.println(ecDetector.getComponent(4));
    }

}