package org.jlab.clas12.an.abs;

import org.jlab.clas12.fastMC.base.DetectorLayer;
import org.jlab.clas12.fastMC.base.DetectorType;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.physics.Vector3;

public class AnExample {
    
    public static void main(String[] args){
        String filename="/Users/baltzell/data/CLAS12/rg-a/recon/pass1/skim4_005032.hipo";
        
        HipoChain chain = new HipoChain();
        chain.addFile(filename);
        chain.open();
        
        Clas12Event clas12Event = new Clas12Event(chain);
        
        TCanvas c1 = new TCanvas("c1",600,500);
        c1.getCanvas().initTimer(3000);
        
        c1.divide(2, 2);
        H2F h2pbF = new H2F("h2pbF",100,0,5,100,0,1.5);
        H2F h2pbC = new H2F("h2pbC",100,0,5,100,0,1.5);
        H2F h2sf = new H2F("h2sf",100,0,5,100,0,0.4);
        H1F h1b  = new H1F("h1b",100,0,1.5);

        Vector3 mom=new Vector3();
        
        c1.cd(0).draw(h2pbF);
        c1.cd(1).draw(h2pbC);
        c1.cd(2).draw(h2sf);
        c1.cd(3).draw(h1b);

        while(clas12Event.readNext()==true){

            double beta,energy;
            int index;
            
            // forward electrons:
            int order=0;
            while ((index=clas12Event.getIndex(11,order++))>=0) {
                if (clas12Event.getRegion(index)==Clas12Event.REGION_FORWARD) {
                    energy = clas12Event.getResponse(Clas12Event.RESPONSE_ENERGY, DetectorType.ECAL.getDetectorId(),index);
                    clas12Event.getVector(mom,index);
                    h2sf.fill(mom.mag(),energy/mom.mag());
                }
            }

            // forward/central hadrons:
            for (index=0; index<clas12Event.count(); index++) {
                if (Math.abs(clas12Event.getPid(index))<100) continue;
                if (clas12Event.getCharge(index)==0) {
                    if (clas12Event.getRegion(index)==Clas12Event.REGION_FORWARD) {
                        beta = clas12Event.getResponse(Clas12Event.RESPONSE_BETA, DetectorType.ECAL.getDetectorId(),DetectorLayer.PCAL,index);
                        h1b.fill(beta);
                    }
                } 
                else {
                    clas12Event.getVector(mom,index);
                    switch (clas12Event.getRegion(index)) {
                        case Clas12Event.REGION_FORWARD:
                            beta = clas12Event.getResponse(Clas12Event.RESPONSE_BETA, DetectorType.FTOF.getDetectorId(),DetectorLayer.FTOF1B,index);
                            h2pbF.fill(mom.mag(),beta);
                            break;
                        case Clas12Event.REGION_CENTRAL:
                            beta = clas12Event.getResponse(Clas12Event.RESPONSE_BETA, DetectorType.CTOF.getDetectorId(),1,index);
                            h2pbC.fill(mom.mag(),beta);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
