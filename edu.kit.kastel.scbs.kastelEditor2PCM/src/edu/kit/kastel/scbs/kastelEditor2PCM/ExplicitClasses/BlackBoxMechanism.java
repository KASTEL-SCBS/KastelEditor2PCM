package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;

public class BlackBoxMechanism extends AbstractEditorElement {

	private final boolean authenticity;
	private final boolean confidentiality;
	private final boolean integrity;
	private final String extraHg;
	
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
	
	
	
}
