package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

public class Asset extends AbstractEditorElement {
	
	public Asset(String name) {
		super.setName(name);
	}
	
	public String getEditorAssetName() {
		return super.getName();
	}
	
	@Override
	public String toString() {
		return super.getName();
	}
	
}
