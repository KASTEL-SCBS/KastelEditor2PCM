package edu.kit.kastel.scbs.kastelEditor2PCM.extensions.JOANAFlowModel;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.annotations.Expose;

import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionInformationTrackingElement;

public class JOANAFlowTrackingElement extends ExtensionInformationTrackingElement{
	@Expose private String joanaFlowiId;
	@Expose private String component_source;
	@Expose private String functionalRequirementId_source;
	@Expose private String assetName_source;
	@Expose private Collection<String> bbmIds_sink;
	
	public JOANAFlowTrackingElement(String joanaFlowId, String componentId, String functionalRequirementId_source, String assetName) {
		this.joanaFlowiId = joanaFlowId;
		this.component_source = componentId;
		this.functionalRequirementId_source = functionalRequirementId_source;
		this.assetName_source = assetName;
		bbmIds_sink = new ArrayList<String>();
	}
	
	public void addBBMSinkId(String id) {
		bbmIds_sink.add(id);
	}
}
