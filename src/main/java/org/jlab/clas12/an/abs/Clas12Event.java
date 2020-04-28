package org.jlab.clas12.an.abs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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

    public static final int REGION_TAGGER=1;
    public static final int REGION_FORWARD=2;
    public static final int REGION_CENTRAL=3;
    public static final int REGION_BACKWARD=4;

    private HipoChain hipoChain      = null;
    private Event     hipoEvent      = new Event();
    //-------------------------------------------------
    // These are the banks to be read from data stream
    // Each bank name is configurable....
    private Bank      particleBank = null;
    private Bank      trackBank = null;
    private Bank      trajectoryBank = null;
    private Bank      calorimeterBank = null;
    private Bank      scintillatorBank = null;
    private Bank      cherenkovBank = null;
    private Bank      forwardtaggerBank = null;
    private Bank      runConfigBank = null;
    private Bank      recEventBank = null;
    
    private final String particleBankName     = "REC::Particle";
    private final String trackBankName        = "REC::Track";
    private final String trajectoryBankName   = "REC::Traj";
    private final String calorimeterBankName  = "REC::Calorimeter";
    private final String scintillatorBankName = "REC::Scintillator";
    private final String cherenkovBankName    = "REC::Cherenkov";
    private final String forwardtaggerBankName= "REC::ForwardTagger";
    private final String runConfigBankName    = "RUN::config";
    private final String recEventBankName     = "REC::Event";
  
    private final Set <Bank> detectorBanks = new HashSet();
    private final Map <Integer,Bank> detectorTypeBanks = new HashMap();
    private final Map <Integer,Map <Integer,List<Integer>> > trajectoryOrder = new HashMap<>();
    
    private final Reference detectorRefs = new ReferenceMap();
    private final Reference trajectoryRefs = new ReferenceMap();
    
    public Clas12Event(HipoChain chain){
        hipoChain = chain;
        initialize();
    }
   
    private void initialize(){

        SchemaFactory factory = hipoChain.getSchemaFactory();
        //factory.show();
        if(factory.hasSchema(runConfigBankName)==true){
            runConfigBank = new Bank(factory.getSchema(runConfigBankName));
            System.out.println(">>>>> initalizing config bank : " + runConfigBankName);
        }
        if(factory.hasSchema(recEventBankName)==true){
            recEventBank = new Bank(factory.getSchema(runConfigBankName));
            System.out.println(">>>>> initalizing config bank : " + recEventBankName);
        }
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
        if(factory.hasSchema(forwardtaggerBankName)==true){
            forwardtaggerBank = new Bank(factory.getSchema(forwardtaggerBankName));
            System.out.println(">>>>> initalizing detector bank : " + forwardtaggerBankName);
        }
        
        detectorTypeBanks.put(DetectorType.FTCAL.getDetectorId(),forwardtaggerBank);
        detectorTypeBanks.put(DetectorType.FTHODO.getDetectorId(),forwardtaggerBank);
        detectorTypeBanks.put(DetectorType.ECAL.getDetectorId(),calorimeterBank);
        detectorTypeBanks.put(DetectorType.CTOF.getDetectorId(),scintillatorBank);
        detectorTypeBanks.put(DetectorType.FTOF.getDetectorId(),scintillatorBank);
        detectorTypeBanks.put(DetectorType.CND.getDetectorId(),scintillatorBank);
        detectorTypeBanks.put(DetectorType.BAND.getDetectorId(),scintillatorBank);
        detectorTypeBanks.put(DetectorType.HTCC.getDetectorId(),cherenkovBank);
        detectorTypeBanks.put(DetectorType.LTCC.getDetectorId(),cherenkovBank);

        detectorBanks.addAll(detectorTypeBanks.values());

        // note, adding and layer ordering here dictates path ordering:
        // (this can be dropped completely if trajectory bank is ordered by path)
        extendTrajectoryOrder(1,DetectorType.FTHODO.getDetectorId(),1);
        extendTrajectoryOrder(1,DetectorType.FTCAL.getDetectorId(),1);
        extendTrajectoryOrder(2,DetectorType.HTCC.getDetectorId(),1);
        extendTrajectoryOrder(2,DetectorType.DC.getDetectorId(),1,6,12);
        extendTrajectoryOrder(2,DetectorType.LTCC.getDetectorId(),1);
        extendTrajectoryOrder(2,DetectorType.FTOF.getDetectorId(),1,2,3);
        extendTrajectoryOrder(2,DetectorType.ECAL.getDetectorId(),1,4,7);
        extendTrajectoryOrder(3,DetectorType.CVT.getDetectorId(),1,6,12);
        extendTrajectoryOrder(3,DetectorType.CTOF.getDetectorId(),1);
        extendTrajectoryOrder(3,DetectorType.CND.getDetectorId(),1);
        extendTrajectoryOrder(4,DetectorType.BAND.getDetectorId(),1);
    }

    private void extendTrajectoryOrder(int region, int detector,int ... layers) {
        if (!trajectoryOrder.containsKey(region)) {
            trajectoryOrder.put(region, new LinkedHashMap());
        }
        if (!trajectoryOrder.get(region).containsKey(detector)) {
            trajectoryOrder.get(region).put(detector, new ArrayList());
        }
        for (int layer : layers) {
            trajectoryOrder.get(region).get(detector).add(layer);
        }
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
        particleBank.putInt("pid", index, pid);
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
            
        final int region = getRegion(index);
            
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

            switch (region) {
                case REGION_TAGGER:
                    break;
                case REGION_FORWARD:
                    for (int layer : DetectorLayer.ECAL_LAYERS) {
                        if (getPosition(v3,DetectorType.ECAL.getDetectorId(),layer,index,FRAME_GLOBAL)) {
                            path.addPoint(v3.x(),v3.y(),v3.z());
                        }
                    }
                    break;
                case REGION_CENTRAL:
                    if (getPosition(v3,DetectorType.CND.getDetectorId(),1,index,FRAME_GLOBAL)) {
                        path.addPoint(v3.x(),v3.y(),v3.z());
                    }
                    break;
                case REGION_BACKWARD:
                    if (getPosition(v3,DetectorType.BAND.getDetectorId(),1,index,FRAME_GLOBAL)) {
                        path.addPoint(v3.x(),v3.y(),v3.z());
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Not supported yet.");
            }
        }

        else {

            for (int detector : trajectoryOrder.get(region).keySet()) {
                for (int layer : trajectoryOrder.get(region).get(detector)) {
                    if (DetectorType.ECAL.getDetectorId() == detector) {
                        if (getPosition(v3,detector,layer,index,FRAME_GLOBAL)) {
                            path.addPoint(v3.x(),v3.y(),v3.z());
                        }
                    }
                    else {
                        if (getTrackPosition(v3,detector,layer,index,FRAME_GLOBAL)) {
                            path.addPoint(v3.x(),v3.y(),v3.z());
                        }
                    }
                }
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
        // duplicate code or make new array?
        int[] index=new int[pid.length];
        for(int i = 0; i < pid.length; i++){
            index[i] = this.getIndex(pid[i],order[i]);
        }
        combine(vL,index,sign,mass);
    }

    @Override
    public double getResponse(int type, int detector, int layer, int particle) {
        double ret=-1;
        int dindex = detectorRefs.get(particle,detector,layer);
        if (dindex>=0) {
            switch (type) {
                case RESPONSE_PATH:
                    int tindex = this.trajectoryRefs.get(particle, detector, layer);
                    if (tindex>=0) ret=trajectoryBank.getFloat("path",tindex);
                    break;
                case RESPONSE_ENERGY:
                    ret=detectorTypeBanks.get(detector).getFloat("energy",dindex);
                    break;
                case RESPONSE_TIME:
                    ret=detectorTypeBanks.get(detector).getFloat("time",dindex);
                    break;
                case RESPONSE_BETA:
                    double time = getResponse(RESPONSE_TIME,detector,layer,particle);
                    double path = getResponse(RESPONSE_PATH,detector,layer,particle);
                    double stime = recEventBank.getFloat("startTime",0);
                    ret = path / (time-stime);
                    break;
                default:
                    throw new UnsupportedOperationException("Not supported yet.");
            }
        }
        return ret;
    }
    
    public double getResponse(int type, int detector, int particle) {
        double ret=-1;
        if (detectorRefs.contains(particle,detector)) {
            switch (type) {
                case RESPONSE_ENERGY:
                    Bank bank = detectorTypeBanks.get(detector);
                    for (int layer : DetectorLayer.ECAL_LAYERS) {
                        ret=0;
                        int dindex = detectorRefs.get(particle,DetectorType.ECAL.getDetectorId(),layer);
                        if (dindex>=0) ret += bank.getFloat("energy",dindex);
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Not supported yet.");
            }
        }
        return ret;
    }

    @Override
    public boolean getPosition(Vector3 v3, int detector, int layer, int particle, int frame) {
        int dindex = detectorRefs.get(particle,detector,layer);
        Bank bank = detectorTypeBanks.get(detector);
        if (dindex>=0) {
            switch (frame) {
                case FRAME_LOCAL:
                    if (detector==DetectorType.ECAL.getDetectorId()) {
                        v3.setXYZ(bank.getFloat("lu",dindex),
                                bank.getFloat("lv",dindex),
                                bank.getFloat("lw",dindex)
                        );
                    }
                    return true;
                case FRAME_GLOBAL:
                    v3.setXYZ(bank.getFloat("x",dindex),
                            bank.getFloat("y",dindex),
                            bank.getFloat("z",dindex)
                    );
                    return true;
                default:
                    throw new UnsupportedOperationException("Not supported yet.");
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
    /**
     * Note, REC::Particle.status is already assigned (and non-zero), but the
     * 5th digit is available.  It's a short so it can take values 0/1/2 with
     * overflowing.
     */
    public void setStatus(int index, int status) {
        //if (status<0 || status>2) this will not work!!!
        if(particleBank!=null) {
            short s = particleBank.getShort("status", index);
            s += s<0 ? -10000*status : 10000*status;
            particleBank.putShort("status", index, s);
        }
    }

    @Override
    public int getStatus(int index) {
        if(particleBank!=null) {
            return Math.abs(particleBank.getInt("status",index))/10000;
        }
        return -1;
    }

    @Override
    public long getEventProperty(int type, int flag) {
        long ret=-1;
        switch (type) {
            case PROP_RUNNUMBER:
                ret = runConfigBank.getLong("run",0);
                break;
            case PROP_EVENTNUMBER:
                ret = runConfigBank.getLong("event",0);
                break;
            case PROP_TRIGGERBITS:
                ret = runConfigBank.getLong("trigger",0);
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
        return ret;
    }

    @Override
    public int getProperty(int propertyType, int particle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
