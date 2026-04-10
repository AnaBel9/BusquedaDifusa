package org.ejemplo;

import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lang.XLocalizable;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;

public class BusquedaDifusaImpl extends WeakBase implements XServiceInfo, XLocalizable, XBusquedaDifusa{

    private final XComponentContext m_xContext;
    private static final String m_implementationName = BusquedaDifusaImpl.class.getName();
    private static final String[] m_serviceNames = { "org.ejemplo.BusquedaDifusa", "com.sun.star.sheet.AddIn"};

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

    public void setLocale(com.sun.star.lang.Locale eLocale) {
        m_locale = eLocale;
    }

    public com.sun.star.lang.Locale getLocale() {
        return m_locale;
    }

    @Override
    public double levNgrams(String a, String b) {

        if (a.isEmpty() && b.isEmpty()) return 1.0;
        if (a.isEmpty() || b.isEmpty()) return 0.0;

        String p1norm = normalizar(a);
        String p2norm = normalizar(b);

        Set<String> A = ngramsSet(p1norm, 3);
        Set<String> B = ngramsSet(p2norm, 3);

        double A2B = 0.0;
        double B2A = 0.0;

        for (String i : A) {

            double mejorAB = 0.0;

            for (String j : B) {

                int lev = matrizLevenshtein(i, j)[i.length()][j.length()];
                double dist = (double) (i.length() - lev) / i.length();

                if (dist > mejorAB)
                    mejorAB = dist;
            }

            A2B += mejorAB;
        }

        A2B = A2B / A.size();

        for (String i : B) {

            double mejorBA = 0.0;

            for (String j : A) {

                int lev = matrizLevenshtein(i, j)[i.length()][j.length()];
                double dist = (double) (i.length() - lev) / i.length();

                if (dist > mejorBA)
                    mejorBA = dist;
            }

            B2A += mejorBA;
        }

        B2A = B2A / B.size();

        return Math.round(((A2B + B2A) / 2) * 100.0) / 100.0;
    }

    private String normalizar(String texto) {

        java.util.Locale javaLocale = new java.util.Locale(m_locale.Language, m_locale.Country);
        texto = texto.toLowerCase(javaLocale);

        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("\\p{M}", "");

        texto = texto.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]", "");

        texto = texto.replaceAll("\\s+", " ");

        texto = texto.replaceAll("\\s+$", "");
        texto = " " + texto.replaceAll("^\\s+", "");

        return texto;
    }

    private Set<String> ngramsSet(String s, int n) {

        Set<String> output = new HashSet<>();

        if (s.length() < n) {
            output.add(s);
            return output;
        }

        for (int i = 0; i <= s.length() - n; i++) {
            output.add(s.substring(i, i + n));
        }

        return output;
    }

    private int[][] matrizLevenshtein(String xs, String ys) {
        int n = xs.length();
        int m = ys.length();

        int[][] q = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) q[i][0] = i;
        for (int j = 0; j <= m; j++) q[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {

                if (xs.charAt(i - 1) == ys.charAt(j - 1)) {
                    q[i][j] = q[i - 1][j - 1];
                } else {
                    q[i][j] = 1 + Math.min(
                            Math.min(q[i - 1][j], q[i][j - 1]),
                            q[i - 1][j - 1]
                    );
                }
            }
        }

        return q;
    }

}
