package org.jlab.clas12.an.abs;

/**
 *
 * @author gavalian
 * @author baltzell
 */
public interface Reference {
    public void put(int source,int type,int layer,int dest);
    public int  get(int source,int type,int layer);
    public boolean contains(int source,int type,int layer);
    public void clear();
}
