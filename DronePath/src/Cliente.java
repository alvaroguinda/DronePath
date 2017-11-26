
import java.util.*;

/**
 * 
 */
public class Cliente {

    /**
     * Default constructor
     */
    public Cliente() {
    }

    /**
     * 
     */
    private String nombre;

    /**
     * 
     */
    private static ArrayList<Peticion> peticiones = new ArrayList<>();

    /**
     * @param origen 
     * @param destino 
     * @param prioridad
     */
    public static Peticion anadirPeticion(String origen, String destino, Prioridad prioridad) {
        // TODO implement here
        Peticion p = new Peticion(origen, destino, prioridad);
        peticiones.add(p);
        return p;
    }

    /**
     * @param peticion
     */
    public static void eliminarPeticion(Peticion peticion) {
        // TODO implement here
        peticiones.remove(peticion);
    }

    /**
     * @param peticion
     */
    public static String realizarPeticion(String origen, String destino, Prioridad prioridad, Mapa mapa) {
        // TODO implement here
        Peticion p = buscarPeticion(origen, destino, prioridad);
        if (p != null)
            return p.ejecutarPeticion(mapa);
        return "";
    }

    private static Peticion buscarPeticion(String origen, String destino, Prioridad prioridad) {
        for (Peticion p : peticiones) {
            if (p.obtenerOrigen().equals(origen) && (p.obtenerDestino().equals(destino)) && (prioridad == p.obtenerPrioridad())) {
                return p;
            }
        }
        return null;
    }
    
    public static ArrayList<Peticion> obtenerPeticiones () {
        return peticiones;
    }
    
    public static Peticion obtenerPeticion(int index) {
        return peticiones.get(index);
    }
}