package autolinee;

import java.util.HashMap;
import java.util.LinkedList;

import autoMobility.MobilityMap;
import base_simulator.Messaggi;
import base_simulator.scheduler;

public class Utente{

	private int id;
	private int nodo_uscita;
	private int nodo_attesa;
	private scheduler s;
	final String START_GENERATION = "start_generation";
	private LinkedList<Integer> linee_percorribili;
	private MobilityMap roadMap;
	
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
			//TODO algoritmo di scelta basato su lunghezza viaggio o su velocita media
			//aggiungi l'utente alle code per le linee percorribili
			HashMap<Integer,LinkedList<Utente>> linee = roadMap.getLinee(nodo_attesa);
    		for(Integer j : linee_percorribili){
    			LinkedList<Utente> utenti = linee.get(j);
    			utenti.add(this);
    			//aggiorna la lista delle linee
    			linee.put(j, utenti);
    		}
    		//aggiorna le linee nella fermata
    		roadMap.addLinee(nodo_attesa, linee);
		}
	}
	
	public void setMappa(MobilityMap roadMap){
		this.roadMap = roadMap;
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
