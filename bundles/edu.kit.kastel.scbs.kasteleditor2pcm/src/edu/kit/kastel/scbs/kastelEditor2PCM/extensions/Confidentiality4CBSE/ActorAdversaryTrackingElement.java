package edu.kit.kastel.scbs.kastelEditor2PCM.extensions.Confidentiality4CBSE;

import java.util.HashMap;
import java.util.Map;

import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionInformationTrackingElement;

public class ActorAdversaryTrackingElement extends ExtensionInformationTrackingElement{
	
	private Map<String,String> actorNameToAdversaryIdMap;
	
	public ActorAdversaryTrackingElement() {
		this.actorNameToAdversaryIdMap = new HashMap<String,String>();
	}
	
	public void addActorNameAdversaryIdPair(String actorName, String adversaryId) {
		actorNameToAdversaryIdMap.put(actorName, adversaryId);
	}
	

}
