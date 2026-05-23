package padel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de persistencia en TEXTO PLANO para objetos PartidoPadel.
 *
 * Utiliza BufferedWriter / BufferedReader del paquete java.io para escribir
 * y leer datos en un formato delimitado por '|' en un archivo .txt.
 *
 * Formato de cada línea en el archivo:
 *   pareja1|pareja2|setsP1|setsP2|fecha
 *
 * Ejemplo:
 *   García-López|Martínez-Ruiz|2|1|2025-06-10
 *
 * Ventajas del formato texto:
 *  - Legible por cualquier editor de texto o herramienta externa.
 *  - Fácil de exportar a hojas de cálculo (CSV/TSV) o sistemas de terceros.
 *  - Sin dependencia de la JVM para interpretar los datos.
 *
 * Desventajas frente a la serialización binaria:
 *  - Requiere parsear (splitting) manualmente cada línea.
 *  - A medida que la clase crece (más atributos, relaciones entre objetos),
 *    el formato se vuelve complejo de mantener y propenso a errores de parsing.
 *  - No gestiona referencias circulares ni grafos de objetos de forma automática.
 */
public class GestorPadelTexto {

    private final String rutaArchivo;
    private static final String SEPARADOR = "|";
    private static final String SEPARADOR_REGEX = "\\|";  // Escapado para split()

    /**
     * @param rutaArchivo Ruta al archivo .txt donde se almacenarán los partidos.
     */
    public GestorPadelTexto(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    // ──── GUARDAR ────────────────────────────────────────────────────────────

    /**
     * Escribe la lista completa de partidos en el archivo de texto.
     * Cada partido ocupa una línea con campos separados por '|'.
     * Sobrescribe el contenido anterior.
     *
     * @param partidos Lista de partidos a persistir.
     * @throws IOException Si ocurre un error de escritura.
     */
    public void guardarPartidos(List<PartidoPadel> partidos) throws IOException {
        // BufferedWriter mejora el rendimiento al escribir en lotes (buffer interno)
        // StandardCharsets.UTF_8 garantiza compatibilidad internacional de caracteres
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(rutaArchivo), StandardCharsets.UTF_8))) {

            for (PartidoPadel p : partidos) {
                bw.write(serializarLinea(p));
                bw.newLine();   // Separador de registros: salto de línea
            }
            System.out.println("[TEXTO] " + partidos.size()
                    + " partido(s) guardado(s) en: " + rutaArchivo);
        }
    }

    /**
     * Añade un solo partido al final del archivo de texto (modo append).
     *
     * @param partido Partido a agregar.
     * @throws IOException Si ocurre un error de escritura.
     */
    public void agregarPartido(PartidoPadel partido) throws IOException {
        // true = modo append (no sobreescribe)
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(rutaArchivo, true), StandardCharsets.UTF_8))) {

            bw.write(serializarLinea(partido));
            bw.newLine();
            System.out.println("[TEXTO] Partido agregado: " + partido.getPareja1()
                    + " vs " + partido.getPareja2());
        }
    }

    // ──── CARGAR ─────────────────────────────────────────────────────────────

    /**
     * Lee y parsea el archivo de texto, reconstruyendo la lista de partidos.
     * Las líneas vacías o con formato incorrecto se omiten con un aviso.
     *
     * @return Lista de objetos PartidoPadel, o lista vacía si el archivo no existe.
     * @throws IOException Si ocurre un error de lectura.
     */
    public List<PartidoPadel> cargarPartidos() throws IOException {
        List<PartidoPadel> partidos = new ArrayList<>();
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            System.out.println("[TEXTO] Archivo no encontrado; se retorna lista vacía.");
            return partidos;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(rutaArchivo), StandardCharsets.UTF_8))) {

            String linea;
            int numeroLinea = 0;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;   // Ignorar líneas en blanco

                PartidoPadel partido = parsearLinea(linea, numeroLinea);
                if (partido != null) {
                    partidos.add(partido);
                }
            }
        }

        System.out.println("[TEXTO] " + partidos.size()
                + " partido(s) cargado(s) desde: " + rutaArchivo);
        return partidos;
    }

    // ──── MÉTODOS PRIVADOS DE PARSING ────────────────────────────────────────

    /**
     * Convierte un objeto PartidoPadel a su representación en texto plano.
     * Formato: pareja1|pareja2|setsP1|setsP2|fecha
     */
    private String serializarLinea(PartidoPadel p) {
        return p.getPareja1() + SEPARADOR
             + p.getPareja2() + SEPARADOR
             + p.getSetsPareja1() + SEPARADOR
             + p.getSetsPareja2() + SEPARADOR
             + p.getFecha();
    }

    /**
     * Parsea una línea de texto y construye un objeto PartidoPadel.
     * Retorna null si la línea tiene formato inválido.
     *
     * @param linea       Línea a parsear.
     * @param numeroLinea Número de línea (para mensajes de error útiles).
     */
    private PartidoPadel parsearLinea(String linea, int numeroLinea) {
        String[] campos = linea.split(SEPARADOR_REGEX);

        if (campos.length != 5) {
            System.err.println("[TEXTO] Línea " + numeroLinea
                    + " ignorada (campos incorrectos): " + linea);
            return null;
        }

        try {
            String pareja1 = campos[0].trim();
            String pareja2 = campos[1].trim();
            int setsP1    = Integer.parseInt(campos[2].trim());
            int setsP2    = Integer.parseInt(campos[3].trim());
            String fecha  = campos[4].trim();

            return new PartidoPadel(pareja1, pareja2, setsP1, setsP2, fecha);

        } catch (NumberFormatException e) {
            System.err.println("[TEXTO] Línea " + numeroLinea
                    + " ignorada (error de número): " + linea);
            return null;
        }
    }

    // ──── UTILIDADES ─────────────────────────────────────────────────────────

    public boolean eliminarArchivo() {
        return new File(rutaArchivo).delete();
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }
}
