package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;

public abstract class EditorElement extends Entity {


	public void setName(String name) {
		super.setName(StringUtil.removeCharAndStringSymbols(name));
	}
}
