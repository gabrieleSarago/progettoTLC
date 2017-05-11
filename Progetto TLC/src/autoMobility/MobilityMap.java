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
    public HashMap<String, Bus_node> vehicles = new HashMap<String, Bus_node>();
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
        dijkstra.setSource(cityRoadMap.getNode("T"));
        dijkstra.compute();

        // Print the lengths of all the shortest paths
        for (Node node : cityRoadMap) {
            System.out.printf("%s->%s:%10.2f%n", dijkstra.getSource(), node,
                    dijkstra.getPathLength(node));

        }

        /* Color in blue all the nodes on the shortest path form A to B
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
        }*/

        dijkstra_avg_speed.init(cityRoadMap);
        dijkstra_avg_speed.setSource(cityRoadMap.getNode("T"));
        dijkstra_avg_speed.compute();
        System.out.println("\n\n...Calcolo dei cammini in base alle velocità medie...");
        // Print the lengths of all the shortest paths
        for (Node node : cityRoadMap) {
            System.out.printf("%s->%s:%10.2f%n", dijkstra_avg_speed.getSource(), node,
                    dijkstra_avg_speed.getPathLength(node));
        }
        
        for (Node n : cityRoadMap) {
            n.addAttribute("label", n.getId());
            if (n.getId().equals("T")){
            	if (n.getAttribute("ui.style") == null)
                    n.addAttribute("ui.style", "fill-color: red; size: 30px,30px;");
            	else
                    n.setAttribute("ui.style", "fill-color: red; size: 30px,30px;");
            }
            else if (n.getAttribute("ui.style") == null) {
            	n.addAttribute("ui.style", "fill-color: blue; size: 10px,10px;");
            } else 
            	n.setAttribute("ui.style", "fill-color: blue; size: 10px,10px;");
        }
        /*
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
        }*/

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
        cityRoadMap = new SingleGraph("Autolinee");
        
        //Nodo Terminal
        cityRoadMap.addNode("T");
        cityRoadMap.getNode("T").setAttribute("xy", 450, 600);
        //Nodi Fermate
        cityRoadMap.addNode("6");
        cityRoadMap.getNode("6").setAttribute("xy", 600, 620);
        cityRoadMap.addNode("7");
        cityRoadMap.getNode("7").setAttribute("xy", 1070, 630);
        cityRoadMap.addNode("9");
        cityRoadMap.getNode("9").setAttribute("xy", 1200, 640);
        cityRoadMap.addNode("15");
        cityRoadMap.getNode("15").setAttribute("xy", 1400, 640);
        cityRoadMap.addNode("47");
        cityRoadMap.getNode("47").setAttribute("xy", 1625, 620);
        cityRoadMap.addNode("18");
        cityRoadMap.getNode("18").setAttribute("xy", 200, 600);
        cityRoadMap.addNode("19");
        cityRoadMap.getNode("19").setAttribute("xy", 200, 450);
        cityRoadMap.addNode("20");
        cityRoadMap.getNode("20").setAttribute("xy", 0, 450);
        cityRoadMap.addNode("29");
        cityRoadMap.getNode("29").setAttribute("xy", 50, 250);
        cityRoadMap.addNode("30");
        cityRoadMap.getNode("30").setAttribute("xy", 250, 250);
        cityRoadMap.addNode("35");
        cityRoadMap.getNode("35").setAttribute("xy", 125, 30);
        cityRoadMap.addNode("36");
        cityRoadMap.getNode("36").setAttribute("xy", 400, 0);
        cityRoadMap.addNode("33");
        cityRoadMap.getNode("33").setAttribute("xy", 400, 320);
        cityRoadMap.addNode("34");
        cityRoadMap.getNode("34").setAttribute("xy", 200, 320);
        cityRoadMap.addNode("25");
        cityRoadMap.getNode("25").setAttribute("xy", 210, 750);
        cityRoadMap.addNode("26");
        cityRoadMap.getNode("26").setAttribute("xy", 300, 760);
        cityRoadMap.addNode("8");
        cityRoadMap.getNode("8").setAttribute("xy", 850, 770);
        cityRoadMap.addNode("27");
        cityRoadMap.getNode("27").setAttribute("xy", 250, 930);
        cityRoadMap.addNode("21");
        cityRoadMap.getNode("21").setAttribute("xy", 10, 1000);
        cityRoadMap.addNode("22");
        cityRoadMap.getNode("22").setAttribute("xy", 300, 1200);
        cityRoadMap.addNode("28");
        cityRoadMap.getNode("28").setAttribute("xy", 370, 1050);
        cityRoadMap.addNode("51");
        cityRoadMap.getNode("51").setAttribute("xy", 400, 930);
        cityRoadMap.addNode("32");
        cityRoadMap.getNode("32").setAttribute("xy", 400, 450);
        cityRoadMap.addNode("38");
        cityRoadMap.getNode("38").setAttribute("xy", 470, 430);
        cityRoadMap.addNode("12");
        cityRoadMap.getNode("12").setAttribute("xy", 550, 450);
        cityRoadMap.addNode("1");
        cityRoadMap.getNode("1").setAttribute("xy", 570, 890);
        cityRoadMap.addNode("31");
        cityRoadMap.getNode("31").setAttribute("xy", 430, 70);
        cityRoadMap.addNode("37");
        cityRoadMap.getNode("37").setAttribute("xy", 550, 30);
        cityRoadMap.addNode("39");
        cityRoadMap.getNode("39").setAttribute("xy", 540, 240);
        cityRoadMap.addNode("13");
        cityRoadMap.getNode("13").setAttribute("xy", 500, 310);
        cityRoadMap.addNode("23");
        cityRoadMap.getNode("23").setAttribute("xy", 570, 1030);
        cityRoadMap.addNode("16");
        cityRoadMap.getNode("16").setAttribute("xy", 850, 1030);
        cityRoadMap.addNode("24");
        cityRoadMap.getNode("24").setAttribute("xy", 750, 1150);
        cityRoadMap.addNode("14");
        cityRoadMap.getNode("14").setAttribute("xy", 900, 310);
        cityRoadMap.addNode("48");
        cityRoadMap.getNode("48").setAttribute("xy", 800, 150);
        cityRoadMap.addNode("10");
        cityRoadMap.getNode("10").setAttribute("xy", 1000, 450);
        cityRoadMap.addNode("11");
        cityRoadMap.getNode("11").setAttribute("xy", 1150, 440);
        cityRoadMap.addNode("40");
        cityRoadMap.getNode("40").setAttribute("xy", 1300, 470);
        cityRoadMap.addNode("49");
        cityRoadMap.getNode("49").setAttribute("xy", 1270, 300);
        cityRoadMap.addNode("50");
        cityRoadMap.getNode("50").setAttribute("xy", 1400, 400);
        cityRoadMap.addNode("2");
        cityRoadMap.getNode("2").setAttribute("xy", 1110, 890);
        cityRoadMap.addNode("3");
        cityRoadMap.getNode("3").setAttribute("xy", 1300, 890);
        cityRoadMap.addNode("4");
        cityRoadMap.getNode("4").setAttribute("xy", 1310, 1030);
        cityRoadMap.addNode("5");
        cityRoadMap.getNode("5").setAttribute("xy", 1130, 1070);
        cityRoadMap.addNode("17");
        cityRoadMap.getNode("17").setAttribute("xy", 1250, 1170);
        cityRoadMap.addNode("41");
        cityRoadMap.getNode("41").setAttribute("xy", 1470, 800);
        cityRoadMap.addNode("42");
        cityRoadMap.getNode("42").setAttribute("xy", 1460, 980);
        cityRoadMap.addNode("44");
        cityRoadMap.getNode("44").setAttribute("xy", 1700, 910);
        cityRoadMap.addNode("43");
        cityRoadMap.getNode("43").setAttribute("xy", 1700, 1150);
        cityRoadMap.addNode("45");
        cityRoadMap.getNode("45").setAttribute("xy", 1680, 770);
        cityRoadMap.addNode("46");
        cityRoadMap.getNode("46").setAttribute("xy", 1800, 720);
        
        //Archi
        cityRoadMap.addEdge("T1", "T", "1").addAttribute("length", 200);
        cityRoadMap.addEdge("T6", "T", "6").addAttribute("length", 150);
        cityRoadMap.addEdge("61", "6", "1").addAttribute("length", 200);
        cityRoadMap.addEdge("67", "6", "7").addAttribute("length", 350);
        cityRoadMap.addEdge("78", "7", "8").addAttribute("length", 100);
        cityRoadMap.addEdge("89", "8", "9").addAttribute("length", 300);
        cityRoadMap.addEdge("915", "9", "15").addAttribute("length", 250);
        cityRoadMap.addEdge("1541", "15", "41").addAttribute("length", 100);
        cityRoadMap.addEdge("1547", "15", "47").addAttribute("length", 100);
        cityRoadMap.addEdge("4745", "47", "45").addAttribute("length", 120);
        cityRoadMap.addEdge("4546", "45", "46").addAttribute("length", 70);
        cityRoadMap.addEdge("4544", "45", "44").addAttribute("length", 150);
        cityRoadMap.addEdge("4142", "41", "42").addAttribute("length", 150);
        cityRoadMap.addEdge("4443", "44", "43").addAttribute("length", 130);
        cityRoadMap.addEdge("4243", "42", "43").addAttribute("length", 150);
        cityRoadMap.addEdge("442", "4", "42").addAttribute("length", 200);
        cityRoadMap.addEdge("54", "5", "4").addAttribute("length", 200);
        cityRoadMap.addEdge("517", "5", "17").addAttribute("length", 200);
        cityRoadMap.addEdge("34", "3", "4").addAttribute("length", 150);
        cityRoadMap.addEdge("24", "2", "4").addAttribute("length", 300);
        cityRoadMap.addEdge("23", "2", "3").addAttribute("length", 200);
        cityRoadMap.addEdge("216", "2", "16").addAttribute("length", 270);
        cityRoadMap.addEdge("12", "1", "2").addAttribute("length", 400);
        cityRoadMap.addEdge("83", "8", "3").addAttribute("length", 500);
        cityRoadMap.addEdge("215", "2", "15").addAttribute("length", 600);
        cityRoadMap.addEdge("245", "24", "5").addAttribute("length", 310);
        cityRoadMap.addEdge("2324", "23", "24").addAttribute("length", 115);
        cityRoadMap.addEdge("2316", "23", "16").addAttribute("length", 210);
        cityRoadMap.addEdge("2223", "22", "23").addAttribute("length", 330);
        cityRoadMap.addEdge("2122", "21", "22").addAttribute("length", 340);
        cityRoadMap.addEdge("2728", "27", "28").addAttribute("length", 190);
        cityRoadMap.addEdge("2851", "28", "51").addAttribute("length", 100);
        cityRoadMap.addEdge("511", "51", "1").addAttribute("length", 230);
        cityRoadMap.addEdge("710", "7", "10").addAttribute("length", 215);
        cityRoadMap.addEdge("119", "11", "9").addAttribute("length", 230);
        cityRoadMap.addEdge("T12", "T", "12").addAttribute("length", 170);
        cityRoadMap.addEdge("1210", "12", "10").addAttribute("length", 390);
        cityRoadMap.addEdge("1011", "10", "11").addAttribute("length", 130);
        cityRoadMap.addEdge("1140", "11", "40").addAttribute("length", 140);
        cityRoadMap.addEdge("4050", "40", "50").addAttribute("length", 170);
        cityRoadMap.addEdge("4950", "49", "50").addAttribute("length", 170);
        cityRoadMap.addEdge("1449", "14", "49").addAttribute("length", 225);
        cityRoadMap.addEdge("1411", "14", "11").addAttribute("length", 245);
        cityRoadMap.addEdge("1314", "13", "14").addAttribute("length", 330);
        cityRoadMap.addEdge("1213", "12", "13").addAttribute("length", 200);
        cityRoadMap.addEdge("3813", "38", "13").addAttribute("length", 180);
        cityRoadMap.addEdge("3238", "32", "38").addAttribute("length", 90);
        cityRoadMap.addEdge("T32", "T", "32").addAttribute("length", 100);
        cityRoadMap.addEdge("1448", "14", "48").addAttribute("length", 170);
        cityRoadMap.addEdge("3948", "39", "48").addAttribute("length", 210);
        cityRoadMap.addEdge("1339", "13", "39").addAttribute("length", 60);
        cityRoadMap.addEdge("3139", "31", "39").addAttribute("length", 240);
        cityRoadMap.addEdge("3137", "31", "37").addAttribute("length", 190);
        cityRoadMap.addEdge("3031", "30", "31").addAttribute("length", 220);
        cityRoadMap.addEdge("3035", "30", "35").addAttribute("length", 170);
        cityRoadMap.addEdge("3536", "35", "36").addAttribute("length", 170);
        cityRoadMap.addEdge("2930", "29", "30").addAttribute("length", 120);
        cityRoadMap.addEdge("3034", "30", "34").addAttribute("length", 120);
        cityRoadMap.addEdge("3429", "34", "29").addAttribute("length", 120);
        cityRoadMap.addEdge("3330", "33", "30").addAttribute("length", 150);
        cityRoadMap.addEdge("3233", "32", "33").addAttribute("length", 130);
        cityRoadMap.addEdge("T18", "T", "18").addAttribute("length", 210);
        cityRoadMap.addEdge("1819", "18", "19").addAttribute("length", 140);
        cityRoadMap.addEdge("1920", "19", "20").addAttribute("length", 120);
        cityRoadMap.addEdge("2021", "20", "21").addAttribute("length", 800);
        cityRoadMap.addEdge("1825", "18", "25").addAttribute("length", 230);
        cityRoadMap.addEdge("2526", "25", "26").addAttribute("length", 120);
        cityRoadMap.addEdge("2527", "25", "27").addAttribute("length", 210);
        cityRoadMap.addEdge("2627", "26", "27").addAttribute("length", 210);
        cityRoadMap.addEdge("T26", "T", "26").addAttribute("length", 220);
        cityRoadMap.addEdge("2029", "20", "29").addAttribute("length", 200);
        cityRoadMap.addEdge("2228", "22", "28").addAttribute("length", 70);
        
        cityRoadMap.getEdge("T1").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("T6").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("61").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("67").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("78").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("89").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("915").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1541").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1547").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("4745").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("4546").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("4544").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("4142").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("4443").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("4243").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("442").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("54").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("517").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("34").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("24").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("23").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("216").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("12").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("83").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("215").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("245").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2324").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2316").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2223").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2122").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2728").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2851").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("511").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("710").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("119").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("T12").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1210").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1011").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1140").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("4050").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("4950").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1449").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1411").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1314").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1213").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("3813").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("3238").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("T32").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1448").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("3948").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1339").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("3139").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("3137").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("3031").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("3035").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("3536").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2930").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("3034").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("3429").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("3330").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("3233").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("T18").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1819").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1920").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2021").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("1825").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2526").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2527").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2627").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("T26").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2029").addAttribute("avgSpeed", 6.5);
        cityRoadMap.getEdge("2228").addAttribute("avgSpeed", 6.5);

        for (Edge e : cityRoadMap.getEachEdge()) {
            e.addAttribute("label", "" + (int) e.getNumber("length"));
        }
    }

    public boolean validatePos(String id, double x, double y) {
        boolean res = true;
        for (Entry<String, Bus_node> entry : vehicles.entrySet()) {
            String key = entry.getKey();
            Bus_node car = (Bus_node) entry.getValue();
            if (!key.equals(id) && car.getX() == x && car.getY() == y) {
                res = false;
                break;
            }
        }
        if (res == true) {
            Bus_node car = vehicles.get(id);
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
        Bus_node car = vehicles.get(id);
        car.setX(x);
        car.setY(y);	

    }
}
