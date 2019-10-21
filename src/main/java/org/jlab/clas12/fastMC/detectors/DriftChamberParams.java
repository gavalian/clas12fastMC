package org.jlab.clas12.fastMC.detectors;

import java.util.HashMap;
import java.util.Map;

public class DriftChamberParams {

    private Map<Integer, HashMap<String, Double>> driftChamberParamMap = this.initMap();

    private Map initMap(){
        HashMap<Integer, HashMap<String, Double>> paramMap = new HashMap<>();
        paramMap.put(1, new HashMap<String, Double>(){{
            put("wpdist", 0.3861); put("thmin", 4.694); put("dist2tgt", 228.078);
        }});

        paramMap.put(2, new HashMap<String, Double>(){{
            put("wpdist", 0.4042); put("thmin", 4.495); put("dist2tgt", 238.687);
        }});

        paramMap.put(3, new HashMap<String, Double>(){{
            put("wpdist", 0.6219); put("thmin", 4.812); put("dist2tgt", 351.544);
        }});

        paramMap.put(4, new HashMap<String, Double>(){{
            put("wpdist", 0.6586); put("thmin", 4.771); put("dist2tgt", 371.773);
        }});

        paramMap.put(5, new HashMap<String, Double>(){{
            put("wpdist", 0.9351); put("thmin", 4.333); put("dist2tgt", 489.099);
        }});

        paramMap.put(6, new HashMap<String, Double>(){{
            put("wpdist", 0.9780); put("thmin", 4.333); put("dist2tgt", 511.236);
        }});

        return paramMap;
    }

    public DriftChamberParams() {
    }

    public HashMap<String, Double> getParams(int superLayer){
        return this.driftChamberParamMap.get(superLayer);
    }

    public double getTHMin(int superLayer){
        return this.getParams(superLayer).get("thmin");
    }

    public double getWPD(int superLayer){
        return getParams(superLayer).get("wpdist");
    }

    public double getDist2Targ(int superLayer){
        return getParams(superLayer).get("dist2tgt");
    }

}
