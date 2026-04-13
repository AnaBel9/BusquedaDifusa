package org.ejemplo;

import com.sun.star.uno.XInterface;
import com.sun.star.lang.XLocalizable;

public interface XBusquedaDifusa extends XInterface, XLocalizable{
    double levNgrams(String a, String b, int modo);
}
