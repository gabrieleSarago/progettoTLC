package autolinee;

public class Utente{

	private int id;
	private int nodo_uscita;
	
	public Utente(int id, int nodo_uscita){
		this.id = id;
		this.nodo_uscita = nodo_uscita;
	}

	public int getId() {
		return id;
	}

	public int getNodo_uscita() {
		return nodo_uscita;
	}
	
}
