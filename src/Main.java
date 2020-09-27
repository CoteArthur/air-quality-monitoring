import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		Sequenceur seq = new Sequenceur();
		
		//Attente d'un input
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
