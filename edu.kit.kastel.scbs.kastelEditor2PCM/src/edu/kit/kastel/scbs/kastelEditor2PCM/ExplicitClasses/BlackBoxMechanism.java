package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;

public class BlackBoxMechanism extends AbstractEditorElement {

	private final boolean authenticity;
	private final boolean confidentiality;
	private final boolean integrity;
	private final String extraHg;
	private String pcmInterfaceId;
	private String pcmOperationId;
	
	public BlackBoxMechanism(String name, boolean authenticity, boolean confidentiality, boolean integrity, String extraHg) {
		super.setName(name);
		this.authenticity = authenticity;
		this.confidentiality = confidentiality;
		this.integrity = integrity;
		this.extraHg = extraHg;
	}

	public boolean providesAuthenticity() {
		return authenticity;
	}

	public boolean providesConfidentiality() {
		return confidentiality;
	}

	public boolean providesIntegrity() {
		return integrity;
	}

	public String getExtraHg() {
		return extraHg;
	}

	public String getName() {
		return super.getName();
	}

	public String getBbmComponentId() {
		return super.getPcmElementId();
	}

	public void setBbmComponentId(String bbmComponentId) {
		super.setPcmElementId(bbmComponentId);
	}

	public String getPcmInterfaceId() {
		return pcmInterfaceId;
	}

	public void setPcmInterfaceId(String pcmInterfaceId) {
		this.pcmInterfaceId = pcmInterfaceId;
	}

	public String getPcmOperationId() {
		return pcmOperationId;
	}

	public void setPcmOperationId(String pcmOperationId) {
		this.pcmOperationId = pcmOperationId;
	}
	
	
	
}
