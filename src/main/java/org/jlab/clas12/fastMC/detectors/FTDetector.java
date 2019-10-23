package org.jlab.clas12.fastMC.detectors;

import org.apache.commons.math3.geometry.Vector;
import org.jlab.clas12.fastMC.base.Detector;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.clas12.fastMC.base.DetectorType;
import org.jlab.jnp.geom.prim.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FTDetector extends Detector {

    public FTDetector(){
        this.setDetectorRegion(DetectorRegion.TAGGER);
        this.setType(DetectorType.FT);
        this.setDistanceToTarget(189.8);
        this.init();
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
    public boolean validHit(Path3D path) {
        return getHits(path).size() > 0;
    }

    @Override
    public void init() {
        Sector3D sector3D = createCal();
        sector3D.show();
        Shape3D calorimeter = new Shape3D();
        calorimeter.addFace(createCal());
        this.addComponent(calorimeter);
    }

    private Sector3D createCal(){
        Arc3D calArc = new Arc3D(new Point3D(16.83, 0.0, 0.0), //radius
                                 new Point3D(0.0, 0.0, 0.0), //center
                                 new Vector3D(0.0, 0.0, 1.0), //normal vecot
                                 Math.toRadians(360)); //theta in rads
        calArc.translateXYZ(0,0,this.getDistanceToTarget());
        return new Sector3D(calArc, 10.83);
    }
}
