package padel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Clase principal del Taller Pádel.
 * @author Pablo
 */
public class Padel {

    // Las constantes deben estar a nivel de CLASE, no dentro de main()
    private static final String RUTA_BINARIO = "datos/partidos.dat";
    private static final String RUTA_TEXTO   = "datos/partidos.txt";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        imprimirSeparador("TALLER PÁDEL – Persistencia en Java");

        // ─── 1. CREAR DATOS DE PRUEBA ──────────────────────────────────────
        List<PartidoPadel> partidos = Arrays.asList(
            new PartidoPadel("García-López",     "Martínez-Ruiz",  2, 1, "2025-06-10"),
            new PartidoPadel("Fernández-Torres", "Sánchez-Gómez",  2, 0, "2025-06-10"),
            new PartidoPadel("Navarro-Díaz",     "Moreno-Jiménez", 1, 2, "2025-06-11"),
            new PartidoPadel("Romero-Alonso",    "Castro-Reyes",   2, 2, "2025-06-11")
        );

        System.out.println("Partidos creados en memoria:");
        partidos.forEach(System.out::println);

        // Asegurar que el directorio 'datos/' exista
        new java.io.File("datos").mkdirs();

        // ─── 2. PERSISTENCIA BINARIA ───────────────────────────────────────
        imprimirSeparador("PASO 1 — Serialización Binaria (.dat)");

        GestorPadelBinario gestorBinario = new GestorPadelBinario(RUTA_BINARIO);

        try {
            gestorBinario.guardarPartidos(partidos);

            PartidoPadel extra = new PartidoPadel("Vega-Ortiz", "Blanco-Serrano", 2, 1, "2025-06-12");
            gestorBinario.agregarPartido(extra);
            System.out.println("Partido extra agregado correctamente.");

        } catch (IOException e) {
            System.err.println("Error al guardar datos binarios: " + e.getMessage());
        }

        // ─── 3. CARGAR DESDE BINARIO ───────────────────────────────────────
        imprimirSeparador("PASO 2 — Carga desde archivo binario");

        try {
            List<PartidoPadel> cargadosBinario = gestorBinario.cargarPartidos();
            System.out.println("\nPartidos recuperados desde .dat:");
            cargadosBinario.forEach(p -> System.out.println("  " + p));

        } catch (IOException e) {
            System.err.println("Error al cargar datos binarios: " + e.getMessage());
        }

        // ─── 4. PERSISTENCIA EN TEXTO PLANO ───────────────────────────────
        imprimirSeparador("PASO 3 — Texto Plano (.txt)");

        GestorPadelTexto gestorTexto = new GestorPadelTexto(RUTA_TEXTO);

        try {
            gestorTexto.guardarPartidos(partidos);

            PartidoPadel extra2 = new PartidoPadel("Gil-Mora", "Pardo-Navarro", 0, 2, "2025-06-12");
            gestorTexto.agregarPartido(extra2);

        } catch (IOException e) {
            System.err.println("Error al guardar texto plano: " + e.getMessage());
        }

        // ─── 5. CARGAR DESDE TEXTO PLANO ──────────────────────────────────
        imprimirSeparador("PASO 4 — Carga desde archivo de texto");

        try {
            List<PartidoPadel> cargadosTexto = gestorTexto.cargarPartidos();
            System.out.println("\nPartidos recuperados desde .txt:");
            cargadosTexto.forEach(p -> System.out.println("  " + p));

        } catch (IOException e) {
            System.err.println("Error al cargar texto plano: " + e.getMessage());
        }

        // ─── 6. MOSTRAR CONTENIDO RAW DEL ARCHIVO .TXT ────────────────────
        imprimirSeparador("PASO 5 — Contenido raw del archivo .txt");
        System.out.println("(Este es el texto que se ve al abrir partidos.txt con un editor)\n");

        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.FileReader(RUTA_TEXTO, java.nio.charset.StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println("  " + linea);
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo de texto: " + e.getMessage());
        }

        // ─── 7. RESUMEN COMPARATIVO ───────────────────────────────────────
        imprimirSeparador("RESUMEN COMPARATIVO");
        System.out.println("""
            ┌───────────────────┬──────────────────────────┬──────────────────────────┐
            │ Característica    │ Binario (.dat)           │ Texto Plano (.txt)       │
            ├───────────────────┼──────────────────────────┼──────────────────────────┤
            │ Legibilidad       │ No legible (bytes)       │ Legible con editor       │
            │ Velocidad E/S     │ Más rápida               │ Más lenta                │
            │ Complejidad       │ Automática (JVM)         │ Parsing manual           │
            │ Interoperabilidad │ Solo Java                │ Cualquier sistema        │
            │ Escalabilidad     │ Alta (grafo de objetos)  │ Baja (más atributos=     │
            │                   │                          │ más parsing)             │
            │ Compatibilidad    │ Requiere serialVersionUID│ Siempre compatible       │
            └───────────────────┴──────────────────────────┴──────────────────────────┘
            """);

        System.out.println("Archivos generados:");
        System.out.println("  • " + RUTA_BINARIO + " (abrir con editor de hex para ver los bytes)");
        System.out.println("  • " + RUTA_TEXTO   + " (abrir con cualquier editor de texto)");
        System.out.println("\n¡Taller completado exitosamente!");
    }

    private static void imprimirSeparador(String titulo) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + titulo);
        System.out.println("=".repeat(60));
    }
}