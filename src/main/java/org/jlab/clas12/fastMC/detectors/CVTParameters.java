package org.jlab.clas12.fastMC.detectors;

import java.util.HashMap;
import java.util.Map;

public class CVTParameters {

    private Map<Integer, HashMap<String, Double>> cvtParamMap = this.initMap();


    private Map initMap(){
        HashMap<Integer, HashMap<String, Double>> paramMap = new HashMap<>();
        paramMap.put(1, new HashMap<String, Double>(){{
            put("numRegions", 10.0); put("zDistance", 11.4390); put("yDistance", 6.5588); put("theta", 65.588);
        }});
        paramMap.put(2, new HashMap<String, Double>(){{
            put("numRegions", 14.0); put("zDistance", 15.3821); put("yDistance", 9.3198); put("theta", 93.198);
        }});
        paramMap.put(3, new HashMap<String, Double>(){{
            put("numRegions", 18.0); put("zDistance", 19.2967); put("yDistance", 12.0608); put("theta", 120.608);
        }});
        paramMap.put(4, new HashMap<String, Double>(){{
            put("numRegions", 24.0); put("zDistance", 25.1414); put("yDistance", 16.1533); put("theta", 161.533);
        }});

        return paramMap;
    }

    private HashMap<String, Double> getLayer(int layer){return cvtParamMap.get(layer);}

    public int getNumRegions(int layer){
        return this.getLayer(layer).get("numRegions").intValue();
    }

    public double getZDistance(int layer){
        return this.getLayer(layer).get("zDistance");
    }

    public double getYDistance(int layer){
        return this.getLayer(layer).get("yDistance");
    }

    public double getTheta(int layer){
        return this.getLayer(layer).get("theta");
    }

}
