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
import org.jlab.jnp.geom.prim.Path3D;

/**
 *
 * @author gavalian
 */
public class ECDetector extends Detector {

    @Override
    public List<DetectorHit> getHits(Path3D path) {
        List<DetectorHit> hits = new ArrayList<DetectorHit>();
        return hits;
    }
    
}
