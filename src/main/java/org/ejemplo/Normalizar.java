package org.ejemplo;
import java.text.Normalizer;

public class Normalizar{

    private com.sun.star.lang.Locale m_Locale;

    public Normalizar(com.sun.star.lang.Locale m_locale) {
        this.m_Locale = m_locale;
    }

    public String procesar(String texto) {

        java.util.Locale javaLocale = new java.util.Locale(m_Locale.Language, m_Locale.Country);
        texto = texto.toLowerCase(javaLocale);

        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("\\p{M}", "");

        texto = texto.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]", "");

        texto = texto.replaceAll("\\s+", " ");

        texto = texto.replaceAll("\\s+$", "");

        texto = texto.replaceAll("^\\s+", "");

        return texto;
    }


}
