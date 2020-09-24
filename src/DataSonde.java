import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortFactoryJSSC;
import com.intelligt.modbus.jlibmodbus.serial.SerialUtils;

import jssc.SerialPortList;

/**
 * Classe contenant les donnees en rapport avec la sonde E4000 et les methodes permettant de les acquerir et les manipuler
 * @author Arthur COTE
 *
 */
public class DataSonde {
	
	private int adresse;
	private String salle;
	private int valCO2 = 0;
	private int valCOV = 0;
	private float valTemp = 0;
	private int valHygro = 0;
	
	/**
	 * Constructeur de la classe DataSonde
	 * @param adresse : un entier correspondant a la sonde souhaitee
	 * @param salle : une chaine de caracteres correspondant au nom de la salle dans laquelle se trouve la sonde
	 */
	public DataSonde(int adresse, String salle) {
		this.adresse = adresse;
		this.salle = salle;
	}
	
	/**
	 * Methode permettant de demander et remplir les donnees de la sonde via Modbus
	 */
	public void demandeData() {
		
		//preparation de la connexion
		SerialParameters sp = new SerialParameters();
        int[] registerValues = null;
        try {
            String[] dev_list = SerialPortList.getPortNames();
            // si il y a au moins un port serie
            if (dev_list.length > 0) {
                sp.setDevice(dev_list[1]);
                sp.setBaudRate(SerialPort.BaudRate.BAUD_RATE_9600);
                sp.setDataBits(8);
                sp.setParity(SerialPort.Parity.NONE);
                sp.setStopBits(1);
                
                SerialUtils.setSerialPortFactory(new SerialPortFactoryJSSC());
                ModbusMaster m = ModbusMasterFactory.createModbusMasterRTU(sp);
                m.connect();

                int slaveID = adresse;
                int offset = 0;
                int quantity = 6;
                
                try {
                	//demade des donnees
                    registerValues = m.readHoldingRegisters(slaveID, offset, quantity);
                    
                    //rangement des donnees dans les attributs
                    valCO2 = registerValues[2];
            		valCOV = registerValues[3];
                    valTemp = ((float)registerValues[4]/10);
                    valHygro = registerValues[5];
                    
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        m.disconnect();
                    } catch (ModbusIOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Methode permettant d'afficher les valeurs des attribut de l'objet
	 */
	public void afficheVal() {
		System.out.println("\nValeurs la sonde "+ salle +" :");
		System.out.println("Taux de CO2: "+ valCO2 +" ppm");
		System.out.println("Taux de COV: "+ valCOV +" ppm");
        System.out.println("Temperature: "+ valTemp +" C");
        System.out.println("Hygrometrie: "+ valHygro +" %\n");
	}
	
	
	/**
	 * Getteur de l'adresse
	 * @return Renvoi de l'adresse de la sonde
	 */
	public int getAdresse() {
		return adresse;
	}
	
	/**
	 * Getter de valCO2
	 * @return Renvoi du taux de CO2
	 */
	public int getCO2() {
		return valCO2;
	}
	
	/**
	 * Getteur de valCOV
	 * @return Renvoi du taux de COV
	 */
	public int getCOV() {
		return valCOV;
	}
	
	/**
	 * Getteur de valTemp
	 * @return Renvoi de la temperature
	 */
	public float getTemp() {
		return valTemp;
	}
	
	/**
	 * Getteur de valHygro
	 * @return Renvoi de l'hygrometrie
	 */
	public int getHygro() {
		return valHygro;
	}
	
	/**
	 * Getteur de salle
	 * @return Renvoi du nom de la salle
	 */
	public String getSalle() {
		return salle;
	}
}
