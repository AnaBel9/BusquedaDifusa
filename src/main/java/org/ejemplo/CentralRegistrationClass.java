package org.ejemplo;

import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;

public class CentralRegistrationClass {
    public static XSingleComponentFactory __getComponentFactory(String sImplementationName) {
        return BusquedaDifusaImpl.__getComponentFactory(sImplementationName);
    }

    public static boolean __writeRegistryServiceInfo(XRegistryKey xRegistryKey) {
        return BusquedaDifusaImpl.__writeRegistryServiceInfo(xRegistryKey);
    }
}
