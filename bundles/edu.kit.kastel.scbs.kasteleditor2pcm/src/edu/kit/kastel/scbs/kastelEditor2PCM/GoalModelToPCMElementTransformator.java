package edu.kit.kastel.scbs.kastelEditor2PCM;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.palladiosimulator.pcm.system.SystemFactory;
import org.palladiosimulator.pcm.system.System;

import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Asset;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.BlackBoxMechanism;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.ServiceComponent;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.HardGoal;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.InterfaceMapping;
import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;
import edu.kit.kastel.scbs.kastelEditor2PCM.KASTELGoalModelReader;



public class GoalModelToPCMElementTransformator {
	
	Resource res;
	PCMRepresentation pcm;
	boolean considerAssets;
	
	public void generateRepositoryModel(KASTELGoalModelReader reader, String modelFilePath, boolean considerAssets) {
		
		this.considerAssets = considerAssets;
		if(!modelFilePath.endsWith(".repository")) {
			modelFilePath += ".repository";
		}
		
		this.res = new XMLResourceImpl(URI.createFileURI(modelFilePath));
		Repository repo = RepositoryFactory.eINSTANCE.createRepository();
		this.res.getContents().add(repo);
		repo.setEntityName(reader.getModelName());
		
		pcm = new PCMRepresentation(repo);
		
		generateInterfacesFromKASTELFunctionalRequirements(reader.getFunctionalRequirements());
		generateComponentsFromKASTELServices(reader.getServices());
		generateBBMComponentsFromKASTELBBM(reader.getBlackBoxMechanisms());
		
		extendPalladioComponents(reader);
	}
	
	public boolean savePCMModel() {
		try {
			res.save(null);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean saveSystems(String targetBasePath) {
		
		boolean success = true;
		for(System system : pcm.getSystems()) {
			String completePath = targetBasePath + "/" + system.getEntityName() + ".system";
			
			Resource systemResource = new XMLResourceImpl(URI.createFileURI(completePath));
			systemResource.getContents().add(system);
			
			try {
				systemResource.save(null);
			} catch (IOException e){
				success = false;
			}
		}
		
		return success;
	}
	
	private void extendPalladioComponents(KASTELGoalModelReader reader) {
		Set<ServiceComponent> components = reader.getServices();
		
		extendComponentsWithFunctionalRequirementInterfaces(components);
		extendComponentsWithHardGoalsAndMechanisms(components);
		extendComponentsSeffsWithBlackBoxMechanisms(components);

	}
	
	private void generateInterfacesFromKASTELFunctionalRequirements(Collection<InterfaceMapping> functionalRequirements) {
		
		for(InterfaceMapping functionalRequirement : functionalRequirements) {
			OperationInterface functionalRequirementInterface = RepositoryFactory.eINSTANCE.createOperationInterface();
			functionalRequirementInterface.setEntityName("I" + StringUtil.cleanAndTrim(functionalRequirement.getName(), UpperOrLower.UPPER));


			String operationSignatureName = StringUtil.cleanAndTrim(functionalRequirement.getName(), UpperOrLower.LOWER);
			if(considerAssets) {
			for(Asset asset : functionalRequirement.getAssets()) {
				OperationSignature signature = RepositoryFactory.eINSTANCE.createOperationSignature();
				signature.setEntityName(operationSignatureName);
				functionalRequirement.addOperationSignature(signature.getEntityName(), signature.getId());
				functionalRequirement.addAssetToOperationSignature(asset, signature.getId());
				
				Parameter parameter = RepositoryFactory.eINSTANCE.createParameter();
				parameter.setParameterName(StringUtil.cleanAndTrim(asset.getName(), UpperOrLower.LOWER));
				parameter.setDataType__Parameter(generateDataTypeFromAssetWhenNotExisting(asset));
				signature.getParameters__OperationSignature().add(parameter);
				functionalRequirementInterface.getSignatures__OperationInterface().add(signature);
			}
			} else {
				OperationSignature signature = RepositoryFactory.eINSTANCE.createOperationSignature();
				signature.setEntityName(operationSignatureName);
				functionalRequirement.addOperationSignature(signature.getEntityName(), signature.getId());
				functionalRequirementInterface.getSignatures__OperationInterface().add(signature);
			}
			
			functionalRequirement.setId(functionalRequirementInterface.getId());
			pcm.addInterfaceToRepo(functionalRequirementInterface);
		}
	}
	
	private CompositeDataType generateDataTypeFromAssetWhenNotExisting(Asset asset) {
		for(DataType datatype : pcm.getRepository().getDataTypes__Repository()) {
			if((StringUtil.cleanAndTrim(((CompositeDataType)datatype).getEntityName(),UpperOrLower.UPPER).equals(StringUtil.cleanAndTrim(asset.getName(), UpperOrLower.UPPER)))){
				return (CompositeDataType)datatype;
			}
		}
		
		CompositeDataType dataType = RepositoryFactory.eINSTANCE.createCompositeDataType();
		dataType.setEntityName(StringUtil.cleanAndTrim(asset.getName(), UpperOrLower.UPPER));
		asset.setId(dataType.getId());
		pcm.addDataType(dataType);
		
		return dataType;
	}
	
	private void generateComponentsFromKASTELServices(Collection<ServiceComponent> services) {
		for(ServiceComponent service : services) {
			System system = SystemFactory.eINSTANCE.createSystem();
			
			
			system.setEntityName(StringUtil.cleanAndTrim(service.getName(), UpperOrLower.UPPER));
			service.setSystemId(system.getId());
			
			pcm.addSystem(system);
			
			BasicComponent base = RepositoryFactory.eINSTANCE.createBasicComponent();
			base.setEntityName(StringUtil.cleanAndTrim(service.getName(), UpperOrLower.UPPER));
			pcm.addRepositoryComponent(base);
			service.setComponentId(base.getId());
			
			AssemblyContext  mainContext =  CompositionFactory.eINSTANCE.createAssemblyContext();
			mainContext.setEntityName(StringUtil.cleanAndTrim(service.getName(),UpperOrLower.UPPER));
			mainContext.setEncapsulatedComponent__AssemblyContext(base);
			system.getAssemblyContexts__ComposedStructure().add(mainContext);
		}
	}
	
	private void generateBBMComponentsFromKASTELBBM(Collection<BlackBoxMechanism> bbms) {
		
		for(BlackBoxMechanism bbm : bbms) {
			
			String trimmedAndLeadingUpperBBMName = StringUtil.cleanAndTrim(bbm.getName(),UpperOrLower.UPPER);
			
			BasicComponent bbmComponent = RepositoryFactory.eINSTANCE.createBasicComponent();
			bbmComponent.setEntityName(trimmedAndLeadingUpperBBMName);
			pcm.addRepositoryComponent(bbmComponent);
			bbm.setBbmComponentId(bbmComponent.getId());
			
			OperationInterface bbmInterface = RepositoryFactory.eINSTANCE.createOperationInterface();
			bbmInterface.setEntityName("I"+trimmedAndLeadingUpperBBMName);
			InterfaceMapping mapping = new InterfaceMapping(bbmInterface.getEntityName(), bbmInterface.getId());
			bbm.addPrimaryInterface(mapping);
			
			String blackBoxOperationSignatureName = StringUtil.cleanAndTrim(bbm.getName(),UpperOrLower.LOWER);
			
			if(considerAssets) {
			for(Asset asset : bbm.getEditorAssets()) {
					OperationSignature signature = RepositoryFactory.eINSTANCE.createOperationSignature();
					signature.setEntityName(blackBoxOperationSignatureName);
					Parameter parameter = RepositoryFactory.eINSTANCE.createParameter();
					parameter.setDataType__Parameter(generateDataTypeFromAssetWhenNotExisting(asset));
					mapping.addAsset(asset);
					parameter.setParameterName(StringUtil.cleanAndTrim(asset.getName(), UpperOrLower.LOWER));
					signature.getParameters__OperationSignature().add(parameter);
					bbmInterface.getSignatures__OperationInterface().add(signature);
					bbm.addOperationSignatureToPrimaryInterface(signature.getEntityName(), signature.getId());
					bbm.addAssetToOperationSignatureOfPrimaryInterface(asset, signature.getId());
				}
			} else {
				OperationSignature signature = RepositoryFactory.eINSTANCE.createOperationSignature();
				signature.setEntityName(blackBoxOperationSignatureName);
				bbmInterface.getSignatures__OperationInterface().add(signature);
				bbm.addOperationSignatureToPrimaryInterface(signature.getEntityName(), signature.getId());
			}
			
			pcm.addInterfaceToRepo(bbmInterface);
			
			OperationProvidedRole bbmOpProvidedRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
			bbmOpProvidedRole.setEntityName(StringUtil.cleanAndTrim(bbm.getName(),UpperOrLower.LOWER));
			
			bbmOpProvidedRole.setProvidedInterface__OperationProvidedRole(bbmInterface);
			bbmComponent.getProvidedRoles_InterfaceProvidingEntity().add(bbmOpProvidedRole);
		}
	}
	
	private void extendComponentsWithFunctionalRequirementInterfaces(Set<ServiceComponent> serviceComponents) {
		
		
		for(ServiceComponent serviceComponent : serviceComponents) {
			
			Collection<OperationInterface> interfacesForComponent = pcm.findInterfacesForComponent(serviceComponent);
			
			System targetSystem = pcm.getSystemById(serviceComponent.getSystemId());
			BasicComponent baseComponent = pcm.getBasicComponentInRepositoryById(serviceComponent.getComponentId());
			
			if(targetSystem == null || baseComponent == null) {
				java.lang.System.out.println("Did not found corresponding components, model not complete for: " + serviceComponent.getName());
				continue;
			}
			
			for(OperationInterface opInt : interfacesForComponent) {
				connectInterfacesToSystemAndFunctionalComponent(targetSystem, baseComponent, opInt);
				generateTemplateSeffForProvidedSignaturesOfPalladioComponent(baseComponent, opInt);
			}
		}
	}
	
	
	private void extendComponentsWithHardGoalsAndMechanisms(Set<ServiceComponent> serviceComponents) {
		
		
		for(ServiceComponent serviceComponent : serviceComponents) {
			
			System targetSystem = pcm.getSystemById(serviceComponent.getSystemId());
			RepositoryComponent functionalComponent = null;
			
			Set<RepositoryComponent> blackBoxComponents = new HashSet<RepositoryComponent>();
			
			for(RepositoryComponent repositoryComponent : pcm.getRepository().getComponents__Repository()) {
			
				if(repositoryComponent.getId().equals(serviceComponent.getComponentId())) {
					functionalComponent = repositoryComponent;
				} else {
					for(BlackBoxMechanism bbm : serviceComponent.getBlackBoxMechanisms()) {
						if(repositoryComponent.getId().equals(bbm.getBbmComponentId())) {
							blackBoxComponents.add(repositoryComponent);
						}
					}
				}
			}
				
				if(targetSystem == null || functionalComponent == null || blackBoxComponents.size() != serviceComponent.getBlackBoxMechanisms().size()) {
					break;
				}
			
			
			for(RepositoryComponent bbCompo : blackBoxComponents) {
				for(ProvidedRole role : bbCompo.getProvidedRoles_InterfaceProvidingEntity()) {
					
					OperationRequiredRole  bbmReqRole = RepositoryFactory.eINSTANCE.createOperationRequiredRole();
					OperationInterface bbmOpInt = ((OperationProvidedRole)role).getProvidedInterface__OperationProvidedRole();
					
					bbmReqRole.setEntityName(StringUtil.cleanAndTrim(bbmOpInt.getEntityName(),UpperOrLower.LOWER));
					bbmReqRole.setRequiredInterface__OperationRequiredRole(bbmOpInt);
					functionalComponent.getRequiredRoles_InterfaceRequiringEntity().add(bbmReqRole);
					
					AssemblyContext bbCompoContext = CompositionFactory.eINSTANCE.createAssemblyContext();
					bbCompoContext.setEncapsulatedComponent__AssemblyContext(bbCompo);
					bbCompoContext.setEntityName(StringUtil.cleanAndTrim(bbCompo.getEntityName(),UpperOrLower.UPPER));
					targetSystem.getAssemblyContexts__ComposedStructure().add(bbCompoContext);
					
					AssemblyConnector assemblyConnector = CompositionFactory.eINSTANCE.createAssemblyConnector();
					AssemblyContext functionalContext = null;
					
					for(AssemblyContext context : targetSystem.getAssemblyContexts__ComposedStructure()) {
						if(context.getEncapsulatedComponent__AssemblyContext().getId().equals(functionalComponent.getId())) {
							functionalContext = context;
							break;
						}
					}
					
					assemblyConnector.setProvidingAssemblyContext_AssemblyConnector(bbCompoContext);
					assemblyConnector.setProvidedRole_AssemblyConnector((OperationProvidedRole) role);
					assemblyConnector.setRequiredRole_AssemblyConnector(bbmReqRole);
					assemblyConnector.setRequiringAssemblyContext_AssemblyConnector(functionalContext);
					
					targetSystem.getConnectors__ComposedStructure().add(assemblyConnector);
				}
			}
		}
		
	}
	

	
	private void connectInterfacesToSystemAndFunctionalComponent(System system, RepositoryComponent functionalityComponent, OperationInterface opInterface) {
		
		OperationProvidedRole compositeProvRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
		compositeProvRole.setEntityName(StringUtil.cleanAndTrim(opInterface.getEntityName(),UpperOrLower.LOWER));
		compositeProvRole.setProvidedInterface__OperationProvidedRole(opInterface);
		system.getProvidedRoles_InterfaceProvidingEntity().add(compositeProvRole);
	
		OperationProvidedRole baseProvRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
		baseProvRole.setEntityName(StringUtil.cleanAndTrim(opInterface.getEntityName(),UpperOrLower.LOWER));
		baseProvRole.setProvidedInterface__OperationProvidedRole(opInterface);
		functionalityComponent.getProvidedRoles_InterfaceProvidingEntity().add(baseProvRole);
		
		ProvidedDelegationConnector compositeBaseInterfaceDelegation = CompositionFactory.eINSTANCE.createProvidedDelegationConnector();

		for(AssemblyContext context : system.getAssemblyContexts__ComposedStructure()) {
			if(context.getEncapsulatedComponent__AssemblyContext().getId().equals(functionalityComponent.getId())) {	
				compositeBaseInterfaceDelegation.setAssemblyContext_ProvidedDelegationConnector(context);
				compositeBaseInterfaceDelegation.setInnerProvidedRole_ProvidedDelegationConnector(baseProvRole);
				compositeBaseInterfaceDelegation.setOuterProvidedRole_ProvidedDelegationConnector(compositeProvRole);			
				
				system.getConnectors__ComposedStructure().add(compositeBaseInterfaceDelegation);
				break;
			}
		}		
	}
	
	private void generateTemplateSeffForProvidedSignaturesOfPalladioComponent(BasicComponent functionalityComponent, OperationInterface operationInterface) {
		for(Signature signature : operationInterface.getSignatures__OperationInterface()) {
			ResourceDemandingSEFF seff = SeffFactory.eINSTANCE.createResourceDemandingSEFF();
			seff.setBasicComponent_ServiceEffectSpecification(functionalityComponent);
			seff.setDescribedService__SEFF(signature);
			
			StartAction startAction = SeffFactory.eINSTANCE.createStartAction();
			StopAction stopAction = SeffFactory.eINSTANCE.createStopAction();
			
			stopAction.setPredecessor_AbstractAction(startAction);
			
			seff.getSteps_Behaviour().add(startAction);
			seff.getSteps_Behaviour().add(stopAction);
		
			functionalityComponent.getServiceEffectSpecifications__BasicComponent().add(seff);
		}
	}
	
	private void extendComponentsSeffsWithBlackBoxMechanisms(Set<ServiceComponent> components) {
		for (ServiceComponent component : components) {
			fillComponentSeffs(component);
		}
	}
	
	private void fillComponentSeffs(ServiceComponent component) {
		BasicComponent functionalityComponent = getFunctionalityComponentFromRepository(component);
		
		for(InterfaceMapping req : component.getProvidedFunctionalRequirements()) {
			for(String operationSignatureId : req.getOperationSignatures().keySet()) {
			ResourceDemandingSEFF seff = (ResourceDemandingSEFF)getSeffForFunctionalRequirementAndComponent(operationSignatureId, functionalityComponent);
			fillSeffWithBlackBoxMechanismCalls(component, seff, req);
			}
		}
		
	}
	
	public BasicComponent getFunctionalityComponentFromRepository(ServiceComponent editorComponent) {
		return pcm.getBasicComponentInRepositoryById(editorComponent.getComponentId());
	}
	
	private ServiceEffectSpecification getSeffForFunctionalRequirementAndComponent(String operationSignatureId, BasicComponent component){
		
		for(ServiceEffectSpecification seff : component.getServiceEffectSpecifications__BasicComponent()) {
			if(seff.getDescribedService__SEFF().getId().equals(operationSignatureId)) {
				return seff;
			}
		}
		return null;
	}
	
	private void fillSeffWithBlackBoxMechanismCalls(ServiceComponent component, ResourceDemandingSEFF seff, InterfaceMapping requirement) {
		if(seff == null) {
			return;
		}
		BasicComponent palladioComponent = getFunctionalityComponentFromRepository(component);

		Set<BlackBoxMechanism> bbms = extractBlackBoxMechanismsForRequirement(component, requirement);
		
		for(BlackBoxMechanism bbm : bbms) {
			ExternalCallAction action = SeffFactory.eINSTANCE.createExternalCallAction();
			action.setEntityName("Call"+StringUtil.cleanAndTrim(bbm.getName(), UpperOrLower.UPPER));
			
			for(RequiredRole role : palladioComponent.getRequiredRoles_InterfaceRequiringEntity()) {
				OperationRequiredRole operationRequiredRole = (OperationRequiredRole) role;
				
				if (operationRequiredRole.getRequiredInterface__OperationRequiredRole().getId().equals(bbm.getPrimaryInterfaceId())) {
					action.setRole_ExternalService(operationRequiredRole);
					for(Signature signature : operationRequiredRole.getRequiredInterface__OperationRequiredRole().getSignatures__OperationInterface()) {
						if(StringUtil.cleanAndTrim(signature.getEntityName(),UpperOrLower.LOWER).equals(StringUtil.cleanAndTrim(bbm.getName(),UpperOrLower.LOWER))) {
							action.setCalledService_ExternalService((OperationSignature)signature);
							break;
						}
					}
					break;
				}
			}
			seff.getSteps_Behaviour().add(action);
		}
		
		postprocessFilledSeff_ConnectActionsStartStopRandom(seff);
		
	}
	
	private Set<BlackBoxMechanism> extractBlackBoxMechanismsForRequirement(ServiceComponent component, InterfaceMapping requirement){
		Set<BlackBoxMechanism> bbms = new HashSet<BlackBoxMechanism>();
		for(HardGoal hg : component.getHardGoals()) {
			if(hg.getFunctionalRequirement().equals(requirement) && !(hg.getBBM() == null)) {
				bbms.add(hg.getBBM());
			}
		}
		
		return bbms;
	}
	
	private void postprocessFilledSeff_ConnectActionsStartStopRandom(ResourceDemandingSEFF seff) {
		if(seff == null || (seff.getSteps_Behaviour().size() < 2 )) {
			return;
		}
		StartAction startAction = null;
		StopAction stopAction = null;
		
		for(AbstractAction action : seff.getSteps_Behaviour()) {
			if(action instanceof StartAction) {
				startAction = (StartAction)action;
			} 
			
			if(action instanceof StopAction) {
				stopAction = (StopAction) action;
			}
		}
		
		java.lang.System.out.println("Start: " + startAction.getId());
		java.lang.System.out.println("Stop: " + stopAction.getId());
		
		boolean startActionConnected = false;
		AbstractAction predecessor = null;
		
		for(AbstractAction currentStep : seff.getSteps_Behaviour()) {
			if(!currentStep.getId().equals(startAction.getId()) && !currentStep.getId().equals(stopAction.getId()) && !startActionConnected) {
				startAction.setSuccessor_AbstractAction(currentStep);
				currentStep.setPredecessor_AbstractAction(startAction);
				currentStep.setSuccessor_AbstractAction(stopAction);
				startActionConnected = true;
			} else if(!currentStep.getId().equals(stopAction.getId()) && !currentStep.getId().equals(startAction.getId())) {
				stopAction.setPredecessor_AbstractAction(currentStep);
				currentStep.setSuccessor_AbstractAction(stopAction);
				currentStep.setPredecessor_AbstractAction(predecessor);
				predecessor.setSuccessor_AbstractAction(currentStep);
			}
			predecessor = currentStep;
		}
	}
	
	public enum UpperOrLower {
		UPPER,
		LOWER,
		KEEP;
	};
	
	
	public PCMRepresentation getRepositoryModel() {
		return pcm;
	}
	
	public Optional<OperationInterface> findInterfaceById(String id) {
		for(Interface interf : pcm.getRepository().getInterfaces__Repository()) {
			if(interf instanceof OperationInterface && interf.getId().equals(id)) {
				return Optional.ofNullable((OperationInterface)interf);
			}
		}
		
		return Optional.empty();
	}
}
