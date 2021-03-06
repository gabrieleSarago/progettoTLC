/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autolinee;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.graphstream.graph.Node;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import autoMobility.Bus_node;
import autoMobility.MobilityMap;
import base_simulator.Applicazione;
import base_simulator.Grafo;
import base_simulator.Infos;
import base_simulator.NetworkInterface;
import base_simulator.canale;
import base_simulator.link_extended;
import base_simulator.scheduler;
import base_simulator.layers.LinkLayer;
import base_simulator.layers.TransportLayer;
import base_simulator.layers.physicalLayer;
import reti_tlc_gruppo_0.netLayerLinkState;
import reti_tlc_gruppo_0.nodo_router;

/**
 *
 * @author afsantamaria
 */
public class Main_app{

	private static scheduler s;
    
    private static void init_sim_parameters() {
    	//millisecondi - 83 minuti di simulazione
        s = new scheduler(5400000, false);
    }

    private String conf_file_path;

    /**
     * Creates new form main_app
     */
    public Main_app() {
    	//parte grafica
        File conf_file = new File("src/conf.xml");
        if (conf_file.exists()) {
            startParsing(conf_file);
        } else if (conf_file_path != null) {
            conf_file = new File(conf_file_path);
            startParsing(conf_file);
        } else {
            System.out.println("File non esistente");
        }
        //3 . Ready to start simulation
        new Thread(s).start();
        //initComponents();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main_app.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main_app.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main_app.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main_app.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        init_sim_parameters();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main_app();
            }
        });
    }

    private Infos info = new Infos();

    MobilityMap roadMap;

    @SuppressWarnings("rawtypes")
	private boolean startParsing(File xmlFile) {
        roadMap = new MobilityMap();
        SAXBuilder saxBuilder = new SAXBuilder();
        boolean res = false;
        try {
        	
        	//documento su cui fare parsing
            Document document = (Document) saxBuilder.build(xmlFile);
            //Radice del file xml
            Element rootElement = document.getRootElement();
            
            //canale di comunicazione
            List listElement = rootElement.getChildren("canali");

            for (int i = 0; i < listElement.size(); i++) {
                /*
<canale id="0" tipo="WIRED" capacita="5000000" dim_pacchetto = "1526" tempo_propagazione = "10"></canale> 
                 */
                Element node = (Element) listElement.get(i);

                List listElement1 = node.getChildren("canale");
                for (Object listElement2 : listElement1) {

                    int id = Integer.valueOf(((Element) listElement2).getAttributeValue("id"));
                    double capacita = Double.valueOf(((Element) listElement2).getAttributeValue("capacita"));
                    System.out.println("tipo: "
                            + ((Element) listElement2).getAttributeValue("tipo"));
                    double dim_pckt = Double.valueOf(((Element) listElement2).getAttributeValue("dim_pacchetto"));

                    double tempo_prop = Double.valueOf(((Element) listElement2).getAttributeValue("tempo_propagazione"));

                    canale c = new canale(s, id, capacita, dim_pckt, tempo_prop);

                    info.addCanale(c);
                }
            }
            
            //Nodi macchine presenti nel file xml
            
            listElement = rootElement.getChildren("nodo_host");
            int idCanale = 1;
            int lastNodeId = 0;
            for (Object nodo : listElement) {

                int id = Integer.valueOf(((Element) nodo).getAttributeValue("id"));
                lastNodeId = id;
                int gateway = Integer.valueOf(((Element) nodo).getAttributeValue("gateway"));
                int numero_nodi = Integer.parseInt(((Element) nodo).getAttributeValue("net_size"));
                String nodo_ingresso = ((Element) nodo).getAttributeValue("nodo_ingresso");
                String nodo_uscita = ((Element) nodo).getAttributeValue("nodo_uscita");
                double exitGateAt = Double.parseDouble(((Element) nodo).getAttributeValue("exitAt"));
                int showUI = Integer.valueOf(((Element) nodo).getAttributeValue("showUI"));
                
                Grafo grafo = new Grafo(numero_nodi);

//                physicalLayer pl = new physicalLayer(s, 0.0);
                Physical80211P pl = new Physical80211P(s, 0.0);
                LinkLayer ll = new LinkLayer(s, 5.0);
//                netLayerLinkState nl = new netLayerLinkState(s, 5.0, grafo);
                waveNetLayer nl = new waveNetLayer(s, 5.0, grafo,showUI);
//                tcpTransportLayer tl = new tcpTransportLayer(s, 5.0);
                waveFSCTPTransportLayer tl = new waveFSCTPTransportLayer(s, 5.0);

//                nodo_host nh = new nodo_host(s, id, pl, ll, nl,tl, null, "nodo_host", gateway);
                /**
                 * ****PER TEST VANET
                 */
                NodoAutobus nh = new NodoAutobus(s, id, pl, ll, nl, tl, null, "nodo_host", gateway);

                nh.setMappa(roadMap);
                nh.setNodo_ingresso(nodo_ingresso);
                nh.setNodo_uscita(nodo_uscita);
                nh.setExitFromGate(exitGateAt);
                //car_node car = new car_node(id, 0, 0);
                Bus_node car = new Bus_node(0, 0, id);
                roadMap.vehicles.put("" + id, car);

                canale c = new canale(s, idCanale,
                        info.getCanale(0).returnCapacita(),
                        info.getCanale(0).getDimensione_pacchetto(),
                        info.getCanale(0).getTempo_di_propagazione());
                info.addCanale(c);
                idCanale++;

                nh.setMy_wireless_channel(c);

//                nh.setNodo_ingresso("A");
//                nh.setNodo_uscita("E");
                /**
                 * ****FINE CONFIGURAZIONE TEST VANET
                 */
//                pl.connectPhysicalLayer(ll, nh);
//                ll.connectLinkLayer(pl, nl, nh);
//                nl.connectNetworkLayer(tl, ll, nh);
//                tl.connectTransportLayer(nl, nh);
                //Setto gli elementi relativi al networking
                nl.setDefaultGateway(gateway);

//                Element node = (Element) listElement.get(i);
//                List listElement1 = node.getChildren("canale");
                
                //Non � presente nel file di configurazione
                List listElement1 = ((Element) nodo).getChildren("interfaces");

                for (Object interfaces_list : listElement1) {

                    List intertace_list = ((Element) interfaces_list).getChildren("interface");

                    for (Object obj_interfaccia : intertace_list) {
                        System.out.println("idinterfaccia:" + ((Element) obj_interfaccia).getAttributeValue("id"));
                        int if_id = Integer.valueOf(((Element) obj_interfaccia).getAttributeValue("id"));
                        String IP = ((Element) obj_interfaccia).getAttributeValue("IP");
                        int dest = Integer.valueOf(((Element) obj_interfaccia).getAttributeValue("dest"));
                        int channelId = Integer.valueOf(((Element) obj_interfaccia).getAttributeValue("canale"));
                        double metrica = Double.valueOf(((Element) obj_interfaccia).getAttributeValue("metrica"));
                        NetworkInterface nic = new NetworkInterface(if_id, IP, dest, channelId, metrica);
                        nh.addNIC(nic);
                        //TODO:Da fare inserimento statico delle entry nelle tabelle di routing, sulle interfacce dei vicini 
                        /**
                         * La entry dovrebbe essere del tipo DEST NEXTHOP COSTO
                         * dest dest metrica
                         */
                        nl.addRoutingTableEntry(dest, dest, metrica);

                        grafo.setCosto(nh.getId(), dest, metrica, 0.0);
                        grafo.setCosto(dest, nh.getId(), metrica, 0.0);
                    }
                }
                
                //Commentato nel file xml, quindi non serve
                listElement1 = ((Element) nodo).getChildren("application");

                for (Object application_element : listElement1) {
                    Element app_item = (Element) application_element;
                    String tipo = app_item.getAttributeValue("type");

                    Double rate = Double.valueOf(app_item.getAttributeValue("rate"));
                    int TON = Integer.valueOf(app_item.getAttributeValue("TON"));
                    int TOFF = Integer.valueOf(app_item.getAttributeValue("TOFF"));
                    int port = Integer.valueOf(app_item.getAttributeValue("port"));
                    int dest = Integer.valueOf(app_item.getAttributeValue("dest"));
                    double size = Double.valueOf(app_item.getAttributeValue("size"));
                    int start = Integer.valueOf(app_item.getAttributeValue("start"));
                    double pckt_size = Double.valueOf(app_item.getAttributeValue("pckt_size"));
                    String payload = app_item.getAttributeValue("payload");
                    String filePath = app_item.getAttributeValue("file");
                    int availableSpaceForSession = Integer.valueOf(app_item.getAttributeValue("availableSpaceForSession"));
                    Applicazione app = new Applicazione(rate, TON, TOFF, port, dest, size, pckt_size, tipo, start, payload, filePath);

                    if (app.getTipo().equals(app.SIMPLE_APP_TCP)) {
                        //1. Aggiungo applicazione solo se sorgente abilitando la porta
                        //2. Sulla destinazione sarà aperta la porta solo su esplicita richiesta da parte di una sorgente
                        nh.addApplicazione(app);
                    }

                    tl.setAvailableSpaceForSession(availableSpaceForSession * (int) pckt_size);

                }

                System.out.println("Creato nodo_host con id: " + nh.getId() + " e gateway:" + nh.getGTW());

                info.addNodo(nh);

            }
            
            //percorsi
            listElement = rootElement.getChildren("percorso");
            //lista dei percorsi presenti nel file .xml
            HashMap<String, ArrayList<Node>> percorsi = roadMap.getPercorsi();
            int id = 0;
            //per ogni percorso crea una lista di nodi da attraversare e la aggiunge alla lista dei percorsi
            for (Object nodo : listElement) {
            	ArrayList<Node> percorso = new ArrayList<>();
            	String linea = ((Element) nodo).getAttributeValue("linea");
            	//primo nodo
            	Node nodo_partenza = roadMap.getCityRoadMap().getNode(((Element) nodo).getAttributeValue("nodo_partenza"));
            	percorso.add(nodo_partenza);
            	for(Object nodo1 : ((Element) nodo).getChildren("nodo_intermedio")){
            		percorso.add(roadMap.getCityRoadMap().getNode(((Element) nodo1).getText()));
            	}
            	//ultimo nodo
            	Node nodo_arrivo = roadMap.getCityRoadMap().getNode(((Element) nodo).getAttributeValue("nodo_arrivo"));
            	percorso.add(nodo_arrivo);
            	percorsi.put(linea, percorso);
            	id++;
            }
            roadMap.setPercorsi(percorsi);
            
            listElement = rootElement.getChildren("fermata");
            //HashMap id percorso, lista utenti
            HashMap<String, LinkedList<Utente>> linee;
            Random r = new Random();
        	//id utente inteso come incrementale per qualsiasi utente di qualsiasi fermata
        	int id_utente = 0;
            //per ogni nodo fermata
            for(Object nodo : listElement){
            	//id nodo fermata
            	int id_fermata = Integer.parseInt(((Element) nodo).getAttributeValue("id"));
            	//numero massimo di utenti generati in questa fermata
            	//int numUtenti = Integer.parseInt(((Element) nodo).getAttributeValue("numUtenti"));
            	//numero massimo di utenti che genera da 10 a un numero massimo di utenti.
            	int numUtenti = (new Random()).nextInt(Integer.parseInt(((Element) nodo).getAttributeValue("numUtenti")) - 10) + 10;
            	Statistica.setUtenti(numUtenti);
            	double generationRate = Integer.parseInt(((Element) nodo).getAttributeValue("generationRate"));
            	double tempoAttesa = Integer.parseInt(((Element) nodo).getAttributeValue("exitAt"));
            	linee = new HashMap<>();
            	//per ogni percorso si prende l'id e si crea una lista di utenti
            	String id_linea;
            	for(Entry<String, ArrayList<Node>> e : percorsi.entrySet()){
            		id_linea = e.getKey();
            		ArrayList<Node> linea = e.getValue();
            		//verifica che la linea passi dalla fermata
            		for(Node n : linea){
            			//se l'id di un nodo della linea coincide col nodo
            			//della fermata allora la linea passa per la nostra fermata
            			//quindi aggiungi la linea all'insieme di linee possedute
            			//dalla nostra fermata.
            			if(n.getId().equals(((Element) nodo).getAttributeValue("id"))){
            				linee.put(id_linea, new LinkedList<Utente>());
            				break;
            			}
            		}
            	}
            	roadMap.addLinee(id_fermata, linee);
            	
            	//generazione degli utenti
            	double tempoGenerazione =  60000.0 / generationRate;
            	
            	for(int i = 0; i < numUtenti; i++){

            		//prende le linee che passano per la fermata e ne
            		//aggiunge i nodi a una LinkedHashSet, per poi scegliere un
            		//nodo casuale come nodo destinazione dell'utente
            		HashMap<String, LinkedList<Utente>> percorsi_fermata = roadMap.getLinee(id_fermata);
            		LinkedHashSet<Node> nodi = new LinkedHashSet<>();
            		//per ogni linea aggiungi i nodi nella linkedHashSet
            		for(Entry<String, LinkedList<Utente>> e : percorsi_fermata.entrySet()){
            			String id_temp = e.getKey();
            			//System.out.println("id_temp: "+id_temp);
            			ArrayList<Node> percorso_temp = percorsi.get(id_temp);
            			nodi.addAll(percorso_temp);
            		}
            		//rimuovi il nodo della fermata attuale
            		Node partenza = roadMap.getCityRoadMap().getNode(""+id_fermata);
            		nodi.remove(partenza);
            		//System.out.println(partenza.getId());
            		//System.out.println("utente "+id_utente+ " fermata " + id_fermata+ " nodi destinazioni "+nodi.toString());
            		//scegli una posizione casuale del nodo
            		int scelta_nodo = r.nextInt(nodi.size()+1);
            		int scelta_corrente = 0;
            		int nodo_uscita = 0;
            		for(Node n : nodi){
            			if(scelta_corrente == scelta_nodo){
            				//scegli il nodo di destinazione
            				nodo_uscita = Integer.parseInt(n.getId());
            				break;
            			}
            			scelta_corrente++;
            		}
            		//linee percorribili dall'utente
            		LinkedList<String> linee_percorribili = new LinkedList<>();
            		//per ogni percorso che passa per la fermata attuale
            		//verifica che sia presente il nodo destinazione dell'utente attuale
            		for(Entry<String, LinkedList<Utente>> e : percorsi_fermata.entrySet()){
            			String id_temp = e.getKey();
            			ArrayList<Node> percorso_temp = percorsi.get(id_temp);
            			if(percorso_temp.contains(roadMap.getCityRoadMap().getNode(""+nodo_uscita))){
            				linee_percorribili.add(id_temp);
            			}
            		}
            	
            		//genera l'utente
            		Utente u = new Utente(s, id_utente, nodo_uscita, id_fermata, linee_percorribili);
            		u.setMappa(roadMap);
            		u.setExitFromGate(tempoAttesa);
            		//System.out.format("E' stato generato l'utente %d con partenza %d e destinazione %d \n", id_utente, id_fermata, nodo_uscita);
            		id_utente++;
            		tempoAttesa += tempoGenerazione;
            	}
            }
            
            //nodo pozzo
            
            listElement = rootElement.getChildren("pozzo");
            int counterNodeId = 1000;
            //il primo percorso � A = 65
            int id_percorso = 65;
            for (Object nodo : listElement) {
            	//lista delle fermate da cui passare
            	ArrayList<Node> percorso;
                //String nodo_ingresso = ((Element) nodo).getAttributeValue("nodo_ingresso");
                //String nodo_uscita = ((Element) nodo).getAttributeValue("nodo_uscita");
            	//ingresso = primo elemento, uscita = ultimo elemento della lista
            	String nodo_ingresso;
            	String nodo_uscita;
            	
            	double exitGateAt = Double.parseDouble(((Element) nodo).getAttributeValue("exitAt"));
                double generationRate = Double.parseDouble(((Element) nodo).getAttributeValue("generationRate"));
                double maxVehicles = Double.parseDouble(((Element) nodo).getAttributeValue("maxVehicles"));
                int gateway = Integer.valueOf(((Element) nodo).getAttributeValue("gateway"));
                int showUI = Integer.valueOf(((Element) nodo).getAttributeValue("showUI"));

                double interExitTime = 60000.0 / generationRate;

                int vehicleCounter = 0;
                for (vehicleCounter = 0; vehicleCounter < maxVehicles; vehicleCounter++) {

                    id = lastNodeId + counterNodeId;
                    counterNodeId++;
                    Grafo grafo = new Grafo(5);

                    Physical80211P pl = new Physical80211P(s, 0.0);
                    LinkLayer ll = new LinkLayer(s, 5.0);

                    waveNetLayer nl = new waveNetLayer(s, 5.0, grafo,showUI);
                    
                    waveFSCTPTransportLayer tl = new waveFSCTPTransportLayer(s, 5.0);
                    
                    //nodi macchina generati dal pozzo
                    //costruttore che si prende il percorso e fa partire il movimento dell'autobus
                    //aggiorna percorso
                    String linea = ""+(char)id_percorso;
                    percorso = percorsi.get(linea);
                    //aggiorna nodi terminali
                    nodo_ingresso = percorso.get(0).getId();
                	nodo_uscita = percorso.get(percorso.size()-1).getId();
                    NodoAutobus nh = new NodoAutobus(s, id, pl, ll, nl, tl, null, "nodo_host", gateway, linea, percorso);
                    //id_percorso serve ad assegnare un percorso all'autobus
                    //si incrementa l'id, se questo supera il numero di percorsi si pone a 0
                    id_percorso++;
                    if(!percorsi.containsKey(""+(char)id_percorso)){
                    	id_percorso = 65;
                    }
                    
                    nh.setMappa(roadMap);
                    nh.setNodo_ingresso(nodo_ingresso);
                    nh.setNodo_uscita(nodo_uscita);
                    nh.setExitFromGate(exitGateAt);
                    
                    Bus_node bus = new Bus_node(0, 0, id);
                    roadMap.vehicles.put("" + id, bus);

                    canale c = new canale(s, idCanale,
                            info.getCanale(0).returnCapacita(),
                            info.getCanale(0).getDimensione_pacchetto(),
                            info.getCanale(0).getTempo_di_propagazione());
                    info.addCanale(c);
                    idCanale++;

                    nh.setMy_wireless_channel(c);

                    nl.setDefaultGateway(gateway);

                    //Aggiorno il tempo di uscita del prossimo veicolo
                    exitGateAt += interExitTime;
                    info.addNodo(nh);
                }
            }
            
            
            //commentato nel file xml
            listElement = rootElement.getChildren("router");

            for (Object routers_list : listElement) {

                int node_id = Integer.valueOf(((Element) routers_list).getAttributeValue("id"));
                int gateway = Integer.valueOf(((Element) routers_list).getAttributeValue("gateway"));
                int numero_nodi = Integer.parseInt(((Element) routers_list).getAttributeValue("net_size"));
                Grafo grafo = new Grafo(numero_nodi);

                physicalLayer pl = new physicalLayer(s, 0.0);
                LinkLayer ll = new LinkLayer(s, 5.0);
                netLayerLinkState nl = new netLayerLinkState(s, 5.0, grafo);
                TransportLayer tl = new TransportLayer(s, 5.0);

                nodo_router nr = new nodo_router(s, node_id, pl, ll, nl, tl, null, "nodo_router", 0);
//PHY
                pl.connectPhysicalLayer(ll, nr);
//LL                
                ll.connectLinkLayer(pl, nl, nr);
//NET                
                nl.connectNetworkLayer(tl, ll, nr);
                nl.setDefaultGateway(gateway);
//TRASP                
                tl.connectTransportLayer(nl, nr);

                System.out.println("Ho aggiunto un " + nr.getTipo() + " con id..:" + nr.getId());

                List protocol_list = ((Element) routers_list).getChildren("protocol");

                for (Object protocol_element : protocol_list) {

                    Element item = (Element) protocol_element;

                    String tipo = item.getAttributeValue("tipo");
                    int TTL = 0;

                    if (tipo.equals("OSPF")) {
                        TTL = Integer.valueOf(item.getAttributeValue("TTL"));

                        nl.enableFullOSPF();
                        nl.setTTL_LSA(TTL);
                    }

                    String routing = item.getAttributeValue("ROUTING");
                    /**
                     * TODO : These parameters may be erased, The routing rules
                     * are moved to nl specialization
                     */
                    nr.setProtocol(tipo);
                    nr.setRouting(routing);
                    nr.setTTL(TTL);
                }

                List listElement1 = ((Element) routers_list).getChildren("interfaces");

                //Faccio la clear dei rami allocati e alloco una nuova struttura dati
                //popolo il grafo solo con i propri vicini                
                for (Object interfaces_list : listElement1) {
                    List intertace_list = ((Element) interfaces_list).getChildren("interface");

                    for (Object obj_interfaccia : intertace_list) {
                        System.out.println("idinterfaccia:" + ((Element) obj_interfaccia).getAttributeValue("id"));
                        int if_id = Integer.valueOf(((Element) obj_interfaccia).getAttributeValue("id"));
                        String IP = ((Element) obj_interfaccia).getAttributeValue("IP");
                        int channelId = Integer.valueOf(((Element) obj_interfaccia).getAttributeValue("canale"));
                        int dest = Integer.valueOf(((Element) obj_interfaccia).getAttributeValue("dest"));
                        double metrica = Double.valueOf(((Element) obj_interfaccia).getAttributeValue("metrica"));
                        NetworkInterface nic = new NetworkInterface(if_id, IP, dest, channelId, metrica);
                        nr.addNIC(nic);

                        //Inserimento dati di routing
                        /**
                         * La entry dovrebbe essere del tipo DEST NEXTHOP COSTO
                         * dest dest metrica
                         */
                        nl.addRoutingTableEntry(dest, dest, metrica);

//Popolazione iniziale topologia                        
                        grafo.setCosto(nr.getId(), dest, metrica, 0.0);
                        grafo.setCosto(dest, nr.getId(), metrica, 0.0);

                    }
                }

                info.addNodo(nr);

            }

            listElement = rootElement.getChildren("network");

            for (Object network_list : listElement) {
                List branches = ((Element) network_list).getChildren("ramo");
                for (Object branch_element : branches) {
                    Element branch = ((Element) branch_element);

                    double metric = Double.valueOf(branch.getAttributeValue("metrica"));
                    int nodo_iniziale = Integer.valueOf(branch.getAttributeValue("start"));
                    int nodo_finale = Integer.valueOf(branch.getAttributeValue("end"));
                    String tipo = branch.getAttributeValue("tipo");
                    link_extended l = new link_extended(nodo_iniziale, nodo_finale, metric);
                    info.addLink(l);

                    if (tipo.equals("full")) {
                        link_extended l1 = new link_extended(nodo_finale, nodo_iniziale, metric);
                        info.addLink(l1);

                    }
                }
            }

            s.setInfo(info);

        } catch (IOException io) {
            System.out.println(io.getMessage());
        } catch (JDOMException jdomex) {

            System.out.println(jdomex.getMessage());

        }
        return res;
    }

}
