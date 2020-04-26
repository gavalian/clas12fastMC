package org.jlab.clas12.an.abs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author baltzell
 */
public class ReferenceMap implements Reference {

    private final Map <Integer,Map<Integer,Map>> cache = new HashMap<>();
   
    public ReferenceMap() {}
    
    @Override
    public void put(int source,int type,int layer,int dest) {
        if (!cache.containsKey(source)) {
            cache.put(source,new HashMap<>());
        }
        if (!cache.get(source).containsKey(type)) {
            cache.get(source).put(type,new TreeMap<Integer,Integer>());
        }
        cache.get(source).get(type).put(layer,dest);
    }

    @Override
    public int get(int source,int type,int layer) {
        if (contains(source,type,layer)) {
            return (int)cache.get(source).get(type).get(layer);
        }
        return -1;
    }
    
    @Override
    public Set<Integer> get(int source,int type) {
        if (contains(source,type)) {
            return cache.get(source).get(type).keySet();
        }
        return null;
    }

    @Override
    public boolean contains(int source,int type) {
        if (cache.containsKey(source)) {
            if (cache.get(source).containsKey(type)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean contains(int source,int type,int layer) {
        if (contains(source,type)) {
            if (cache.get(source).get(type).containsKey(layer)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void clear() {
        cache.clear();
    }
    
}
