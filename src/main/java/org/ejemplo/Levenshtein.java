package org.ejemplo;

public class Levenshtein {

    public int matrizLevenshtein(String xs, String ys) {
        int n = xs.length();
        int m = ys.length();

        if(n == 0) return m;
        if(m == 0) return n;

        int[][] q = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) q[i][0] = i;
        for (int j = 0; j <= m; j++) q[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {

                if (xs.charAt(i - 1) == ys.charAt(j - 1)) {
                    q[i][j] = q[i - 1][j - 1];
                } else {
                    q[i][j] = 1 + Math.min(
                            Math.min(q[i][j - 1], q[i - 1][j]), //inserción, eliminación
                            q[i - 1][j - 1] //sustitución
                    );
                }
            }
        }

        return q[n][m];
    }
}
