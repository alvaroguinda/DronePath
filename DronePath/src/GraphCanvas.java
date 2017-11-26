
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

class GraphCanvas extends JPanel implements Runnable {
	// drawing area for the graph

	final int MAXNODES = 20;
	final int MAX = MAXNODES+1;
	final int NODESIZE = 26;
	final int NODERADIX = 13;
        final int DRONERADIX = 5;
        final int DRONESIZE = 10;
	final int DIJKSTRA = 1;

        String names[] = new String [] {"H. Sant Joan Deu","Clinica Diagonal","H. Bellvitge","3","Corachan",
            "Dexeus","6","Teknon","Sagrada Familia","Clinic","H. Mare Deu del Mar","H. Quiron","H. Sant Pau","H. Nens BCN","\nH. Vall d'Hebron","15","16","17"};
        
	// basic graph information
	Point node[] = new Point[MAX];          // node
	int weight[][] = new int[MAX][MAX];     // weight of arrow
	Point arrow[][] = new Point[MAX][MAX];  // current position of arrowhead
	Point startp[][] = new Point[MAX][MAX]; // start and
	Point endp[][] = new Point[MAX][MAX];   // endpoint of arrow
	float dir_x[][] = new float[MAX][MAX];  // direction of arrow
	float dir_y[][] = new float[MAX][MAX];  // direction of arrow

	// graph information while running algorithm
	boolean algedge[][] = new boolean[MAX][MAX];
        Color coloredge[][] = new Color[MAX][MAX];
        int droneposition[][] = new int[MAX][MAX];
	int dist[] = new int[MAX];
	int finaldist[] = new int[MAX];
	Color colornode[] = new Color[MAX];
	boolean changed[] = new boolean[MAX];   // indicates distance change during algorithm   
	int numchanged =0; 
	int neighbours=0;

        boolean painting = false;
        
	int step=0;

	// information used by the algorithm to find the next 
	// node with minimum distance
	int mindist, minnode, minstart, minend;

	int numnodes=0;      // number of nodes
	int emptyspots=0;    // empty spots in array node[] (due to node deletion)
	int startgraph=0;    // start of graph
	int hitnode;         // mouse clicked on or close to this node
	int node1, node2;    // numbers of nodes involved in current action

	Point thispoint=new Point(0,0); // current mouseposition
	Point oldpoint=new Point(0, 0); // previous position of node being moved

	// current action
	boolean newarrow = false;
	boolean movearrow = false;
	boolean movestart = false;
	boolean deletenode = false;
	boolean movenode = false;
	boolean performalg = false;
	boolean clicked = false;

	// fonts
	Font roman= new Font("TimesRoman", Font.BOLD, 12);
	Font helvetica= new Font("Helvetica", Font.BOLD, 15);
	FontMetrics fmetrics = getFontMetrics(roman);
	int h = (int)fmetrics.getHeight()/3;

	// for run option
	Thread algrthm;

	// current algorithm, (in case more algorithms are added)
	int algorithm;

	// algorithm information to be displayed in documetation panel
	String showstring = new String("");

	boolean stepthrough=false;

	// locking the screen while running the algorithm
	boolean Locked = false;

	javax.swing.JPanel parent;

	private BufferedImage img;

	private Mapa mapa;

	GraphCanvas(javax.swing.JPanel myparent, Mapa mapa) {
		this.mapa = mapa;
		parent = myparent;
		close_demand = false;
                peticiones = new ArrayList<Peticion>();
		for (int i=0;i<MAXNODES;i++) {
                    colornode[i]=Color.gray;
                    for (int j=0; j<MAXNODES;j++) {
                        algedge[i][j]=false;
                        droneposition[i][j] = -1;
                    }
		}
		performalg = false;
		algorithm=DIJKSTRA;
		//setBackground(Color.white);
                setOpaque(false);
		setSize(new Dimension(parent.getWidth(),parent.getHeight()));
	}

	public void resizeCanvas() {
		setSize(new Dimension(parent.getWidth(),parent.getHeight()));
	}

	public void start() {
		if (algrthm != null) algrthm.resume();
	}

	public void init() {
		for (int i=0;i<MAXNODES;i++) {
			colornode[i]=Color.gray;
			//for (int j=0; j<MAXNODES;j++)
			//	algedge[i][j]=false;
		}
		//colornode[startgraph]=Color.blue;
		performalg = false;
	}

	public void clear() {
		// removes graph from screen
		startgraph=0;
		numnodes=0;
		emptyspots=0;
		/*init();
		for(int i=0; i<MAXNODES; i++) {
			node[i]=new Point(0, 0);
			for (int j=0; j<MAXNODES;j++)
				weight[i][j]=0;
		}*/
		//if (algrthm != null) algrthm.stop();
		repaint();
	}

        private boolean started = false;
        
	public void runalg() {
		// gives an animation of the algorithm
                if (started)
                    return;
                started = true;
		initalg();
		performalg = true;
                close_demand = false;
		algrthm = new Thread(this);
		algrthm.start();                
	}

	public void stepalg() {
		// lets you step through the algorithm
		initalg();
		performalg = true;
		nextstep();
	}

	public void initalg() {
		init();
		for(int i=0; i<MAXNODES; i++) {
			dist[i]=-1;
			finaldist[i]=-1;
			for (int j=0; j<MAXNODES;j++) {
                            //algedge[i][j] = false;
                        }
		}
		dist[startgraph]=0;
		finaldist[startgraph]=0;
		step=0;
	}

	public void nextstep() {
		// calculates a step in the algorithm (finds a shortest 
		// path to a next node).
		finaldist[minend]=mindist;
		algedge[minstart][minend]=true;
		colornode[minend]=Color.orange;
		// build more information to display on documentation panel
		step++;
		repaint();
	}

	private boolean close_demand;

	public void stop() {
		close_demand = true;
		algrthm.suspend();
	}

        public class Step {
            public Nodo origen;
            public Nodo destino;
            public Prioridad prioridad;
            public int tiempo;
            public Color color;
            public Peticion peticion;
            
            public Step (Nodo origen, Nodo destino, Prioridad prioridad, int tiempo, Color c, Peticion p) {
                this.origen = origen;
                this.destino = destino;
                this.prioridad = prioridad;
                this.tiempo = tiempo;
                this.color = c;
                this.peticion = p;
            }
            
            public boolean siguientePaso () {
                tiempo--;
                return (tiempo == 0);
            }
            
            public boolean compararIguales(Step t) {
                boolean result = t.origen.obtenerNombre().equals(origen.obtenerNombre());
                result &= t.destino.obtenerNombre().equals(destino.obtenerNombre());
                return result;
            }
            
        }
        
        private ArrayList<Step> steps;
        
        @Override
	public void run() {
            if (steps == null)
                steps = new ArrayList<>();
            while (close_demand == false) {
                initalg();
                //steps.clear();
                try {
                    algrthm.sleep(500);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
                if (peticiones != null) {
                    if (peticiones.size() > 0) {
                        ArrayList<Peticion> copy_peticion = new ArrayList<>(peticiones);
                        for (Peticion p : copy_peticion) {
                            if (p.estaEjecutada()) {
                                Nodo origen = p.obtenerSiguienteNodoCamino();
                                if (origen == null) {
                                    peticiones.remove(p);
                                    if (peticiones.isEmpty())
                                        break;
                                    continue;
                                }
                                Nodo destino = p.obtenerNodoSiguiente();
                                if (destino == null) {
                                    peticiones.remove(p);
                                    if (peticiones.isEmpty())
                                        break;
                                    continue;
                                }
                                int distancia = mapa.obtenerDistancia(origen, destino);
                                Step s = new Step(origen, destino, p.obtenerPrioridad(), distancia, p.obtenerColor(), p);
                                boolean found = false;
                                ArrayList<Step> copy = new ArrayList<>(steps);
                                for (Step a : copy) {
                                    if (a.compararIguales(s)) {
                                        if (a.prioridad.compareTo(s.prioridad) < 0) {
                                            if (a.tiempo < s.tiempo) {
                                                found = true;
                                                break;
                                            }
                                            steps.remove(a);
                                            found = false;
                                            break;
                                        }
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    steps.add(s);
                                    System.out.println("added "+s.origen.obtenerIndice() + "->" +s.destino.obtenerIndice());
                                }
                            }
                            if ((peticiones == null) || (peticiones.isEmpty())) {
                                break;
                            }
                        }
                    }
                }
                algedge = new boolean[MAX][MAX];
                if ((steps == null) || (steps.isEmpty()))
                    break;
                if (steps.size() > 0) {
                    ArrayList<Step> copy = new ArrayList<>(steps);
                    for (Step s : copy) {
                        algedge[s.origen.obtenerIndice()][s.destino.obtenerIndice()] = true;
                        coloredge[s.origen.obtenerIndice()][s.destino.obtenerIndice()] = s.color;
                        droneposition[s.origen.obtenerIndice()][s.destino.obtenerIndice()] = s.tiempo;
                        s.siguientePaso();
                        if (s.tiempo <= 0) {
                            algedge[s.origen.obtenerIndice()][s.destino.obtenerIndice()] = false;
                            coloredge[s.origen.obtenerIndice()][s.destino.obtenerIndice()] = Color.GRAY;
                            droneposition[s.origen.obtenerIndice()][s.destino.obtenerIndice()] = -1;
                            s.peticion.avanzarEnElCamino();
                            steps.remove(s);
                        }
                    }
                }
                System.out.println("Repainting");
                painting = true;
                repaint();
            }
            started = false;
	}

        private ArrayList<Peticion> peticiones;
        
        public void anadir_peticion (Peticion p) {
            peticiones.add(p);            
        }
        
	public void showexample() {
		// draws a graph on the screen
		int width, heigth;
		//clear();
		//init();
		numnodes=18;
		emptyspots=0;
		for(int i=0; i<MAXNODES; i++) {
			node[i]=new Point(0, 0);
			for (int j=0; j<MAXNODES;j++)
				weight[i][j]=0;
		}
		width=getWidth()/96;
		heigth=getHeight()/64;
		node[0]= new Point(26*width, 12*heigth);
                node[1]= new Point(22*width, 14*heigth);   
		node[2]= new Point(16*width, 40*heigth);
                node[3]= new Point(32*width, 48*heigth); 
		node[4]= new Point(42*width, 20*heigth);
                node[5]= new Point(35*width, 23*heigth);
		node[6]= new Point(40*width, 40*heigth);
                node[7]= new Point(48*width, 11*heigth); 
		node[8]= new Point(47*width, 18*heigth);
                node[9]= new Point(46*width, 32*heigth);
		node[10]=new Point(62*width, 56*heigth);
                node[11]=new Point(57*width, 10*heigth);
		node[12]=new Point(70*width, 28*heigth);
                node[13]=new Point(61*width, 38*heigth);
		node[14]=new Point(65*width, 5*heigth);
                node[15]=new Point(76*width, 16*heigth);
		node[16]=new Point(84*width, 32*heigth);
                node[17]=new Point(76*width, 44*heigth);

		weight[0][1]=90; weight[0][7]=20;
		weight[1][0]=90; weight[1][5]=90; 
		weight[2][3]=20;
		weight[3][2]=20; weight[3][6]=10; weight[3][10]=30;
		weight[4][5]=70; weight[4][7]=60;
		weight[5][1]=90; weight[5][4]=70; weight[5][6]=70; weight[5][9]=20;
		weight[6][3]=10; weight[6][5]=70; weight[6][9]=60; weight[6][10]=30;
		weight[7][0]=20; weight[7][4]=60; weight[7][11]=50; weight[7][14]=20;
		weight[8][9]=90; weight[8][12]=30;
		weight[9][5]=20; weight[9][6]=60; weight[9][8]=90; weight[9][10]=10; weight[9][12]=40;
		weight[10][3]=30; weight[10][6]=30; weight[10][9]=10; weight[10][17]=30;
		weight[11][7]=50; weight[11][12]=10; weight[11][14]=30;
		weight[12][8]=30; weight[12][9]=40; weight[12][11]=10; weight[12][13]=70; weight[12][15]=70; weight[12][16]=60;
		weight[13][12]=70; weight[13][16]=10; weight[13][17]=80;
		weight[14][7]=20; weight[14][11]=30; weight[14][15]=40;
		weight[15][12]=70; weight[15][14]=40; weight[15][16]=10;
		weight[16][12]=60; weight[16][13]=10; weight[16][15]=10; weight[16][17]=10;
		weight[17][10]=30; weight[17][13]=80; weight[17][16]=10;

		for (int i=0;i<numnodes;i++)
                    for (int j=0;j<numnodes;j++)
                        if (weight[i][j]>0)
                            arrowupdate(i, j, weight[i][j]);

	}

	public void arrowupdate(int p1, int p2, int w) {
		// make a new arrow from node p1 to p2 with weight w, or change
		// the weight of the existing arrow to w, calculate the resulting 
		// position of the arrowhead
		int dx, dy;
		float l;
		weight[p1][p2]=w;

		// direction line between p1 and p2
		dx = node[p2].x-node[p1].x;
		dy = node[p2].y-node[p1].y;

		// distance between p1 and p2
		l = (float)( Math.sqrt((float)(dx*dx + dy*dy)));
		dir_x[p1][p2]=dx/l;
		dir_y[p1][p2]=dy/l;

		// calculate the start and endpoints of the arrow,
		// adjust startpoints if there also is an arrow from p2 to p1
		if (weight[p2][p1]>0) {
                    startp[p1][p2] = new Point((int)(node[p1].x-5*dir_y[p1][p2]), 
                    (int)(node[p1].y+5*dir_x[p1][p2]));
                    endp[p1][p2] = new Point((int)(node[p2].x-5*dir_y[p1][p2]), 
                    (int)(node[p2].y+5*dir_x[p1][p2]));
		}
		else {
                    startp[p1][p2] = new Point(node[p1].x, node[p1].y);
                    endp[p1][p2] = new Point(node[p2].x, node[p2].y);
		}

		// range for arrowhead is not all the way to the start/endpoints
		int diff_x = (int)(Math.abs(20*dir_x[p1][p2]));
		int diff_y = (int)(Math.abs(20*dir_y[p1][p2]));

		// calculate new x-position arrowhead
		if (startp[p1][p2].x>endp[p1][p2].x) {
                    arrow[p1][p2] = new Point(endp[p1][p2].x + diff_x +
                    (Math.abs(endp[p1][p2].x-startp[p1][p2].x) - 2*diff_x )*
                    50/100 , 0);
		}
		else {
                    arrow[p1][p2] = new Point(startp[p1][p2].x + diff_x +
                    (Math.abs(endp[p1][p2].x-startp[p1][p2].x) - 2*diff_x )*
                    50/100, 0);
		}

		// calculate new y-position arrowhead
		if (startp[p1][p2].y>endp[p1][p2].y) {
                    arrow[p1][p2].y=endp[p1][p2].y + diff_y +
                    (Math.abs(endp[p1][p2].y-startp[p1][p2].y) - 2*diff_y )*
                    50/100;
		}
		else {
                    arrow[p1][p2].y=startp[p1][p2].y + diff_y +
                    (Math.abs(endp[p1][p2].y-startp[p1][p2].y) - 2*diff_y )*
                    50/100;
		}
	}

	public String intToString(int i) {
		char c=(char)((int)'a'+i);
		return ""+c;
	}

	public void drawarrow(Graphics g, int i, int j) {
		// draw arrow between node i and node j
		int x1, x2, x3, y1, y2, y3;

		// calculate arrowhead
		x1= (int)(arrow[i][j].x - 3*dir_x[i][j] + 6*dir_y[i][j]);
		x2= (int)(arrow[i][j].x - 3*dir_x[i][j] - 6*dir_y[i][j]);
		x3= (int)(arrow[i][j].x + 6*dir_x[i][j]);

		y1= (int)(arrow[i][j].y - 3*dir_y[i][j] - 6*dir_x[i][j]);
		y2= (int)(arrow[i][j].y - 3*dir_y[i][j] + 6*dir_x[i][j]);
		y3= (int)(arrow[i][j].y + 6*dir_y[i][j]);

		int arrowhead_x[] = { x1, x2, x3, x1 };
		int arrowhead_y[] = { y1, y2, y3, y1 };

                g.setColor(Color.MAGENTA);
		// draw arrow
		g.fillPolygon(arrowhead_x, arrowhead_y, 4);

		// write weight of arrow at an appropriate position
		int dx = (int)(Math.abs(7*dir_y[i][j]));
		int dy = (int)(Math.abs(7*dir_x[i][j]));
		String str = new String("" + weight[i][j]);
                
                //g.setColor(coloredge[i][j]);
		if ((startp[i][j].x>endp[i][j].x) && (startp[i][j].y>=endp[i][j].y))
			g.drawString( str, arrow[i][j].x + dx, arrow[i][j].y - dy);
		if ((startp[i][j].x>=endp[i][j].x) && (startp[i][j].y<endp[i][j].y))
			g.drawString( str, arrow[i][j].x - fmetrics.stringWidth(str) - dx , 
			arrow[i][j].y - dy);
		if ((startp[i][j].x<endp[i][j].x) && (startp[i][j].y<=endp[i][j].y))
			g.drawString( str, arrow[i][j].x - fmetrics.stringWidth(str) , 
			arrow[i][j].y + fmetrics.getHeight());
		if ((startp[i][j].x<=endp[i][j].x) && (startp[i][j].y>endp[i][j].y))
			g.drawString( str, arrow[i][j].x + dx, 
			arrow[i][j].y + fmetrics.getHeight() );
                
                g.setColor(Color.GRAY);
		// if edge already chosen by algorithm change color
		if (algedge[i][j]) {
                    System.out.println("painting line "+i+"->"+j);
                    g.setColor(coloredge[i][j]);
                }
                Graphics2D g2 = (Graphics2D) g;
                float f = new Float(1.5);
                g2.setStroke(new BasicStroke(f));
                g2.drawLine(startp[i][j].x, startp[i][j].y, endp[i][j].x, endp[i][j].y);
	}

        private static BufferedImage resizeImage(BufferedImage originalImage, int type,
                                         Integer img_width, Integer img_height)
        {
            BufferedImage resizedImage = new BufferedImage(img_width, img_height, type);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, img_width, img_height, null);
            g.dispose();

            return resizedImage;
        }
        
        @Override
	public void paintComponent (Graphics g) {
            super.paintComponent(g);
            
            try {
                img = ImageIO.read(getClass().getResource("mapa1.png"));
                Dimension newMaxSize = new Dimension(getWidth(), getHeight());
                BufferedImage resizedImg = resizeImage(img, BufferedImage.TYPE_INT_RGB, newMaxSize.width, newMaxSize.height);
                g.drawImage(resizedImg, 0, 0, this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            showexample();
            
            mindist=0;
            minnode=MAXNODES;
            minstart=MAXNODES;
            minend=MAXNODES;
            for(int i=0; i<MAXNODES; i++) {
                    changed[i]=false;
            }
            numchanged=0;
            neighbours=0;
            g.setFont(helvetica);
            g.setColor(Color.black);
            setSize(new Dimension(parent.getWidth(),parent.getHeight()));

            // draw a new arrow upto current mouse position
            if (newarrow)
                g.drawLine(node[node1].x, node[node1].y, thispoint.x, thispoint.y);
            // draw all arrows
            for (int i=0; i<numnodes; i++) {
                for (int j=0; j<numnodes; j++) {
                    if (weight [i][j]>0) {
                        // if algorithm is running then perform next step for this arrow
                        drawarrow(g, i, j);
                    }
                }
            }

            // if arrowhead has been dragged to 0, draw it anyway, so the user
            // will have the option to make it positive again
            if (movearrow && weight[node1][node2]==0) {
                drawarrow(g, node1, node2);
                g.drawLine(startp[node1][node2].x, startp[node1][node2].y, 
                endp[node1][node2].x, endp[node1][node2].y);
            }

            // draw the nodes
            for (int i=0; i<numnodes; i++) {
                if (node[i].x>0) {
                    g.setColor(colornode[i]);
                    g.fillOval(node[i].x-NODERADIX, node[i].y-NODERADIX, 
                    NODESIZE, NODESIZE);
                }
            }

            g.setColor(Color.black);

            // draw black circles around nodes, write their names to the screen
            g.setFont(helvetica);
            for (int i=0; i<numnodes; i++)
            if (node[i].x>0) {
                    g.setColor(Color.black);
                    g.drawOval(node[i].x-NODERADIX, node[i].y-NODERADIX, 
                    NODESIZE, NODESIZE);
                    g.setColor(Color.BLUE);
                    //g.drawString(intToString(i), node[i].x-14, node[i].y-14);
                    Graphics2D g2 = (Graphics2D) g;
                    float f3 = new Float(3);
                    g2.setStroke(new BasicStroke(f3));
                    g.drawString(names[i], node[i].x-14, node[i].y-14);
                    float f1 = new Float(1);
                    g2.setStroke(new BasicStroke(f1));
                    
            }
            
            for (int i=0; i<numnodes; i++) {
                for (int j=0; j<numnodes; j++) {
                    if (droneposition[i][j] >= 0) {
                        //pintar el drone en el mapa
                        Color c = coloredge[i][j];
                        g.setColor(c);
                        int x = ((node[j].x-node[i].x)/(weight[i][j]))*(weight[i][j]-droneposition[i][j]+1) + node[i].x;
                        int y = ((node[j].y-node[i].y)/(weight[i][j]))*(weight[i][j]-droneposition[i][j]+1) + node[i].y;
                        g.fillOval(x, y, DRONESIZE, DRONESIZE);
                    }
                }
            }
	}
}