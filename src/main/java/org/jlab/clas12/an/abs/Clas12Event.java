package org.jlab.clas12.an.abs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jlab.clas12.an.base.DetectorEvent;
import org.jlab.clas12.fastMC.base.DetectorLayer;
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
 * @author baltzell
 */
public class Clas12Event implements DetectorEvent {
 
    static final int FRAME_LOCAL=1;
    static final int FRAME_GLOBAL=2;
    static final int RESPONSE_ENERGY=1;
    static final int RESPONSE_TIME=2;
   
    private HipoChain hipoChain      = null;
    private Event     hipoEvent      = new Event();
    //-------------------------------------------------
    // These are the banks to be read from data stream
    // Each bank name is configurable....
    private Bank      particleBank   = null;
    private Bank      trackBank   = null;
    private Bank      trajectoryBank = null;
    private Bank      calorimeterBank   = null;
    private Bank      scintillatorBank   = null;
    private Bank      cherenkovBank = null;
    
    private final String particleBankName     = "REC::Particle";
    private final String trackBankName        = "REC::Track";
    private final String trajectoryBankName   = "REC::Traj";
    private final String calorimeterBankName  = "REC::Calorimeter";
    private final String scintillatorBankName = "REC::Scintillator";
    private final String cherenkovBankName    = "REC::Cherenkov";
   
    private final Map <Integer,Bank> detectorTypeBanks = new HashMap();
    private final Set <Bank> detectorBanks = new HashSet();
    private final Reference detectorRefs = new ReferenceMap();
    private final Reference trajectoryRefs = new ReferenceMap();
    
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
        
        if(factory.hasSchema(trackBankName)==true){
            trackBank = new Bank(factory.getSchema(trackBankName));
            System.out.println(">>>>> initalizing track bank : " + trackBankName);
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
        
        detectorBanks.add(calorimeterBank);
        detectorBanks.add(scintillatorBank);
        detectorBanks.add(cherenkovBank);

        detectorTypeBanks.put(DetectorType.FTCAL.getDetectorId(),calorimeterBank);
        detectorTypeBanks.put(DetectorType.ECAL.getDetectorId(),calorimeterBank);
        detectorTypeBanks.put(DetectorType.CTOF.getDetectorId(),scintillatorBank);
        detectorTypeBanks.put(DetectorType.FTOF.getDetectorId(),scintillatorBank);
        detectorTypeBanks.put(DetectorType.CND.getDetectorId(),scintillatorBank);
        detectorTypeBanks.put(DetectorType.BAND.getDetectorId(),scintillatorBank);
        detectorTypeBanks.put(DetectorType.HTCC.getDetectorId(),cherenkovBank);
        detectorTypeBanks.put(DetectorType.LTCC.getDetectorId(),cherenkovBank);
    }
    
    private void loadReferences() {
        for (Bank bank : detectorBanks) {
            for (int ii=0; ii<bank.getRows(); ii++) {
                int pindex = bank.getShort("pindex", ii);
                int dtype = bank.getByte("detector",ii);
                int layer = bank.getByte("layer",ii);
                detectorRefs.put(pindex,dtype,layer,ii);
            }
        }
        for (int ii=0; ii<trackBank.getRows(); ii++) {
            int pindex = trackBank.getShort("pindex", ii);
            int dtype = trackBank.getByte("detector",ii);
            detectorRefs.put(pindex,dtype,1,ii);
        }
        for (int ii=0; ii<trajectoryBank.getRows(); ii++) {
            int pindex = trajectoryBank.getShort("pindex", ii);
            int dtype = trajectoryBank.getByte("detector",ii);
            int layer = trajectoryBank.getByte("layer",ii);
            trajectoryRefs.put(pindex,dtype,layer,ii);
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
            
        Vector3 v3=new Vector3();

        path.clear();

        if (this.particleBank.getInt("charge", index)==0) {
                    
            // use trigger particle's vertex, if there is one:
            if (particleBank.getShort("status",0)<0) {
                getVertex(v3,0);
                path.addPoint(v3.x(),v3.y(),v3.z());
            }
            else {
                path.addPoint(0.0,0.0,0.0);
            }

            switch (this.getRegion(index)) {
                case 1:
                    break;
                case 2:
                    for (int layer : DetectorLayer.ECAL_LAYERS) {
                        if (getPosition(v3,DetectorType.ECAL.getDetectorId(),layer,index,FRAME_GLOBAL)) {
                            path.addPoint(v3.x(),v3.y(),v3.z());
                        }
                    }
                    break;
                case 3:
                    if (getPosition(v3,DetectorType.CND.getDetectorId(),1,index,FRAME_GLOBAL)) {
                        path.addPoint(v3.x(),v3.y(),v3.z());
                    }
                    break;
                case 4:
                    if (getPosition(v3,DetectorType.BAND.getDetectorId(),1,index,FRAME_GLOBAL)) {
                        path.addPoint(v3.x(),v3.y(),v3.z());
                    }
                    break;
                default:
                    break;
            }
        }

        else {

            switch (this.getRegion(index)) {
                case 1:
                    break;
                case 2:
                    // FIXME:  init some Map of relevant detector/layer instead of this repitition
                    // Or prune trajectoryRefs upon init and then use its natural ordering?
                    if (getTrackPosition(v3,DetectorType.HTCC.getDetectorId(),1,index,FRAME_GLOBAL)) {
                        path.addPoint(v3.x(),v3.y(),v3.z());
                    }
                    for (int layer : DetectorLayer.DC_LAYERS) {
                        if (getTrackPosition(v3,DetectorType.DC.getDetectorId(),layer,index,FRAME_GLOBAL)) {
                            path.addPoint(v3.x(),v3.y(),v3.z());
                        }
                    }
                    if (getTrackPosition(v3,DetectorType.LTCC.getDetectorId(),1,index,FRAME_GLOBAL)) {
                        path.addPoint(v3.x(),v3.y(),v3.z());
                    }
                    for (int layer : DetectorLayer.FTOF_LAYERS) {
                        if (getTrackPosition(v3,DetectorType.FTOF.getDetectorId(),layer,index,FRAME_GLOBAL)) {
                            path.addPoint(v3.x(),v3.y(),v3.z());
                        }
                    }
                    for (int layer : DetectorLayer.ECAL_LAYERS) {
                        if (getTrackPosition(v3,DetectorType.ECAL.getDetectorId(),layer,index,FRAME_GLOBAL)) {
                            path.addPoint(v3.x(),v3.y(),v3.z());
                        }
                    }
                    break;
                case 3:
                    for (int layer : DetectorLayer.CVT_LAYERS) {
                        if (getTrackPosition(v3,DetectorType.CVT.getDetectorId(),layer,index,FRAME_GLOBAL)) {
                            path.addPoint(v3.x(),v3.y(),v3.z());
                        }
                    }
                    if (getTrackPosition(v3,DetectorType.CTOF.getDetectorId(),1,index,FRAME_GLOBAL)) {
                        path.addPoint(v3.x(),v3.y(),v3.z());
                    }
                    if (getTrackPosition(v3,DetectorType.CND.getDetectorId(),1,index,FRAME_GLOBAL)) {
                        path.addPoint(v3.x(),v3.y(),v3.z());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean readNext() {
        detectorRefs.clear();
        trajectoryRefs.clear();
        if(hipoChain.hasNext()==false) return false;
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
    public double getResponse(int type, int detector, int layer, int particle) {
        double ret=-1;
        int dindex = detectorRefs.get(particle,detector,layer);
        if (dindex>=0) {
            Bank bank = detectorTypeBanks.get(detector);
            switch (type) {
                case RESPONSE_ENERGY:
                    ret=bank.getFloat("energy",dindex);
                    break;
                case RESPONSE_TIME:
                    ret=bank.getFloat("time",dindex);
                    break;
                default:
                    break;
            }
        }
        return ret;
    }

    @Override
    public boolean getPosition(Vector3 v3, int detector, int layer, int particle, int frame) {
        int dindex = detectorRefs.get(particle,detector,layer);
        if (dindex>=0) {
            Bank bank = detectorTypeBanks.get(detector);
            if (frame==FRAME_LOCAL) {
                if (detector==DetectorType.ECAL.getDetectorId()) {
                    v3.setXYZ(bank.getFloat("lu",dindex),
                            bank.getFloat("lv",dindex),
                            bank.getFloat("lw",dindex)
                    );
                }
                return true;
            }
            else if (frame==FRAME_GLOBAL) {
                v3.setXYZ(bank.getFloat("x",dindex),
                        bank.getFloat("y",dindex),
                        bank.getFloat("z",dindex)
                );
                return true;
            }
        }
        return false;
    }
    
    public boolean getTrackPosition(Vector3 v3, int detector, int layer, int particle, int frame) {
        int dindex = trajectoryRefs.get(particle,detector,layer);
        if (dindex>=0) {
            if (frame==FRAME_GLOBAL) {
                v3.setXYZ(trajectoryBank.getFloat("x",dindex),
                        trajectoryBank.getFloat("y",dindex),
                        trajectoryBank.getFloat("z",dindex)
                );
                return true;
            }
        }
        return false;
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
