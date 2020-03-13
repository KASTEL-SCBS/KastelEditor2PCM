package edu.kit.kastel.scbs.kastelEditor2PCM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.system.System;

import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.InterfaceMapping;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.ServiceComponent;
import edu.kit.kastel.scbs.kastelEditor2PCM.GoalModelToPCMElementTransformator.UpperOrLower;
import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;

public class PCMRepresentation {
	Collection<System> systems;
	Repository repo;
	
	public PCMRepresentation(Repository repo) {
		this.repo = repo;
		systems = new ArrayList<System>();
	}
	
	
	
	public Set<OperationInterface> findInterfacesForComponent(ServiceComponent component){
		Set<OperationInterface> interfacesForComponent = new HashSet<OperationInterface>();
		
		for(Interface opInt : repo.getInterfaces__Repository()) {
			for(InterfaceMapping functionalRequirement : component.getProvidedFunctionalRequirements()) {
				if(StringUtil.trimWhiteSpace(opInt.getEntityName(),UpperOrLower.UPPER).equals(StringUtil.trimWhiteSpace(functionalRequirement.getName(),UpperOrLower.UPPER))) {
					interfacesForComponent.add((OperationInterface) opInt);
				}
			}
		}
		
		return interfacesForComponent;
	}
	
	public Collection<System> getSystems(){
		return systems;
	}
	
	public void addInterfaceToRepo(OperationInterface functionalRequirementInterface) {
		repo.getInterfaces__Repository().add(functionalRequirementInterface);
	}
	
	public void addDataType(DataType dataType) {
		repo.getDataTypes__Repository().add(dataType);
	}
	
	public Repository getRepository() {
		return repo;
	}
	
	public void addSystem(System system) {
		systems.add(system);
	}
	
	public void addRepositoryComponent(RepositoryComponent component) {
		repo.getComponents__Repository().add(component);
	}
	
	public System getSystemById(String id) {
		for(System system : systems) {
			if(system.getId().equals(id)) {
				return system;
			} 
		}
		
		return null;
	}
	
	public RepositoryComponent getComponentInRepositoryById(String id) {
		for(RepositoryComponent repositoryComponent : repo.getComponents__Repository()) {
			
			
			if(repositoryComponent.getId().equals(id)) {
				return repositoryComponent;
			}
			
		
		}
		
		return null;
	}
	
	public BasicComponent getBasicComponentInRepositoryById(String id) {
		
		RepositoryComponent component = getComponentInRepositoryById(id);
		
		if(component != null) {
			return (BasicComponent) component;
		}
		
		return null;
		
	}



	public BasicComponent getFunctionalityComponentFromRepository(ServiceComponent compo) {
		return getBasicComponentInRepositoryById(compo.getId());
	}
	
	public System getSystem(ServiceComponent compo) {
		return getSystemById(compo.getSystemId());
	}
	
	public OperationInterface getInterfaceFromComponentForProvidedRoles(RepositoryComponent component, String id) {
		for(ProvidedRole role : component.getProvidedRoles_InterfaceProvidingEntity()) {

			OperationProvidedRole opProvRole; 
			
			if(role instanceof OperationProvidedRole) {
				opProvRole = (OperationProvidedRole) role;
				
				OperationInterface opInt = opProvRole.getProvidedInterface__OperationProvidedRole();
				
				if(opInt.getId().equals(id)) {
					return opInt;
				}
			} 
		}
		
		return null;
	}



	public OperationSignature getOperationSignatureFromInterface(OperationInterface interf,
			String signatureId) {
		for(OperationSignature signature : interf.getSignatures__OperationInterface()) {
			if(signature.getId().equals(signatureId)) {
				return signature;
			}
		}
		return null;
	}
	
}
