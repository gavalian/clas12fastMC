/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clas12.fastMC;

import java.util.List;
import java.util.Random;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.core.Clas12FastMC;
import org.jlab.clas12.fastMC.detectors.DCDetectorLayers;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.DataManager;
import org.jlab.jnp.readers.TextFileWriter;
import org.jlab.jnp.utils.benchmark.ProgressPrintout;

/**
 *
 * @author gavalian
 */
public class DebugResolutions {
    
    Clas12FastMC clas12FastMC = Clas12FastMC.clas12forward();
    
    public DebugResolutions(){
        
    }
    
    public void processFile(String filename){
        
        H1F h = new H1F("h",120,0.4,1.8);
        H1F hmc = new H1F("h",120,0.4,1.8);
        
        H1F rh = new H1F("h",120,0.1,1.8);
        H1F rhmc = new H1F("h",120,0.1,1.8);
        
        TCanvas c = new TCanvas("c",1200,1200);
        c.getCanvas().initTimer(5000);
        c.divide(2,2);
        c.cd(0).draw(h).cd(1).draw(hmc);
        c.cd(2).draw(rh).cd(3).draw(rhmc);
        
        clas12FastMC.initResolutions();
        clas12FastMC.setResolution(true);
        
        HipoReader reader = new HipoReader();
        reader.open(filename);
        Bank bank = reader.getBank("mc::event");
        Event event = new Event();
        int counter = 0;
        
        EventFilter filter = new EventFilter("11:2212:211:-211:Xn");
        EventFilter filterrec = new EventFilter("11:211:-211:Xn");
        ProgressPrintout progress = new ProgressPrintout();
        while(reader.hasNext()&&counter<8000000){
            counter++;
            reader.nextEvent(event);
            event.read(bank);
            progress.updateStatus();
            PhysicsEvent phys = DataManager.getPhysicsEvent(10.6, bank);
            if(filter.isValid(phys)==true){
                PhysicsEvent rec = clas12FastMC.processEvent(phys);
                if(filterrec.isValid(rec)==true){
                    //System.out.println(rec.toLundString());
                    //System.out.println(phys.toLundString());
                    Particle pmc = phys.getParticle("[b]+[t]-[11]-[211]-[-211]");
                    Particle p = rec.getParticle("[b]+[t]-[11]-[211]-[-211]");
                    
                    Particle rpmc  = phys.getParticle("[211]+[-211]");
                    Particle rp    = rec.getParticle("[211]+[-211]");
                    
                    h.fill(p.mass());
                    hmc.fill(pmc.mass());
                    
                    rh.fill(rp.mass());
                    rhmc.fill(rpmc.mass());
                    
                }
            }
        }
    }
    
    public static Particle random(int pid, double pmin, double pmax, double thmin, double thmax, double phimin, double phimax){
       double cosmin = Math.cos(thmax);
       double cosmax = Math.cos(thmin);
              
       Random r = new Random();
       
       double cosv = r.nextDouble()*Math.abs(cosmin-cosmax)+cosmin;
       double  theta = Math.acos(cosv);
       //System.out.printf("min/max = %8.6f/%8.5f = %8.6f\n",cosmin,cosmax,cosv);
       double phi = phimin + r.nextDouble()*Math.abs(phimax-phimin);
       double pv   = pmin + r.nextDouble()*Math.abs(pmax-pmin);
       Particle p = Particle.createWithPid(pid, pv*Math.sin(theta)*Math.cos(phi), 
                pv*Math.sin(theta)*Math.sin(phi), pv*Math.cos(theta), 0.0, 0.0, 0.0);
       //p.vector().setPxPyPzM(pv*, pv, pv*Math.cos(thv), part.mass());
       return p;
    }
    
    public static void fill(H2F h , List<DetectorHit> hits){
        for(int i =0; i < hits.size(); i++){
            h.fill(hits.get(i).getComponent(), hits.get(i).getLayer());
        }
    }
    
    public static void fillfi(H1F h , Particle p){
            h.fill((p.vector().phi()+Math.PI)/(2*Math.PI));
    }
    
    public static String getFeatures12( List<DetectorHit>  hits){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < 6; i++){
            str.append(",");
            
            int    index = i*6;
            double summ = 0.0;
            
            for(int k = 0; k < 6; k++){
                summ += hits.get(index+k).getComponent();
            }
            double mean = summ/6.0;
            double valueDown = hits.get(index).getComponent();
            double valueUp   = hits.get(index+5).getComponent();
            double slope     = (valueUp-valueDown)/6.0;
            str.append(String.format("%.4f,%.4f", mean/112.0,(slope+1.25)/2.5));
        }
        return str.toString();
    }
    
    public static String getFeatures6( List<DetectorHit>  hits){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < 6; i++){
            str.append(",");
            double summ = 0.0;
            int   index = i*6;
            for(int k = 0; k < 6; k++){
                summ += hits.get(index+k).getComponent();
            }
            str.append(String.format("%.4f", summ/6.0/112.0));
        }
        return str.toString();
    }
    
    public static String getParticleString(Particle p){
        double mom = (p.vector().p()-0.5)/5.0;
        double tmin = Math.toRadians(5.0);
        double tmax = Math.toRadians(45.0);
        double theta = (p.vector().theta() - tmin)/(tmax-tmin);
        double phi   = (p.vector().phi()+Math.PI)/(2*Math.PI);
        return String.format("%.4f,%.4f,%.4f", mom,theta,phi);
    }
    
    public static void produce(int maxEvents){
        
        TCanvas c = new TCanvas("-c-",1200,900);
        c.getCanvas().initTimer(2000);
        H2F     h2 = new H2F("h2",112,0.5,112.5,36,0.5,36.5);
        H1F     h3 = new H1F("h2",120,0.0,1.0);
        c.divide(1, 2);
        c.cd(0).draw(h2).cd(1).draw(h3);
        
        
        Clas12FastMC fmc = Clas12FastMC.clas12forward();
        DCDetectorLayers dc = new DCDetectorLayers();
        //Particle p = new Particle();
        ProgressPrintout progress = new ProgressPrintout();
        TextFileWriter w1 = new TextFileWriter();
        w1.open("tracks_features_6.csv");
        
        TextFileWriter w2 = new TextFileWriter();
        w2.open("tracks_features_12.csv");
        
        TextFileWriter w3 = new TextFileWriter();
        w3.open("tracks_features_36.csv");
        
        for(int i = 0; i < maxEvents; i++){
            progress.updateStatus();
            Particle p = DebugResolutions.random(11, 0.5, 5.5, 
                    Math.toRadians(5.0), Math.toRadians(45.0),
                    0.0, Math.PI*2.0);
            
            Path3D path = fmc.getPath(p);
            List<DetectorHit>  hits = dc.getHits(path);

            //System.out.printf("%4d : %s\n",hits.size(),p.vector().toString().replace("\\s+", " "));
            if(hits.size()==36){
                /*System.out.printf("%4d : %s,%s\n",
                hits.size(),p.vector().toString(),
                dc.getHitsString(hits));*/
                int sector = hits.get(0).getSector();
                if(sector==3){
                    
                    String part = DebugResolutions.getParticleString(p);
                    String data3 = String.format("%s,%s", part,dc.getHitsString(hits));
                    
                    String data1 = String.format("%s,%d%s", part,sector,DebugResolutions.getFeatures6(hits));
                    String data2 = String.format("%s,%d%s", part,sector,DebugResolutions.getFeatures12(hits));
                    
                    //System.out.println(data);
                    w3.writeString(data3);
                    w1.writeString(data1);
                    w2.writeString(data2);
                    DebugResolutions.fill(h2, hits);
                    DebugResolutions.fillfi(h3, p);
                }
            }
        }
        w1.close();
        w2.close();
        w3.close();
        System.out.println(">>>> DONE");
    }
    /**
     * This program tests the resolution functions on files produced
     * with clasdis with flag --pid 113
     * @param args 
     */
    public static void main(String[] args){
        
        String filename = "dis_generated_10M.hipo";
        
        DebugResolutions res = new DebugResolutions();
        //res.processFile(filename);
        DebugResolutions.produce(1200*5000);
    }
}
