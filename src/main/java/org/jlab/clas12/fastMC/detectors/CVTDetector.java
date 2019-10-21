package org.jlab.clas12.fastMC.detectors;

import org.jlab.clas12.fastMC.base.Detector;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.clas12.fastMC.base.DetectorType;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.geom.prim.Point3D;
import org.jlab.jnp.geom.prim.Shape3D;
import org.jlab.jnp.geom.prim.Triangle3D;

import java.util.List;

public class CVTDetector extends Detector {
    CVTParameters parameters = new CVTParameters();

    public CVTDetector(){
        this.setType(DetectorType.CVT);
        this.setDetectorRegion(DetectorRegion.CENTRAL);
        this.init();
    }


    @Override
    public List<DetectorHit> getHits(Path3D path) {
        return null;
    }

    @Override
    public boolean validHit(Path3D path) {
        return false;
    }

    @Override
    public void init() {
        for(int i = 1; i < 5; i ++){
            int numRegions = parameters.getNumRegions(i);
            double zDistance = parameters.getZDistance(i);
            double yDistance = parameters.getYDistance(i);
            for(int j = 0; j < numRegions; j++){
                Shape3D component = this.createComponent();
                component.translateXYZ(0,0, zDistance);
                component.translateXYZ(0, yDistance, 0);
                component.rotateY(Math.toRadians(360.0/j));
            }
        }
    }

    private Shape3D createComponent(){
        Shape3D component = new Shape3D();
        Point3D point1 = new Point3D(2.1, 0.0, -33.51);
        Point3D point2 = new Point3D(2.1, 0.0, 0.0);
        Point3D point3 = new Point3D(-2.1, 0.0, 0.0);
        Point3D point4 = new Point3D(-2.1, 0.0, -33.51);

        Triangle3D triangle1 = new Triangle3D(point1, point2, point3);
        Triangle3D triangle2 = new Triangle3D(point1, point3, point4);

        component.addFace(triangle1);
        component.addFace(triangle2);
        return component;
    }
}
