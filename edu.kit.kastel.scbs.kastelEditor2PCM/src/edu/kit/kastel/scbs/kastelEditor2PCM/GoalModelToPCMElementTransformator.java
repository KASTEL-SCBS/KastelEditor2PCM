package edu.kit.kastel.scbs.kastelEditor2PCM;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.CompositeComponent;
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
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;

import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Asset;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.BlackBoxMechanism;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Component;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.FunctionalRequirement;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.HardGoal;
import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;
import edu.kit.kastel.scbs.kastelEditor2PCM.KASTELGoalModelReader;



public class GoalModelToPCMElementTransformator {
	Repository repo;
	Resource res;
	
	public void generateRepositoryModel(KASTELGoalModelReader reader, String modelFilePath) {
		

		if(!modelFilePath.endsWith(".repository")) {
			modelFilePath += ".repository";
		}
		
		this.res = new XMLResourceImpl(URI.createFileURI(modelFilePath));
		repo = RepositoryFactory.eINSTANCE.createRepository();
		this.res.getContents().add(repo);
		repo.setEntityName(reader.getModelName());
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
	
	private void extendPalladioComponents(KASTELGoalModelReader reader) {
		Set<Component> components = reader.getServices();
		
		extendComponentsWithFunctionalRequirementInterfaces(components);
		extendComponentsWithHardGoalsAndMechanisms(components);
		extendComponentsSeffsWithBlackBoxMechanisms(components);
		
		
	}
	
	private void generateInterfacesFromKASTELFunctionalRequirements(Collection<FunctionalRequirement> functionalRequirements) {
		
		for(FunctionalRequirement functionalRequirement : functionalRequirements) {
			OperationInterface functionalRequirementInterface = RepositoryFactory.eINSTANCE.createOperationInterface();
			functionalRequirementInterface.setEntityName(StringUtil.trimWhiteSpace(functionalRequirement.getName(), UpperOrLower.UPPER));


			
			for(Asset asset : functionalRequirement.getAssets()) {
				OperationSignature signature = RepositoryFactory.eINSTANCE.createOperationSignature();
				signature.setEntityName(StringUtil.trimWhiteSpace(functionalRequirement.getName(), UpperOrLower.LOWER));
				functionalRequirement.includeOperationSignatureAndAssetMapping(asset, signature.getId());
				
				Parameter parameter = RepositoryFactory.eINSTANCE.createParameter();
				parameter.setParameterName(StringUtil.trimWhiteSpace(asset.getName(), UpperOrLower.LOWER));
				parameter.setDataType__Parameter(generateDataTypeFromAssetWhenNotExisting(asset));
				
				signature.getParameters__OperationSignature().add(parameter);
				functionalRequirementInterface.getSignatures__OperationInterface().add(signature);
			}
			
			functionalRequirement.setId(functionalRequirementInterface.getId());
			repo.getInterfaces__Repository().add(functionalRequirementInterface);
		}
	}
	
	private CompositeDataType generateDataTypeFromAssetWhenNotExisting(Asset asset) {
		for(DataType datatype : repo.getDataTypes__Repository()) {
			if((StringUtil.trimWhiteSpace(((CompositeDataType)datatype).getEntityName(),UpperOrLower.UPPER).equals(StringUtil.trimWhiteSpace(asset.getName(), UpperOrLower.UPPER)))){
				return (CompositeDataType)datatype;
			}
		}
		
		CompositeDataType dataType = RepositoryFactory.eINSTANCE.createCompositeDataType();
		dataType.setEntityName(StringUtil.trimWhiteSpace(asset.getName(), UpperOrLower.UPPER));
		asset.setId(dataType.getId());
		repo.getDataTypes__Repository().add(dataType);
		
		return dataType;
	}
	
	private void generateComponentsFromKASTELServices(Collection<Component> services) {
		for(Component service : services) {
			CompositeComponent component = RepositoryFactory.eINSTANCE.createCompositeComponent();
			
			component.setEntityName(StringUtil.trimWhiteSpace(service.getName(), UpperOrLower.UPPER)+"Composite");
			repo.getComponents__Repository().add(component);
			service.setPcmCompositeComponentId(component.getId());
			
			BasicComponent base = RepositoryFactory.eINSTANCE.createBasicComponent();
			base.setEntityName(StringUtil.trimWhiteSpace(service.getName(), UpperOrLower.UPPER)+"Functionality");
			repo.getComponents__Repository().add(base);
			service.setPcmFunctionalComponentId(base.getId());
			
			AssemblyContext  mainContext =  CompositionFactory.eINSTANCE.createAssemblyContext();
			mainContext.setEntityName(StringUtil.trimWhiteSpace(service.getName(),UpperOrLower.UPPER));
			mainContext.setEncapsulatedComponent__AssemblyContext(base);
			component.getAssemblyContexts__ComposedStructure().add(mainContext);
		}
	}
	
	private void generateBBMComponentsFromKASTELBBM(Collection<BlackBoxMechanism> bbms) {
		
		for(BlackBoxMechanism bbm : bbms) {
			BasicComponent bbmComponent = RepositoryFactory.eINSTANCE.createBasicComponent();
			bbmComponent.setEntityName(StringUtil.trimWhiteSpace(bbm.getName(),UpperOrLower.UPPER));
			repo.getComponents__Repository().add(bbmComponent);
			bbm.setBbmComponentId(bbmComponent.getId());
			
			OperationInterface bbmInterface = RepositoryFactory.eINSTANCE.createOperationInterface();
			bbmInterface.setEntityName(StringUtil.trimWhiteSpace(bbm.getName(),UpperOrLower.UPPER));
			bbm.setPcmInterfaceId(bbmInterface.getId());
			
			for(Asset asset : bbm.getTargetAssets()) {
				OperationSignature signature = RepositoryFactory.eINSTANCE.createOperationSignature();
				signature.setEntityName(StringUtil.trimWhiteSpace(bbm.getName(),UpperOrLower.LOWER));
				Parameter parameter = RepositoryFactory.eINSTANCE.createParameter();
				parameter.setDataType__Parameter(generateDataTypeFromAssetWhenNotExisting(asset));
				parameter.setParameterName(StringUtil.trimWhiteSpace(asset.getName(), UpperOrLower.LOWER));
				signature.getParameters__OperationSignature().add(parameter);
				bbmInterface.getSignatures__OperationInterface().add(signature);
				bbm.addPcmOperationSignatureIdForTargetAsset(signature.getId(), asset);
			}
			
			repo.getInterfaces__Repository().add(bbmInterface);
			
			
			
			OperationProvidedRole bbmOpProvidedRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
			bbmOpProvidedRole.setEntityName(StringUtil.trimWhiteSpace(bbm.getName(),UpperOrLower.LOWER));
			
			bbmOpProvidedRole.setProvidedInterface__OperationProvidedRole(bbmInterface);
			bbmComponent.getProvidedRoles_InterfaceProvidingEntity().add(bbmOpProvidedRole);
		}
	}
	
	private void extendComponentsWithFunctionalRequirementInterfaces(Set<Component> components) {
		
		
		for(Component service : components) {
			
			Collection<OperationInterface> interfacesForComponent = findInterfacesForComponent(service);
			
			CompositeComponent compositeComponent = null;
			BasicComponent baseComponent = null;
			
			for(RepositoryComponent repoComp : repo.getComponents__Repository()) {
				if(repoComp.getId().equals(service.getPcmCompositeComponentId())) {
					compositeComponent = (CompositeComponent)repoComp;
				} else if(repoComp.getId().equals(service.getPcmFunctionalComponentId())) {
					baseComponent = (BasicComponent)repoComp;
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
				generateTemplateSeffForProvidedSignaturesOfPalladioComponent(baseComponent, opInt);
			}
		}
	}
	
	
	private void extendComponentsWithHardGoalsAndMechanisms(Set<Component> components) {
		
		
		for(Component service : components) {
			
			CompositeComponent compositeComponent = null;
			RepositoryComponent functionalComponent = null;
			
			Set<RepositoryComponent> blackBoxComponents = new HashSet<RepositoryComponent>();
			
			for(RepositoryComponent repoComp : repo.getComponents__Repository()) {
				if(repoComp.getId().equals(service.getPcmCompositeComponentId())) {
					compositeComponent = (CompositeComponent)repoComp;
				} else if(repoComp.getId().equals(service.getPcmFunctionalComponentId())) {
					functionalComponent = repoComp;
				} else {
					for(BlackBoxMechanism bbm : service.getBlackBoxMechanisms()) {
						if(repoComp.getId().equals(bbm.getBbmComponentId())) {
							blackBoxComponents.add(repoComp);
						}
					}
				}
				
				
				
				if(compositeComponent != null && functionalComponent != null && blackBoxComponents.size() == service.getBlackBoxMechanisms().size()) {
					break;
				}
			}
			
			for(RepositoryComponent bbCompo : blackBoxComponents) {
				for(ProvidedRole role : bbCompo.getProvidedRoles_InterfaceProvidingEntity()) {
					
					OperationRequiredRole  bbmReqRole = RepositoryFactory.eINSTANCE.createOperationRequiredRole();
					OperationInterface bbmOpInt = ((OperationProvidedRole)role).getProvidedInterface__OperationProvidedRole();
					
					bbmReqRole.setEntityName(StringUtil.trimWhiteSpace(bbmOpInt.getEntityName(),UpperOrLower.LOWER));
					bbmReqRole.setRequiredInterface__OperationRequiredRole(bbmOpInt);
					functionalComponent.getRequiredRoles_InterfaceRequiringEntity().add(bbmReqRole);
					
					AssemblyContext bbCompoContext = CompositionFactory.eINSTANCE.createAssemblyContext();
					bbCompoContext.setEncapsulatedComponent__AssemblyContext(bbCompo);
					bbCompoContext.setEntityName(StringUtil.trimWhiteSpace(bbCompo.getEntityName(),UpperOrLower.UPPER));
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
	
	private Set<OperationInterface> findInterfacesForComponent(Component component){
		Set<OperationInterface> interfacesForComponent = new HashSet<OperationInterface>();
		
		for(Interface opInt : repo.getInterfaces__Repository()) {
			for(FunctionalRequirement functionalRequirement : component.getProvidedFunctionalRequirements()) {
				if(StringUtil.trimWhiteSpace(opInt.getEntityName(),UpperOrLower.UPPER).equals(StringUtil.trimWhiteSpace(functionalRequirement.getName(),UpperOrLower.UPPER))) {
					interfacesForComponent.add((OperationInterface) opInt);
				}
			}
		}
		
		return interfacesForComponent;
	}
	
	private void connectInterfacesToCompositeAndFunctionalComponents(CompositeComponent compositeComponent, RepositoryComponent functionalityComponent, OperationInterface opInterface) {
		
		OperationProvidedRole compositeProvRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
		compositeProvRole.setEntityName(StringUtil.trimWhiteSpace(opInterface.getEntityName(),UpperOrLower.LOWER));
		compositeProvRole.setProvidedInterface__OperationProvidedRole(opInterface);
		compositeComponent.getProvidedRoles_InterfaceProvidingEntity().add(compositeProvRole);
	
		OperationProvidedRole baseProvRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
		baseProvRole.setEntityName(StringUtil.trimWhiteSpace(opInterface.getEntityName(),UpperOrLower.LOWER));
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
	
	private void extendComponentsSeffsWithBlackBoxMechanisms(Set<Component> components) {
		for (Component component : components) {
			fillComponentSeffs(component);
		}
	}
	
	private void fillComponentSeffs(Component component) {
		BasicComponent functionalityComponent = getFunctionalityComponentFromRepository(component);
		
		for(FunctionalRequirement req : component.getProvidedFunctionalRequirements()) {
			//TODO: make for all interfaces
			for(String operationSignatureId : req.getAssetOperationSignatureIdRelation().values()) {
			ResourceDemandingSEFF seff = (ResourceDemandingSEFF)getSeffForFunctionalRequirementAndComponent(operationSignatureId, functionalityComponent);
				fillSeffWithBlackBoxMechanismCalls(component, seff, req);
			}
		}
		
	}
	
	private BasicComponent getFunctionalityComponentFromRepository(Component editorComponent) {
		
		for(RepositoryComponent component : repo.getComponents__Repository()) {
			if(component.getId().equals(editorComponent.getPcmFunctionalComponentId())) {
				return (BasicComponent)component;
			}
		}
		return null;
	}
	
	private ServiceEffectSpecification getSeffForFunctionalRequirementAndComponent(String operationSignatureId, BasicComponent component){
		
		for(ServiceEffectSpecification seff : component.getServiceEffectSpecifications__BasicComponent()) {
			if(seff.getDescribedService__SEFF().getId().equals(operationSignatureId)) {
				return seff;
			}
		}
		return null;
	}
	
	private void fillSeffWithBlackBoxMechanismCalls(Component component, ResourceDemandingSEFF seff, FunctionalRequirement requirement) {
		if(seff == null) {
			return;
		}
		BasicComponent palladioComponent = getFunctionalityComponentFromRepository(component);

		Set<BlackBoxMechanism> bbms = extractBlackBoxMechanismsForRequirement(component, requirement);
		
		for(BlackBoxMechanism bbm : bbms) {
			ExternalCallAction action = SeffFactory.eINSTANCE.createExternalCallAction();
			
			for(RequiredRole role : palladioComponent.getRequiredRoles_InterfaceRequiringEntity()) {
				OperationRequiredRole operationRequiredRole = (OperationRequiredRole) role;
				
				if (StringUtil.trimWhiteSpace(operationRequiredRole.getRequiredInterface__OperationRequiredRole().getEntityName(),UpperOrLower.LOWER).equals(StringUtil.trimWhiteSpace(bbm.getName(),UpperOrLower.LOWER))) {
					action.setRole_ExternalService(operationRequiredRole);
					for(Signature signature : operationRequiredRole.getRequiredInterface__OperationRequiredRole().getSignatures__OperationInterface()) {
						if(StringUtil.trimWhiteSpace(signature.getEntityName(),UpperOrLower.LOWER).equals(StringUtil.trimWhiteSpace(bbm.getName(),UpperOrLower.LOWER))) {
							action.setCalledService_ExternalService((OperationSignature)signature);
							break;
						}
					}
					break;
				}
			}
			
			seff.getSteps_Behaviour().add(action);
			
		}
	}
	
	private Set<BlackBoxMechanism> extractBlackBoxMechanismsForRequirement(Component component, FunctionalRequirement requirement){
		Set<BlackBoxMechanism> bbms = new HashSet<BlackBoxMechanism>();
		for(HardGoal hg : component.getHardGoals()) {
			if(hg.getFunctionalRequirement().equals(requirement) && !(hg.getBBM() == null)) {
				bbms.add(hg.getBBM());
			}
		}
		
		return bbms;
	}
	
	
	public enum UpperOrLower {
		UPPER,
		LOWER,
		KEEP;
	};
	
	
	public Repository getRepositoryModel() {
		return repo;
	}
}
