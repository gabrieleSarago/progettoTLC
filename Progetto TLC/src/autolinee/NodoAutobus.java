/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autolinee;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import autoMobility.Bus_node;
import autoMobility.MobilityMap;
import base_simulator.Grafo;
import base_simulator.Messaggi;
import base_simulator.Nodo;
import base_simulator.canale;
import base_simulator.scheduler;
import base_simulator.layers.LinkLayer;
import base_simulator.layers.NetworkLayer;
import base_simulator.layers.TransportLayer;
import base_simulator.layers.physicalLayer;
import reti_tlc_gruppo_0.nodo_host;

/**
 *
 * @author franco
 */
public class NodoAutobus extends nodo_host {

    final String UPDATE_POSITION = "update_pos";
    final String START_ROAD_RUN = "start_road_run";
    final double UPDATE_POSITION_TIME = 1000.0; //UPDATE_POSITION_TIME
    final double STOP_WAITING_TIME = 10000.0; //WAIT AT ROAD_CROSS
    //tempo impiegato dagli utenti a salire
    final double TEMPO_SALITA = 3000.0;
    final double TEMPO_DISCESA = 2000.0;
    final int POSTI_MAX = 50;
    String nodo_ingresso;
    String nodo_uscita;
    int index_nodo_attuale;
    int verso = -1;
    int id_percorso;

    double currX = 0;
    double currY = 0;
    double currDistance = 0;

    MobilityMap cityMap;
    Graph mappa;
    Dijkstra dijkstra;
    
    //linea assegnata all'autobus
    ArrayList<Node> percorso;
    
    Utente[] utenti;
    int numPosti = 0;
    
    private canale my_wireless_channel;
    
    private boolean carIsPowerOff = true;
    private String POWER_OFF = "car power off";
    

    public canale getMy_wireless_channel() {
        return my_wireless_channel;
    }

    public void setMy_wireless_channel(canale my_wireless_channel) {
        this.my_wireless_channel = my_wireless_channel;
    }

    public NodoAutobus(scheduler s, int id_nodo, physicalLayer myPhyLayer, LinkLayer myLinkLayer, NetworkLayer myNetLayer, TransportLayer myTransportLayer, Grafo network, String tipo, int gtw) {
        super(s, id_nodo, myPhyLayer, myLinkLayer, myNetLayer, myTransportLayer, network, tipo, gtw);
        dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");

    }
    
    public NodoAutobus(scheduler s, int id_nodo, physicalLayer myPhyLayer, LinkLayer myLinkLayer, NetworkLayer myNetLayer, TransportLayer myTransportLayer, Grafo network, String tipo, int gtw, int id_percorso, ArrayList<Node> percorso) {
        super(s, id_nodo, myPhyLayer, myLinkLayer, myNetLayer, myTransportLayer, network, tipo, gtw);
        this.id_percorso = id_percorso;
        this.percorso = percorso;
        utenti = new Utente[POSTI_MAX];
        dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
        
    }

    public String getNodo_ingresso() {
        return nodo_ingresso;
    }

    public void setNodo_ingresso(String nodo_ingresso) {
        this.nodo_ingresso = nodo_ingresso;
    }

    public String getNodo_uscita() {
        return nodo_uscita;
    }

    public void setNodo_uscita(String nodo_uscita) {
        this.nodo_uscita = nodo_uscita;
    }

    public MobilityMap getMappa() {
        return cityMap;
    }

    public void setMappa(MobilityMap mappa) {
        this.cityMap = mappa;
        this.mappa = mappa.cityRoadMap;
    }

    public void calcolaNuovaPosizione(Edge e, double x1, double y1, double x2, double y2) {
        double avgSpeed = (Double) e.getAttribute("avgSpeed");
        double angle = Math.atan(Math.abs(y2 - y1) / Math.abs(x2 - x1));
        double distance = avgSpeed * UPDATE_POSITION_TIME / 1000.0;

        double addX = 0;
        double addY = 0;
        //Find quadrante
        if ((x2 >= x1) && (y2 >= y1)) {
            //SONO nel primo quadrante sono tutti contributi positivi
            addX = 1;
            addY = 1;
            verso = 0;
        } else if ((x2 >= x1) && (y2 < y1)) {
            //SONO nel secondo quadrante i contributi di y sono negativi
            addX = 1;
            addY = -1;
            verso = 1;
        } else if ((x2 < x1) && (y2 < y1)) {
            //SONO nel terzo quadrante i contributi di y sono negativi
            addX = -1;
            addY = -1;
            verso = 2;
        } else if ((x2 < x1) && (y2 >= y1)) {
            //SONO nel terzo quadrante i contributi di y sono negativi
            addX = -1;
            addY = 1;
            verso = 3;
        }
        
        //si applica il verso al Nodo autobus ogni volta che aggiorna la posizione
        for(Entry<String, Bus_node> entry : cityMap.getVeicoli().entrySet()){
        	String id = entry.getKey();
        	int i = Integer.parseInt(id);
        	if(i == this.id_nodo){
        		Bus_node bus = entry.getValue();
        		bus.setVerso(verso);
        		//TODO put?
        	}
        }
        
        double temp_currX = currX + (addX) * distance * Math.cos(angle);
        double temp_currY = currY + (addY) * distance * Math.sin(angle);

        if (cityMap.validatePos("" + this.id_nodo, temp_currX, temp_currY, verso)) {
            currX = temp_currX;
            currY = temp_currY;
            currDistance = currDistance + distance;
        }
    }
    
    public void setPercorso(List<Node> percorso){
    	this.percorso = (ArrayList<Node>) percorso;
    }
    
    @Override
    public void Handler(Messaggi m) {
        if (m.getTipo_Messaggio().equals(START_ROAD_RUN)) {
            carIsPowerOff = false;
            /*dijkstra.init(mappa);
            dijkstra.setSource(mappa.getNode(nodo_ingresso));
            dijkstra.compute();*/
            
            
            
            /*list1 = new ArrayList<Node>();
            
            for (Node node : dijkstra.getPathNodes(mappa.getNode(nodo_uscita))) {
                list1.add(0, node);
            }*/
            
            //all'inizio il primo nodo è il terminal
            index_nodo_attuale = 0;
            
            Node curr = percorso.get(index_nodo_attuale);
            
            int id_fermata = Integer.parseInt(curr.getId());
            
            /*utenti che sono saliti e che sono scesi dal bus
            numero_utenti[0] = utenti scesi
            numero_utenti[1] = utenti saliti*/
            int[] numero_utenti = getUtenti(id_fermata);
            
            double waitingTime = UPDATE_POSITION_TIME + numero_utenti[1]*TEMPO_SALITA;
            
            Object x1 = ((Object[]) curr.getAttribute("xy"))[0];
            Object y1 = ((Object[]) curr.getAttribute("xy"))[1];

            currX = Double.parseDouble("" + x1);
            currY = Double.parseDouble("" + y1);
            currDistance = 0;

            m.setTipo_Messaggio(UPDATE_POSITION);
            m.shifta(waitingTime);
            m.setDestinazione(this);
            m.setSorgente(this);
            s.insertMessage(m);

        } else if (m.getTipo_Messaggio().equals(UPDATE_POSITION)) {
            //TODO: MANAGE MOVEMENTS
            //if (!nodo_ingresso.equals(nodo_uscita)) {
                if (index_nodo_attuale < percorso.size() - 1) {           
                    Node curr = percorso.get(index_nodo_attuale);
                    Node next = percorso.get(index_nodo_attuale + 1);

                    Object x1 = ((Object[]) curr.getAttribute("xy"))[0];
                    Object x2 = ((Object[]) next.getAttribute("xy"))[0];
                    Object y1 = ((Object[]) curr.getAttribute("xy"))[1];
                    Object y2 = ((Object[]) next.getAttribute("xy"))[1];

                    double xComp = Math.pow((Double.parseDouble("" + x2) - Double.parseDouble("" + x1)), 2.0);
                    double yComp = Math.pow((Double.parseDouble("" + y2) - Double.parseDouble("" + y1)), 2.0);
                    double segment_length = Math.sqrt(xComp + yComp);

                    //Get average speed from cityMap by reading edge info
                    String edge_label = curr.toString() + next.toString();
                    
                    Edge e = mappa.getEdge(edge_label);
                    
                    if(e == null){
                    	edge_label = next.toString() + curr.toString();
                    	e = mappa.getEdge(edge_label);
                    }
                    
                    calcolaNuovaPosizione(e, Double.parseDouble("" + x1),
                            Double.parseDouble("" + y1),
                            Double.parseDouble("" + x2),
                            Double.parseDouble("" + y2));

                    double waitingTime = UPDATE_POSITION_TIME;
                    
                    //Sono arrivato alla fine del segmento
                    if (currDistance >= segment_length) {
                        currX = Double.parseDouble("" + x2);
                        currY = Double.parseDouble("" + y2);
                        cityMap.updateVehiclePos("" + this.id_nodo, currX, currY);
                        currDistance = 0;
                        index_nodo_attuale++;
                        int id_fermata = Integer.parseInt(next.getId());
                        
                        int[] numero_utenti = getUtenti(id_fermata);
                        /*il tempo di attesa è dato da un tempo di fermata dell'autobus
                        un tempo per far salire gli utenti sull'autobus e
                        un tempo per ripartire. Il tempo di farmata e di partenza consiste
                        nello STOP_WAITING_TIME */
                        waitingTime = STOP_WAITING_TIME + numero_utenti[1]*TEMPO_SALITA + numero_utenti[0]*TEMPO_DISCESA;
                        //System.out.println("nodo " + this.getId() + " Arrivato su incrocio " + next + " al tempo " + s.orologio.getCurrent_Time());
                        
                        if(this.nodo_uscita.equals(next.toString())){
                            carIsPowerOff = true;
                            for(Nodo n : info.getNodes()){
                            	//come arriva al terminal non viene più disegnato.
                            	cityMap.getCityRoadMap().getNode(""+this.id_nodo).clearAttributes();
                                Messaggi m1 = new Messaggi(POWER_OFF,this,my_wireless_channel,n,s.orologio.getCurrent_Time());
                                m1.setNodoSorgente(this);
                                m1.saliPilaProtocollare = false;
                                m1.setNextHop(n);
                                m1.setNextHop_id(n.getId());
                                s.insertMessage(m1);
                            }
                        }
                    }

                    //System.out.println("nodo " + this.getId() + " posizione x,y (" + currX + "," + currY + ") al tempo " + s.orologio.getCurrent_Time());
                    if(carIsPowerOff == false)
                    {
                      m.shifta(waitingTime);
                      s.insertMessage(m);
                    }
                }
            //}

        } else if (m.getTipo_Messaggio().equals("DISCOVER_NEIGHBOURS")) {
            if(this.carIsPowerOff == false)
            {
                if (m.saliPilaProtocollare == false) {
                    //Invia messaggio a canale
                    m.shifta(0);
                    m.setDestinazione(my_wireless_channel);
                    m.setSorgente(this);
                    s.insertMessage(m);
                } else {
                    //invia messaggio a PHY
                    m.shifta(0);
                    m.setDestinazione(this.myPhyLayer);
                    m.setSorgente(this);
                    s.insertMessage(m);
                }
            }
        } else {
            super.Handler(m);
        }
    }

    public void setExitFromGate(double exitGateAt) {
        Messaggi m = new Messaggi(START_ROAD_RUN, this, this, this, s.orologio.getCurrent_Time());        
        m.shifta(exitGateAt);
        s.insertMessage(m);
    }
    
    public synchronized int[] getUtenti(int id_fermata){
    	int[] ris = new int[2];
    	//fai scendere gli utenti dall'autobus
    	/*TODO Se uso una LL viene generata una ConcurrentModificationException
    	 * questo perchè l'autobus rimuove l'utente, la size diminuisce
    	 * e l'iteratore non riesce ad andare avanti.
    	*/
    	for(int i = 0; i < utenti.length; i++){
    		if(utenti[i] != null && utenti[i].getNodo_uscita() == id_fermata){
    			System.out.format("L'utente %d è sceso alla fermata %d dall'autobus %d \n", utenti[i].getId(), id_fermata, id_nodo);
    			//si conserva nell'utente il momento in cui finisce di viaggiare
    			utenti[i].setTermineViaggio(s.orologio.getCurrent_Time());
    			//stampa i tempi di attesa e di viaggio da usare nelle statistiche
    			System.out.format("Utente %d - tempo di attesa %d - tempo viaggio %d \n", utenti[i].getId(), (int)utenti[i].getTempoAttesa(), (int)utenti[i].getTempoViaggio());
    			utenti[i] = null;
    			numPosti--;
    			ris[0]++;
    		}
    	}
    	//ottieni gli utenti in attesa alla fermata corrente e che attendono
        //questo autobus che passa per il percorso che abbiamo scelto
    	LinkedList<Utente> utentiAttesa = cityMap.getUtenti(id_fermata, id_percorso);
        //fai salire gli utenti sull'autobus, controllando che
        //l'autobus non sia pieno
        for(int i = 0; i < utentiAttesa.size(); i++){
        	if(numPosti < POSTI_MAX){
        		/*Non c'è bisogno di fare nessun controllo da parte dell'autobus
        		perchè l'utente sceglie la coda relativa a un percorso
        		e l'autobus che segue quel percorso lo preleva.*/
        		Utente u = utentiAttesa.removeFirst();
        		//si conserva nell'Utente il momento in cui inizia a viaggiare
        		u.setInizioViaggio(s.orologio.getCurrent_Time());
        		//rimuove l'utente dalla coda in cui è presente
        		cityMap.rimuovi_utente(u, id_fermata, id_percorso);
        		utenti[numPosti] = u;
    			System.out.format("L'utente %d è salito sull'autobus %d dalla fermata %d \n", u.getId(), id_nodo, id_fermata);
        		numPosti++;
        		ris[1]++;
        	}
        	else break;
        }
        return ris;
    }

}
