package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import com.google.gson.annotations.Expose;

public abstract class Entity {
	@Expose private String id;
	@Expose private String name;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
