
import java.util.*;
import java.io.*;

/**
 * 
 */
public class Mapa {

    /**
    *
    */
    private Calle [] calles;

    /**
     * Default constructor
     */
    public Mapa(Calle [] calles) {
        this.calles = calles;
        mapa = new HashMap<>(calles.length);

        // one pass to find all vertices
        for (Calle c : calles) {
            if (!mapa.containsKey(c.obtenerNombreOrigen()))
                mapa.put(c.obtenerNombreOrigen(), c.obtenerOrigen());
            if (!mapa.containsKey(c.obtenerNombreDestino()))
                mapa.put(c.obtenerNombreDestino(), c.obtenerDestino());
        }

        // another pass to set neighbouring vertices
        for (Calle c : calles) {
            mapa.get(c.obtenerNombreOrigen()).incluirVecino(c.obtenerDestino(), c.obtenerDistancia());
            mapa.get(c.obtenerNombreDestino()).incluirVecino(c.obtenerOrigen(), c.obtenerDistancia()); // for undirected graphs
        }
    }

    /**
     * 
     */
    private Map<String, Nodo> mapa;

    public boolean buscarNodo (String nombre) {
        return mapa.containsKey(nombre);
    }
    
    public int obtenerDistancia (Nodo origen, Nodo destino) {
        if (origen == null)
            return 0;
        if (destino == null)
            return 0;
        for (Calle c : calles) {
            if ((c.obtenerNombreOrigen().equals(origen.obtenerNombre())) && (c.obtenerNombreDestino().equals(destino.obtenerNombre()))) {
                return c.obtenerDistancia();
            }
            if ((c.obtenerNombreDestino().equals(origen.obtenerNombre())) && (c.obtenerNombreOrigen().equals(destino.obtenerNombre()))) {
                return c.obtenerDistancia();
            }
        }
        return -1;
    }
    
    /**
     * @param origen
     */
    public void calcularCamino(String origen) {
        if (!mapa.containsKey(origen)) {
            System.err.printf("El mapa no contiene un nodo origen con el nombre \"%s\"\n",origen);
            return;
        }
        final Nodo inicio = mapa.get(origen);
        ArrayList<Nodo> q = new ArrayList<>();

        // set-up the vertices
        for (Nodo n : mapa.values()) {
            n.asignarPrevio(n == inicio ? inicio : null);
            n.asignarDistancia(n == inicio ? 0 : Integer.MAX_VALUE);
            ArrayList<Nodo> aux = new ArrayList<>();
            if (q.isEmpty()) {
                aux.add(n);
             
            }
            else {
                for (Nodo n1 : q) {
                    if (n.obtenerDistancia() <= n1.obtenerDistancia())
                        aux.add(n);
                    else
                        aux.add(n1);
                }
            }
            q = aux;
        }

        dijkstra(q);
    }

    /**
     * 
     */
    private void dijkstra(final ArrayList<Nodo> q) {
        Nodo u, v;

        while (!q.isEmpty()) {
            u = q.get(0); // vertex with shortest distance (first iteration will return the source)
            q.remove(0);
            if (u.obtenerDistancia() == Integer.MAX_VALUE) 
                break;

            // look at distances for all neighbours
            Map<Nodo, Integer> vecinos = u.obtenerVecinos();
            for (Map.Entry<Nodo, Integer> a : vecinos.entrySet()) {
                v = a.getKey(); // the neighbour in this iteration

                for (Calle c : this.calles) {
                    if ((c.obtenerOrigen().equals(u.obtenerNombre())) && (c.obtenerDestino().equals(v.obtenerNombre())))
                        if (c.calleEnUso()) {
                            q.remove(v);
                            continue;
                        }
                }
                final Integer distanciaAlternativa = u.obtenerDistancia() + a.getValue();
                if (distanciaAlternativa < v.obtenerDistancia()) {
                    q.remove(v);
                    v.asignarDistancia(distanciaAlternativa);
                    v.asignarPrevio(u);
                    q.add(v);
                }
            }
        }
    }

    /**
     * 
     */
    public ArrayList<Nodo> mostrarCaminoADestinoList(String destino) {
        if (!mapa.containsKey(destino)) {
            System.err.printf("El mapa no contiene un nodo destino con el nombre \"%s\"", destino);
            return null;
        }
        ArrayList<Nodo> n = mapa.get(destino).obtenerCaminoList();
        return n;
    }
    
    
    /**
    *
    */
    public String mostrarCaminoADestino(String destino) {
        if (!mapa.containsKey(destino)) {
            System.err.printf("El mapa no contiene un nodo destino con el nombre \"%s\"\n", destino);
            return "";
        }

        String s = mapa.get(destino).mostrarCamino();
        System.out.println();
        return s;
    }

    /**
    *
    */
    public void mostrarTodosLosCaminosDesdeOrigen() {
        for (Nodo n : mapa.values()) {
            n.mostrarCamino();
            System.out.println();
        }
    }

}