
import java.util.*;

/**
 * 
 */
public class Calle {

    /**
     * Default constructor
     */
    public Calle(Nodo origen, Nodo destino, Integer distancia) {
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
        this.enUso = false;
    }

    /**
     * 
     */
    private Integer distancia;

    public Integer obtenerDistancia() {
        return distancia;
    }

    /**
     * 
     */
    private Nodo origen;

    public String obtenerNombreOrigen() {
        return origen.obtenerNombre();
    }

    public Nodo obtenerOrigen() {
        return origen;
    }

    /**
     * 
     */
    private Nodo destino;

    public String obtenerNombreDestino() {
        return destino.obtenerNombre();
    }

    public Nodo obtenerDestino() {
        return destino;
    }

    /**
     * 
     */
    private boolean enUso;

    public boolean calleEnUso() {
        return enUso;
    }

}