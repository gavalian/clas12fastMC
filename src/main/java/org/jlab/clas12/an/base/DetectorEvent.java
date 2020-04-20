/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.an.base;

import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.physics.LorentzVector;
import org.jlab.jnp.physics.Vector3;

/**
 *
 * @author gavalian
 */
public interface DetectorEvent {
    
    public int     getPid(int index);
    public void    setPid(int pid, int index);
    public int     count();
    public int     getIndex(int pid, int order);
    public void    setStatus(int index, int status);
    public int     getStatus(int index);
    
    public void    getVector(Vector3 v3, int index);
    public void    getVertex(Vector3 v3, int index);
    
    public void    getPath(Path3D path, int index);
    
    public boolean readNext();
    
    public void    combine(LorentzVector vL, int[] index, int[] sign, double[] mass);
    public void    combine(LorentzVector vL, int[]   pid, int[] order, int[] sign, double[] mass);
    
    public double  getResponse(int type, int detector);
    public void    getPosition(Vector3 v3, int detector, int index);
    
    
}
