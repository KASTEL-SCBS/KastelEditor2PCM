package edu.kit.kastel.scbs.kastelEditor2PCM;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import edu.kit.kastel.scbs.confidentiality.*;
import edu.kit.kastel.scbs.confidentiality.data.DataFactory;
import edu.kit.kastel.scbs.confidentiality.data.DataIdentifying;
import edu.kit.kastel.scbs.confidentiality.data.DataSet;
import edu.kit.kastel.scbs.confidentiality.repository.ParametersAndDataPair;
import edu.kit.kastel.scbs.confidentiality.repository.RepositoryFactory;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Asset;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.HardGoal;
 

public class ConfidentialitySpecificationGenerator {

	ConfidentialitySpecification spec;
	Resource res;
	
	public void generateConfidentialitySpecification(KastelEditorJsonReader reader, File confidentialityModelFile) {
		String resultPath = confidentialityModelFile.getAbsolutePath();

	
		if(!resultPath.endsWith(".confidentiality")) {
			resultPath += ".confidentiality";
		}
		
		this.res = new XMLResourceImpl(URI.createFileURI(resultPath));
		spec = ConfidentialityFactory.eINSTANCE.createConfidentialitySpecification();
		res.getContents().add(spec);
		
		processHardGoals(reader.getHardGoalsAsCollection());
	}
	
	public boolean saveConfidentialityModel() {
		try {
			res.save(null);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
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
			
			
			DataSet set = findOrGenerateDataSet(hg.getSg().getAsset());
			
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
	
	
}
