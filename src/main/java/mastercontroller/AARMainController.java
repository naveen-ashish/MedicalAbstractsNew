package mastercontroller;


public class AARMainController {
	
	
	public static void main (String[] args) {
		
		io.ReadResources RR = new io.ReadResources();
		
		RR.readAllResources();
		
//		new tell_sentence.TellSentenceGenerator().execute(RR);
		
		new proximity.ProximityFeatureExtractor().execute(RR);
	}
	
}