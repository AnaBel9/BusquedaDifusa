package org.ejemplo;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MetricasTest {
    @Test
    public void metricas(){
        BusquedaDifusaImpl motor = new BusquedaDifusaImpl(null);

        // 1. Definimos un set de datos de prueba (Palabra A, Palabra B, Similitud esperada aprox)
        String[][] casos = {
                {"casa", "caza"},
                {"algoritmo", "logaritmo"},
                {"LibreOffice", "Libre Office"},
                {"música", "musica"},
                {"desoxirribonucleico", "desoxirribunucleico"},
                {"casa", "calle"},
                {"calle", "casa"},
                {"casa", "casa"},
                {"ana", "maria"},
                {"agua", "manantial"},
                {"patata", "palabra"},
                {"roberto", "alberto"},
                {"cateto", "cateta"},
                {"Juan Pérez y A. Belen Alba", "A. B. Alba, J. Perez"},
                {"elliot", "ellie"},
                {"jose", "José Vélez"},
                {"lavadora", "cegadora"},
                {"Ana Belen, Alba", "Ana Belen Alba"},
                {"مدرسة", "مدرسيّة"},
                {"2 cerezas", "3 cerezas"},
                {"经济发展", "经济发达"},
                {"文化交流", "文化教育"},
                {"123456", "1234"},
                {"经济发展", "palabra"},
                {"水", "电脑"},
                {"مدرسة", "شمس"},
                {"中国", "中文"},
                {"hola", ""},
                {"aaaaa", "aaa"},
                {"aaabaaa", "aaaaaa"},
                {"marinayjavier@marinayjavier.com", "https://www.tapiceroscortinasvigo.com"},
                // Nuevos casos (árabe)
                {"كتاب", "كتاب"},
                {"كبرى", "كبير"},
                {"مدرسة", "شمس"},

                // Nuevos casos (chino)
                {"学生", "学生"},
                {"中国", "中文"},
                {"水", "电脑"},

                // Nuevos casos (japonés)
                {"ねこ", "ねこ"},
                {"がくせい", "がっこう"},
                {"山", "テレビ"}
        };

        System.out.println("=== INICIANDO LABORATORIO DE MÉTRICAS ===");

        // 2. "Calentamiento" (Warm-up)
        // La JVM optimiza el código mientras se ejecuta. Corremos 1000 veces sin medir.
        for (int i = 0; i < 1000; i++) {
            motor.levNgrams("warmup", "warmup", 3);
        }

        // 3. Pruebas por cada modo
        for (int modo = 1; modo <= 3; modo++) {
            String nombreModo = (modo == 1) ? "LEVENSHTEIN" : (modo == 2) ? "3-GRAMS" : "MEZCLA (Default)";
            System.out.println("\n--- Probando MODO " + modo + ": " + nombreModo + " ---");

            long start = System.nanoTime();

            for (String[] caso : casos) {
                double res = motor.levNgrams(caso[0], caso[1], modo);
                System.out.println(String.format("[%s] vs [%s] -> Score: %.4f", caso[0], caso[1], res));
            }

            long end = System.nanoTime();
            double tiempoMedio = (end - start) / (double) casos.length / 1_000_000.0;
            System.out.println("Tiempo medio por comparación: " + tiempoMedio + " ms");
        }
    }

    @Test
    public void estudioEscalabilidadEmpresas() throws Exception {
        BusquedaDifusaImpl motor = new BusquedaDifusaImpl(null);
        Path inputPath = Paths.get("src/test/resources/empresas_total.csv");
        // El archivo de salida se creará en la carpeta target del proyecto
        Path outputPath = Paths.get("target/estudio_escalabilidad.csv");

        List<String> todasLasLineas = Files.readAllLines(inputPath);
        todasLasLineas.remove(0); // Quitar cabecera

        int[] intervalos = {10, 50, 100, 250, 500, 1000};

        // Preparar el archivo de resultados con su propia cabecera
        List<String> filasDestino = new ArrayList<>();
        filasDestino.add("Intervalo;Modo;TiempoTotal_ms;TiempoOperacion_ms");

        System.out.println("=== INICIANDO ESTUDIO Y GENERANDO ARCHIVO ===");

        for (int n : intervalos) {
            int limite = Math.min(n, todasLasLineas.size());

            for (int modo = 1; modo <= 3; modo++) {
                long start = System.nanoTime();

                for (int i = 0; i < limite; i++) {
                    String[] col = todasLasLineas.get(i).split(",", -1);
                    String correo = (col.length >= 4) ? col[3] : "";
                    String web = (col.length >= 5) ? col[4] : "";
                    motor.levNgrams(correo, web, modo);
                }

                long end = System.nanoTime();
                double totalMs = (end - start) / 1_000_000.0;
                double mediaMs = totalMs / limite;

                // Formatear para consola
                System.out.printf("%-10d | %-17.4f | %-21.6f | Modo %d%n",
                        limite, totalMs, mediaMs, modo);

                // Formatear para el CSV (usamos punto y coma para que Excel/Calc lo abra bien)
                // Usamos Locale.US para que los decimales salgan con punto (.) y no den problemas
                String filaCsv = String.format("%d;%d;%.4f;%.6f",
                        limite, modo, totalMs, mediaMs);
                filasDestino.add(filaCsv);
            }
        }

        // Guardar todas las filas en el archivo
        Files.write(outputPath, filasDestino);
        System.out.println("\nArchivo generado con éxito en: " + outputPath.toAbsolutePath());
    }

    @Test
    public void generarResultadosComparacionCsv() throws Exception {
        // 1. Configuración de rutas y motor
        BusquedaDifusaImpl motor = new BusquedaDifusaImpl(null);
        Path inputPath = Paths.get("src/test/resources/empresas_total.csv");
        Path outputPath = Paths.get("target/resultados_comparacion.csv");

        // 2. Lectura de datos
        List<String> todasLasLineas = Files.readAllLines(inputPath);
        if (todasLasLineas.isEmpty()) return;

        todasLasLineas.remove(0); // Eliminar cabecera original

        // 3. Preparar lista para el CSV de salida con su cabecera
        List<String> filasDestino = new ArrayList<>();
        filasDestino.add("Correo;Web;Modo;Resultado");

        // Limitamos a 1000 pares de palabras como máximo
        int limiteMaximo = Math.min(1000, todasLasLineas.size());

        System.out.println("Procesando " + limiteMaximo + " registros...");

        for (int i = 0; i < limiteMaximo; i++) {
            String[] col = todasLasLineas.get(i).split(",", -1);

            // Extraer correo (columna 4) y web (columna 5) según tu ejemplo
            String correo = (col.length >= 4) ? col[3].trim() : "";
            String web = (col.length >= 5) ? col[4].trim() : "";

            // Ejecutar para los 3 modos solicitados
            for (int modo = 1; modo <= 3; modo++) {
                // Obtenemos el resultado del método
                double resultado = motor.levNgrams(correo, web, modo);

                // Formatear fila: Correo;Web;Modo;Resultado
                // Usamos String.valueOf o format para asegurar el formato numérico
                String filaCsv = String.format("%s;%s;%d;%.4f",
                        correo, web, modo, resultado);
                filasDestino.add(filaCsv);
            }
        }

        // 4. Guardar en el archivo de salida
        Files.write(outputPath, filasDestino);

        System.out.println("=== PROCESO FINALIZADO ===");
        System.out.println("Archivo generado en: " + outputPath.toAbsolutePath());
    }

    @Test
    public void debugComparacionUnica() throws Exception {
        // 1. Inicializa tu motor
        BusquedaDifusaImpl motor = new BusquedaDifusaImpl(null);

        // 2. CONFIGURA AQUÍ TUS PALABRAS PARA PRUEBAS
        String palabra1 = "Ana";
        String palabra2 = "An";

        System.out.println("=== INICIANDO DEBUG ===");
        System.out.println("Palabra 1: [" + palabra1 + "]");
        System.out.println("Palabra 2: [" + palabra2 + "]");
        System.out.println("-----------------------");

        // 3. Ejecuta los 3 modos y muestra el resultado por consola
        for (int modo = 1; modo <= 3; modo++) {
            double resultado = motor.levNgrams(palabra1, palabra2, modo);

            System.out.printf("MODO %d -> Resultado: %.4f%n", modo, resultado);
        }

        System.out.println("=== FIN DEL DEBUG ===");
    }
}
