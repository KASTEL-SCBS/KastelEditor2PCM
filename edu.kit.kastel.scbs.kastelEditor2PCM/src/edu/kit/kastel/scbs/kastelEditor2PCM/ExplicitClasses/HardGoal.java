package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

public class HardGoal {

	private final String name;
	private final String extra_hg;
	private final String extra_hgUsed;
	private final String original_hg;
	private final String serviceName;
	private final String functionalRequirement;
	private final SoftGoal SoftGoal;

	
	
	public HardGoal(String name, String serviceName, SoftGoal sg, String functionalRequirement, String extra_hg, String extra_hgUsed, String original_hg) {
		this.name = name;
		this.serviceName = serviceName;
		this.functionalRequirement = functionalRequirement;
		this.SoftGoal = sg;
		this.extra_hg = extra_hg;
		this.extra_hgUsed = extra_hgUsed;
		this.original_hg = original_hg;

	}


	public String isExtra_hg() {
		return extra_hg;
	}


	public String isExtra_hgUsed() {
		return extra_hgUsed;
	}


	public String isOriginal_hg() {
		return original_hg;
	}


	public String getServiceName() {
		return serviceName;
	}

	public SoftGoal getSg() {
		return SoftGoal;
	}


	public String getName() {
		return name;
	}


	public String getFunctionalRequirement() {
		return functionalRequirement;
	}
	
	
	
}
