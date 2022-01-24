/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clas12.fastMC;

import java.util.Arrays;
import java.util.List;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.clas12.fastMC.core.Clas12FastMC;
import org.jlab.clas12.fastMC.detectors.DCDetector;
import org.jlab.clas12.fastMC.detectors.DCDetectorLayers;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.readers.TextFileWriter;
import org.jlab.jnp.utils.benchmark.ProgressPrintout;

/**
 *
 * @author gavalian
 */
public class ProcessMCDC {
    
    public static double[] getFetures6(List<DetectorHit> layers){
        double[] means = new double[6];
        for(int i = 0; i < 6; i++){
            double summ = 0.0;
            int   count = 0;
            for(int s = 0; s < 6; s++){
                summ += layers.get(i*6+s).getComponent();
                count++;
            }
            means[i] = summ/count/112.0;
        }
        return means;
    }
    
    public static double[] getFetures9(List<DetectorHit> layers){
        
        double[] means = ProcessMCDC.getFetures6(layers);
        double[] features = new double[9];
        for(int k = 0; k < means.length; k++){ features[k] = means[k];}
        features[6] = means[1]-means[0];
        features[7] = means[3]-means[2];
        features[8] = means[5]-means[4];
        
        features[6] = (features[6]+0.1)/0.2;
        features[7] = (features[7]+0.1)/0.2;
        features[8] = (features[8]+0.1)/0.2;
        
        
        return features;
    }
    
    public static double[] getFetures11(List<DetectorHit> layers){
        
        double[] means = ProcessMCDC.getFetures6(layers);
        double[] features = new double[11];
        for(int k = 0; k < means.length; k++){ features[k] = means[k];}
        features[ 6] = means[1]-means[0];
        features[ 7] = means[3]-means[2];
        features[ 8] = means[5]-means[4]; 
        
        features[6] = (features[6]+0.1)/0.2;
        features[7] = (features[7]+0.1)/0.2;
        features[8] = (features[8]+0.1)/0.2;
        
        features[ 9] = (means[0]+means[1])*0.5 - (means[3]+means[2])*0.5;
        features[10] = (means[3]+means[2])*0.5 - (means[4]+means[5])*0.5;
        
        features[9]  = (features[9] + 0.03)/(0.15-0.03);
        features[10] = (features[10] + 0.04)/(0.4-0.04);
        return features;
    }
    
    public static boolean isValid(double[] f){
        for(int i = 0; i < f.length; i++)
            if(f[i]<0.0||f[i]>1.000) return false;
        return true;
    }
    
    public static double[] getOutputs(Particle p){
        double[] result = new double[]{
            (p.vector().p()-0.5)/5.0,
            (Math.toDegrees(p.vector().theta())-5.0)/40.0,
            (p.vector().phi()+Math.PI/6)/(Math.PI/3)
        };
        return result;
    }
    public static void main(String[] args){
        //Particle p = new Particle();
        
        DCDetectorLayers dc = new DCDetectorLayers();
        DCDetector       det = new DCDetector();
        
        Clas12FastMC clas12FastMC = new Clas12FastMC();
        TextFileWriter w = new TextFileWriter();
        w.open("mc_particle_train.csv");
        
        clas12FastMC.addConfiguration(11, DetectorRegion.FORWARD,   "DC", 6);
        ProgressPrintout progress = new ProgressPrintout();
        //for(int k = 0; k < 1000000; k++){
        for(int k = 0; k < 1000000; k++){

            progress.updateStatus();
            
            Particle p = Particle.random(11, 0.5, 5.5, 
                    Math.toRadians(5), Math.toRadians(45), 
                    -Math.PI/6, Math.PI/6);
            Path3D path = clas12FastMC.getPath(p);
            //path.show();
            List<DetectorHit> hits = det.getHits(path);
            List<DetectorHit> layers = dc.getHits(path);
            //int sector = dc.getSectorIntersection(path);
            //System.out.println(layers.size() + "  " + hits.size() + " >>>> " + p);
            //System.out.println(DCDetectorLayers.dataString(layers));
            if(layers.size()==36){
                double[] means   = ProcessMCDC.getFetures6(layers);
                double[] means9  = ProcessMCDC.getFetures9(layers);
                double[] means11 = ProcessMCDC.getFetures11(layers);
                double[]  out = ProcessMCDC.getOutputs(p);
                String dataString = String.format("%s,%s",
                        Arrays.toString(means),Arrays.toString(out));
                String dataString9 = String.format("%s,%s",
                        Arrays.toString(means9),Arrays.toString(out));
                String dataString11 = String.format("%s,%s",
                        Arrays.toString(means11),Arrays.toString(out));
                
                String fixedString = dataString
                        .replaceAll("\\[", "")
                        .replaceAll("]", "")
                        .replaceAll(" ", "");
                
                String fixedString9 = dataString9
                        .replaceAll("\\[", "")
                        .replaceAll("]", "")
                        .replaceAll(" ", "");
                
                String fixedString11 = dataString11
                        .replaceAll("\\[", "")
                        .replaceAll("]", "")
                        .replaceAll(" ", "");
                //System.out.println(fixedString);
                
                int sector = layers.get(0).getSector();
                if(sector==1){
                    if(ProcessMCDC.isValid(means)) w.writeString("f6--" + fixedString);
                    if(ProcessMCDC.isValid(means9)) w.writeString("f9--" + fixedString9);
                    if(ProcessMCDC.isValid(means11)) w.writeString("f11--" + fixedString11);
                }
                //System.out.println( " sector = " + layers.get(0).getSector());
            }
        }
        w.close();
    }
}
