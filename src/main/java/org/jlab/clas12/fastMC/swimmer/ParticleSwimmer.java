/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.fastMC.swimmer;

import cnuphys.magfield.CompositeField;
import cnuphys.magfield.MagneticFieldInitializationException;
import cnuphys.magfield.MagneticFields;
import cnuphys.magfield.Solenoid;
import cnuphys.magfield.Torus;
import cnuphys.rk4.RungeKuttaException;
import cnuphys.swim.DefaultListener;
import cnuphys.swim.DefaultSwimStopper;
import cnuphys.swim.SwimTrajectory;
import cnuphys.swim.Swimmer;
import cnuphys.swimZ.SwimZ;
import cnuphys.swimZ.SwimZException;
import cnuphys.swimZ.SwimZResult;
import cnuphys.swimZ.SwimZStateVector;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jlab.clas12.fastMC.tests.SwimmerTest;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.physics.Particle;


/**
 *
 * @author gavalian
 */
public class ParticleSwimmer {
    
    private  CompositeField compositeField;
    private  MagneticFields mf;
    
    private static final double rmax = 10.0;
    private static final double maxPathLength = 12.5;
    private static final double hdata[] = new double[3];
    private  Swimmer swimmer = null;
    private  SwimZ   swimmerZ = null;
    
    public ParticleSwimmer(){                
        //this.initMagneticField(-1.0, -1.0);                
        this.initMagneticFieldResources(-1.0, -1.0);
        swimmer = new Swimmer();
    }
    
    public ParticleSwimmer(double torusScale, double solenoidScale){
        
        this.initMagneticFieldResources(torusScale, solenoidScale);
        
        if(compositeField == null){
            System.out.println(" NULL FIELD");
        } else {
            System.out.println(" NOT A NULL FIELD");
        }                
        swimmer = new Swimmer();
        swimmerZ = new SwimZ();
    }
    
    
    private void initMagneticFieldResources(Double torusScale, Double solenoidScale){
        

        String filetorus = "/data/magfield/clas12-fieldmap-torus.dat";
        String filesolenoid = "/data/magfield/clas12-fieldmap-solenoid.dat";
        
         mf = MagneticFields.getInstance();
         InputStream    in_torus = getClass().getResourceAsStream(filetorus);
         InputStream in_solenoid = getClass().getResourceAsStream(filesolenoid);
         

        try {
            mf.initializeMagneticFieldsFromFile(in_torus,in_solenoid,filetorus,filesolenoid);
            //mf.initializeMagneticFields(jnpDataDirectory + "/etc/data/magfield", "clas12-fieldmap-torus.dat", "clas12-fieldmap-solenoid.dat");
        } catch (MagneticFieldInitializationException | FileNotFoundException ex) {
            Logger.getLogger(ParticleSwimmer.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }
    
    
    private void initMagneticField(Double torusScale, Double solenoidScale){
        String jnpDataDirectory = System.getenv("JNP_DATA");
         if(jnpDataDirectory==null){
             jnpDataDirectory = System.getProperty("JNP_DATA");
         }
         
         if(jnpDataDirectory==null){
             System.out.println("[ParticleSwimmer] : Oooops, this doesn't look rigth......");
             System.out.println("[ParticleSwimmer] : environment JNP_DATA is not set. no fileds will be loaded.");
             System.out.println("[ParticleSwimmer] : We are looking for two files to be present on your system.");
             System.out.println("[ParticleSwimmer] : Torus    : $JNP_DATA/etc/data/magfield/clas12-fieldmap-torus.dat");
             System.out.println("[ParticleSwimmer] : Solenoid : $JNP_DATA/etc/data/magfield/clas12-fieldmap-solenoid.dat");
             System.out.println("\n\n");
         }
         
         mf = MagneticFields.getInstance();
         try {
             
             mf.initializeMagneticFields(jnpDataDirectory + "/etc/data/magfield", "clas12-fieldmap-torus.dat", "clas12-fieldmap-solenoid.dat");
         } catch (FileNotFoundException | MagneticFieldInitializationException ex) {
             Logger.getLogger(ParticleSwimmer.class.getName()).log(Level.SEVERE, null, ex);
         }        
    }
    /**
     * initializes the magnetic field. it should be located in the COATJAVA
     * distribution with relative path "/etc/data/magfield"
     * @param torusScale scale of the toroidal field
     * @param solenoidScale scale of the solenoid field
     */
    private void initFiled(Double torusScale, Double solenoidScale){
        Torus torus = null;
         Solenoid solenoid = null;
         //will read mag field assuming
         String jnpDataDirectory = System.getenv("JNP_DATA");
         if(jnpDataDirectory==null){
             jnpDataDirectory = System.getProperty("JNP_DATA");
         }
         
         if(jnpDataDirectory==null){
             System.out.println("[ParticleSwimmer] : Oooops, this doesn't look rigth......");
             System.out.println("[ParticleSwimmer] : environment JNP_DATA is not set. no fileds will be loaded.");
             System.out.println("[ParticleSwimmer] : We are looking for two files to be present on your system.");
             System.out.println("[ParticleSwimmer] : Torus    : $JNP_DATA/etc/data/magfield/clas12-fieldmap-torus.dat");
             System.out.println("[ParticleSwimmer] : Solenoid : $JNP_DATA/etc/data/magfield/clas12-fieldmap-solenoid.dat");
             System.out.println("\n\n");
         }
         
         //String clasDictionaryPath = CLASResources.getResourcePath("etc");
         String magfieldDir = jnpDataDirectory + "/etc/data/magfield/";

         String torusFileName = System.getenv("JNP_MAGFIELD_TORUSMAP");
         if (torusFileName==null) torusFileName = "clas12-fieldmap-torus.dat";
         File torusFile = new File(magfieldDir + torusFileName);
         try {
             torus = Torus.fromBinaryFile(torusFile);
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         }
         
         //OK, see if we can create a Solenoid
         String solenoidFileName = System.getenv("JNP_MAGFIELD_SOLENOIDMAP");
         if (solenoidFileName==null) solenoidFileName = "clas12-fieldmap-solenoid.dat";
         //OK, see if we can create a Torus
         /*if(clasDictionaryPath == "../clasJLib")
             solenoidFileName = clasDictionaryPath + "/data/solenoid/v1.0/solenoid-srr.dat";
         */
         File solenoidFile = new File(magfieldDir + solenoidFileName);
         try {
             solenoid = Solenoid.fromBinaryFile(solenoidFile);
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         }
         		
         compositeField = new CompositeField();
         
         if (solenoid != null) {
             solenoid.setScaleFactor(solenoidScale);
             compositeField.add(solenoid);
             System.out.println("is zero field " + compositeField.isZeroField());
         }
         
         if (torus != null) {             
             torus.setScaleFactor(torusScale);
             compositeField.add(torus);
         }
         
         
         System.out.println ( "  SCALE FACTOR = " + compositeField.getScaleFactor());
    }
    
    
    public Path3D getParticlePathZ(Particle part){
        Path3D path = new Path3D();
        int Q = part.charge();
        SwimZStateVector stateVec = new SwimZStateVector(
                part.vertex().x(), part.vertex().y(),part.vertex().z(),
                part.px()/part.p(),part.py()/part.p());
        double[] hdata = new double[3];
        try {
            SwimZResult result = swimmerZ.adaptiveRK(Q, part.p(), stateVec, 800.00, 10e-4, hdata);
            List<SwimZStateVector> trajectory = result.getTrajectory();
            for(int i = 0; i < trajectory.size(); i++){
                path.addPoint(trajectory.get(i).x, trajectory.get(i).y, trajectory.get(i).z);
            }
        } catch (SwimZException ex) {
            Logger.getLogger(ParticleSwimmer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return path;
    }
    /**
     * Returns a Path3D object for particle swam through the magnetic field.
     * For neutral particles returns a simple path of a straight line that 
     * originates at the vertex.
     * @param part particle
     * @return 
     */
    public Path3D getParticlePath(Particle part){
        
        if(part.charge()==0){
            Path3D  ppath = new Path3D();
            ppath.addPoint(part.vertex().x(), 
                    part.vertex().y(),part.vertex().z());
            ppath.addPoint(
                    1500.0 * part.px(),
                    1500.0 * part.py(),
                    1500.0 * part.pz()
            );
            return ppath;
        }
        
        DefaultListener listener = new DefaultListener();
        DefaultSwimStopper stopper = new DefaultSwimStopper(rmax);
        
        // step size in m
        double stepSize = 5e-4; // m
        int  charge     = part.charge();
        //System.out.println("CHARGE = " + charge);
        float data[] = new float[3];        
//        swimmer.getProbe().field(0.5f,0.5f,0.5f, data);
//        System.out.println(" FILED = " + data[0] + " " + data[1] + "  " + data[2]);
        try {
            /*
            int nstep = swimmer.swim(charge, 
                    part.vertex().x(),part.vertex().y(),part.vertex().z(),
                    part.vector().p(),part.vector().theta(),part.vector().phi(),
                    stopper, listener, maxPathLength,
                    stepSize, Swimmer.CLAS_Tolerance, hdata);*/
            SwimTrajectory traj = swimmer.swim(charge, 
                                        part.vertex().x()/100.0,part.vertex().y()/100.0,part.vertex().z()/100.0,
                                        part.vector().p(),
                                        Math.toDegrees(part.vector().theta()),
                                        Math.toDegrees(part.vector().phi()),
                                        stopper, maxPathLength, stepSize,
					Swimmer.CLAS_Tolerance, hdata);
                        
                        
            Path3D  particlePath = new Path3D();
            
            for(int loop = 0; loop < traj.size(); loop++){
                particlePath.addPoint(
                        100.0*traj.get(loop)[0], 
                        100.0*traj.get(loop)[1], 
                        100.0*traj.get(loop)[2] 
                );
            }
            return particlePath;
            //double[] lastY = listener.getLastStateVector();
            //printSummary("\nresult from adaptive stepsize method with errvect",
            //        nstep, momentum, lastY, hdata);
            
       } catch (RungeKuttaException e) {
           e.printStackTrace();
       }
        return null;
    }
    
    
    public void readResourceFile(){
        try {
            InputStream in = getClass().getResourceAsStream("/data/magfield/clas12-fieldmap-solenoid.dat");
            DataInputStream dos = new DataInputStream(in);
            int magicnum = dos.readInt();
            System.out.printf("%8d -> %X\n",magicnum,magicnum);
        } catch (IOException ex) {
            Logger.getLogger(ParticleSwimmer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException{
        
        
        ParticleSwimmer sw = new ParticleSwimmer();
        //sw.readResourceFile();        
        /*
        try {
            ParticleSwimmer sw = new ParticleSwimmer();
            
            URL url = sw.getClass().getResource("/data/magfield/clas12-fieldmap-torus.dat");
            System.out.println(url);
            
            System.out.println("file : " + url.getFile());
            System.out.println("\n\n>>> URI : " + url.toURI().toString());
            
            
            File f = new File(url.toURI());
            System.out.println(" file exists : " + f.exists());
            //System.out.println("ref  : " + url.getQuery());
        } catch (URISyntaxException ex) {
            Logger.getLogger(ParticleSwimmer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       */
        //FileReader fileReader = new FileReader("src/main/resources/file.txt");
        //BufferedReader bufferedReader = new BufferedReader(fileReader);
        
        //Stream<String> lines = bufferedReader.lines();
                
    }
}
