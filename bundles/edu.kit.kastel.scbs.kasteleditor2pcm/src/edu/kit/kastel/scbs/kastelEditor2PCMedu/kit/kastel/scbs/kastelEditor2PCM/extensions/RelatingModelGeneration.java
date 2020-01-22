package edu.kit.kastel.scbs.kastelEditor2PCMedu.kit.kastel.scbs.kastelEditor2PCM.extensions;

import java.io.File;
import java.util.Collection;

import org.eclipse.emf.ecore.resource.Resource;

import edu.kit.kastel.scbs.kastelEditor2PCM.KASTELGoalModelReader;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionInformationTrackingElement;

public interface RelatingModelGeneration {

	void generateRelatedModel(KASTELGoalModelReader reader, String targetLocation);
	Resource getRelatedModelResource();
	boolean saveRelatingModel();
	
}
