package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;

import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;

public class Component extends AbstractEditorElement {
	
	private String pcmCompositeComponentId;
	
	//Direkt Editor Assignments
	private Collection<HardGoal> hardGoals;
	
	//Palladio Component Containments
	private Set<BlackBoxMechanism> blackBoxMechanisms;
	private Set<FunctionalRequirement> providedFunctionalRequirements;
	
	public Component(String name) {
		super.setName(name);
		hardGoals = new ArrayList<HardGoal>();
		blackBoxMechanisms = new HashSet<BlackBoxMechanism>();
		providedFunctionalRequirements = new HashSet<FunctionalRequirement>();
	}

	public String getName() {
		return super.getName();
	}

	public String getPcmCompositeComponentId() {
		return pcmCompositeComponentId;
	}

	public void setPcmCompositeComponentId(String pcmCompositeComponentId) {
		this.pcmCompositeComponentId = pcmCompositeComponentId;
	}

	public String getPcmFunctionalComponentId() {
		return super.getPcmElementId();
	}

	public void setPcmFunctionalComponentId(String pcmFunctionalComponentId) {
		super.setPcmElementId(pcmFunctionalComponentId);
	}

	public Collection<HardGoal> getHardGoals() {
		return hardGoals;
	}	
	
	public Set<BlackBoxMechanism> getBlackBoxMechanisms(){
		return blackBoxMechanisms;
	}

	public Set<FunctionalRequirement> getProvidedFunctionalRequirements() {
		return providedFunctionalRequirements;
	}
	
	public void addFunctionalRequirement(FunctionalRequirement requirement) {
		providedFunctionalRequirements.add(requirement);
	}
	
	public Multimap<FunctionalRequirement, BlackBoxMechanism> extractFunctionalRequirementAndBlackBoxMechanismCorrespondences(){
		
		Multimap<FunctionalRequirement, BlackBoxMechanism> correspondences = ListMultimapBuilder.hashKeys().linkedListValues().build();
		
		for(HardGoal hg : hardGoals) {
			correspondences.put(hg.getFunctionalRequirement(), hg.getBBM());
		}
		
		
		return null;
		
	}
	
}
