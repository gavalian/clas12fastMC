/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.an.abs;

/**
 *
 * @author gavalian
 */
public interface Reference {
    public void insert(int detector_type, int pindex);
    public int  get(int detector_type);
    public void clear();
}
