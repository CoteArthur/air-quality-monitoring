import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.swing.Timer;

public class Sequenceur implements ActionListener {

	private AccesBDD accesBDD;
	private Acquisition acquisition;
	private Timer timer;
	
	private Mccrypt mccrypt;
	private CThingSpeak thingspeak;
	
	public Sequenceur() {
		// TODO Auto-generated method stub
		accesBDD = new AccesBDD();
		acquisition = new Acquisition(accesBDD);
		
		mccrypt = new Mccrypt(accesBDD);
		
		int attente = 15 * 60 * 1000;
		timer = new Timer(attente, this);
		
		System.out.println("Debut du timer");
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		acquisition.lectureData(LocalDateTime.now(ZoneId.of("Europe/Paris")));
		
		mccrypt.majAff(acquisition.getListe());
		
		thingspeak = new CThingSpeak();
		thingspeak.setListeSondes(acquisition.getListe());
		thingspeak.start();
	}

}
