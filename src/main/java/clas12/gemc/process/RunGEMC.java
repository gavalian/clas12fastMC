/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clas12.gemc.process;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jnp.utils.file.FileUtils;

/**
 *
 * @author gavalian
 */
public class RunGEMC {
    
    String filename = "";
    int    nThreads = 1;
    int    nEvents  = 1000;
    
    public RunGEMC(String f, int nt, int ne){
        filename = f; nThreads = nt; nEvents = ne;
    }
    
    public void process(){
        List<Thread>  threads = new ArrayList<>();
        for(int i = 0; i < nThreads; i++){
            GemcProcess process = new GemcProcess(i+1);
            process.setFile(filename, i*nEvents, nEvents);
            Thread th = new Thread(process);
            threads.add(th);
        }
        
        for(Thread th : threads){ th.start();}
        boolean keepRun = true;
        while(keepRun==true){
            int count = 0;
            
            try {
                Thread.sleep(20000);
            } catch (InterruptedException ex) {
                Logger.getLogger(RunGEMC.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            for(int i = 0; i < threads.size(); i++){
                if(threads.get(i).isAlive()==true) count++;
            }
            if(count==0) keepRun = false;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
            //Date date = new Date();
            LocalDateTime now = LocalDateTime.now();  
            System.out.printf(">>>> [%s] threads running #%7d/%7d\n", dtf.format(now), count,threads.size());
            
        }
        System.out.println("\n>>> exiting.... ");
        List<String> files = FileUtils.getFilesInDirectoryRecursive(".", "*out*ev");
        Collections.sort(files);
        for(String file : files){ System.out.println("\t ->>>> " + file);}
    }
    
    public static void main(String[] args){
        String     file = args[0];
        int    nThreads = Integer.parseInt(args[1]);
        int    nEvents  = Integer.parseInt(args[2]);
        RunGEMC gemc = new RunGEMC(file,nThreads,nEvents);
        gemc.process();
    }
}
