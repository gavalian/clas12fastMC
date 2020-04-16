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
    private String detectorBankName    = "REC::response";
    
    public Clas12Event(HipoChain chain){
        hipoChain = chain;
    }
    
    private void initilize(){
        SchemaFactory factory = hipoChain.getSchemaFactory();
        if(factory.hasSchema(particleBankName)==true){
            particleBank = new Bank(factory.getSchema(particleBankName));
        }
        
        if(factory.hasSchema(trajectoryBankName)==true){
            trajectoryBank = new Bank(factory.getSchema(trajectoryBankName));
        }
        
        if(factory.hasSchema(detectorBankName)==true){
            detectorBank = new Bank(factory.getSchema(detectorBankName));
        }
    }
    
    @Override
    public int getPid(int index) {
        if(particleBank!=null) particleBank.getInt(0,index);
        return -1;
    }

    @Override
    public void setPid(int pid, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count() {
        if(particleBank!=null) particleBank.getRows();
        return -1;
    }

    @Override
    public int getIndex(int pid, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean readNext() {
        if(hipoChain.hasNext()==false) return false;
        hipoChain.nextEvent(hipoEvent);
        if(particleBank != null) hipoEvent.read(particleBank);
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
    public void getPosition(Vector3 v3, int detector) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStatus(int index, int status) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getStatus(int index) {
        return 1;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
