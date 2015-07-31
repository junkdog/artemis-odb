package com.artemis.injection;

/**
 * @author Snorre E. Brekke
 */
class CachedField {
    public CachedField(boolean wire, String name) {
        this.wire = wire;
        this.name = name;
    }

    public boolean wire;
    public String name;
    public boolean legacy;
}
