package padel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de persistencia BINARIA para objetos PartidoPadel.
 *
 * Utiliza ObjectOutputStream / ObjectInputStream del paquete java.io
 * para serializar y deserializar objetos directamente a/desde un archivo .dat.
 *
 * Ventajas del formato binario:
 *  - Almacena el grafo completo de objetos Java de forma automática.
 *  - No requiere parsing manual: Java reconstruye el objeto íntegro.
 *  - Ideal cuando la aplicación es el único consumidor de los datos.
 *
 * Desventajas:
 *  - El archivo .dat es ilegible por humanos o por otras herramientas.
 *  - Cambios en la clase (nuevos campos, renombrados) pueden romper
 *    la compatibilidad si no se gestiona serialVersionUID.
 */
public class GestorPadelBinario {

    private final String rutaArchivo;

    /**
     * @param rutaArchivo Ruta al archivo .dat donde se almacenará la lista de partidos.
     */
    public GestorPadelBinario(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    // ──── GUARDAR ────────────────────────────────────────────────────────────

    /**
     * Serializa y guarda la lista completa de partidos en el archivo binario.
     * Sobrescribe cualquier contenido previo.
     *
     * @param partidos Lista de partidos a persistir.
     * @throws IOException Si ocurre un error de escritura.
     */
    public void guardarPartidos(List<PartidoPadel> partidos) throws IOException {
        // FileOutputStream abre (o crea) el archivo en modo escritura
        // ObjectOutputStream envuelve el stream y añade la cabecera de serialización
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(rutaArchivo))) {

            oos.writeObject(partidos);   // Serializa toda la lista de una vez
            System.out.println("[BINARIO] " + partidos.size()
                    + " partido(s) guardado(s) en: " + rutaArchivo);
        }
    }

    /**
     * Serializa y añade un solo partido al final de la lista existente en disco.
     * Si el archivo no existe, crea uno nuevo con este partido.
     *
     * @param partido Partido a agregar.
     * @throws IOException Si ocurre un error de E/S.
     */
    public void agregarPartido(PartidoPadel partido) throws IOException {
        List<PartidoPadel> lista = new java.util.ArrayList<>(cargarPartidos()); // Carga los datos existentes
        lista.add(partido);
        guardarPartidos(lista);            // Reescribe el archivo completo
    }

    // ──── CARGAR ─────────────────────────────────────────────────────────────

    /**
     * Deserializa y recupera la lista de partidos desde el archivo binario.
     *
     * @return Lista de objetos PartidoPadel, o lista vacía si el archivo no existe.
     * @throws IOException            Si ocurre un error de lectura.
     * @throws ClassNotFoundException Si la clase PartidoPadel no se encuentra en el classpath.
     */
    @SuppressWarnings("unchecked")
    public List<PartidoPadel> cargarPartidos() throws IOException {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            System.out.println("[BINARIO] Archivo no encontrado; se retorna lista vacía.");
            return new ArrayList<>();
        }

        // ObjectInputStream reconstruye el objeto desde los bytes del archivo
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(rutaArchivo))) {

            List<PartidoPadel> partidos = (List<PartidoPadel>) ois.readObject();
            System.out.println("[BINARIO] " + partidos.size()
                    + " partido(s) cargado(s) desde: " + rutaArchivo);
            return partidos;

        } catch (ClassNotFoundException e) {
            throw new IOException("No se pudo reconstruir el objeto: clase no encontrada.", e);
        }
    }

    // ──── UTILIDADES ─────────────────────────────────────────────────────────

    /**
     * Elimina el archivo binario del sistema de ficheros.
     * @return true si se eliminó exitosamente, false en caso contrario.
     */
    public boolean eliminarArchivo() {
        return new File(rutaArchivo).delete();
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }
}
