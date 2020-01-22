package edu.kit.kastel.scbs.kastelEditor2PCMedu.kit.kastel.scbs.kastelEditor2PCM.extensions.Confidentiality4CBSE;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import edu.kit.kastel.scbs.confidentiality.ConfidentialityFactory;
import edu.kit.kastel.scbs.confidentiality.ConfidentialitySpecification;
import edu.kit.kastel.scbs.confidentiality.data.DataFactory;
import edu.kit.kastel.scbs.confidentiality.data.DataIdentifying;
import edu.kit.kastel.scbs.confidentiality.data.DataSet;
import edu.kit.kastel.scbs.confidentiality.repository.ParametersAndDataPair;
import edu.kit.kastel.scbs.confidentiality.repository.RepositoryFactory;
import edu.kit.kastel.scbs.kastelEditor2PCM.KASTELGoalModelReader;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Asset;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.HardGoal;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionInformationTrackingElement;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionTracking;
import edu.kit.kastel.scbs.kastelEditor2PCMedu.kit.kastel.scbs.kastelEditor2PCM.extensions.RelatingModelGeneration;
 

public class ConfidentialitySpecificationGenerator implements RelatingModelGeneration, ExtensionTracking{

	ConfidentialitySpecification spec;
	Resource res;
	
	public DataSet findOrGenerateDataSet(Asset asset) {
		for(DataIdentifying set : spec.getDataIdentifier()) {
			if(((DataSet) set).getName().equals(asset.getName())) {
				return (DataSet)set;
			}
		}
		
		DataSet dataSet = DataFactory.eINSTANCE.createDataSet();
		dataSet.setName(asset.getName());
		spec.getDataIdentifier().add(dataSet);
		return dataSet;
	}
	
	public void processHardGoals(Collection<HardGoal> hardgoals) {
		for(HardGoal hg : hardgoals) {
			
			
			DataSet set = findOrGenerateDataSet(hg.getSoftGoal().getAsset());
			
			ParametersAndDataPair localPair = null;
			
			for(ParametersAndDataPair p : spec.getParametersAndDataPairs()) {
				if(p.getName().equals(hg.getFunctionalRequirement().getName())) {
				  localPair = p;
				  break;
				}	
			}
			
			if(localPair != null) {
				localPair.getDataTargets().add(set);
				continue;
			}
			
			localPair = RepositoryFactory.eINSTANCE.createParametersAndDataPair();
			localPair.setName(hg.getFunctionalRequirement().getName());
			localPair.getDataTargets().add(set);
			localPair.getParameterSources().add("\\call");
			spec.getParametersAndDataPairs().add(localPair);
			
		}
	}
	
	public ConfidentialitySpecification getSpecification() {
		return spec;
	}

	@Override
	public void generateRelatedModel(KASTELGoalModelReader reader, String targetLocation) {

		if(!targetLocation.endsWith(".confidentiality")) {
			targetLocation += ".confidentiality";
		}
		
		this.res = new XMLResourceImpl(URI.createFileURI(targetLocation));
		spec = ConfidentialityFactory.eINSTANCE.createConfidentialitySpecification();
		res.getContents().add(spec);
		
		processHardGoals(reader.getHardGoalsAsCollection());
		
	}

	@Override
	public Resource getRelatedModelResource() {
		return res;
	}

	@Override
	public Collection<ExtensionInformationTrackingElement> getExtensionTrackingElement() {
		// TODO Auto-generated method stub
		return null;
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
