package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

public class SoftGoal {

	private final String name;
	private final String concern;
	private final String asset;
	private final boolean priority;
	
	
	public SoftGoal(String name, String cbValue, boolean priority) {
		this.name = name;
		
		String[] cbValues = cbValue.split("\u00a1");
		this.concern = cbValues[0];
		this.asset = cbValues[1];
		this.priority = priority;
	}

	public String getConcern() {
		return concern;
	}

	public String getAsset() {
		return asset;
	}

	public boolean isPriority() {
		return priority;
	}

	public String getName() {
		return name;
	}
	
	
}
