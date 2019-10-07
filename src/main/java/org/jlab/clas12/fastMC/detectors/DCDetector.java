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

public class DCDetector extends Detector {
    private DriftChamberParams params = new DriftChamberParams();

    public DCDetector() {
        this.setType(DetectorType.DC);
        this.setDetectorRegion(DetectorRegion.FORWARD);
        this.setTilt(25.0);
        init();
    }


    @Override
    public List<DetectorHit> getHits(Path3D path) {
        List<DetectorHit> hits;
        ArrayList<Point3D> intersectionPoints;
        intersectionPoints = this.intersection(path);
        hits = points2Hits(intersectionPoints);
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
        for(int j = 1; j < 7; j++){
            for(int i = 0; i < 6; i++) {
                Triangle3D sector = createSector(j);
                sector.show();
                sector.translateXYZ(0, 0, this.getDistanceToTarget());
                sector.rotateY(Math.toRadians(this.getTilt()));
                sector.rotateZ(Math.toRadians(i * 60));
                Shape3D shape = new Shape3D();
                shape.addFace(sector);
                this.addComponent(shape);
            }
        }
    }

    private double height(int slNumber){
        return 111 * 4 * this.params.getWPD(slNumber) * Math.cos(Math.toRadians(30));
    }

    private double distanceBelowX(int slNumber){
        return this.params.getDist2Targ(slNumber)*(Math.tan(Math.toRadians(25 - this.params.getTHMin(slNumber))));
    }

    private Triangle3D createSector(int slNumber){
        System.out.println("height = " + height(slNumber));
        System.out.println("distance = " + distanceBelowX(slNumber));
        return new Triangle3D(height(slNumber) - distanceBelowX(slNumber), -height(slNumber)*Math.tan(Math.toRadians(30)), 0,
                height(slNumber) - distanceBelowX(slNumber), height(slNumber)*Math.tan(Math.toRadians(30)),  0,
                -distanceBelowX(slNumber),              0,                      0);
    }
}
