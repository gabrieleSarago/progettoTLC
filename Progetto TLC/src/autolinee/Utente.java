package autolinee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import autoMobility.MobilityMap;
import base_simulator.Messaggi;
import base_simulator.scheduler;

public class Utente{

	private int id;
	private int nodo_uscita;
	private int nodo_attesa;
	//percorso scelto dall'utente sulla base di un criterio
	private String percorso_scelto;
	private scheduler s;
	final String START_GENERATION = "start_generation";
	private LinkedList<String> linee_percorribili;
	private MobilityMap roadMap;
	private HashMap<String, ArrayList<Node>> percorsi;
	private String[] percorsi_migliori = {"","",""};
	private int percorso_corrente = 0;
	
	private double inizioAttesa, inizioViaggio, termineViaggio;

	public Utente(scheduler s, int id, int nodo_uscita, int nodo_attesa, LinkedList<String> linee_percorribili){
		this.s = s;
		this.id = id;
		this.nodo_uscita = nodo_uscita;
		this.nodo_attesa = nodo_attesa;
		this.linee_percorribili = linee_percorribili;
	}

	public int getId() {
		return id;
	}

	public int getNodo_uscita() {
		return nodo_uscita;
	}
	
	public void Handler(Messaggi m){
		if (m.getTipo_Messaggio().equals(START_GENERATION)) {
			//algoritmo di scelta basato su lunghezza viaggio o su meno fermate
			//busMinHop();
			//busMinLenght();
			percorso_scelto = busMinTime();
			accoda();
		}
	}
	
	private void accoda(){
		//aggiunge l'utente alla coda per il percorso scelto
		HashMap<String,LinkedList<Utente>> linee = roadMap.getLinee(nodo_attesa);
		for(String j : linee_percorribili){
			if(j.equals(percorso_scelto)){
				LinkedList<Utente> utenti = linee.get(j);
				utenti.add(this);
				//aggiorna la lista delle linee
				linee.put(j, utenti);
				break;
			}
		}
		//aggiorna le linee nella fermata
		roadMap.addLinee(nodo_attesa, linee);
	}
	
	//Algoritmi di scelta dell'autobus
	//algoritmo basato sul numero minore di fermate intermedie
	private String busMinHop(){
		String res = "";
		//per ogni linea percorribile estrai la lista di fermate
		//calcola in numero di hop dal nodo_attesa al nodo_uscita
		//se il numero di fermate è minore rispetto a un altro percorso aggiorna l'id
		//del percorso scelto e il numero di fermate intermedie.
		//id percorso scelto
		percorso_scelto = "";
		//minimo numero di fermate che inizialmente deve essere enorme
		int min_fermate = Integer.MAX_VALUE;
		//per ogni linea percorribile
		for(String id_linea : linee_percorribili){
			boolean start = false;
			//fermate appartenenti al percorso
			ArrayList<Node> percorso = percorsi.get(id_linea);
			int numFermateCorrente = 0;
			//per ogni fermata della linea
			for(Node n: percorso){
				int id_corrente = Integer.parseInt(n.getId());
				//come incontriamo la fermata di attesa dell'utente iniziamo a contare
				if(id_corrente == nodo_attesa)
					start = true;
				//se incontriamo il nodo di destinazione 
				//e abbiamo già incontrato quello di partenza
				if(id_corrente == nodo_uscita && start){
					//se il numero di fermate è minore rispetto al valore minimo
					//allora aggiorna il minimo e l'id del percorso scelto
					if(numFermateCorrente < min_fermate){
						min_fermate = numFermateCorrente;
						res = id_linea;
						percorsi_migliori[2] = percorsi_migliori[1];
						percorsi_migliori[1] = percorsi_migliori[0];
						percorsi_migliori[0] = id_linea;
					}
					//passa alla prossima linea percorribile
					break;
				}
				//se abbiamo già incontrato il nodo iniziale
				//allora è possibile contare le fermate intermedie
				if(start){
					//System.out.println("utente"+id+" "+numFermateCorrente + " "+id_corrente);
					numFermateCorrente++;
				}
			}
		}
		System.out.format("L'utente %d ha scelto la linea %d con numero di fermate %d \n", id, res, min_fermate);
		//System.out.println("percorso migliore : "+ percorsi.get(percorso_scelto).toString());
		return res;
	}
	
	//algoritmo basato sulla lunghezza minore percorsa
	private String busMinLenght(){
		String res = "";
		//per ogni linea percorribile
		int min_lenght = Integer.MAX_VALUE;
		for(String id_linea : linee_percorribili){
			boolean start = false;
			int min_lenght_corrente = 0;
			ArrayList<Node> percorso = percorsi.get(id_linea);
			int id_precedente = 0;
			for(Node n: percorso){
				int id_corrente = Integer.parseInt(n.getId());
				if(id_corrente == nodo_attesa)
					start = true;
				if(id_corrente == nodo_uscita && start && id_precedente != id_corrente){
					String label = id_precedente+""+id_corrente;
					Edge e = roadMap.getCityRoadMap().getEdge(label);
					if(e == null){
						label = id_corrente+""+id_precedente;
						e = roadMap.getCityRoadMap().getEdge(label);
					}
					min_lenght_corrente += (Integer) e.getAttribute("length");
					if(min_lenght_corrente < min_lenght){
						min_lenght = min_lenght_corrente;
						res = id_linea;
						percorsi_migliori[2] = percorsi_migliori[1];
						percorsi_migliori[1] = percorsi_migliori[0];
						percorsi_migliori[0] = id_linea;
					}
					break;
				}
				if(start && id_corrente != nodo_attesa){
					String label = id_precedente+""+id_corrente;
					Edge e = roadMap.getCityRoadMap().getEdge(label);
					//nel momento in cui l'autobus torna indietro l'etichetta è al contrario
					//e non viene riconosciuta dal grafo
					if(e == null){
						label = id_corrente+""+id_precedente;
						e = roadMap.getCityRoadMap().getEdge(label);
					}
					min_lenght_corrente += (Integer) e.getAttribute("length");
				}
				id_precedente = id_corrente;
			}
		}
		System.out.format("L'utente %d ha scelto la linea %d con una lunghezza %d \n", id, res, min_lenght);
		//System.out.println("percorso migliore : "+ percorsi.get(percorso_scelto).toString());
		return res;
	}
	
	//algoritmo basato sul tempo minore per arrivare a destinazione
	private String busMinTime(){
		String res = "";
		//per ogni linea percorribile
		int min_time = Integer.MAX_VALUE;
		for(String id_linea : linee_percorribili){
			boolean start = false;
			int lenght_corrente = 0;
			int min_time_corrente = 0;
			ArrayList<Node> percorso = percorsi.get(id_linea);
			int id_precedente = 0;
			for(Node n: percorso){
				int id_corrente = Integer.parseInt(n.getId());
				if(id_corrente == nodo_attesa){
					start = true;
				}
				//non devono coincidere la fermata precedente e quella corrente,
				//questo accade se il nodo_attesa è 0
				if(id_corrente == nodo_uscita && start && id_precedente != id_corrente){
					String label = id_precedente+""+id_corrente;
					Edge e = roadMap.getCityRoadMap().getEdge(label);
					if(e == null){
						label = id_corrente+""+id_precedente;
						e = roadMap.getCityRoadMap().getEdge(label);
					}
					lenght_corrente += (Integer) e.getAttribute("length");
					double speed = (Double) e.getAttribute("avgSpeed");
					min_time_corrente += lenght_corrente/speed;
					if(min_time_corrente < min_time){
						min_time = min_time_corrente;
						res = id_linea;
						percorsi_migliori[2] = percorsi_migliori[1];
						percorsi_migliori[1] = percorsi_migliori[0];
						percorsi_migliori[0] = id_linea;
					}
					break;
				}
				if(start && id_corrente != nodo_attesa){
					String label = id_precedente+""+id_corrente;
					Edge e = roadMap.getCityRoadMap().getEdge(label);
					if(e == null){
						label = id_corrente+""+id_precedente;
						e = roadMap.getCityRoadMap().getEdge(label);
					}
					/*if(e == null) {
						System.out.println(id_corrente+" "+id_precedente);
						System.out.println("linea = "+id_linea);
					}*/
					lenght_corrente += (Integer) e.getAttribute("length");
					double speed = (Double) e.getAttribute("avgSpeed");
					min_time_corrente += lenght_corrente/speed;
				}
				id_precedente = id_corrente;
			}
		}
		System.out.format("L'utente %d ha scelto la linea %s con un tempo %d \n", id, res, min_time);
		//System.out.println("percorso migliore : "+ percorsi.get(percorso_scelto).toString());
		return res;
	}
	
	public void setMappa(MobilityMap roadMap){
		this.roadMap = roadMap;
		percorsi = roadMap.getPercorsi();
	}
	
	public void setExitFromGate(double tempoAttesa){
		 Messaggi m = new Messaggi(START_GENERATION, this, this, this, s.orologio.getCurrent_Time());        
	     m.shifta(tempoAttesa);
	     s.insertMessage(m);
	}
	
	public void setInizioAttesa(double inizioAttesa) {
		this.inizioAttesa = inizioAttesa;
	}

	public void setInizioViaggio(double inizioViaggio) {
		this.inizioViaggio = inizioViaggio;
	}

	public void setTermineViaggio(double termineViaggio) {
		this.termineViaggio = termineViaggio;
		Statistica.setStatiche(getTempoViaggio(), getTempoAttesa());
	}
	
	//tempo totale di attesa
	public double getTempoAttesa(){
		//momento in cui sale sull'autobus - momento in cui inizia 
		//ad attendere(viene generato)
		return inizioViaggio - inizioAttesa;
	}
	
	//tempo in viaggio
	public double getTempoViaggio(){
		return termineViaggio - inizioViaggio;
	}
	
	//sceglie un'altra linea nel momento in cui l'autobus della linea già scelta è pieno
	public void aggiornaPercorsoMigliore(){
		percorso_corrente++;
		//se ci troviamo a sceglie per più di 3 volte la linea di attesa
		if(percorso_corrente >= percorsi_migliori.length){
			//si crea una copia della lista delle linee percorribili
			//tranne le prime tre scelte
			LinkedList<String> copia = new LinkedList<>();
			copia.addAll(linee_percorribili);
			copia.remove(percorsi_migliori[0]);
			copia.remove(percorsi_migliori[1]);
			copia.remove(percorsi_migliori[2]);
			//scegli una linea casuale tra quelle rimaste
			if(copia.size()!=0){
				int percorso_casuale = (new Random()).nextInt(copia.size());
				percorso_scelto = copia.get(percorso_casuale);
				accoda();
				System.out.println("L'utente "+id+" ha scelto la nuova linea "+percorso_scelto);
			}
		}
		else{
			//se non ci sono abbastanza linee da scegliere
			//l'array avrà elementi "-1" e a quel punto non è possibile scegliere
			//una nuova linea
			if(!(percorsi_migliori[percorso_corrente].equals(""))){
				percorso_scelto = percorsi_migliori[percorso_corrente];
				accoda();
				System.out.println("L'utente "+id+" ha scelto la nuova linea "+percorso_scelto);
			}
		}
	}
}
