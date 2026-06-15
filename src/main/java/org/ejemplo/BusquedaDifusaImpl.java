package org.ejemplo;

import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lang.XLocalizable;

import java.util.List;

public class BusquedaDifusaImpl extends WeakBase implements XServiceInfo, XLocalizable, XBusquedaDifusa{

    private final XComponentContext m_xContext;
    private static final String m_implementationName = BusquedaDifusaImpl.class.getName();
    private static final String[] m_serviceNames = { "org.ejemplo.BusquedaDifusa"};

    private com.sun.star.lang.Locale m_locale = new com.sun.star.lang.Locale();

    public BusquedaDifusaImpl(XComponentContext context) {
        this.m_xContext = context;
    }

    public static XSingleComponentFactory __getComponentFactory(String sImplementationName) {
        XSingleComponentFactory xFactory = null;

        if (sImplementationName.equals(m_implementationName))
            xFactory = Factory.createComponentFactory(BusquedaDifusaImpl.class, m_serviceNames);
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo(XRegistryKey xRegistryKey) {
        return Factory.writeRegistryServiceInfo(m_implementationName, m_serviceNames, xRegistryKey);
    }

    //XServiceInfo
    @Override
    public String getImplementationName() {
        return m_implementationName;
    }

    @Override
    public boolean supportsService(String sService) {
        for (String s : m_serviceNames) {
            if (sService.equals(s)) return true;
        }
        return false;
    }

    @Override
    public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }

    //XLocalizable
    public void setLocale(com.sun.star.lang.Locale eLocale) {
        m_locale = eLocale;
    }

    public com.sun.star.lang.Locale getLocale() {
        return m_locale;
    }

    //XBusquedaDifusa
    @Override
    public double levNgrams(String a, String b, Object modo) {

        Normalizar normalizador = new Normalizar(getLocale());

        String p1norm = normalizador.procesar(a);
        String p2norm = normalizador.procesar(b);

        int valorModo = 3;

        if(modo instanceof Integer){
            valorModo = (Integer) modo;
        }else if(modo instanceof Double){
            valorModo = ((Double) modo).intValue();
        }

        if(valorModo == 1){
            //LEVENSHTEIN
            Levenshtein levenshtein = new Levenshtein();

            return levenshtein.matrizLevenshtein(p1norm, p2norm);
        }else if (valorModo == 2){
            //3-GRAMAS
            N_Gramas trigramas = new N_Gramas();

            List<String> A = trigramas.ngramsList(p1norm, 3);
            List<String> B = trigramas.ngramsList(p2norm, 3);

            return trigramas.calcularComunes(A, B);
        }else{
            //MEZCLA
            Mezcla combinacion = new Mezcla();

            return combinacion.mezcla(p1norm, p2norm);
        }
    }
}
