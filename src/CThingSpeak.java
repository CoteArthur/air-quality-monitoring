import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CThingSpeak extends Thread{
		
	private final String USER_AGENT = "Mozilla/5.0";
	private int numField =1;
	private ArrayList<DataSonde> listeSondes;

	
	public CThingSpeak() {
		
	}
	
	public void run() {
			
			System.out.println("Je suis rentre dans le Thread");
			this.envoiDatasCloud();
		}
		
		public void setListeSondes(ArrayList<DataSonde> listeSondes) {
			this.listeSondes = listeSondes;
		}
		
		
		
		public void envoiDatasCloud() {
					//Recupere les sondes une par une			
			for(int numSonde = 0 ;numSonde<listeSondes.size();numSonde++){				
				System.out.println("----------------- Test de la sonde "+ (numSonde) +" -----------------");		
				//-------Envoi les donnees de la sonde 1-------------						
					try {
						
				  		System.out.println("Test du field "+this.numField+" avec la valeur de "+listeSondes.get(numSonde).getTemp());
						sendGet(this.numField,""+listeSondes.get(numSonde).getTemp());
						this.numField++;//=1
						Thread.sleep(1000*15);
						
				  		System.out.println("Test du field "+this.numField+" avec la valeur de "+listeSondes.get(numSonde).getCO2());
						sendGet(this.numField,""+listeSondes.get(numSonde).getCO2());
						this.numField++;//=1
						Thread.sleep(1000*15);

				  		System.out.println("Test du field "+this.numField+" avec la valeur de "+listeSondes.get(numSonde).getCOV());
						sendGet(this.numField,""+listeSondes.get(numSonde).getCOV());
						this.numField++;//=1
						Thread.sleep(1000*15);

				  		System.out.println("Test du field "+this.numField+" avec la valeur de "+listeSondes.get(numSonde).getHygro());
						sendGet(this.numField,""+listeSondes.get(numSonde).getHygro());
						this.numField++;//=1
						Thread.sleep(1000*15);
						
					} catch (Exception e) {
						System.out.println("Erreur envoi donnes sonde : "+numSonde+" field : "+this.numField);
						e.printStackTrace();
					}	
			}

			this.numField =1; //reinitialisation du numero de field
		}

		
		
		
		
	// HTTP GET request
		public void sendGet(int field,String valAEnvoyer) throws Exception {

			String url = "https://api.thingspeak.com/update?api_key=9EMCDDKIZ8IHXO8A&field"+field+"="+valAEnvoyer;		
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");
			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			//print result
			System.out.println(response.toString());
			System.out.println("----------------------------------------------------");

		}
}
