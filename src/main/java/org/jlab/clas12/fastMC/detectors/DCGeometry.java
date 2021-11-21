package org.jlab.clas12.fastMC.detectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DCGeometry {
    
    public static double[][][] points = new double[36][112][6];
    
    public static void init(){
        try {
            InputStream    is = DCGeometry.class.getResourceAsStream("dc_wires.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str = "";
            while ((str = reader.readLine()) != null) {
                //System.out.println(str);
                String[] tokens = str.trim().split("\\s+");
                if(tokens.length>7){
                    int layer = Integer.parseInt(tokens[0]);
                    int wire = Integer.parseInt(tokens[1]);
                    for(int p = 0; p < 6; p++){
                        double coord = Double.parseDouble(tokens[2+p]);
                        DCGeometry.points[layer][wire][p] = coord;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DCGeometry.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public static void show(){
        for(int l = 0; l < 36; l++){
            for(int w = 0; w < 112; w++){
                System.out.printf("%5d %5d >>> %s\n",l,w,
                        Arrays.toString(DCGeometry.points[l][w]));
            }
        }
    }
    public static void main(String[] args){
        DCGeometry.init();
        DCGeometry.show();
    }
}

