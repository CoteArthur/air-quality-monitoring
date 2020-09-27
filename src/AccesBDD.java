import java.sql.* ;
import java.util.ArrayList;

/**
 * Classe chargee de la gestion des requetes SQL et des connexions a la BDD
 * @author Arthur COTE
 *
 */
public class AccesBDD {
	
	public AccesBDD() {
	}
	
	/**
	 * Methode qui lit la table 'paramete_sonde' afin de preparer la liste de DataSonde.
	 * @return Renvoie une liste de DataSonde prete a etre manipulee. Les DataSonde possedent comme champs remplis l'adresse de la sonde et le nom de la salle.
	 */
	public ArrayList<DataSonde> lireConfigSonde(){		
		
		ArrayList<DataSonde> listeSonde = new ArrayList<DataSonde>();
		try {
			//connexion a la BDD 'sqa' en tant que 'pi'
			Class.forName("com.mysql.jdbc.Driver");			
			String url = "jdbc:mysql://172.16.125.36/sqa?useSSL=false"; 
			Connection conn = DriverManager.getConnection(url,"pi","raspberry");
	        
	        //preparation et envois de la requete SQL
	        Statement st = conn.createStatement();
	        ResultSet rs = st.executeQuery("SELECT * FROM parametre_sonde");
	        
	        //on parcours la reponse, pour chaque ligne, on ajoute une nouvelle sonde dans la listeSonde
	        try {
				while ( rs.next() ) {
					listeSonde.add(new DataSonde(rs.getInt("ADRESSE_SONDE"), rs.getString("NOM_SALLE")));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	        
	        //fermeture de la connexion
            conn.close();
	        
		} catch (Exception e) { 
            System.err.println("Got an exception! "); 
            System.err.println(e.getMessage()); 
        }
		
		return listeSonde;
	}
	
	/**
	 * Methode qui range dans la table 'datas' les donnees de chaque sondes dans la liste
	 * @param listeSonde une liste de DataSonde
	 * @param strDate une chaine de caracteres avec la date et l'heure au format "yyyy-MM-dd HH:mm:ss"
	 */
	public void rangerData(ArrayList<DataSonde> listeSonde, String strDate) {
		
		try {
			//pour chaque DataSonde
			for (int j = 0; j < listeSonde.size(); j++) {
				
				//connexion a la BDD 'sqa' en tant que 'pi'
				Class.forName("com.mysql.jdbc.Driver");			
				String url = "jdbc:mysql://172.16.125.36/sqa?useSSL=false"; 
	            Connection conn = DriverManager.getConnection(url,"pi","raspberry"); 
	            
	            //preparation et envoi de la requete SQL
	            Statement st = conn.createStatement();
	            System.out.println("SQL : INSERT INTO datas (ID, DATES, CO2, COV, TEMP, HUM, SALLE) VALUES (NULL, \""+strDate+"\", '"+listeSonde.get(j).getCO2()+"', '"+listeSonde.get(j).getCOV()+"', '"+listeSonde.get(j).getTemp()+"', '"+listeSonde.get(j).getHygro()+"', '"+listeSonde.get(j).getSalle()+"');");
	            st.executeUpdate("INSERT INTO datas (ID, DATES, CO2, COV, TEMP, HUM, SALLE) VALUES (NULL, \""+strDate+"\", '"+listeSonde.get(j).getCO2()+"', '"+listeSonde.get(j).getCOV()+"', '"+listeSonde.get(j).getTemp()+"', '"+listeSonde.get(j).getHygro()+"', '"+listeSonde.get(j).getSalle()+"');");
	            
	            //fermeture de la connexion
	            conn.close();
			}
			
		} catch (Exception e) { 
            System.err.println("Got an exception! "); 
            System.err.println(e.getMessage()); 
        } 
	}
	
	/**
	 * Methode qui lit la table 'datas' et range dans la table 'datas_heures' les valeurs moyennes de la derniere heure
	 * @param salle une chane de caracteres avec le nom de la salle
	 * @param strDate une chaine de caracteres avec la date et l'heure au format "yyyy-MM-dd HH:mm:ss"
	 */
	public void rangerDataHeure(String salle, String strDate) {
		
		//initialisation a zero dans le cas d'un probleme avec la requete
		int avgCO2 = 0;
		int avgCOV = 0;
		int avgTemp = 0;
		int avgHygro = 0;
		
		try {
			//connexion a la BDD 'sqa' en tant que 'pi'
			Class.forName("com.mysql.jdbc.Driver");			
			String url = "jdbc:mysql://172.16.125.36/sqa?useSSL=false"; 
			Connection conn = DriverManager.getConnection(url,"pi","raspberry");
	        
	        //preparation et envois de la requete SQL
	        Statement st = conn.createStatement();
	        System.out.println("SQL : SELECT AVG(CO2), AVG(COV), AVG(TEMP), AVG(HUM) FROM datas WHERE SALLE=\""+salle+"\"AND DATES >= \""+strDate+"\"");
	        ResultSet rs = st.executeQuery("SELECT AVG(CO2), AVG(COV), AVG(TEMP), AVG(HUM) FROM datas WHERE SALLE=\""+salle+"\"AND DATES >= \""+strDate+"\"");

	        //rangement des valeurs dans les variables
	        try {
	        	if (rs.next()){
	        		avgCO2 = Math.round(rs.getInt(1));
	        		avgCOV = Math.round(rs.getInt(2));
	        		avgTemp = Math.round(rs.getInt(3));
	        		avgHygro = Math.round(rs.getInt(4));
	        	}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	        
	        //si avgCO2 a ete change
	        if(avgCO2!=0) {
	        	System.out.println("INSERT INTO datas_heures (ID, DATES, CO2, COV, TEMP, HUM, SALLE) VALUES (NULL, CURRENT_TIMESTAMP, '"+avgCO2+"', '"+avgCOV+"', '"+avgTemp+"', '"+avgHygro+"', '"+salle+"');");	
		        st.executeUpdate("INSERT INTO datas_heures (ID, DATES, CO2, COV, TEMP, HUM, SALLE) VALUES (NULL, CURRENT_TIMESTAMP, '"+avgCO2+"', '"+avgCOV+"', '"+avgTemp+"', '"+avgHygro+"', '"+salle+"');");
	        }
        		        
	        //fermeture de la connexion
            conn.close();
	        
		} catch (Exception e) { 
            System.err.println("Got an exception! "); 
            System.err.println(e.getMessage()); 
        } 
		
		System.out.println("Moyenne de la salle "+salle+": ");
		System.out.println("  Moyenne de CO2: "+avgCO2);
		System.out.println("  Moyenne de COV: "+avgCOV);
		System.out.println("  Moyenne de Temp: "+avgTemp);
		System.out.println("  Moyenne de Hygro: "+avgHygro+"\n");
		
	}
	
	/**
	 * Methode qui lit la table 'datas_heures' et range dans la table 'datas_icone' les valeurs d'icone de la journee
	 * @param salle une chane de caracteres avec le nom de la salle
	 * @param strDate une chaine de caracteres avec la date et l'heure au format "yyyy-MM-dd HH:mm:ss"
	 */
	public void rangerIcone(String salle, String strDate) {
		
		int n0=0, n1=0, n2=0;
		float f1, f2, icone;
		ArrayList<Integer> tabCO2 = new ArrayList<Integer>();
		
		try {
			//connexion a la BDD 'sqa' en tant que 'pi'
			Class.forName("com.mysql.jdbc.Driver");			
			String url = "jdbc:mysql://172.16.125.36/sqa?useSSL=false"; 
			Connection conn = DriverManager.getConnection(url,"pi","raspberry");
		    
		    //preparation et envois de la requete SQL
		    Statement st = conn.createStatement();
		    System.out.println("SQL : SELECT CO2 FROM datas WHERE SALLE=\""+salle+"\"AND DATES >= \""+strDate+"\"");
		    ResultSet rs = st.executeQuery("SELECT CO2 FROM datas WHERE SALLE=\""+salle+"\"AND DATES >= \""+strDate+"\"");
		
		    //on parcours la reponse, pour chaque ligne, on ajoute un Integer dans tabCO2
		    try {
		    	while (rs.next()){
		    		tabCO2.add(rs.getInt(1));
		    	}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		    
		    for (int j = 0; j < tabCO2.size(); j++) { //pour chaque valeur de tabCO2
		    	
		    	if(tabCO2.get(j) < 1000)
		    		n0++;
		    	
		    	if(tabCO2.get(j) >= 1000 && tabCO2.get(j) <= 1700)
		    		n1++;
		    	
		    	if(tabCO2.get(j) > 1700)
		    		n2++;
		    }
		    
		    //calcul de l'icone
		    f1 = (float) n1 / (n0+n1+n2);	//proportion de valeurs comprises entre 1000 et 1700ppm
		    f2 = (float) n2 / (n0+n1+n2);	//proportion de valeurs superieures a 1700ppm
		    icone = (float) ( (2.5/Math.log10(2)) * Math.log10(1+f1+(3*f2)) );
		    icone = (float) (Math.round(icone * 10) / 10); //arrondissement de l'icone a 10^-1
		    
		    System.out.println("\nIcone de la salle "+salle+" :");
		    System.out.println(" n0:"+n0+" n1:"+n1+" n2:"+n2);
		    System.out.println(" f1:"+f1+" f2:"+f2);
		    System.out.println(" icone:"+icone+"\n");
		    
		    System.out.println("SQL : INSERT INTO datas_icone (ID, DATES, ICONE, SALLE) VALUES (NULL, CURRENT_TIMESTAMP, '"+icone+"','"+salle+"');");
		    st.executeUpdate("INSERT INTO datas_icone (ID, DATES, ICONE, SALLE) VALUES (NULL, CURRENT_TIMESTAMP, '"+icone+"','"+salle+"');");
		    
		    //fermeture de la connexion
		    conn.close();
		    
		} catch (Exception e) { 
		    System.err.println("Got an exception! "); 
		    System.err.println(e.getMessage()); 
		} 
	}
	
	public ArrayList<String> lireConfigAff() {
		ResultSet result = null;
		ArrayList<String> array = new ArrayList<String>();
		
	 try {
	      //Connexion a la BDD 'parametre_sonde' en tant de 'pi'
			Class.forName("com.mysql.jdbc.Driver");			
			String url = "jdbc:mysql://172.16.125.36/sqa?useSSL=false"; 
			Connection conn = DriverManager.getConnection(url,"pi","raspberry");
	         
	      Statement state = conn.createStatement();
	      //Requete SQL sur la base de donnee 
	      System.out.println("SELECT `parametre` FROM `parametre_afficheur` WHERE `ON/OFF` ORDER BY `parametre_afficheur`.`ID` ");
	      result = state.executeQuery("SELECT `parametre` FROM `parametre_afficheur` WHERE `ON/OFF` ORDER BY `parametre_afficheur`.`ID` ");
	     
	      try
	      {
	            while (result.next())
	            {
	            	//Remplissage de l'ArrayList  
	                array.add(result.getString("parametre"));
	            }
	      }catch (SQLException queryE)
	        {
	            System.out.println("Erreur de requete : " + queryE);
	        } 
	    
	    
	      result.close();
	      state.close();
	         
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	return array;     
	}
	
}
