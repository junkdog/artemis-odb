package com.artemis.generator.strategy.e;

import com.artemis.Component;

import java.util.List;

/**
 * @author Daan van Yperen
 */
public class Proof extends Component {
    private int pri;
    protected int prot;
    public int pub;
    int undef;

    public List<Object> gen;

    public void clear() {}
    public long clear2() { return 0;}
    public void setDepth(long blaValue) {}
    public long getDepth() { return 0; }
    public void set(int pri, int prot, int pub) {}
    public Proof rocket(Proof p) { return null;}

    private void clearP() {}
    private void setDepthP(long blaValue) {}
    private long getDepthP() { return 0; }
    private void setP(int pri, int prot, int pub) {}

    protected void clearPRO() {}
    protected void setDepthPRO(long blaValue) {}
    protected long getDepthPRO() { return 0; }
    protected void setPRO(int pri, int prot, int pub) {}

    void clearT() {}
    void setDepthT(long blaValue) {}
    long getDepthT() { return 0; }
    void setT(int pri, int prot, int pub) {}
}
