package edu.kit.kastel.scbs.kastelEditor2PCM.extensions.JOANAFlowModel;

import java.util.ArrayList;
import java.util.Collection;

import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionInformationTrackingElement;

public class JOANAFlowTrackingElement extends ExtensionInformationTrackingElement{
	private String joanaFlowiId;
	private String functionalRequirementId_source;
	private Collection<String> bbmIds_sink;
	
	public JOANAFlowTrackingElement(String joanaFlowId, String functionalRequirementId_source) {
		this.joanaFlowiId = joanaFlowId;
		this.functionalRequirementId_source = functionalRequirementId_source;
		bbmIds_sink = new ArrayList<String>();
	}
	
	public void addBBMSinkId(String id) {
		bbmIds_sink.add(id);
	}
}
