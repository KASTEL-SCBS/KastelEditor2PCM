package edu.kit.kastel.scbs.kastelEditor2PCM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

import JOANAFlow4Palladio.EntryPoint;
import JOANAFlow4Palladio.FlowSpecification;
import JOANAFlow4Palladio.JOANAFlow4PalladioFactory;
import JOANAFlow4Palladio.JOANARoot;
import JOANAFlow4Palladio.ParameterSink;
import JOANAFlow4Palladio.Source;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Asset;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Component;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.FunctionalRequirement;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.HardGoal;



public class JoanaFlow4PCMGenerator {
	Resource res;
	JOANARoot model;
	
	public void generateModel(KASTELGoalModelReader reader, Repository repository, String modelFilePath) {
		
		
		if(!modelFilePath.endsWith(".joanaflow4palladio")) {
			modelFilePath += ".joanaflow4palladio";
		}
		
		this.res = new XMLResourceImpl(URI.createFileURI(modelFilePath));
		model = JOANAFlow4PalladioFactory.eINSTANCE.createJOANARoot();

		buildFlows(reader, repository);
		this.res.getContents().add(model);

		try {
			res.save(null);
		} catch (IOException e) {
			
		}
		
	}

	private void buildFlows(KASTELGoalModelReader reader, Repository repository) {
		
		
		for(Component component : reader.getServices()) {
			BasicComponent bc = getBasicComponentById(component.getPcmFunctionalComponentId(), repository);
			if(bc == null) {
				continue;
			}
			
			for(FunctionalRequirement fReq : component.getProvidedFunctionalRequirements()) {
				for(Asset asset : fReq.getAssets()) {
					FlowSpecification flowSpec = JOANAFlow4PalladioFactory.eINSTANCE.createFlowSpecification();
					flowSpec.setId(EcoreUtil.generateUUID());
					EntryPoint ep = JOANAFlow4PalladioFactory.eINSTANCE.createEntryPoint();
					
					OperationSignature signature = getOperationSignatureById(fReq.getOperationSignaturePCMIdForAsset(asset), repository);
				
					ep.setComponent(bc);
					ep.setSignature(signature);
					
					flowSpec.setEntrypoint(ep);
				
					
					Collection<ParameterSink> sinks = new ArrayList<ParameterSink>();
					for(HardGoal hg : component.getHardGoals()) {
						if(hg.getFunctionalRequirement().equals(fReq) && hg.getSoftGoal().getAsset().equals(asset)) {
							
							if(flowSpec.getSource().isEmpty()) {
							Source source = JOANAFlow4PalladioFactory.eINSTANCE.createSource();
							source.setComponent(bc);
							source.setSignature(signature);
							
							Parameter parameter = getParameterEqualingAssetFromOperationSignature(signature, asset);
								if(parameter != null) {
									source.getParameter().add(parameter);
								}
							
							flowSpec.getSource().add(source);
							}
							
							BasicComponent bbmComponent = getBasicComponentById(hg.getBBM().getPcmElementId(), repository);
							OperationSignature bbmOperationSignature = getOperationSignatureById(hg.getBBM().getPcmOperationSignatureIdForTargetAsset(hg.getSoftGoal().getAsset()), repository);
							
							ParameterSink sink = JOANAFlow4PalladioFactory.eINSTANCE.createParameterSink();
							sink.setComponent(bbmComponent);
							sink.setSignature(bbmOperationSignature);
							
							addSinkForBBMWhenNotExisting(sinks, sink);
						}
					}
					
					for(ParameterSink sink : sinks) {
						flowSpec.getSink().add(sink);
					}
					
					
					if(!flowSpec.getSource().isEmpty() && !flowSpec.getSource().isEmpty()) {
						model.getFlowspecification().add(flowSpec);
					}
				
				}
			}
		}
	}
	
	
	private BasicComponent getBasicComponentById(String id, Repository repository) {
		
		for(RepositoryComponent searching : repository.getComponents__Repository()) {
			if(searching.getId().equals(id)) {
				return (BasicComponent)searching;
			}
		}
		
		return null;
	}
	
	private OperationSignature getOperationSignatureById(String id, Repository repository) {
		for(Interface opInterface : repository.getInterfaces__Repository()) {
			for(OperationSignature signature : ((OperationInterface)opInterface).getSignatures__OperationInterface()) {
				if(signature.getId().equals(id)) {
					return signature;
				}
			}
		}
		return null;
	}
	
	private void addSinkForBBMWhenNotExisting(Collection<ParameterSink> sinks, ParameterSink sink) {
		for(ParameterSink iterated : sinks) {
			if(iterated.getComponent().equals(sink.getComponent()) && iterated.getSignature().equals(sink.getSignature())) {
				return;
			}
		}
		
		sinks.add(sink);
	}
	
	private Parameter getParameterEqualingAssetFromOperationSignature(OperationSignature operationSignature, Asset asset) {
		for(Parameter parameter : operationSignature.getParameters__OperationSignature()) {
			if(parameter.getParameterName().equals(asset.getName())) {
				return parameter;
			}
		}
		
		return null;
	}
	
}
