/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.an.base;

/**
 *
 * @author gavalian
 */
public interface EventModifier {
    public boolean apply(DetectorEvent detEvent);
}
