
import java.awt.Color;
import java.util.*;

/**
 * 
 */
public class Peticion {

    /**
     * Default constructor
     */
    public Peticion(String origen, String destino, Prioridad prioridad) {
        this.origen = origen;
        this.destino = destino;
        this.prioridad = prioridad;
        this.ejecutada = false;
        this.camino = new ArrayList<Nodo>();
    }

    private Color color;
    private String color_s;
    
    public void establecerColor (Color c, String cs) {
        this.color = c;
        this.color_s = cs;
    }
    
    public Color obtenerColor () {
        return this.color;
    }
    
    public String obtenerColorString () {
        return this.color_s;
    }
    
    /**
     * 
     */
    private boolean ejecutada;
    
    public boolean estaEjecutada() {
        return ejecutada;
    }
    
    public void ejecutarPeticion() {
        this.ejecutada = true;
    }
    
    /**
     * 
     */
    private String origen;

    public String obtenerOrigen() {
        return origen;
    }
    
    /**
     * 
     */
    private String destino;

    public String obtenerDestino() {
        return destino;
    }
    
    /**
     * 
     */
    private Prioridad prioridad;

    public Prioridad obtenerPrioridad() {
        return prioridad;
    }
    
    /**
     * 
     */
    private Mapa mapa;

    /**
     * 
     */
    private Cliente cliente;

    /**
     * 
     */
    private ArrayList<Nodo> camino;
    private int index;
    
    public Nodo obtenerSiguienteNodoCamino () {
        if (index >= camino.size()) {
            return null;
        }
        return camino.get(index);
    }
    
    public Nodo obtenerNodoSiguiente() {
        if (index + 1 >= camino.size()) {
            return null;
        }
        return camino.get(index+1);
    }
    
    public void avanzarEnElCamino () {
        index++;
    }
    
    /**
     * 
     */
    public String ejecutarPeticion(Mapa mapa) {
        mapa.calcularCamino(origen);
        String s = mapa.mostrarCaminoADestino(destino);
        camino = mapa.mostrarCaminoADestinoList(destino);
        index = 0;
        //mapa.mostrarTodosLosCaminosDesdeOrigen();
        return s;
    }

}