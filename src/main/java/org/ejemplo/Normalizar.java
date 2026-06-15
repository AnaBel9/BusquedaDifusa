package org.ejemplo;
import java.text.Normalizer;

public class Normalizar{

    private com.sun.star.lang.Locale m_Locale;

    public Normalizar(com.sun.star.lang.Locale m_locale) {
        this.m_Locale = m_locale;
    }

    public String procesar(String texto) {

        //configurar idioma
        java.util.Locale javaLocale = new java.util.Locale(m_Locale.Language, m_Locale.Country);

        //pasa a minúsculas
        texto = texto.toLowerCase(javaLocale);

        //eliminar acentos y diacríticos
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("\\p{M}", "");

        //elimina lo que no sea letras, números y espacios
        texto = texto.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]", "");

        //reduce múltiples espacios a uno solo
        texto = texto.replaceAll("\\s+", " ");

        //elimina espacios del final
        texto = texto.replaceAll("\\s+$", "");

        //elimina espacios del inicio
        texto = texto.replaceAll("^\\s+", "");

        return texto;
    }


}
