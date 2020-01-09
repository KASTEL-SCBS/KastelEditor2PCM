package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

public class HardGoal {

	private final String name;
	private final String serviceName;
	private FunctionalRequirement functionalRequirement;
	private BlackBoxMechanism bbm;
	private final String uID;
	private final SoftGoal SoftGoal;

	
	//KASTEL Editor Member Strings for in Hardgoals -- has to be Updated whenever changes are made in the JSON Layout of the KASTEL Editor
	public static final String COMPONENT_ID = "component_id";
	public static final String FUNCTIONAL_REQUIREMENT_ID = "freq_id";
	public static final String SOFTGOAL_ID = "sg_id";
	public static final String UNIQUE_ID = "unique_id";
	
	
	public HardGoal(String name, String serviceName, SoftGoal sg, FunctionalRequirement functionalRequirement, String uniqueId) {
		this.name = name;
		this.serviceName = serviceName;
		this.functionalRequirement = functionalRequirement;
		this.SoftGoal = sg;
		this.uID = uniqueId;
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
