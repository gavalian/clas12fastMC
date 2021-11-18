/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clas12.fastMC;

import java.util.List;
import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.clas12.fastMC.core.Clas12FastMC;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.Schema;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.hipo4.io.HipoWriter;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.DataManager;
import org.jlab.jnp.utils.benchmark.ProgressPrintout;
import org.jlab.jnp.utils.options.OptionStore;

/**
 *
 * @author gavalian
 */
public class ProcessMC {
    
    HipoChain chain = new HipoChain();
    HipoWriter writer = new HipoWriter();
    
    String outputFile = "clas12fastmc_output.hipo";
    
    EventFilter  mcFilter = new EventFilter();
    EventFilter  outFilter = new EventFilter();
    Clas12FastMC clas12FastMC = null;//new Clas12FastMC();
            
    public ProcessMC(){
        //System.setProperty("JNP_DATA","./");
    }
    
    public HipoChain getChain(){return this.chain;}
    
    public ProcessMC setOutput(String output){
        outputFile = output; return this;
    }
    
    public void open(){
        chain.open();
        writer.getSchemaFactory().copy(chain.getSchemaFactory());
        Schema recSchema = LundConverter.getParticleSchema("rec::event",22002,1);
        writer.getSchemaFactory().addSchema(recSchema);
        writer.setCompressionType(1);
        writer.open(outputFile);
    }
    
    public ProcessMC setMCFilter(String filter){
        mcFilter = new EventFilter(filter); return this;
    }
    
    public ProcessMC setOutFilter(String filter){
        outFilter = new EventFilter(filter); return this;
    }
    public void event2bank(Bank b, PhysicsEvent pe){
        int rows = pe.count();
        b.setRows(rows);
        for(int i = 0; i < rows; i++){
            b.putInt("pid", i, pe.getParticle(i).pid());
            b.putFloat("px", i, (float) pe.getParticle(i).vector().px());
            b.putFloat("py", i, (float) pe.getParticle(i).vector().py());
            b.putFloat("pz", i, (float) pe.getParticle(i).vector().pz());
            b.putFloat("vx", i, (float) pe.getParticle(i).vertex().x());
            b.putFloat("vy", i, (float) pe.getParticle(i).vertex().y());
            b.putFloat("vz", i, (float) pe.getParticle(i).vertex().z());
            b.putInt("charge", i, pe.getParticle(i).charge());
            b.putInt("status", i, 1);
            b.putFloat("chi2", i, 1.0f);
            b.putFloat("beta", i, 0.0f);            
        }
    }
    
    
    public void process(){
        
        Event event = new Event();
        Bank mcParticle  = new Bank(chain.getSchemaFactory().getSchema("mc::event"));
                
        Schema recSchema = LundConverter.getParticleSchema("rec::event",22002,1);
        Bank   recParticle = new Bank(recSchema);
                
        int counter = 0; int counterFW = 0; int counterP = 0;
        ProgressPrintout progress = new ProgressPrintout();
        
        clas12FastMC.show();
        
        while(chain.hasNext()==true){
            counter++;
            counterFW++;
            chain.nextEvent(event);
            event.read(mcParticle);
            
            progress.updateStatus();
            PhysicsEvent mcEvent = DataManager.getPhysicsEvent(10.6, mcParticle);
            //PhysicsEvent outEvent = new PhysicsEvent();
            if(mcFilter.isValid(mcEvent)==true){
                PhysicsEvent recEvent = clas12FastMC.processEvent(mcEvent);            
                if(outFilter.isValid(recEvent)==true){
                    this.event2bank(recParticle, recEvent);
                    event.write(recParticle);
                    writer.addEvent(event);
                }
            }
        }
        System.out.printf("===> isolated events # %d / %d / %d\n",
                counter,counterP,counterFW);
        //System.out.printf("===> isolated events # %d\n",counter);
        writer.close();
    }
    
    public void init(){
        System.out.println("INITIALIZING FAST MC ");
        clas12FastMC = new Clas12FastMC();
        
        clas12FastMC.addConfiguration(2212, DetectorRegion.FORWARD,   "DC", 6);
        clas12FastMC.addConfiguration(2212, DetectorRegion.FORWARD, "FTOF", 1);
        // Different ways you can detect proton
        // This is proton in forward detector
        clas12FastMC.addConfiguration(211, DetectorRegion.FORWARD,   "DC", 6);
        clas12FastMC.addConfiguration(211, DetectorRegion.FORWARD, "FTOF", 1);
        
        clas12FastMC.addConfiguration(-211, DetectorRegion.FORWARD,   "DC", 6);
        clas12FastMC.addConfiguration(-211, DetectorRegion.FORWARD, "FTOF", 1);
        
        clas12FastMC.addConfiguration(11, DetectorRegion.FORWARD,   "DC", 6);
        clas12FastMC.addConfiguration(11, DetectorRegion.FORWARD, "FTOF", 1);
        clas12FastMC.addConfiguration(11, DetectorRegion.FORWARD, "ECAL", 1);                
        
        clas12FastMC.show();
        //clas12FastMC.addConfiguration(11, DetectorRegion.FORWARD, "FTOF", 1);
    }
    
    public static void debug(){
        ProcessMC mc = new ProcessMC();
        
        mc.getChain().addFile("dis_generated.hipo");
        mc.setOutput("a5.hipo");
        String filter = "X+:X-:Xn";
        
        mc.setMCFilter(filter).setOutFilter(filter);
        mc.init();
        mc.open();
        mc.process();
    }
    
    public static void main(String[] args){        
        
        //if(args.length==0) ProcessMC.debug();
        
        OptionStore store = new OptionStore("clas12fastmc");
        store.addCommand("-convert", "converts lund files to a hipo file");
        store.getOptionParser("-convert").addRequired("-o", "output file name");
        
        store.addCommand("-fastmc", "runs the file through fast mc and writes output bank rec::event");
        store.getOptionParser("-fastmc").addRequired("-o", "output file name");
        store.getOptionParser("-fastmc").addOption("-f", "X+:X-:Xn", "mc event filter");
        store.getOptionParser("-fastmc").addOption("-wf", "X+:X-:Xn", "output write filter");
        
        store.parse(args);
        
        if(store.getCommand().compareTo("-convert")==0){
            List<String> lundFiles = store.getOptionParser("-convert").getInputList();
            String        hipoFile = store.getOptionParser("-convert").getOption("-o").stringValue();
            LundConverter.convert2hipo(lundFiles, hipoFile, true);
        }
        
        if(store.getCommand().compareTo("-fastmc")==0){
            List<String>  files = store.getOptionParser("-fastmc").getInputList();
            String       output = store.getOptionParser("-fastmc").getOption("-o").stringValue();
            String       filter = store.getOptionParser("-fastmc").getOption("-f").stringValue();
            String       outfilter = store.getOptionParser("-fastmc").getOption("-wf").stringValue();
            
            ProcessMC mc = new ProcessMC();
            mc.getChain().addFiles(files);
            mc.setOutput(output);
            mc.init();
            mc.setMCFilter(filter).setOutFilter(outfilter);
            mc.open();
            mc.process();
        }
        
        
        /*
        
        ProcessMC mc = new ProcessMC();
        
        //mc.getChain().addFile("sidis_sample_2.hipo");
        mc.getChain().addFile("sidis_data.hipo");
        mc.init();        
        mc.setMCFilter("2-:2+:Xn");
        mc.setOutFilter("11:211:-211:Xn");
        
        mc.open();
        mc.process();*/
    }
}
