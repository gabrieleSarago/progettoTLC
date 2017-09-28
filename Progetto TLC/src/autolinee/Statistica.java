package autolinee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
		if(!f.exists()) {
			f.createNewFile();
		}
		salvaStat(f,numUtenti);
		System.out.println("numUtenti = "+numUtenti);
		f = new File("src/autolinee/tempoViaggio.txt");
		if(!f.exists()) {
			f.createNewFile();
		}
		salvaStat(f, tempoViaggio/numUtenti);
		System.out.println("tempo viaggio = "+tempoViaggio/numUtenti);
		f = new File("src/autolinee/tempoAttesa.txt");
		if(!f.exists()) {
			f.createNewFile();
		}
		salvaStat(f, tempoAttesa/numUtenti);
		System.out.println("tempo attesa = "+tempoAttesa/numUtenti);
		f = new File("src/autolinee/mediaPosti.txt");
		if(!f.exists()) {
			f.createNewFile();
		}
		salvaStat(f, totPosti/numAutobus);
		System.out.println("media posti = "+totPosti/numAutobus);
		f = new File("src/autolinee/utentiGenerati.txt");
		if(!f.exists()) {
			f.createNewFile();
		}
		salvaStat(f, utentiGenerati);
		System.out.println("utenti generati = "+utentiGenerati);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void salvaStat(File f, double stat) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String linea = "", l = "";
			while(l != null) {
				l = br.readLine();
				if(l != null)
					linea += l+"\n";
			}
			br.close();
			PrintWriter pw = new PrintWriter(new FileWriter(f));
			pw.print(linea);
			pw.print(stat);
			pw.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void salvaStat(File f, int stat) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String linea = "", l = "";
			while(l != null) {
				l = br.readLine();
				if(l != null)
					linea += l+"\n";
			}
			br.close();
			PrintWriter pw = new PrintWriter(new FileWriter(f));
			pw.print(linea);
			pw.print(stat);
			pw.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static void setStatisticheAutobus(int mediaPosti){
		numAutobus++;
		totPosti+=mediaPosti;
	}
}
