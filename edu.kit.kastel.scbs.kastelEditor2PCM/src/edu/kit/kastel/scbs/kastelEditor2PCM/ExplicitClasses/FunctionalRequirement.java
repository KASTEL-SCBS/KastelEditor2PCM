package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import java.util.HashSet;
import java.util.Set;

public class FunctionalRequirement extends AbstractEditorElement {
	
	public Set<Asset> assets;

	public FunctionalRequirement(String name) {
		super.setName(name);
		this.assets = new HashSet<Asset>();
	}
	
	public String getEditorFunctionalRequirementName() {
		return super.getName();
	}
	
	public void addAsset(Asset asset) {
		assets.add(asset);
	}
	
	public Set<Asset> getAssets(){
		return assets;
	}
}
