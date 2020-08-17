package edu.kit.kastel.scbs.kastelEditor2PCM.extensions.SimplePCMFlowModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

import edu.kit.kastel.scbs.kastelEditor2PCM.KASTELGoalModelReader;
import edu.kit.kastel.scbs.kastelEditor2PCM.PCMRepresentation;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.Asset;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.BlackBoxMechanism;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.HardGoal;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.InterfaceMapping;
import edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses.ServiceComponent;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionInformationTrackingElement;
import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionTracking;
import edu.kit.kastel.scbs.kastelEditor2PCM.extensions.RelatingModelGeneration;
import edu.kit.kastel.scbs.simpleflowmodel4pcm.Flow;
import edu.kit.kastel.scbs.simpleflowmodel4pcm.Flows;
import edu.kit.kastel.scbs.simpleflowmodel4pcm.SignatureSink;
import edu.kit.kastel.scbs.simpleflowmodel4pcm.SignatureSource;
import edu.kit.kastel.scbs.simpleflowmodel4pcm.Simpleflowmodel4pcmFactory;

public class FlowGenerator implements RelatingModelGeneration, ExtensionTracking {

	private Flows flows;
	private Resource res;
	private PCMRepresentation pcm;
	private Collection<ExtensionInformationTrackingElement> trackingElements;
	private boolean useSimpleJoanaflowIds;
	private boolean considerAssets;

	public FlowGenerator(PCMRepresentation pcm, boolean useSimpleJoanaflowIds, boolean considerAssets) {
		this.pcm = pcm;
		this.trackingElements = new ArrayList<ExtensionInformationTrackingElement>();
		this.useSimpleJoanaflowIds = useSimpleJoanaflowIds;
		this.considerAssets = considerAssets;
	}

	@Override
	public void generateRelatedModel(KASTELGoalModelReader reader, String targetLocation) {

		if (!targetLocation.endsWith(".simpleflowmodel4pcm")) {
			targetLocation += ".simpleflowmodel4pcm";
		}

		this.res = new XMLResourceImpl(URI.createFileURI(targetLocation));
		flows = Simpleflowmodel4pcmFactory.eINSTANCE.createFlows();
		this.res.getContents().add(flows);

		generateContent(reader);
	}

	private void generateContent(KASTELGoalModelReader reader) {
		Collection<ServiceComponent> goalServiceComponents = reader.getServices();
		Integer counter = 0;

		for (ServiceComponent compo : goalServiceComponents) {
			for (InterfaceMapping req : compo.getProvidedFunctionalRequirements()) {
				for (Entry<String, String> operationSignature : req.getOperationSignatures().entrySet()) {
					Flow spec =Simpleflowmodel4pcmFactory.eINSTANCE.createFlow();
					if (useSimpleJoanaflowIds) {
						spec.setId(counter.toString());
						counter++;
					} else {
						spec.setId(EcoreUtil.generateUUID());
					}

					RepositoryComponent component = pcm.getFunctionalityComponentFromRepository(compo);
					OperationInterface interf = pcm.getInterfaceFromComponentForProvidedRoles(component, req.getId());
					OperationSignature sign = pcm.getOperationSignatureFromInterface(interf,
							operationSignature.getKey());

					if (component != null && interf != null && sign != null) {
						spec.setSource(createSignatureSource(component, interf, sign));
					}

					Collection<Asset> assets = req.getAssetsForOperationSignature(operationSignature.getKey());
					FlowTrackingElement trackingElement = null;
					Asset asset = null;
					if (considerAssets) {
						if (assets.size() == 1) {
							asset = (Asset) assets.toArray()[0];
							String assetId = asset.getId();
							trackingElement = new FlowTrackingElement(spec.getId(), compo.getId(),
									req.getId(), sign.getId() ,assetId);
						} else {
							continue;
						}
					} else {
						trackingElement = new FlowTrackingElement(spec.getId(), compo.getId(), req.getId(), sign.getId());
					}

					
					Collection<BlackBoxMechanism> bbms;
					
					if(considerAssets) {
					bbms = findBBMsForFlow(compo, req, asset);
					} else {
						bbms = findBBMsForFlow(compo, req);
					}
					
					fillTrackingElementWithBBMSinks(bbms, spec, trackingElement);
				
					if (spec.getSource() != null && spec.getSink().size() != 0) {
						flows.getFlow().add(spec);
						trackingElements.add(trackingElement);
					}
				}
			}
		}
	}

	private Collection<BlackBoxMechanism> findBBMsForFlow(ServiceComponent component, InterfaceMapping fuReq,
			Asset asset) {
		Collection<BlackBoxMechanism> bbms = new HashSet<BlackBoxMechanism>();

		for (HardGoal hg : component.getHardGoals()) {
			if (hg.getFunctionalRequirement().getId().equals(fuReq.getId()) && hg.getSoftGoal().getAsset().getId().equals(asset.getId())) {
				bbms.add(hg.getBBM());
			}
		}

		return bbms;
	}

	private Collection<BlackBoxMechanism> findBBMsForFlow(ServiceComponent component, InterfaceMapping fuReq) {
		Collection<BlackBoxMechanism> bbms = new HashSet<BlackBoxMechanism>();

		for (HardGoal hg : component.getHardGoals()) {
			if (hg.getFunctionalRequirement().getId().equals(fuReq.getId())) {
				bbms.add(hg.getBBM());
			}
		}

		return bbms;
	}
	
	private SignatureSource createSignatureSource(RepositoryComponent component, OperationInterface interf, OperationSignature sign) {
		SignatureSource source =Simpleflowmodel4pcmFactory.eINSTANCE.createSignatureSource();


		source.setComponent(component);
		source.setInterface(interf);
		source.setSignature(sign);
		source.setId(EcoreUtil.generateUUID());
		
		return source;
	}
	
	private SignatureSink createSignatureSink(RepositoryComponent component, OperationInterface interf, OperationSignature sign) {
		SignatureSink sink =Simpleflowmodel4pcmFactory.eINSTANCE.createSignatureSink();

		sink.setComponent(component);
		sink.setInterface(interf);
		sink.setSignature(sign);
		sink.setId(EcoreUtil.generateUUID());
		
		return sink;
	}
	
	private void fillTrackingElementWithBBMSinks(Collection<BlackBoxMechanism> bbms, Flow spec, FlowTrackingElement trackingElement) {
		for (BlackBoxMechanism bbm : bbms) {

			RepositoryComponent bbmComponent = pcm
					.getBasicComponentInRepositoryById(bbm.getBbmComponentId());
			OperationInterface bbmOpInt = pcm.getInterfaceFromComponentForProvidedRoles(bbmComponent,
					bbm.getPrimaryInterfaceId());
			
			OperationSignature bbmSig = null;
			
			if(considerAssets) {
				//TODO implement when assets are of need 
			} else {
				String firstBBMPrimarySignatureId = bbm.getFirstPrimaryInterfaceSignatureId();
				bbmSig = pcm.getOperationSignatureFromInterface(bbmOpInt,
						firstBBMPrimarySignatureId);
			}
			
			if (bbmComponent != null && bbmOpInt != null && bbmSig != null) {
				spec.getSink().add(createSignatureSink(bbmComponent, bbmOpInt, bbmSig));
				trackingElement.addBBMSink(bbmComponent.getId(), bbmOpInt.getId(), bbmSig.getId());
			}
		}

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
