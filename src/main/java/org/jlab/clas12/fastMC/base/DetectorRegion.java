/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.base;

/**
 *
 * @author gavalian
 */
public enum DetectorRegion {
    
    UNDEFINED (0,"UNDEFINED"),
    FORWARD   (1, "FORWARD"),
    TAGGER    (2, "TAGGER"),
    CENTRAL   (3, "CENTRAL");
    
    private final int    regionType;
    private final String regionName;
    
    DetectorRegion(){
        regionType = 0;
        regionName = "UNDEFINED";
    }
    
    DetectorRegion(int type, String name){
        regionName = name; regionType = type;
    }
    
    public String  getName(){ return regionName;}
    public int     getType(){ return regionType;}
    
    
    public static DetectorRegion getType(Integer detId) {

        for(DetectorRegion id: DetectorRegion.values())
            if (id.getType() == detId) 
                return id;
        return UNDEFINED;
    }
}
