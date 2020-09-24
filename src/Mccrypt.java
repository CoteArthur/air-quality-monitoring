import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

public class Mccrypt {

	private String message;
	private AccesBDD accesBDD;
	private ArrayList<DataSonde> listeSonde;
	private String trameTot;
	
	
	public Mccrypt(AccesBDD accesBDD) {
		this.accesBDD = accesBDD;
		
	}
	
	
	public void majAff(ArrayList<DataSonde> listeSonde) {
		
		this.listeSonde = listeSonde;
		prepTram();
		envoiSurAff();
	}
	
	public void prepTram() {
		
		int valCo2;
		int valCOV;
		float valTemp;
		int valHygro;             
		String salle;
		message = "";
		
		
		// Rentre les valeurs de l'ArrayListe<DataSonde> dans les variables 'valCo2', 'valCOV', 'valTemp', 'valHygro' avant de les reuinirs dans la variable 'message'
		for(int i=0;i<listeSonde.size();i++) {
			valCo2=listeSonde.get(i).getCO2();
			valCOV=listeSonde.get(i).getCOV();
			valTemp=listeSonde.get(i).getTemp();
			valHygro=listeSonde.get(i).getHygro();
			salle=listeSonde.get(i).getSalle();
			
			message=message+salle+ " CO2:"+Integer.toString(valCo2)+"ppm COV:"+Integer.toString(valCOV)+"ppm Temp:" +Float.toString(valTemp)+"<U3A>C Humid:"+Integer.toString(valHygro)+"% / ";
		}
	}
	
	public void envoiSurAff() {
		
		ArrayList<String> param = accesBDD.lireConfigAff();
		/*String entete = "<ID00><BE>05<E>";*/
    	String fin = "<E>";					/*<ID00><BF>06<E>*/
    	String id ="<ID00>";
    	String ligne = "<L1>";
    	String page = "<PA>";
    	//recuperation du paramettre de effetArrive dans l'ArrayList<String>param  
    	String effetArriver = "<"+param.get(0)+">";
    	//recuperation du paramettre de effetVitAttente dans l'ArrayList<String>param  
    	String effVitAttente = "<"+param.get(1)+">";
    	//recuperation du paramettre de tmpAttente dans l'ArrayList<String>param  
    	String tmpAttente = "<"+param.get(2)+">";
    	//recuperation du paramettre de effetfin dans l'ArrayList<String>param  
    	String effetFin = "<"+param.get(3)+">";
    	//recuperation du paramettre de police dans l'ArrayList<String>param  
    	String police = "<"+param.get(4)+">";
    	//recuperation du paramettre de couleur dans l'ArrayList<String>param  
    	String couleur = "<"+param.get(5)+">";
    	
    	
    	//creation d'une variable pour le calcul du Checksum
    	String str = ligne+page+effetArriver+effVitAttente+tmpAttente+effetFin+couleur+police;
    	str = str+message;
    	
    	/*System.out.println(str);*/
		byte[] trame = str.getBytes();
		byte cks =trame[0];
		StringBuilder sb = new StringBuilder();	
		
		//Calcul du CheckSum 
		for(int i = 1;i<trame.length;i++) {
			cks = (byte) (cks ^ trame[i]); 
		}
		
		//recuperation des 2 dernier octets du calcul du Checksum 
	     sb.append(String.format("%02X", cks));
    	
        // Creation de la trame total avec id de la trame, les controles et message et le Checksum  
        trameTot = id+str+sb+fin;
     
        
		try {
			
			//creation de la connection 
			Socket socket = new Socket("172.16.125.145", 4000);
			// recupere le tuyau de sortie
			PrintStream sortie = new PrintStream(socket.getOutputStream() );
			// envoi de la trame sous forme de Byte
			sortie.write(trameTot.getBytes(),0,trameTot.length());
			//netoyage du tuyau 
			sortie.flush();
			//fermeture de la connection 
			socket.close();
		}
			catch(IOException ioe) {	
				System.out.println("Exeption de type IOException");
		}
	}
    	
}