package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;
import com.google.gson.annotations.Expose;

import edu.kit.kastel.scbs.kastelEditor2PCM.GoalModelToPCMElementTransformator.UpperOrLower;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.BaseObjectExtendingTrackingEnhanced;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionInformationTrackingElement;
import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;

public class ServiceComponent extends EditorElement{
	
	@Expose private String systemId;
	
	//Direkt Editor Assignments
	@Expose private Collection<HardGoal> hardGoals;
	
	//Palladio Component Containments
	@Expose private Set<BlackBoxMechanism> blackBoxMechanisms;
	@Expose private Set<InterfaceMapping> providedFunctionalRequirements;
	
	
	public ServiceComponent(String name) {
		super.setName(name);
		hardGoals = new ArrayList<HardGoal>();
		blackBoxMechanisms = new HashSet<BlackBoxMechanism>();
		providedFunctionalRequirements = new HashSet<InterfaceMapping>();
	}

	public String getName() {
		return super.getName();
	}
	
	public String getComponentId() {
		return super.getId();
	}
	
	public void setComponentId(String id) {
		super.setId(id);
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public Collection<HardGoal> getHardGoals() {
		return hardGoals;
	}	
	
	public Set<BlackBoxMechanism> getBlackBoxMechanisms(){
		return blackBoxMechanisms;
	}

	public Set<InterfaceMapping> getProvidedFunctionalRequirements() {
		return providedFunctionalRequirements;
	}
	
	public void addFunctionalRequirement(InterfaceMapping requirement) {
		providedFunctionalRequirements.add(requirement);
	}
	
	public Multimap<InterfaceMapping, BlackBoxMechanism> extractFunctionalRequirementAndBlackBoxMechanismCorrespondences(){
		
		Multimap<InterfaceMapping, BlackBoxMechanism> correspondences = ListMultimapBuilder.hashKeys().linkedListValues().build();
		
		for(HardGoal hg : hardGoals) {
			correspondences.put(hg.getFunctionalRequirement(), hg.getBBM());
		}
		return null;
		
	}
	
	public void exchangeFunctionalRequirements(Set<InterfaceMapping> functionalRequirements) {
		Set<InterfaceMapping> temp = new HashSet<InterfaceMapping>();
		
		boolean found = false;
		
		for(InterfaceMapping fuReq : providedFunctionalRequirements) {
			for(InterfaceMapping toExchange : functionalRequirements) {
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
	
	public void findAndExchangeHardGoalFunctionalRequirement(InterfaceMapping toFind, InterfaceMapping toExchange) {
		for(HardGoal hg : hardGoals) {
			if(hg.getFunctionalRequirement().equals(toFind)) {
				hg.setFunctionalRequirement(toExchange);
			}
		}
	}
}
