package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

public class SoftGoal {

	private final String name;
	private final String concern;
	private final Asset asset;
	private final boolean priority;
	
	
	public SoftGoal(String name, String concern, Asset asset ,boolean priority) {
		this.name = name;
		
	
		this.concern = concern;
		this.asset = asset;
		this.priority = priority;
	}

	public String getConcern() {
		return concern;
	}

	public Asset getAsset() {
		return asset;
	}

	public boolean isPriority() {
		return priority;
	}

	public String getName() {
		return name;
	}
	
	
}
