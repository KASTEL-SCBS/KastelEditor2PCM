package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import com.google.gson.annotations.Expose;

public class SoftGoal {

	@Expose private final String name;
	@Expose private final String concern;
	@Expose private final Asset asset;
	private final Boolean priority;
	
	
	public SoftGoal(String name, String concern, Asset asset ,boolean priority) {
		this.name = name;
		
	
		this.concern = concern;
		this.asset = asset;
		this.priority = priority;
	}
	
	public SoftGoal(String name, String concern, Asset asset) {
		this.name = name;
		
	
		this.concern = concern;
		this.asset = asset;
		this.priority = null;
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
