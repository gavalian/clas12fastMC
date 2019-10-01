package org.jlab.clas12.fastMC.detectors;

import org.jlab.clas12.fastMC.base.Detector;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.clas12.fastMC.base.DetectorType;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.geom.prim.Point3D;
import org.jlab.jnp.geom.prim.Shape3D;
import org.jlab.jnp.geom.prim.Triangle3D;

import java.util.ArrayList;
import java.util.List;

public class DC1Detector extends Detector {
    private double thetaMin;
    private double wirePlaneDistance;

    public DC1Detector() {
        this.setType(DetectorType.DC);
        this.setDetectorRegion(DetectorRegion.FORWARD);
        DriftChamberParams params = new DriftChamberParams();
        this.setDistanceToTarget(params.getDist2Targ(1));
        this.thetaMin = params.getTHMin(1);
        this.wirePlaneDistance = params.getWPD(1);
        this.setTilt(25.0);
        init();
    }


    @Override
    public List<DetectorHit> getHits(Path3D path) {
        List<DetectorHit> hits = new ArrayList<>();
        if (this.hasIntersection(path)) {
            ArrayList<Point3D> intersectionPoints = new ArrayList<>();
            this.intersection(path, intersectionPoints);
            hits = points2Hits(intersectionPoints);
        }
        return hits;
    }

    private ArrayList<DetectorHit> points2Hits(ArrayList<Point3D> points){
        ArrayList<DetectorHit> hits = new ArrayList<>();
        for(Point3D point : points) {
            DetectorHit hit = new DetectorHit(point.x(), point.y(), point.z());
            hit.setDetectorRegion(getDetectorRegion()).setDetectorType(getDetectorType());
            hits.add(hit);
        }
        return hits;
    }

    @Override
    public void init() {
        for(int i = 0; i < 6; i++){
            Triangle3D sector = createSector();
            sector.show();
            sector.translateXYZ(0,0, this.getDistanceToTarget());
            sector.rotateY(Math.toRadians(this.getTilt()));
            sector.rotateZ(Math.toRadians(i * 60));
            Shape3D shape = new Shape3D();
            shape.addFace(sector);
            this.addComponent(shape);

        }
    }

    private double height(){
        return 111 * 4 * this.wirePlaneDistance * Math.cos(Math.toRadians(30));
    }

    private double distanceBelowX(){
        return this.getDistanceToTarget()*(Math.tan(Math.toRadians(25 - this.thetaMin)));
    }

    public Triangle3D createSector(){
        System.out.println("height = " + height());
        System.out.println("distance = " + distanceBelowX());
        return new Triangle3D(height() - distanceBelowX(), -height()*Math.tan(Math.toRadians(30)), 0,
                height() - distanceBelowX(), height()*Math.tan(Math.toRadians(30)),  0,
                -distanceBelowX(),              0,                      0);
    }
}
