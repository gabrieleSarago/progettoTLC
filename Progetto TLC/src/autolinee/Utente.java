package autolinee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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
	private int percorso_scelto;
	private scheduler s;
	final String START_GENERATION = "start_generation";
	private LinkedList<Integer> linee_percorribili;
	private MobilityMap roadMap;
	private HashMap<Integer, ArrayList<Node>> percorsi;
	
	private double inizioAttesa, inizioViaggio, termineViaggio;

	public Utente(scheduler s, int id, int nodo_uscita, int nodo_attesa, LinkedList<Integer> linee_percorribili){
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
			busMinTime();
			//aggiunge l'utente alla coda per il percorso scelto
			HashMap<Integer,LinkedList<Utente>> linee = roadMap.getLinee(nodo_attesa);
    		for(Integer j : linee_percorribili){
    			if(j == percorso_scelto){
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
	}
	
	//Algoritmi di scelta dell'autobus
	//algoritmo basato sul numero minore di fermate intermedie
	private void busMinHop(){
		//per ogni linea percorribile estrai la lista di fermate
		//calcola in numero di hop dal nodo_attesa al nodo_uscita
		//se il numero di fermate è minore rispetto a un altro percorso aggiorna l'id
		//del percorso scelto e il numero di fermate intermedie.
		//id percorso scelto
		percorso_scelto = 0;
		//minimo numero di fermate che inizialmente deve essere enorme
		int min_fermate = Integer.MAX_VALUE;
		//per ogni linea percorribile
		for(int id_linea : linee_percorribili){
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
						percorso_scelto = id_corrente;
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
		System.out.format("L'utente %d ha scelto la linea %d con numero di fermate %d \n", id, percorso_scelto, min_fermate);
	}
	
	//algoritmo basato sulla lunghezza minore percorsa
	private void busMinLenght(){
		//per ogni linea percorribile
		int min_lenght = Integer.MAX_VALUE;
		for(int id_linea : linee_percorribili){
			boolean start = false;
			int min_lenght_corrente = 0;
			ArrayList<Node> percorso = percorsi.get(id_linea);
			int id_precedente = 0;
			for(Node n: percorso){
				int id_corrente = Integer.parseInt(n.getId());
				if(id_corrente == nodo_attesa)
					start = true;
				if(id_corrente == nodo_uscita && start){
					if(min_lenght_corrente < min_lenght){
						min_lenght = min_lenght_corrente;
						percorso_scelto = id_corrente;
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
		System.out.format("L'utente %d ha scelto la linea %d con una lunghezza %d \n", id, percorso_scelto, min_lenght);
	}
	
	//algoritmo basato sul tempo minore per arrivare a destinazione
	private void busMinTime(){
		//per ogni linea percorribile
		int min_time = Integer.MAX_VALUE;
		for(int id_linea : linee_percorribili){
			boolean start = false;
			int lenght_corrente = 0;
			int min_time_corrente = 0;
			ArrayList<Node> percorso = percorsi.get(id_linea);
			int id_precedente = 0;
			for(Node n: percorso){
				int id_corrente = Integer.parseInt(n.getId());
				if(id_corrente == nodo_attesa)
					start = true;
				if(id_corrente == nodo_uscita && start){
					if(min_time_corrente < min_time){
						min_time = min_time_corrente;
						percorso_scelto = id_corrente;
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
					lenght_corrente += (Integer) e.getAttribute("length");
					double speed = (Double) e.getAttribute("avgSpeed");
					min_time_corrente += lenght_corrente/speed;
				}
				id_precedente = id_corrente;
			}
		}
		System.out.format("L'utente %d ha scelto la linea %d con un tempo %d \n", id, percorso_scelto, min_time);
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
}
