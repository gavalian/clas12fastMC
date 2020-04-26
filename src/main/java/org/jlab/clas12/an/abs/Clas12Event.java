package org.jlab.clas12.an.abs;

import java.util.HashSet;
import java.util.Set;
import org.jlab.clas12.an.base.DetectorEvent;
import org.jlab.clas12.fastMC.base.DetectorLayer;
import org.jlab.clas12.fastMC.base.DetectorRegion;
import org.jlab.clas12.fastMC.base.DetectorType;
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
    private Bank      calorimeterBank   = null;
    private Bank      scintillatorBank   = null;
    private Bank      cherenkovBank = null;
    
    private final String particleBankName    = "REC::Particle";
    private final String trajectoryBankName  = "REC::Traj";
    private final String calorimeterBankName    = "REC::Calorimeter";
    private final String scintillatorBankName    = "REC::Scintillator";
    private final String cherenkovBankName    = "REC::Cherenkov";
    
    private final Set <Bank> detectorBanks = new HashSet();
    private final Reference detectorRefs = new ReferenceMap();
    
    public Clas12Event(HipoChain chain){
        hipoChain = chain;
        initialize();
    }
   
    private void loadReferences() {
        for (Bank bank : detectorBanks) {
            for (int ii=0; ii<bank.getRows(); ii++) {
                int pindex = bank.getShort("pindex", ii);
                int layer = bank.getByte("layer",ii);
                int dtype = bank.getByte("detector",ii);
                detectorRefs.put(pindex,dtype,layer,ii);
            }
        }
    }
    
    private void initialize(){

        detectorBanks.add(calorimeterBank);
        detectorBanks.add(scintillatorBank);
        detectorBanks.add(cherenkovBank);
        
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
        
        if(factory.hasSchema(calorimeterBankName)==true){
            calorimeterBank = new Bank(factory.getSchema(calorimeterBankName));
            System.out.println(">>>>> initalizing detector bank : " + calorimeterBankName);
        }
        
        if(factory.hasSchema(scintillatorBankName)==true){
            scintillatorBank = new Bank(factory.getSchema(scintillatorBankName));
            System.out.println(">>>>> initalizing detector bank : " + scintillatorBankName);
        }
        
        if(factory.hasSchema(cherenkovBankName)==true){
            cherenkovBank = new Bank(factory.getSchema(cherenkovBankName));
            System.out.println(">>>>> initalizing detector bank : " + cherenkovBankName);
        }
    }
    
    @Override
    public int getPid(int index) {
        if(particleBank!=null) return particleBank.getInt(0,index);
        return -1;
    }

    public int getRegion(int index) {
        // technically the regions aren't exclusive and this is a bitmask:
        final int stat = particleBank.getInt("status",index)/1000;
        // but we ignore that here and just get the least significant bit:
        return (int)((Math.log10(stat & -stat)) / Math.log10(2)) + 1; 
    }

    @Override
    public void setPid(int pid, int index) {
        throw new UnsupportedOperationException("Not supported yet.");
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
            for (int layer : DetectorLayer.ECAL_LAYERS) {
                final int dindex = detectorRefs.get(index,DetectorType.ECAL.getDetectorId(),layer);
                if (dindex>=0) {
                    path.addPoint(this.calorimeterBank.getFloat("x",dindex),
                                  this.calorimeterBank.getFloat("y",dindex),
                                  this.calorimeterBank.getFloat("z",dindex));
                }
            }
        } else {
            path.clear();
            path.addPoint(0.0, 0.0,  0.0);
            path.addPoint(0.0, 0.0, 10.0);
        }
    }

    @Override
    public boolean readNext() {
        if(hipoChain.hasNext()==false) return false;
        detectorRefs.clear();
        hipoChain.nextEvent(hipoEvent);
        if(particleBank != null) hipoEvent.read(particleBank);
        if(trajectoryBank != null) hipoEvent.read(trajectoryBank);
        if(calorimeterBank != null) hipoEvent.read(calorimeterBank);
        if(scintillatorBank != null) hipoEvent.read(scintillatorBank);
        if(cherenkovBank != null) hipoEvent.read(cherenkovBank);
        loadReferences();
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
    }

    @Override
    public void combine(LorentzVector vL, int[] pid, int[] order, int[] sign, double[] mass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getResponse(int type, int detector, int particle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    // FIXME: don't we need layer?
    public void getPosition(Vector3 v3, int detector, int particle, int frame) {
        if (detector==DetectorType.ECAL.getDetectorId()) {
            int dindex = detectorRefs.get(particle,detector,DetectorLayer.PCAL_U);
            if (dindex>=0) {
                if (frame==1) {
                    v3.setXYZ(calorimeterBank.getFloat("lu",dindex),
                            calorimeterBank.getFloat("lv",dindex),
                            calorimeterBank.getFloat("lw",dindex)
                    );
                    
                }
                else if (frame==2) {
                    v3.setXYZ(calorimeterBank.getFloat("x",dindex),
                            calorimeterBank.getFloat("y",dindex),
                            calorimeterBank.getFloat("z",dindex)
                    );
                }
            }
        }
    }

    @Override
    public void setStatus(int index, int status) {
        if(particleBank!=null) particleBank.putShort("status", index, (short) status);
    }

    @Override
    public int getStatus(int index) {
        if(particleBank!=null) return particleBank.getInt("status",index);
        return -1;
    }

    @Override
    public long getEventProperty(int type, int flag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getProperty(int propertyType, int particle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
