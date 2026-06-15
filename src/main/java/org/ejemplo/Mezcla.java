package org.ejemplo;

import java.util.ArrayList;
import java.util.List;

public class Mezcla {

    public double mezcla(String p1, String p2){
        if (p1.isEmpty() && p2.isEmpty()) return 1.0;
        if (p1.isEmpty() || p2.isEmpty()) return 0.0;

        N_Gramas trigramas = new N_Gramas();

        List<String> A = trigramas.ngramsList(p1, 3);
        List<String> B = trigramas.ngramsList(p2, 3);

        double A2B = comparar(A, B); //A -> B
        double B2A = comparar(B, A); //B -> A

        return Math.round(((A2B + B2A) / 2) * 100.0) / 100.0;
    }

    public double comparar(List<String> origen, List<String> destino){
        Levenshtein levenshtein = new Levenshtein();

        List<String> copiaDestino = new ArrayList<>(destino);

        double sumaSimilitudes = 0.0;

        for(String gramaOrigen : origen){

            if(copiaDestino.isEmpty()) break;

            double mejorSimilitud = 0.0;
            int mejorIndice = -1;

            for(int j = 0; j < copiaDestino.size(); j++){
                String gramaDestino = copiaDestino.get(j);

                int distancia = levenshtein.matrizLevenshtein(gramaOrigen, gramaDestino);
                double similitud = 1.0 - (double) distancia / Math.max(gramaOrigen.length(), gramaDestino.length());

                if(similitud < 0.5) similitud = 0.0;

                if(similitud > mejorSimilitud){
                    mejorSimilitud = similitud;
                    mejorIndice = j;
                }

                if(similitud >= 1.0) break;
            }

            if(mejorIndice != -1){
                copiaDestino.remove(mejorIndice);
            }
            sumaSimilitudes += mejorSimilitud;
        }

        return sumaSimilitudes / origen.size();
    }

}
