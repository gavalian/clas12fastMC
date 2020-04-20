/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.an.abs;

import org.jlab.clas12.an.base.DetectorEvent;
import org.jlab.jnp.geom.prim.Path3D;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.physics.LorentzVector;
import org.jlab.jnp.physics.Vector3;

/**
 *
 * @author gavalian
 */
public class Clas12Event implements DetectorEvent {
    
    private HipoChain hipoChain      = null;
    private Event     hipoEvent      = new Event();
    //-------------------------------------------------
    // These are the banks to be read from data stream
    // Each bank name is configurable....
    private Bank      particleBank   = null;
    private Bank      trajectoryBank = null;
    private Bank      detectorBank   = null;
    
    private String particleBankName    = "REC::Particle";
    private String trajectoryBankName  = "REC::Traj";
    private String detectorBankName    = "REC::Calorimeter";
    
    public Clas12Event(HipoChain chain){
        hipoChain = chain;
        initialize();
    }
    
    private void initialize(){
        SchemaFactory factory = hipoChain.getSchemaFactory();
        //factory.show();
        if(factory.hasSchema(particleBankName)==true){
            particleBank = new Bank(factory.getSchema(particleBankName));
            System.out.println(">>>>> initalizing particle bank : " + particleBankName);
        }
        
        if(factory.hasSchema(trajectoryBankName)==true){
            trajectoryBank = new Bank(factory.getSchema(trajectoryBankName));
            System.out.println(">>>>> initalizing trajectory bank : " + trajectoryBankName);
        }
        
        if(factory.hasSchema(detectorBankName)==true){
            detectorBank = new Bank(factory.getSchema(detectorBankName));
            System.out.println(">>>>> initalizing detector bank : " + detectorBankName);
        }
    }
    
    @Override
    public int getPid(int index) {
        if(particleBank!=null) return particleBank.getInt(0,index);
        return -1;
    }

    @Override
    public void setPid(int pid, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count() {
        if(particleBank!=null) return particleBank.getRows();
        return -1;
    }

    @Override
    public int getIndex(int pid, int order) {
        int skip = 0;
        int count = count();
        for(int i = 0; i < count; i++){
            if(getPid(i)==pid){
                if(skip==order) return i;
                skip++;
            }
        }
        return -1;
    }

    @Override
    public void getVector(Vector3 v3, int index) {
        if(particleBank!=null) 
            v3.setXYZ(particleBank.getFloat(1,index),
                    particleBank.getFloat(2,index),
                    particleBank.getFloat(3,index));
    }

    @Override
    public void getVertex(Vector3 v3, int index) {
        if(particleBank!=null) 
            v3.setXYZ(particleBank.getFloat(4,index),
                    particleBank.getFloat(5,index),
                    particleBank.getFloat(6,index));
    }

    @Override
    public void getPath(Path3D path, int index) {
        int charge = this.particleBank.getInt("charge", index);
        if(charge==0){
            path.clear();
            path.addPoint(0.0,0.0,0.0);
            int nrowsECAL = this.detectorBank.getRows();
            for(int i = 0 ; i < nrowsECAL; i++){
                int pindex = this.detectorBank.getInt("pindex", i);
                if(pindex==index){
                    path.addPoint(this.detectorBank.getFloat("x", i), 
                            this.detectorBank.getFloat("y", i), 
                            this.detectorBank.getFloat("z", i)
                            );
                }
            }
        } else {
            path.clear();
            path.addPoint(0.0, 0.0,  0.0);
            path.addPoint(0.0, 0.0, 10.0);
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean readNext() {
        if(hipoChain.hasNext()==false) return false;
        hipoChain.nextEvent(hipoEvent);
        if(particleBank != null) {
            hipoEvent.read(particleBank);
            //System.out.println(" particle rows = " + particleBank.getRows());
        }
        if(trajectoryBank != null) hipoEvent.read(trajectoryBank);
        if(detectorBank != null) hipoEvent.read(detectorBank);
        return true;
    }

    @Override
    public void combine(LorentzVector vL, int[] index, int[] sign, double[] mass) {
        //vL.reset();
        for(int i = 0; i < index.length; i++){
            double px = particleBank.getFloat(1, index[i]);
            double py = particleBank.getFloat(2, index[i]);
            double pz = particleBank.getFloat(3, index[i]);
            if(sign[i]>=0){
                vL.add(px, py, pz, mass[i]);
            } else {
                vL.sub(px, py, pz, mass[i]);
            }
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void combine(LorentzVector vL, int[] pid, int[] order, int[] sign, double[] mass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getResponse(int type, int detector) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getPosition(Vector3 v3, int detector, int index) {
        int nrowsECAL = this.detectorBank.getRows();
        for(int i = 0 ; i < nrowsECAL; i++){
            int pindex = this.detectorBank.getInt("pindex", i);
            int  layer = this.detectorBank.getInt("layer", i);
            if(pindex==index&&layer==1){
                if(detector==1){
                    v3.setXYZ(
                            detectorBank.getFloat("lu",i),
                            detectorBank.getFloat("lv",i),
                            detectorBank.getFloat("lw",i)
                    );
                }
                if(detector==2){
                    v3.setXYZ(
                            detectorBank.getFloat("x",i),
                            detectorBank.getFloat("y",i),
                            detectorBank.getFloat("z",i)
                    );
                }
            }
        }
    }

    @Override
    public void setStatus(int index, int status) {
        if(particleBank!=null) particleBank.putShort("status", index, (short) status);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getStatus(int index) {
        if(particleBank!=null) return particleBank.getInt("status",index);
        return -1;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
