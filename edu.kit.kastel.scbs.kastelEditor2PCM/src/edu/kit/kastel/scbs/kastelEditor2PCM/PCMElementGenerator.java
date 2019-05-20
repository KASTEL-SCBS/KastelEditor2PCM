package edu.kit.kastel.scbs.kastelEditor2PCM;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.CompleteComponentType;
import org.palladiosimulator.pcm.repository.CompositeComponent;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RepositoryFactory;

import com.google.common.collect.Multimap;

import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.BlackBoxMechanism;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Component;



public class PCMElementGenerator {
	Repository repo;
	Resource res;
	
	public void generateRepositoryModel(KastelEditorJsonReader reader, File pcmModelFile) {
		
		String resultPath = pcmModelFile.getAbsolutePath();
		
		if(!resultPath.endsWith(".repository")) {
			resultPath += ".repository";
		}
		
		this.res = new XMLResourceImpl(URI.createFileURI(resultPath));
		repo = RepositoryFactory.eINSTANCE.createRepository();
		this.res.getContents().add(repo);
		
		generateInterfacesFromKASTELFunctionalRequirements(reader.getFunctionalRequirements());
		generateComponentsFromKASTELServices(reader.getServicesAsCollection());
		generateBBMComponentsFromKASTELBBM(reader.getBlackBoxMechanismsAsList());
		
		extendComponentsWithFunctionalRequirementInterfaces(reader.getServiceFuReqRelationships());
		extendComponentsWithHardGoalsAndMechanisms(reader.getServiceBBMMap());
	}
	
	public boolean savePCMModel() {
		try {
			res.save(null);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public void generateInterfacesFromKASTELFunctionalRequirements(Collection<String> functionalRequirements) {
		
		for(String functionalRequirement : functionalRequirements) {
			OperationInterface functionalRequirementInterface = RepositoryFactory.eINSTANCE.createOperationInterface();
			functionalRequirementInterface.setEntityName(functionalRequirement);
			
			OperationSignature signature = RepositoryFactory.eINSTANCE.createOperationSignature();
			signature.setEntityName(functionalRequirement);
			
			functionalRequirementInterface.getSignatures__OperationInterface().add(signature);
			
			repo.getInterfaces__Repository().add(functionalRequirementInterface);
		}
	}
	
	public void generateComponentsFromKASTELServices(Collection<Component> services) {
		for(Component service : services) {
			CompositeComponent component = RepositoryFactory.eINSTANCE.createCompositeComponent();
			
			component.setEntityName(service.getName()+" Composite");
			repo.getComponents__Repository().add(component);
			service.setPcmCompositeComponentId(component.getId());
			
			//create representation for the functionality, use abstraction (CompleteComponentType) to allow basic or composed instanciation. 
			CompleteComponentType base = RepositoryFactory.eINSTANCE.createCompleteComponentType();
			base.setEntityName(service.getName()+" Functionality");
			repo.getComponents__Repository().add(base);
			service.setPcmFunctionalComponentId(base.getId());
			
			AssemblyContext  mainContext =  CompositionFactory.eINSTANCE.createAssemblyContext();
			mainContext.setEntityName(service.getName());
			mainContext.setEncapsulatedComponent__AssemblyContext(base);
			component.getAssemblyContexts__ComposedStructure().add(mainContext);
		}
	}
	
	public void generateComponentsFromKASTELServicesFromMap(Map<String, Component> services) {
		for(Component service : services.values()) {
			CompositeComponent component = RepositoryFactory.eINSTANCE.createCompositeComponent();
			
			component.setEntityName(service.getName()+" Composite");
			repo.getComponents__Repository().add(component);
			service.setPcmCompositeComponentId(component.getId());
			
			//create representation for the functionality, use abstraction (CompleteComponentType) to allow basic or composed instanciation. 
			CompleteComponentType base = RepositoryFactory.eINSTANCE.createCompleteComponentType();
			base.setEntityName(service.getName()+" Functionality");
			repo.getComponents__Repository().add(base);
			service.setPcmFunctionalComponentId(base.getId());
			
			AssemblyContext  mainContext =  CompositionFactory.eINSTANCE.createAssemblyContext();
			mainContext.setEntityName(service.getName());
			mainContext.setEncapsulatedComponent__AssemblyContext(base);
			component.getAssemblyContexts__ComposedStructure().add(mainContext);
		}
	}
	
	public void generateBBMComponentsFromKASTELBBM(Collection<BlackBoxMechanism> bbms) {
		
		for(BlackBoxMechanism bbm : bbms) {
			CompleteComponentType bbmComponent = RepositoryFactory.eINSTANCE.createCompleteComponentType();
			bbmComponent.setEntityName(bbm.getName());
			repo.getComponents__Repository().add(bbmComponent);
			bbm.setBbmComponentId(bbmComponent.getId());
			
			OperationInterface bbmInterface = RepositoryFactory.eINSTANCE.createOperationInterface();
			bbmInterface.setEntityName(bbm.getName());
			repo.getInterfaces__Repository().add(bbmInterface);
			
			OperationProvidedRole bbmOpProvidedRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
			bbmOpProvidedRole.setEntityName("Provided_" + bbm.getName());
			
			bbmOpProvidedRole.setProvidedInterface__OperationProvidedRole(bbmInterface);
			bbmComponent.getProvidedRoles_InterfaceProvidingEntity().add(bbmOpProvidedRole);
		}
	}
	
	public void extendComponentsWithFunctionalRequirementInterfaces(Multimap<Component, String> serviceFuReqRelationships) {
		
		Set<Component> services = serviceFuReqRelationships.keySet();
		
		for(Component service : services) {
			
			Collection<OperationInterface> interfacesForComponent = findInterfacesForComponent(service, serviceFuReqRelationships);
			
			CompositeComponent compositeComponent = null;
			RepositoryComponent baseComponent = null;
			
			for(RepositoryComponent repoComp : repo.getComponents__Repository()) {
				if(repoComp.getId().equals(service.getPcmCompositeComponentId())) {
					compositeComponent = (CompositeComponent)repoComp;
				} else if(repoComp.getId().equals(service.getPcmFunctionalComponentId())) {
					baseComponent = repoComp;
				}
				
				if(compositeComponent != null && baseComponent != null) {
					break;
				}
			}
			
			if(compositeComponent == null || baseComponent == null) {
				System.out.println("Did not found corresponding components, model not complete for: " + service.getName());
				continue;
			}
			
			for(OperationInterface opInt : interfacesForComponent) {
				connectInterfacesToCompositeAndFunctionalComponents(compositeComponent, baseComponent, opInt);
			}
		}
	}
	
	
	public void extendComponentsWithHardGoalsAndMechanisms(Multimap<Component, BlackBoxMechanism> serivceBlackboxMechanisms) {
		
		
		for(Component service : serivceBlackboxMechanisms.keySet()) {
			
			CompositeComponent compositeComponent = null;
			RepositoryComponent functionalComponent = null;
			
			ArrayList<RepositoryComponent> blackBoxComponents = new ArrayList<RepositoryComponent>();
			
			for(RepositoryComponent repoComp : repo.getComponents__Repository()) {
				if(repoComp.getId().equals(service.getPcmCompositeComponentId())) {
					compositeComponent = (CompositeComponent)repoComp;
				} else if(repoComp.getId().equals(service.getPcmFunctionalComponentId())) {
					functionalComponent = repoComp;
				} else {
					for(BlackBoxMechanism bbm : serivceBlackboxMechanisms.get(service)) {
						if(repoComp.getId().equals(bbm.getBbmComponentId())) {
							blackBoxComponents.add(repoComp);
						}
					}
				}
				
				
				
				if(compositeComponent != null && functionalComponent != null && blackBoxComponents.size() == serivceBlackboxMechanisms.get(service).size()) {
					break;
				}
			}
			
			for(RepositoryComponent bbCompo : blackBoxComponents) {
				for(ProvidedRole role : bbCompo.getProvidedRoles_InterfaceProvidingEntity()) {
					
					OperationRequiredRole  bbmReqRole = RepositoryFactory.eINSTANCE.createOperationRequiredRole();
					OperationInterface bbmOpInt = ((OperationProvidedRole)role).getProvidedInterface__OperationProvidedRole();
					
					bbmReqRole.setEntityName("Required_" + bbmOpInt.getEntityName());
					bbmReqRole.setRequiredInterface__OperationRequiredRole(bbmOpInt);
					functionalComponent.getRequiredRoles_InterfaceRequiringEntity().add(bbmReqRole);
					
					AssemblyContext bbCompoContext = CompositionFactory.eINSTANCE.createAssemblyContext();
					bbCompoContext.setEncapsulatedComponent__AssemblyContext(bbCompo);
					bbCompoContext.setEntityName(bbCompo.getEntityName());
					compositeComponent.getAssemblyContexts__ComposedStructure().add(bbCompoContext);
					
					AssemblyConnector assemblyConnector = CompositionFactory.eINSTANCE.createAssemblyConnector();
					AssemblyContext functionalContext = null;
					
					for(AssemblyContext context : compositeComponent.getAssemblyContexts__ComposedStructure()) {
						if(context.getEncapsulatedComponent__AssemblyContext().getId().equals(functionalComponent.getId())) {
							functionalContext = context;
							break;
						}
					}
					
					assemblyConnector.setProvidingAssemblyContext_AssemblyConnector(bbCompoContext);
					assemblyConnector.setProvidedRole_AssemblyConnector((OperationProvidedRole) role);
					assemblyConnector.setRequiredRole_AssemblyConnector(bbmReqRole);
					assemblyConnector.setRequiringAssemblyContext_AssemblyConnector(functionalContext);
					
					compositeComponent.getConnectors__ComposedStructure().add(assemblyConnector);
				}
			}
		}
		
	}
	
	private Collection<OperationInterface> findInterfacesForComponent(Component component, Multimap<Component, String> serviceFuReqRelationships){
		Collection<OperationInterface> interfacesForComponent = new ArrayList<OperationInterface>();
		
		for(Interface opInt : repo.getInterfaces__Repository()) {
			for(String functionalRequirement : serviceFuReqRelationships.get(component)) {
				if(opInt.getEntityName().equals(functionalRequirement)) {
					interfacesForComponent.add((OperationInterface) opInt);
				}
			}
		}
		
		return interfacesForComponent;
	}
	
	private void connectInterfacesToCompositeAndFunctionalComponents(CompositeComponent compositeComponent, RepositoryComponent functionalityComponent, OperationInterface opInterface) {
		
		OperationProvidedRole compositeProvRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
		compositeProvRole.setEntityName("Provided_" + opInterface.getEntityName());
		compositeProvRole.setProvidedInterface__OperationProvidedRole(opInterface);
		compositeComponent.getProvidedRoles_InterfaceProvidingEntity().add(compositeProvRole);
	
		OperationProvidedRole baseProvRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
		baseProvRole.setEntityName("Provided_" + opInterface.getEntityName());
		baseProvRole.setProvidedInterface__OperationProvidedRole(opInterface);
		functionalityComponent.getProvidedRoles_InterfaceProvidingEntity().add(baseProvRole);
		
		ProvidedDelegationConnector compositeBaseInterfaceDelegation = CompositionFactory.eINSTANCE.createProvidedDelegationConnector();

		for(AssemblyContext context : compositeComponent.getAssemblyContexts__ComposedStructure()) {
			if(context.getEncapsulatedComponent__AssemblyContext().getId().equals(functionalityComponent.getId())) {	
				compositeBaseInterfaceDelegation.setAssemblyContext_ProvidedDelegationConnector(context);
				compositeBaseInterfaceDelegation.setInnerProvidedRole_ProvidedDelegationConnector(baseProvRole);
				compositeBaseInterfaceDelegation.setOuterProvidedRole_ProvidedDelegationConnector(compositeProvRole);			
				
				compositeComponent.getConnectors__ComposedStructure().add(compositeBaseInterfaceDelegation);
				break;
			}
		}
	}
}
