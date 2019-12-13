package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;

import edu.kit.kastel.scbs.kastelEditor2PCM.GoalModelToPCMElementTransformator.UpperOrLower;
import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;

public class ServiceComponent extends EditorElement {
	
	private String systemId;
	
	//Direkt Editor Assignments
	private Collection<HardGoal> hardGoals;
	
	//Palladio Component Containments
	private Set<BlackBoxMechanism> blackBoxMechanisms;
	private Set<FunctionalRequirement> providedFunctionalRequirements;
	
	public ServiceComponent(String name) {
		super.setName(name);
		hardGoals = new ArrayList<HardGoal>();
		blackBoxMechanisms = new HashSet<BlackBoxMechanism>();
		providedFunctionalRequirements = new HashSet<FunctionalRequirement>();
	}

	public String getName() {
		return super.getName();
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getPcmFunctionalComponentId() {
		return super.getId();
	}

	public void setPcmFunctionalComponentId(String pcmFunctionalComponentId) {
		super.setId(pcmFunctionalComponentId);
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
	
	public void exchangeFunctionalRequirements(Set<FunctionalRequirement> functionalRequirements) {
		Set<FunctionalRequirement> temp = new HashSet<FunctionalRequirement>();
		
		boolean found = false;
		
		for(FunctionalRequirement fuReq : providedFunctionalRequirements) {
			for(FunctionalRequirement toExchange : functionalRequirements) {
				if(fuReq.getName().equals(toExchange.getName()) || toExchange.getName().equals(fuReq.getName() + "_" + this.getName())) {
					found = true;
					temp.add(toExchange);
					findAndExchangeHardGoalFunctionalRequirement(fuReq, toExchange);
					break;
				}
			}
			
			if(!found) {
				temp.add(fuReq);
			}
			
			found = false;
		}
		
		providedFunctionalRequirements = temp;
	}
	
	public void findAndExchangeHardGoalFunctionalRequirement(FunctionalRequirement toFind, FunctionalRequirement toExchange) {
		for(HardGoal hg : hardGoals) {
			if(hg.getFunctionalRequirement().equals(toFind)) {
				hg.setFunctionalRequirement(toExchange);
			}
		}
	}
	
}
