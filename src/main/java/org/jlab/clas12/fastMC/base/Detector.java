/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.base;

import java.util.List;
import org.jlab.jnp.geom.prim.Path3D;

/**
 *
 * @author gavalian
 */
public abstract class Detector {
    public abstract List<DetectorHit> getHits(Path3D path);
}
