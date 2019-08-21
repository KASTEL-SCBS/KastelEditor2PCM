package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;

public class Asset extends AbstractEditorElement {
	
	public Asset(String name) {
		super.setName(name);
	}
	
	public String getEditorAssetName() {
		return super.getName();
	}
	
}
