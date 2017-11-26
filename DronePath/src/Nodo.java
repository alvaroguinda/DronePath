
import java.util.*;
import java.io.*;

/**
 * 
 */
public class Nodo implements Comparable<Nodo>{

    private int index;
    
    /**
     * Default constructor
     */
    public Nodo(String nombre, int index) {
        this.nombre = nombre;
        this.index = index;
    }

    public int obtenerIndice () {
        return index;
    }
    
    /**
     * 
     */
    private final String nombre;

    public String obtenerNombre() {
        return nombre;
    }

    /**
     * 
     */
    private Integer distancia = Integer.MAX_VALUE;

    public void asignarDistancia(Integer distancia) {
        this.distancia = distancia;
    }

    public Integer obtenerDistancia() {
        return distancia;
    }

    /**
     * 
     */
    private Nodo previo = null;

    public void asignarPrevio(Nodo previo) {
        this.previo = previo;
    }

    /**
     * 
     */
    private final Map<Nodo, Integer> vecinos = new HashMap<>();

    public void incluirVecino(Nodo n, Integer distancia) {
        vecinos.put(n,distancia);
    }

    public Map<Nodo, Integer> obtenerVecinos() {
        return vecinos;
    }

    /**
     * 
     */
    private Mapa mapa;


    public ArrayList<Nodo> obtenerCaminoList () {
        ArrayList<Nodo> n = new ArrayList<Nodo>();
        if (this == this.previo) {
            n.add(this);
            System.out.printf("%s",this.nombre);
        }
        else if (this.previo == null) {
            n = null;
            System.out.printf("%s(inalcanzable)",this.nombre);
        }
        else {
            n.addAll(this.previo.obtenerCaminoList());
            n.add(this);
            System.out.printf(" -> %s(%d)", this.nombre, this.distancia);
        }
        return n;
    }


    /**
     * 
     */
    public String mostrarCamino() {
        String s = "";
        if (this == this.previo) {
            s+=this.nombre;
            System.out.printf("%s",this.nombre);
        }
        else if (this.previo == null) {
            s+=this.nombre + "(inalcanzable)";
            System.out.printf("%s(inalcanzable)",this.nombre);
        }
        else {
            s+=this.previo.mostrarCamino();
            s+=" -> " + this.nombre;
            System.out.printf(" -> %s(%d)", this.nombre, this.distancia);
        }
        return s;
    }

    /**
     * @param n
     */
    @Override
    public int compareTo(Nodo n) {
        int res =  Integer.compare(this.distancia, n.obtenerDistancia());
        /*
        if (res == 0)
            return 1;
        else if (res < 0)
            return res;
        */
        return res;
        //return this.nombre.compareTo(n.obtenerNombre());
    }

    public String toString () {
        String s = "Nodo: " + nombre + " Distancia: " + distancia + " Vecinos: ";
        for (Nodo i : vecinos.keySet()) {
            s += i.obtenerNombre() + ", ";
        }
        s += "\n";
        return s;
    }

}