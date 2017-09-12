package autolinee;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Statistica {
	
	static int numUtenti = 0;
	static double tempoViaggio = 0;
	static double tempoAttesa = 0;
	static int totPosti = 0;
	static int numAutobus = 0;
	static int utentiGenerati = 0;
	
	public synchronized static void setStatiche(double tViaggio, double tAttesa){
		tempoViaggio+=tViaggio;
		tempoAttesa+=tAttesa;
		numUtenti++;
	}
	
	public synchronized static void setUtenti(int numUtenti){
		utentiGenerati+=numUtenti;
	}
	
	public static void salva(){
		try {
			File f = new File("src/autolinee/log.txt");
			PrintWriter pw = new PrintWriter(f);
			String linea = "numero utenti = " + numUtenti + "\n"+"media tempo viaggio = " + tempoViaggio/numUtenti
					+ "\n"+"media tempo attesa = " + tempoAttesa/numUtenti+"\n"+"media posti occupati = " + totPosti/numAutobus
					+ "\n"+"utenti generati = "+utentiGenerati;
			pw.println(linea);
			pw.close();
			System.out.println(linea);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static void setStatisticheAutobus(int mediaPosti){
		numAutobus++;
		totPosti+=mediaPosti;
	}
}
