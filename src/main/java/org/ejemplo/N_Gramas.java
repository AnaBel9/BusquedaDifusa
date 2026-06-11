package org.ejemplo;

import java.util.ArrayList;
import java.util.List;

public class N_Gramas {

    public List<String> ngramsList(String s, int n) {

        List<String> output = new ArrayList<>();

        if (s.length() < n) {
            output.add(s);
            return output;
        }

        for (int i = 0; i <= s.length() - n; i++) {
            output.add(s.substring(i, i + n));
        }

        return output;
    }

    public int calcularComunes(List<String> A, List<String> B){

        if(A.get(0).isEmpty() || B.get(0).isEmpty()) return 0;

        List<String> Bcopy = new ArrayList<>(B);

        int comunes = 0;

        for (String g : A) {
            if (Bcopy.contains(g)) {
                comunes++;
                Bcopy.remove(g); // evita contar repetidos varias veces
            }
        }

        return comunes;
    }
}
