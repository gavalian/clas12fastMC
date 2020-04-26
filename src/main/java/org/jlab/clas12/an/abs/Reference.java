package org.jlab.clas12.an.abs;

import java.util.Set;

/**
 *
 * @author gavalian
 * @author baltzell
 */
public interface Reference {
    public void put(int source,int type,int layer,int dest);
    public int  get(int source,int type,int layer);
    public Set<Integer> get(int source, int type);
    public boolean contains(int source,int type,int layer);
    public boolean contains(int source,int type);
    public void clear();
}
