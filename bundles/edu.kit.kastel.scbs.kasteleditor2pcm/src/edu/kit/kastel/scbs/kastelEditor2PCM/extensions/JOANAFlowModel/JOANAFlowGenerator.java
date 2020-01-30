package edu.kit.kastel.scbs.kastelEditor2PCM.extensions.JOANAFlowModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

import JOANAFlow4Palladio.EntryPoint;
import JOANAFlow4Palladio.FlowSpecification;
import JOANAFlow4Palladio.JOANAFlow4PalladioFactory;
import JOANAFlow4Palladio.JOANARoot;
import JOANAFlow4Palladio.ParameterSink;
import JOANAFlow4Palladio.Source;
import edu.kit.kastel.scbs.kastelEditor2PCM.KASTELGoalModelReader;
import edu.kit.kastel.scbs.kastelEditor2PCM.PCMRepresentation;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Asset;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.BlackBoxMechanism;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.FunctionalRequirement;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.HardGoal;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.ServiceComponent;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionInformationTrackingElement;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionTracking;
import edu.kit.kastel.scbs.kastelEditor2PCM.extensions.RelatingModelGeneration;

public class JOANAFlowGenerator implements RelatingModelGeneration, ExtensionTracking{

	private JOANARoot flowRoot;
	private Resource res;
	private PCMRepresentation pcm;
	private Collection<ExtensionInformationTrackingElement> trackingElements;
	private boolean useSimpleJoanaflowIds;

	
	public JOANAFlowGenerator(PCMRepresentation pcm, boolean useSimpleJoanaflowIds) {
		this.pcm = pcm;
		this.trackingElements = new ArrayList<ExtensionInformationTrackingElement>();
		this.useSimpleJoanaflowIds = useSimpleJoanaflowIds;
	}
	
	@Override
	public void generateRelatedModel(KASTELGoalModelReader reader, String targetLocation) {
	

		
		if(!targetLocation.endsWith(".joanaflow4palladio")) {
			targetLocation += ".joanaflow4palladio";
		}
		
		this.res = new XMLResourceImpl(URI.createFileURI(targetLocation));
		flowRoot = JOANAFlow4PalladioFactory.eINSTANCE.createJOANARoot();
		this.res.getContents().add(flowRoot);
		
		generateContent(reader);
	}
	
	private void generateContent(KASTELGoalModelReader reader) {
		Collection<ServiceComponent> goalServiceComponents = reader.getServices();
		Integer counter = 0;
		
		for(ServiceComponent compo : goalServiceComponents) {
			for(FunctionalRequirement req : compo.getProvidedFunctionalRequirements()) {
				for(Asset asset : req.assets) {
					
					
					FlowSpecification spec = JOANAFlow4PalladioFactory.eINSTANCE.createFlowSpecification();
					if(useSimpleJoanaflowIds) {
						spec.setId(counter.toString());
						counter++;
					} else {
						spec.setId(EcoreUtil.generateUUID());
					}
					
					RepositoryComponent component = pcm.getFunctionalityComponentFromRepository(compo);
					OperationInterface interf = pcm.getInterfaceFromComponentForProvidedRoles(component, req.getId());
					OperationSignature sign = pcm.getOperationSignatureFromInterface(interf, req.getOperationSignaturePCMIdForAsset(asset));	
					
					
					if(component != null && interf != null && sign != null) {
						EntryPoint ep = JOANAFlow4PalladioFactory.eINSTANCE.createEntryPoint();
						Source source = JOANAFlow4PalladioFactory.eINSTANCE.createSource();
						
						ep.setComponent(component);
						ep.setInterface(interf);
						ep.setSignature(sign);
						
						source.setComponent(component);
						source.setInterface(interf);
						source.setSignature(sign);
						
						spec.setEntrypoint(ep);
						spec.getSource().add(source);
					}
					JOANAFlowTrackingElement trackingElement = new JOANAFlowTrackingElement(spec.getId(),compo.getId(), req.getId(), asset.getEditorAssetName());
					Collection<BlackBoxMechanism> bbms = findBBMsForFlow(compo, req, asset);
					
					for(BlackBoxMechanism bbm : bbms) {
						
						RepositoryComponent bbmComponent = pcm.getBasicComponentInRepositoryById(bbm.getBbmComponentId());
						OperationInterface bbmOpInt = pcm.getInterfaceFromComponentForProvidedRoles(bbmComponent, bbm.getPcmInterfaceId());
						OperationSignature bbmSigForAsset = pcm.getOperationSignatureFromInterface(bbmOpInt, bbm.getPcmOperationSignatureIdForTargetAsset(asset));
						
						if(bbmComponent != null && bbmOpInt != null && bbmSigForAsset != null) {
							ParameterSink sink = JOANAFlow4PalladioFactory.eINSTANCE.createParameterSink();
							
							sink.setComponent(bbmComponent);
							sink.setInterface(bbmOpInt);
							sink.setSignature(bbmSigForAsset);
							
							spec.getSink().add(sink);
							
							trackingElement.addBBMSinkId(bbm.getId());
						}
					}
					
					if(spec.getEntrypoint() != null && spec.getSource().size() != 0 && spec.getSink().size() != 0) {
						flowRoot.getFlowspecification().add(spec);
						trackingElements.add(trackingElement);
					}
				}
			}
		}
	}
	
	private Collection<BlackBoxMechanism> findBBMsForFlow(ServiceComponent component, FunctionalRequirement fuReq, Asset asset){
		Collection<BlackBoxMechanism> bbms = new HashSet<BlackBoxMechanism>();
		
		for(HardGoal hg : component.getHardGoals()) {
			if(hg.getFunctionalRequirement().getId().equals(fuReq.getId()) && hg.getSoftGoal().getAsset().getId().equals(asset.getId())){
				bbms.add(hg.getBBM());
			}
		}
		
		return bbms;
	}

	@Override
	public Resource getRelatedModelResource() {
		return res;
	}
	
	@Override
	public Collection<ExtensionInformationTrackingElement> getExtensionTrackingElement() {
		return trackingElements;
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
