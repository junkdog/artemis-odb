package com.artemis.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Basic extends Component {
    public int x;
    public Basic o;
    public String s;

    public void set(int x) { this.x=x; }
    public void set(int x, Basic o) { this.x=x; this.o = o; }

    public String custom() { return "test";}
    public String custom( String banana ) { return banana; }
}
