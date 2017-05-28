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
	
	public Utente(scheduler s, int id, int nodo_uscita, int nodo_attesa){
		this.s = s;
		this.id = id;
		this.nodo_uscita = nodo_uscita;
		this.nodo_attesa = nodo_attesa;
	}

	public int getId() {
		return id;
	}

	public int getNodo_uscita() {
		return nodo_uscita;
	}
	
	public void Handler(Messaggi m){
		if (m.getTipo_Messaggio().equals(START_GENERATION)) {
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
	
	public void setLineeAttesa(LinkedList<Integer> linee_percorribili){
		this.linee_percorribili = linee_percorribili;
	}
	
	public void setMappa(MobilityMap roadMap){
		this.roadMap = roadMap;
	}
	
	public void setExitFromGate(double tempoAttesa){
		 Messaggi m = new Messaggi(START_GENERATION, this, this, this, s.orologio.getCurrent_Time());        
	     m.shifta(tempoAttesa);
	     s.insertMessage(m);
	}
	
}
