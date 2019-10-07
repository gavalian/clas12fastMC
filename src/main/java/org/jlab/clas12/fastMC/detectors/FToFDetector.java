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

public class FToFDetector extends Detector {

    public FToFDetector(){
        this.setType(DetectorType.FTOF);
        this.setDetectorRegion(DetectorRegion.FORWARD);
        this.setDistanceToTarget(668.09273125591);
        this.setTilt(25.0);
        init();
    }

    @Override
    public List<DetectorHit> getHits(Path3D path) {
        List<DetectorHit> hits = new ArrayList<DetectorHit>();
        ArrayList<Point3D> intersectionPoints = new ArrayList<>();
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
        for(int i = 0; i < 6; i++){
            Shape3D sector1B = createSector1B();
            sector1B.show();
            sector1B.translateXYZ(0,0, this.getDistanceToTarget());
            sector1B.rotateY(Math.toRadians(this.getTilt()));
            sector1B.rotateZ(Math.toRadians(i * 60));
            this.addComponent(sector1B);

            Shape3D sector2 = createSector2();
            sector2.translateXYZ(0,0, 619.9115061130149);
            sector2.rotateY(Math.toRadians(58.11));
            sector2.rotateZ(Math.toRadians(i * 60));
            this.addComponent(sector2);
        }
    }


    private Shape3D createSector1B(){
        Shape3D sector = new Shape3D();
        Triangle3D triangle1 = new Triangle3D(115.31825,  -407.9/2.0, 0.0,
                                              115.38125,   407.9/2.0, 0.0,
                                              -257.92144, -17.27/2.0, 0.0);
        Triangle3D triangle2 = new Triangle3D(115.38125,   407.9/2.0, 0.0,
                                             -257.92144,  -17.27/2.0, 0.0,
                                             -257.92144,   17.27/2.0, 0.0);
        sector.addFace(triangle1);
        sector.addFace(triangle2);
        return sector;
    }

    private Shape3D createSector2(){
        Shape3D sector = new Shape3D();
        Triangle3D triangle1 = new Triangle3D(-161.9371,  -426.2/2.0, 0.0,
                                              -161.9371,   426.9/2.0, 0.0,
                                              -251.1306,  -371.3/2.0, 0.0);
        Triangle3D triangle2 = new Triangle3D(-161.9371,   426.2/2.0, 0.0,
                                              -251.1306,  -371.3/2.0, 0.0,
                                              -251.1306,   371.3/2.0, 0.0);
        sector.addFace(triangle1);
        sector.addFace(triangle2);
        return sector;
    }
}
