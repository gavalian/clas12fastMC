package org.jlab.clas12.an.abs;

import org.jlab.clas12.fastMC.base.DetectorType;
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.physics.Vector3;

public class AnExample {
    
    public static void main(String[] args){
        String filename = "/Users/baltzell/data/CLAS12/rg-a/recon/005153/dst_clas_005153.evio.00010-00014.hipo";
        
        HipoChain chain = new HipoChain();
        chain.addFile(filename);
        chain.open();
        
        Clas12Event clas12Event = new Clas12Event(chain);
        
        TCanvas c1 = new TCanvas("c1",500,500);
        c1.getCanvas().initTimer(3000);
        
        c1.divide(2, 1);
        H2F h2pb = new H2F("h2pb",100,0,5,100,0,1.5);
        H2F h2sf = new H2F("h2sf",100,0,5,100,0,0.4);

        Vector3 mom=new Vector3();
        
        c1.cd(0).draw(h2pb);
        c1.cd(1).draw(h2sf);

        while(clas12Event.readNext()==true){

            int index;
            int order=0;
            while ((index=clas12Event.getIndex(11,order++))>=0) {
                if (clas12Event.getRegion(index)==Clas12Event.REGION_FORWARD) {

                    double beta = clas12Event.getResponse(Clas12Event.RESPONSE_BETA, DetectorType.FTOF.getDetectorId(),index);
                    double energy = clas12Event.getResponse(Clas12Event.RESPONSE_ENERGY, DetectorType.ECAL.getDetectorId(),index);
                    clas12Event.getVector(mom,index);
                    
                    h2pb.fill(mom.mag(),beta);
                    h2sf.fill(energy/mom.mag(),mom.mag());
                }
            }
        }
    }
}
