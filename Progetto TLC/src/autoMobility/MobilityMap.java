/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autoMobility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.view.Viewer;

/**
 *
 * @author afsantamaria
 */
public class MobilityMap {

    public Graph cityRoadMap;
    Dijkstra dijkstra;
    Dijkstra dijkstra_avg_speed;
    public HashMap<String, car_node> vehicles = new HashMap<String, car_node>();
    ProxyPipe pipe;

    public Graph getCityRoadMap() {
        return cityRoadMap;
    }

    public void setCityRoadMap(Graph cityRoadMap) {
        this.cityRoadMap = cityRoadMap;
    }

    public MobilityMap() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        createCityMap();

        // Edge lengths are stored in an attribute called "length"
        // The length of a path is the sum of the lengths of its edges
        dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
        dijkstra_avg_speed = new Dijkstra(Dijkstra.Element.EDGE, null, "avgSpeed");

        // Compute the shortest paths in g from A to all nodes
        dijkstra.init(cityRoadMap);
        dijkstra.setSource(cityRoadMap.getNode("A"));
        dijkstra.compute();

        // Print the lengths of all the shortest paths
        for (Node node : cityRoadMap) {
            System.out.printf("%s->%s:%10.2f%n", dijkstra.getSource(), node,
                    dijkstra.getPathLength(node));

        }

        // Color in blue all the nodes on the shortest path form A to B
        for (Node node : dijkstra.getPathNodes(cityRoadMap.getNode("E"))) {
//            node.addAttribute("ui.style", "fill-color: white; size: 25px,25px;");                        
        }

        // Color in red all the edges in the shortest path tree
        for (Edge edge : dijkstra.getTreeEdges()) {
            //           edge.addAttribute("ui.style", "fill-color: red;");
        }

        // Print the shortest path from A to B
        System.out.println(dijkstra.getPath(cityRoadMap.getNode("E")));

        // Build a list containing the nodes in the shortest path from A to B
        // Note that nodes are added at the beginning of the list
        // because the iterator traverses them in reverse order, from B to A
        ArrayList<Node> list1 = new ArrayList<Node>();
        for (Node node : dijkstra.getPathNodes(cityRoadMap.getNode("E"))) {
            list1.add(0, node);
        }

        dijkstra_avg_speed.init(cityRoadMap);
        dijkstra_avg_speed.setSource(cityRoadMap.getNode("A"));
        dijkstra_avg_speed.compute();
        System.out.println("\n\n...Calcolo dei cammini in base alle velocità medie...");
        // Print the lengths of all the shortest paths
        for (Node node : cityRoadMap) {
            System.out.printf("%s->%s:%10.2f%n", dijkstra_avg_speed.getSource(), node,
                    dijkstra_avg_speed.getPathLength(node));
        }

        for (Node n : cityRoadMap) {
            n.addAttribute("label", n.getId());
            if (n.getId().equals("C")
                    || n.getId().equals("F")
                    || n.getId().equals("E")
                    || n.getId().equals("H")) {
                if (n.getAttribute("ui.style") == null) {
                    n.addAttribute("ui.style", "fill-color: red; size: 30px,30px;");
                } else {
                    n.setAttribute("ui.style", "fill-color: red; size: 30px,30px;");
                }
            } else if (n.getAttribute("ui.style") == null) {
                n.addAttribute("ui.style", "fill-color: gray; size: 20px,20px;");
            } else {
                n.setAttribute("ui.style", "fill-color: gray; size: 20px,20px;");
            }
        }

        //Viewer viewer = cityRoadMap.display();
        //viewer.disableAutoLayout();
        /*Viewer viewer;
        for (int i = 0; i < 100; i++) {
            
            cityRoadMap.getNode("Car").setAttribute("xy",500+(i*5),250);
            viewer = cityRoadMap.display();
            viewer.disableAutoLayout();
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(MobilityMap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
        Viewer v = cityRoadMap.display();
        v.disableAutoLayout();
        pipe = v.newViewerPipe();
        pipe.addAttributeSink(cityRoadMap);

        /*for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(100);
                pipe.pump();
                cityRoadMap.getNode("Car").setAttribute("xy",500+i,250+i);
            } catch (InterruptedException ex) {
                Logger.getLogger(MobilityMap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
        for (Node node : cityRoadMap) {

            System.out.println(node.toString() + " Posizione (x,y) :" + node.getNumber("x") + "," + ((Object[]) node.getAttribute("xy"))[1]);
        }

    }

    /**
     * Creamo la struttura della citta utilizzando una libreria graphStream per
     * la gestione delle strade 1.0 Creamo il grafo della rete 2.0 Associamo la
     * struttura del grafo agli oggetti java
     */
    public void createCityMap() {
        //Node represents the crossway among road
        cityRoadMap = new SingleGraph("ColdRiver");

        cityRoadMap.addNode("A");
        cityRoadMap.getNode("A").setAttribute("xy", 0, 200);
        cityRoadMap.addNode("B");
        cityRoadMap.getNode("B").setAttribute("xy", 100, 300);
        cityRoadMap.addNode("C");
        cityRoadMap.getNode("C").setAttribute("xy", 800, 50);
        cityRoadMap.addNode("D");
        cityRoadMap.getNode("D").setAttribute("xy", 1100, 200);
        cityRoadMap.addNode("E");
        cityRoadMap.getNode("E").setAttribute("xy", 1100, 0);
        cityRoadMap.addNode("F");
        cityRoadMap.getNode("F").setAttribute("xy", 1000, 0);
        cityRoadMap.addNode("G");
        cityRoadMap.getNode("G").setAttribute("xy", 600, 0);
        cityRoadMap.addNode("H");
        cityRoadMap.getNode("H").setAttribute("xy", 450, 100);

        cityRoadMap.addEdge("AB", "A", "B").addAttribute("length", 200);
        cityRoadMap.addEdge("BH", "B", "H").addAttribute("length", 200);
        cityRoadMap.addEdge("AG", "A", "G").addAttribute("length", 200);
        cityRoadMap.addEdge("GC", "G", "C").addAttribute("length", 200);
        cityRoadMap.addEdge("CF", "C", "F").addAttribute("length", 100);
        cityRoadMap.addEdge("GF", "G", "F").addAttribute("length", 200);
        cityRoadMap.addEdge("CD", "C", "D").addAttribute("length", 100);
        cityRoadMap.addEdge("DE", "D", "E").addAttribute("length", 100);
        cityRoadMap.addEdge("FE", "F", "E").addAttribute("length", 100);
        cityRoadMap.addEdge("HC", "H", "C").addAttribute("length", 100);

        cityRoadMap.getEdge("AB").addAttribute("avgSpeed", 8.1);
        cityRoadMap.getEdge("BH").addAttribute("avgSpeed", 9.2);
        cityRoadMap.getEdge("AG").addAttribute("avgSpeed", 10.3);
        cityRoadMap.getEdge("GC").addAttribute("avgSpeed", 8.1);

        cityRoadMap.getEdge("CF").addAttribute("avgSpeed", 7.25);
        cityRoadMap.getEdge("GF").addAttribute("avgSpeed", 9.5);
        cityRoadMap.getEdge("CD").addAttribute("avgSpeed", 8.5);
        cityRoadMap.getEdge("DE").addAttribute("avgSpeed", 9.3);
        cityRoadMap.getEdge("FE").addAttribute("avgSpeed", 8.15);
        cityRoadMap.getEdge("HC").addAttribute("avgSpeed", 9.2);

        for (Edge e : cityRoadMap.getEachEdge()) {
            e.addAttribute("label", "" + (int) e.getNumber("length"));
        }
    }

    public boolean validatePos(String id, double x, double y) {
        boolean res = true;
        for (Entry<String, car_node> entry : vehicles.entrySet()) {
            String key = entry.getKey();
            car_node car = (car_node) entry.getValue();
            if (!key.equals(id) && car.getX() == x && car.getY() == y) {
                res = false;
                break;
            }
        }
        if (res == true) {
            car_node car = vehicles.get(id);
            car.setX(x);
            car.setY(y);

            //Test car nodes
            if (cityRoadMap.getNode(id) == null) {
                cityRoadMap.addNode(id);
                cityRoadMap.getNode(id).setAttribute("label", id);
                cityRoadMap.getNode(id).setAttribute("ui.style", "fill-color: green; size: 10px,10px;");
            }

            cityRoadMap.getNode(id).setAttribute("xy", x, y);
            pipe.pump();
        }
        return res;
    }

    public void updateVehiclePos(String id, double x, double y) {
        car_node car = vehicles.get(id);
        car.setX(x);
        car.setY(y);	

    }
}
