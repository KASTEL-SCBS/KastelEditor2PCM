package edu.kit.kastel.scbs.kastelEditor2PCM.extensions.Confidentiality4CBSE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import com.google.gson.annotations.Expose;

import edu.kit.kastel.scbs.confidentiality.adversary.Adversaries;
import edu.kit.kastel.scbs.confidentiality.adversary.Adversary;
import edu.kit.kastel.scbs.confidentiality.adversary.AdversaryFactory;
import edu.kit.kastel.scbs.kastelEditor2PCM.KASTELGoalModelReader;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Actor;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionInformationTrackingElement;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionTracking;
import edu.kit.kastel.scbs.kastelEditor2PCM.extensions.RelatingModelGeneration;
import edu.kit.kastel.scbs.kastelEditor2PCM.extensions.ActorAttacker.ActorAttackerExtension;


public class AdversaryGenerator implements RelatingModelGeneration,ExtensionTracking{
	Adversaries adversaries;
	Resource res;
	@Expose Collection<ExtensionInformationTrackingElement> trackingElements;
	ActorAdversaryTrackingElement trackingElement = new ActorAdversaryTrackingElement();
	ActorAttackerExtension extension;
	
	public AdversaryGenerator(ActorAttackerExtension extension) {
		this.extension = extension;
		this.trackingElements = new ArrayList<ExtensionInformationTrackingElement>();
	}
	
	private void createAdversaries(Iterable<Actor> actors) {
		for(Actor actor : actors) {
			Adversary adversary = AdversaryFactory.eINSTANCE.createAdversary();
			adversary.setName(actor.getName());
			adversaries.getAdversaries().add(adversary);
			
			trackingElement.addActorNameAdversaryIdPair(actor.getName(), adversary.getId());
		}
	}

	@Override
	public Collection<ExtensionInformationTrackingElement> getExtensionTrackingElement() {
		trackingElements.add(trackingElement);
		return trackingElements;
	}

	@Override
	public void generateRelatedModel(KASTELGoalModelReader reader, String targetLocation) {
		if(!targetLocation.endsWith(".adversary")) {
			targetLocation += ".adversary";
		}
		
		
		this.res = new XMLResourceImpl(URI.createFileURI(targetLocation));
		this.adversaries = AdversaryFactory.eINSTANCE.createAdversaries();
		this.res.getContents().add(adversaries);
		
		
		createAdversaries(extension.getActors());
		
	}

	@Override
	public Resource getRelatedModelResource() {
		return res;
	}

	@Override
	public boolean saveRelatingModel() {
		try {
			res.save(null);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
}
