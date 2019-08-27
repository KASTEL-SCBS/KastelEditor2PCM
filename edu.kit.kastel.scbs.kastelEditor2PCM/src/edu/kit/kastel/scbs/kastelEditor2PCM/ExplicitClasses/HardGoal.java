package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

public class HardGoal {

	private final String name;
	private final String serviceName;
	private FunctionalRequirement functionalRequirement;
	private BlackBoxMechanism bbm;
	private final SoftGoal SoftGoal;

	
	
	public HardGoal(String name, String serviceName, SoftGoal sg, FunctionalRequirement functionalRequirement) {
		this.name = name;
		this.serviceName = serviceName;
		this.functionalRequirement = functionalRequirement;
		this.SoftGoal = sg;
	}

	public SoftGoal getSoftGoal() {
		return SoftGoal;
	}


	public String getName() {
		return name;
	}


	public FunctionalRequirement getFunctionalRequirement() {
		return functionalRequirement;
	}
	
	public void setBlackBoxMechansims(BlackBoxMechanism bbm) {
		if(this.bbm == null)
			this.bbm = bbm;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public BlackBoxMechanism getBBM() {
		return bbm;
	}
	
	public void setFunctionalRequirement(FunctionalRequirement functionalRequirement) {
		this.functionalRequirement = functionalRequirement;
	}
}
