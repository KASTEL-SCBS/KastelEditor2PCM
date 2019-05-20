package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

public class BlackBoxMechanism {

	private final String name;
	private final boolean authenticity;
	private final boolean confidentiality;
	private final boolean integrity;
	private final String extraHg;
	private String bbmComponentId;
	
	public BlackBoxMechanism(String name, boolean authenticity, boolean confidentiality, boolean integrity, String extraHg) {
		this.name = name;
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
		return name;
	}

	public String getBbmComponentId() {
		return bbmComponentId;
	}

	public void setBbmComponentId(String bbmComponentId) {
		this.bbmComponentId = bbmComponentId;
	}
	
	
	
}
