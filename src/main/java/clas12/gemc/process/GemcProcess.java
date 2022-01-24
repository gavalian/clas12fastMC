/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clas12.gemc.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.DataManager;
import org.jlab.jnp.readers.TextFileReader;
import org.jlab.jnp.readers.TextFileWriter;

/**
 *
 * @author gavalian
 */
public class GemcProcess implements Runnable {
    
    private int      threadNumber = 1;
    private String      directory = "gemc";
    private String   lundFileName = "";
    private int        eventsSkip = 0;
    private int         eventsRun = 1000;
    
    public GemcProcess(int tn){
        threadNumber = tn;
    }
    
    public void setFile(String file, int skip, int num){
        lundFileName = file; eventsSkip = skip; eventsRun = num;
    }
    
    public void createDirectory(){
       String dirth = String.format("%s_%05d", directory,threadNumber);
       File f = new File(dirth);
       if (!f.exists()){
           f.mkdirs();
       }
    }
    
    public void fillFile(){
        
        HipoReader r = new HipoReader();
        r.open(lundFileName);
        Bank eventBank = r.getBank("mc::event");
        
        String outputFile = String.format("%s_%05d/gemc_input.lund", directory,threadNumber);
        
        TextFileWriter w = new TextFileWriter();
        w.open(outputFile);
        
        Event event = new Event();
        
        for(int i = 0; i < eventsSkip; i++){ r.nextEvent(event);}
        
        for(int i = 0; i < eventsRun; i++){
            if(r.hasNext()==true){
                r.nextEvent(event); event.read(eventBank);
                PhysicsEvent phys = DataManager.getPhysicsEvent(10.6, eventBank);
                String data = phys.toLundString();
                String nd = data.replaceAll("[\n\r]$", "");
                w.writeString(nd);
            }
        }
        w.close();
    }
    
    
    public void makeGcard(){
        TextFileReader r = new TextFileReader();
        r.open("etc/clas12-default.gcard");
        TextFileWriter w = new TextFileWriter();
        w.open(String.format("%s_%05d/clas12-default.gcard", directory,threadNumber));
        while(r.readNext()==true){
            String line = r.getString();
            if(line.contains("====INPUT====")==true){
                w.writeString(String.format("<option name=\"INPUT_GEN_FILE\"   value=\"LUND, %s_%05d/gemc_input.lund\"/>", directory,threadNumber));
            }
            
            if(line.contains("====OUTPUT====")==true){
                w.writeString(String.format("<option name=\"OUTPUT\"   value=\"evio, %s_%05d/out.ev\"/>",directory,threadNumber));
            }
            
            if(line.contains("====OUTPUT====")==false&&line.contains("====INPUT====")==false) w.writeString(line);
        }
        w.close();
    }
    
    @Override
    public void run() {
        System.out.printf(">>>> starting thread # %5d\n",threadNumber);
        String dir = String.format("%s_%05d", directory,threadNumber);
        String command = 
               // String.format("gemc /group/clas12/gemc/4.4.1/config/clas12-default.gcard -USE_GUI=0 -OUTPUT=\"evio, %s/out.ev\" -INPUT_GEN_FILE=\"LUND, %s/gemc_input.lund\" -N=%d > %s/gemcrun.log"
               //         ,dir,dir,eventsRun,dir);
        
        String.format("gemc %s/clas12-default.gcard -USE_GUI=0 -N=%d"
                ,dir,eventsRun);
        this.createDirectory();
        
        this.fillFile();
        this.makeGcard();
        
        System.out.printf("thred # %d : run >>> %s\n",threadNumber, command);
        try
        {
             BufferedReader is;  // reader for output of process
             String line;
            // Command to create an external process            
            // Running the above command
            Runtime run  = Runtime.getRuntime();
            Process proc = run.exec(command);
            is = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            while ((line = is.readLine()) != null){
                //System.out.println(line);
            }
            
            proc.waitFor();
        }  
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(GemcProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.printf(">>>> finished thread # %5d\n",threadNumber);
    }
}
