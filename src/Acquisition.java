import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Classe chargee de manipuler une liste de DataSonde
 * @author Arthur COTE
 *
 */
public class Acquisition {

	private ArrayList<DataSonde> listeSonde;
	private AccesBDD accesBDD;
	private int minutesDepart = -1;
	
	
	/**
	 * Constructeur de la classe Acquisition
	 * @param accesBDD objet de la classe AccesBDD afin d'effectuer une agregation
	 */
	public Acquisition(AccesBDD accesBDD) {
		this.accesBDD = accesBDD;
	}
	
	/**
	 * Methode qui prepare une liste de DataSonde, la remplie et la range dans la BDD.
	 * Toutes les heures une moyenne des jeux de valeurs sont ranges dans la BDD.
	 * Tous les jours, un valeur d'icone est rangee dans la BDD.
	 * @param date objet de type LocalDateTime
	 */
	public void lectureData(LocalDateTime date){
		//preparation du format de la date
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		//preparation de listeSonde
		listeSonde = new ArrayList<DataSonde>();
		System.out.println("\nLecture de la configuration des sondes");
		listeSonde = accesBDD.lireConfigSonde();
		
		//lecture des moyennes et rangement dans la BDD pour chaque DataSonde
		System.out.println("Il est: "+formatter.format(date)+"\n");
		
		if(date.getMinute() == minutesDepart) {
			System.out.println("-------------------------Une heure est passee--------------------------");
			for (int j = 0; j < listeSonde.size(); j++) {
				accesBDD.rangerDataHeure( listeSonde.get(j).getSalle(), formatter.format(date.minusHours(1)) );
			}
		}
		
		if(minutesDepart == -1) {
			minutesDepart = date.getMinute();
		}
		
		if(date.getHour() == 18 && date.getMinute() == minutesDepart) {
			for (int j = 0; j < listeSonde.size(); j++) {
				accesBDD.rangerIcone(listeSonde.get(j).getSalle(), formatter.format(date.minusHours(12)));
			}
		}
		
		//remplissage de listeSonde
		System.out.println("Remplissage de listeSonde");
		for (int j = 0; j < listeSonde.size(); j++) {
			
			listeSonde.get(j).demandeData();
		    //listeSonde.get(j).afficheVal();
		    
		    //attente d'une seconde pour eviter un surplus sur le flux
		    try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
		
		//rangement des valeurs dans la BDD
		accesBDD.rangerData(listeSonde, formatter.format(date));
	}
	
	/**
	 * Methode qui permet de recuperer la liste de DataSonde
	 * @return Renvoie une liste de DataSonde
	 */
	public ArrayList<DataSonde> getListe(){
		return listeSonde;
	}

}
