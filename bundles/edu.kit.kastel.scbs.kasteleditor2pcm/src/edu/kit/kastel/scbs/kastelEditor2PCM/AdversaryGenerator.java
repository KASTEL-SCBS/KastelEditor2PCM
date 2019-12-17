package edu.kit.kastel.scbs.kastelEditor2PCM;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import edu.kit.kastel.scbs.confidentiality.adversary.Adversaries;
import edu.kit.kastel.scbs.confidentiality.adversary.Adversary;
import edu.kit.kastel.scbs.confidentiality.adversary.AdversaryFactory;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Actor;


public class AdversaryGenerator {
	Adversaries adversaries;
	Resource res;
	
	public void generateAdversaryModel(KASTELGoalModelReader_ActorAttackerExtension reader, String modelFilePath) {

		if(!modelFilePath.endsWith(".adversary")) {
			modelFilePath += ".adversary";
		}
		
		this.res = new XMLResourceImpl(URI.createFileURI(modelFilePath));
		this.adversaries = AdversaryFactory.eINSTANCE.createAdversaries();
		this.res.getContents().add(adversaries);
		
		
		createAdversaries(reader.getActors());
		
		
		saveModel();
	}
	
	private void createAdversaries(Iterable<Actor> actors) {
		for(Actor actor : actors) {
			Adversary adversary = AdversaryFactory.eINSTANCE.createAdversary();
			adversary.setName(actor.getName());
			adversaries.getAdversaries().add(adversary);
		}
	}
	
	public boolean saveModel() {
		try {
			res.save(null);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
}
