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
    
    public static final int FRAME_LOCAL       = 1;
    public static final int FRAME_GLOBAL      = 2;
    public static final int RESPONSE_ENERGY   = 1;
    public static final int RESPONSE_TIME     = 2;
    public static final int PROP_TRIGGERBITS  = 45;
    public static final int PROP_RUNNUMBER    = 46;
    public static final int PROP_EVENTNUMBER  = 47;
    public static final int PROP_UNIXTIME     = 48;
    public static final int PROP_TIMESTAMP    = 49;
    
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
    
    
    public double  getResponse( int responsetype, int detector, int layer, int particle);
    public boolean getPosition( Vector3 v3, int detector, int layer, int particle, int frame);
    public int     getProperty(int propertyType, int particle);
    
    public long    getEventProperty(int type, int flag);
    
    //public long    getParticle(<T extends Particle> p );
    
  /*  public static class DetectorResponseConstraint{
        int type;
        double min; 
        int max;
        
        public DetectorResponseConstraint(int i, double __min, double __max){
            
        }
        
        public boolean isValid(DetectorEvent event, int particle){
            double property = event.getResponse(type,particle);
            return property>min&&property<max?true:false;
        }
    }*/
}
