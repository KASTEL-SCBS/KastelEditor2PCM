package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import edu.kit.kastel.scbs.kastelEditor2PCM.Util.StringUtil;

public abstract class AbstractEditorElement {

	private String pcmElementId;
	private String name;

	public String getPcmElementId() {
		return pcmElementId;
	}

	public void setPcmElementId(String pcmComponentId) {
		this.pcmElementId = pcmComponentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = StringUtil.removeCharAndStringSymbols(name);
	}
}
