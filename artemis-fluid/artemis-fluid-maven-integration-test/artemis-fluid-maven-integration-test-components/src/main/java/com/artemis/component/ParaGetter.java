package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.Fluid;

/**
 * @author Daan van Yperen
 */
@Fluid(swallowGettersWithParameters = true)
public class ParaGetter extends Component {
    public String custom( String a ) { return a; }
}
