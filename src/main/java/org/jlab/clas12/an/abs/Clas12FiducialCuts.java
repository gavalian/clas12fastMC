/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.an.abs;

import org.jlab.clas12.an.abs.DetectorFiducialCuts.ParticleFiducialConfig;
import org.jlab.groot.data.DataLine;
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.detector.base.Detector;
import org.jlab.jnp.detector.base.DetectorManager;
import org.jlab.jnp.detector.base.DetectorType;
import org.jlab.jnp.detector.ec.ECALDetector;
import org.jlab.jnp.geom.prim.Mesh3D;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.physics.Vector3;

/**
 *
 * @author gavalian
 */
public class Clas12FiducialCuts {
    
    public static void configure(DetectorFiducialCuts fiducial){

        ParticleFiducialConfig   proton = new ParticleFiducialConfig(2212);        
        ParticleFiducialConfig   photon = new ParticleFiducialConfig(22);
        ParticleFiducialConfig electron = new ParticleFiducialConfig(11);

        proton.addDetector(DetectorManager.getInstance().getDetector(DetectorType.FTOF));
        photon.addDetector(DetectorManager.getInstance().getDetector(DetectorType.ECAL));
        
        electron.addDetector(DetectorManager.getInstance().getDetector(DetectorType.FTOF));
        electron.addDetector(DetectorManager.getInstance().getDetector(DetectorType.ECAL));
        
        fiducial.addConfig(photon).addConfig(proton).addConfig(electron);
    }
    
    public static void redoECAL(){
        ECALDetector det = (ECALDetector) DetectorManager.getInstance().getDetector(DetectorType.ECAL);
        det.getBounds().clear();
        float[] SURFACE_LOSE = new float[]{
            89.90000f,   -189.74298f,      0.00000f,
          -280.97011f,      0.00000f,      0.00000f,
            89.90000f,    189.74298f,      0.00000f
       };
        float[] SURFACE = new float[] {
            89.90000f,   -179.63349f,      0.00000f,
    	  -261.21018f,      0.00000f,      0.00000f,
    	    89.90000f,    179.63349f,      0.00000f
        };
        
        float[] SURFACE_TIGHT = new float[]{
            89.90000f ,  -174.57875f,      0.00000f,
            -251.33021f,      0.00000f,      0.00000f,
            89.90000f,    174.57875f,      0.00000f
        };
                
        double[] align = new double[]{
            -5.26      , -1.540     , 5.02,  
            -5.03      , -0.688     , 5.02,
            -4.89      , -0.624     , 5.02,
            -5.04      , -0.850     , 5.02,   
            -4.93      , -0.490     , 5.02,                
            -5.31      , -1.186     , 5.02
        };
        
        for(int i = 0; i < 6; i++){
            Mesh3D mesh = new Mesh3D(SURFACE_TIGHT,new int[]{0,1,2});//Mesh3D.triangle(HEIGHT_X + HEIGHT_MX, LENGTH_Y );
            //double moveX = 40.0;
            double rotateY = Math.toRadians(25.0);
            double rotateZ = Math.toRadians(i*60.0);
            int index = i*3;
            mesh.translateXYZ(align[index], align[index+1], align[index+2]);
            mesh.translateXYZ(0.0, 0.0, 698.28000 - 0.50000);
            mesh.rotateY(rotateY);
            mesh.rotateZ(rotateZ);
            det.getBounds().add(mesh);
        }

    }
    public static void main(String[] args){
        String filename = "/Users/gavalian/Work/DataSpace/clas12dst/rec_clas_005038.evio.00390-00394.hipo";
        
        DetectorManager.getInstance().initFiducial();
        Clas12FiducialCuts.redoECAL();
        
        HipoChain chain = new HipoChain();
        chain.addFile(filename);
        chain.open();
        
        Clas12Event clas12Event = new Clas12Event(chain);
        
        DetectorFiducialCuts fiducial = new DetectorFiducialCuts();
        
        Clas12FiducialCuts.configure(fiducial);
        
        
        Path3D path = new Path3D();
        Vector3 vecL = new Vector3();
        Vector3 vecP = new Vector3();
        TCanvas c1 = new TCanvas("c1",500,500);
        c1.getCanvas().initTimer(500);
        
        c1.divide(2, 2);
        H2F h2  = new H2F("h2",240,-420,420,240,-420,420);
        H2F h2a = new H2F("h2",240,-420,420,240,-420,420);
        H2F h2r = new H2F("h2",120,0,100,120,0,100);
        H2F h2u = new H2F("h2",120,0,100,120,0,100);
        DataLine linev = new DataLine(15.,15,15.0,100);
        DataLine lineh = new DataLine(15.,15,100.0,15);
        
        linev.setLineColor(2).setLineStyle(3);
        lineh.setLineColor(2).setLineStyle(3);
        
        c1.cd(0).draw(h2);
        c1.cd(1).draw(h2r).draw(lineh).draw(linev);
        c1.cd(2).draw(h2a);
        c1.cd(3).draw(h2u).draw(lineh).draw(linev);
        while(clas12Event.readNext()==true){
            
            int count = clas12Event.count();
            //System.out.println(">>>>> EVENT particle count = " + count);
            int photonIndex = clas12Event.getIndex(22,0);
            if(photonIndex>=0){
                int status = clas12Event.getStatus(photonIndex);
                clas12Event.getPath(path, photonIndex);
                if(path.getNumLines()>1){
                    //path.show();
                    //System.out.println("N LINES = " + path.getNumLines());
                //System.out.println("status = " + status);
                fiducial.apply(clas12Event);
                int statusFid = clas12Event.getStatus(photonIndex);
                //System.out.println("status FID = " + statusFid);
                vecL.setXYZ(0.0, 0.0, 0.0);
                vecP.setXYZ(0.0, 0.0, 0.0);
                
                clas12Event.getPosition(vecL, 1, photonIndex);
                clas12Event.getPosition(vecP, 2, photonIndex);
                System.out.printf("%3d %5d %12.5f %12.5f %12.5f %12.5f %12.5f %12.5f \n",
                        statusFid,status,
                        vecL.x(),vecL.y(),vecL.z(),
                        vecP.x(),vecP.y(),vecP.z()
                        );
                if(statusFid<0) {
                    h2.fill(vecP.x(), vecP.y());
                    h2r.fill(vecL.y(), vecL.z());
                }
                if(statusFid>0) {
                    h2a.fill(vecP.x(), vecP.y());
                    h2u.fill(vecL.y(), vecL.z());
                }
                } 
            }
        }
    }
}
