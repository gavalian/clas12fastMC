/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clas12.fastMC;

import java.util.List;
import org.jlab.clas12.fastMC.base.DetectorHit;
import org.jlab.clas12.fastMC.core.Clas12FastMC;
import org.jlab.clas12.fastMC.detectors.DCDetectorLayers;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.DataManager;
import org.jlab.jnp.readers.TextFileWriter;
import org.jlab.jnp.utils.benchmark.ProgressPrintout;

/**
 *
 * @author gavalian
 */
public class ProcessJPSI {
    public static void main(String[] args){

        String filename = "negative_sample_fmc.h5";
        int label = 0;
        if(args.length>0) filename= args[0];
        if(args.length>1) label = Integer.parseInt(args[1]);
        
        Clas12FastMC clas12FastMC = Clas12FastMC.clas12forward();
        
        DCDetectorLayers dc = new DCDetectorLayers();
        HipoReader reader = new HipoReader();
        reader.open(filename);
        
        Event event = new Event();
        Bank  bank  = reader.getBank("rec::event");
        Bank  mcbank  = reader.getBank("mc::event");
        ProgressPrintout progress = new ProgressPrintout();
        TextFileWriter w = new TextFileWriter();
        
        w.open("phys_training.csv");
        int counter = 0;
        while(reader.hasNext()&&counter<50000){
            reader.nextEvent(event);
            event.read(bank);
            event.read(mcbank);
            
            progress.updateStatus();
            
            PhysicsEvent physRec = DataManager.getPhysicsEvent(10.5, bank);
            PhysicsEvent physGen = DataManager.getPhysicsEvent(10.5, mcbank);
            
            Particle first = physRec.getParticle(0);

            if(first.pid()==2212){
                //System.out.println(physRec.toLundString());            
                Particle gelec = physGen.getParticle(0);
                String   q2part = String.format("%.5f,%.5f,%.5f", 
                        gelec.vector().p()/10.0,
                        gelec.vector().theta()/Math.toRadians(5.0),
                        (Math.PI + gelec.vector().phi())/(2.0*Math.PI)
                );
                //System.out.printf("%d - theta = %f\n ",gelec.pid(),
                //        gelec.vector().theta()*57.29);
            Particle elec = physRec.getParticleByPid(11,0);
            Particle pos  = physRec.getParticleByPid(-11,0);
            Particle prot = physRec.getParticleByPid(2212,0);
            
            List<DetectorHit> hits_elec = dc.getHits(clas12FastMC.getPath(elec));
            List<DetectorHit> hits_prot = dc.getHits(clas12FastMC.getPath(prot));
            List<DetectorHit> hits_pos  = dc.getHits(clas12FastMC.getPath(pos));
            
            
            if(hits_elec.size()==36&&hits_pos.size()==36&&hits_prot.size()==36
                    &&gelec.vector().theta()<Math.toRadians(5.0)){
                StringBuilder str = new StringBuilder();
                str.append(String.format("%d,%.4f,%.4f,%.4f,",label,
                        elec.vector().px(),elec.vector().py(),elec.vector().pz()
                ));
                str.append(String.format("%.4f,%.4f,%.4f,",
                        pos.vector().px(),pos.vector().py(),pos.vector().pz()
                ));
                str.append(String.format("%.4f,%.4f,%.4f,",
                        prot.vector().px(),prot.vector().py(),prot.vector().pz()
                ));
                str.append(q2part).append(",");
                str.append(dc.getHitsStringSector(hits_elec));
                str.append(",").append(dc.getHitsStringSector(hits_pos));
                str.append(",").append(dc.getHitsStringSector(hits_prot));
                w.writeString(str.toString());
                counter++;
            }
            }
        }
        w.close();
    }
}
