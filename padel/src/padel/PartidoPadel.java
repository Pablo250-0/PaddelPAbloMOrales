package padel;

import java.io.Serializable;

/**
 * Clase modelo que representa un partido de pádel.
 * Implementa Serializable para permitir la persistencia binaria.
 */
public class PartidoPadel implements Serializable {

    // Número de versión para control de compatibilidad en serialización
    private static final long serialVersionUID = 1L;

    private String pareja1;
    private String pareja2;
    private int setsPaeja1;
    private int setsPareja2;
    private String fecha;

    // Constructor vacío necesario para algunas operaciones de deserialización
    public PartidoPadel() {}

    public PartidoPadel(String pareja1, String pareja2, int setsPaeja1, int setsPareja2, String fecha) {
        this.pareja1 = pareja1;
        this.pareja2 = pareja2;
        this.setsPaeja1 = setsPaeja1;
        this.setsPareja2 = setsPareja2;
        this.fecha = fecha;
    }

    // ──── Getters y Setters ────────────────────────────────────────────────

    public String getPareja1() { return pareja1; }
    public void setPareja1(String pareja1) { this.pareja1 = pareja1; }

    public String getPareja2() { return pareja2; }
    public void setPareja2(String pareja2) { this.pareja2 = pareja2; }

    public int getSetsPareja1() { return setsPaeja1; }
    public void setSetsPareja1(int sets) { this.setsPaeja1 = sets; }

    public int getSetsPareja2() { return setsPareja2; }
    public void setSetsPareja2(int sets) { this.setsPareja2 = sets; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    /**
     * Determina el ganador del partido.
     * @return Nombre de la pareja ganadora o "Empate"
     */
    public String getGanador() {
        if (setsPaeja1 > setsPareja2) return pareja1;
        if (setsPareja2 > setsPaeja1) return pareja2;
        return "Empate";
    }

    @Override
    public String toString() {
        return String.format(
            "Partido [%s] | %s vs %s | Marcador: %d-%d | Ganador: %s",
            fecha, pareja1, pareja2, setsPaeja1, setsPareja2, getGanador()
        );
    }
}
