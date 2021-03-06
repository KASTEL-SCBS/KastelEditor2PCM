package edu.kit.kastel.scbs.kastelEditor2PCM.extensions.SimplePCMFlowModel;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.annotations.Expose;

import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.ExtensionInformationTrackingElement;

public class FlowTrackingElement extends ExtensionInformationTrackingElement{
	@Expose private String flowId;
	@Expose private OperationIdentifying source;
	@Expose private String assetId_source;
	@Expose private Collection<OperationIdentifying> sinks;
	
	public FlowTrackingElement(String joanaFlowId, String componentId, String interfaceId_source,String operationId_source ,String assetId) {
		this.flowId = joanaFlowId;
		source = new OperationIdentifying(componentId, interfaceId_source, operationId_source);
		this.assetId_source = assetId;
		sinks = new ArrayList<OperationIdentifying>();
	}
	
	public FlowTrackingElement(String joanaFlowId, String componentId, String interfaceId_source,String operationId_source) {
		this.flowId = joanaFlowId;
		source = new OperationIdentifying(componentId, interfaceId_source, operationId_source);
		sinks = new ArrayList<OperationIdentifying>();
	}
	
	public void addBBMSink(String componentId, String interfaceId, String signatureId) {
		sinks.add(new OperationIdentifying(componentId, interfaceId, signatureId));
	}
	
	public void addBBMSink(String componentId, String interfaceId, String signatureId, String assetId) {
		sinks.add(new OperationIdentifying(componentId, interfaceId, signatureId, assetId));
	}
	
	public class OperationIdentifying  {
		@Expose private String componentId;
		@Expose private String interfaceId;
		@Expose private String signatureId;
		@Expose private String assetId;
		
		public OperationIdentifying(String componentId, String interfaceId, String signatureId ) {
			this.componentId = componentId;
			this.interfaceId = interfaceId;
			this.signatureId = signatureId;
		}
		
		public OperationIdentifying(String componentId, String interfaceId, String signatureId, String assetId) {
			this.componentId = componentId;
			this.interfaceId = interfaceId;
			this.signatureId = signatureId;
			this.assetId = assetId;
		}
	}
}
