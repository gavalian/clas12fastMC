/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clas12.fastMC;

import java.util.List;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.Schema;
import org.jlab.jnp.hipo4.data.Schema.SchemaBuilder;
import org.jlab.jnp.hipo4.io.HipoWriter;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.LundReader;
import org.jlab.jnp.utils.benchmark.ProgressPrintout;

/**
 *
 * @author gavalian
 */
public class LundConverter {
    
    public static Schema getParticleSchema(String name, int group, int id){
        
        SchemaBuilder schemaBuilder = new SchemaBuilder(name,group,id);
        schemaBuilder.addEntry("pid", "I", "");
        schemaBuilder.addEntry("px", "F", "");
        schemaBuilder.addEntry("py", "F", "");
        schemaBuilder.addEntry("pz", "F", "");
        schemaBuilder.addEntry("vx", "F", "");
        schemaBuilder.addEntry("vy", "F", "");
        schemaBuilder.addEntry("vz", "F", "");
        schemaBuilder.addEntry("charge", "I", "");
        schemaBuilder.addEntry("beta", "F", "");
        schemaBuilder.addEntry("chi2", "F", "");
        schemaBuilder.addEntry("status", "I", "");
        Schema schema = schemaBuilder.build();
        return schema;
    }
    
    public static Schema getDetectorSchema(String name, int group, int id){        
        SchemaBuilder schemaBuilder = new SchemaBuilder(name,group,id);
        schemaBuilder.addEntry("det", "I", "");
        schemaBuilder.addEntry("pindex", "I", "");
        schemaBuilder.addEntry("x", "F", "");
        schemaBuilder.addEntry("y", "F", "");
        schemaBuilder.addEntry("z", "F", "");
        schemaBuilder.addEntry("path", "F", "");
        schemaBuilder.addEntry("time", "F", "");
        schemaBuilder.addEntry("energy", "F", "");
        Schema schema = schemaBuilder.build();
        return schema;
    }
    
    public static PhysicsEvent keepFinal(PhysicsEvent event){
        PhysicsEvent keep = new PhysicsEvent();
        for(int i = 0; i < event.count(); i++){
            Particle p = event.getParticle(i);
            if(p.getStatus()==1){ keep.addParticle(p);}
        }
        return keep;
    }
    public static void convert2hipo(List<String> fileList, String outputHipo, boolean finalparticles){
    
        /*SchemaBuilder schemaBuilder = new SchemaBuilder("mc::event",22001,1);
        schemaBuilder.addEntry("pid", "I", "");
        schemaBuilder.addEntry("px", "F", "");
        schemaBuilder.addEntry("py", "F", "");
        schemaBuilder.addEntry("pz", "F", "");
        schemaBuilder.addEntry("vx", "F", "");
        schemaBuilder.addEntry("vy", "F", "");
        schemaBuilder.addEntry("vz", "F", "");
        schemaBuilder.addEntry("charge", "I", "");
        schemaBuilder.addEntry("beta", "F", "");
        schemaBuilder.addEntry("chi2", "F", "");
        schemaBuilder.addEntry("status", "I", "");

        Schema schema = schemaBuilder.build();*/
        
        //schema.parse("I6FI2FI");
        //schema.setNames("pid:px:py:pz:vx:vy:vz:charge:beta:chi2pid:status");
        
        Schema schema = LundConverter.getParticleSchema("mc::event",22001,1);
        
        Event hipoEvent = new Event();
        ProgressPrintout progress = new ProgressPrintout();
        
        HipoWriter writer = new HipoWriter();
        writer.getSchemaFactory().addSchema(schema);
        
        writer.setCompressionType(1);
        
        writer.setMaxSize(16*1024*1024).setMaxEvents(1000000);
        writer.setCompressionType(1);
        
        writer.open(outputHipo);
        
        int counter = 0;
        int eventCounter = 0;

        for(String file : fileList) {
            
            System.out.println("adding file ----> " + file);
            LundReader reader = new LundReader();
            reader.addFile(file);
            
            reader.open();
            PhysicsEvent event = new PhysicsEvent();
            int eventCounterFile = 0;
            
            while( reader.nextEvent(event)
                    //reader.next()==true
                    ){
                //reader.nextEvent(event);
                progress.updateStatus();
                eventCounter++;
                eventCounterFile++;
                if(finalparticles==true){
                    PhysicsEvent keep = LundConverter.keepFinal(event);
                    Bank  node = new Bank(schema,keep.count());
                    reader.fillNode(node, keep);
                    //node.show();
                    //System.out.println(node.nodeString());
                    hipoEvent.reset();
                    hipoEvent.write(node);
                } else {
                    Bank  node = new Bank(schema,event.count());
                    reader.fillNode(node, event);
                    //node.show();
                    //System.out.println(node.nodeString());
                    hipoEvent.reset();
                    hipoEvent.write(node);
                }
                writer.addEvent(hipoEvent);
            }
            counter++;
            System.out.println(" number of event processed = " + eventCounterFile + "  total = " + eventCounter);
        }
        writer.close();
        System.out.println(progress.getUpdateString());
    }
}
