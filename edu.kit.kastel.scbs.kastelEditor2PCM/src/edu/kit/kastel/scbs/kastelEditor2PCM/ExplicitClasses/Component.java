package edu.kit.kastel.scbs.kastelEditor2PCM.ExplicitClasses;

import java.util.ArrayList;
import java.util.Collection;

public class Component {
	
	private final String editorServiceName;
	private String pcmCompositeComponentId;
	private String pcmFunctionalComponentId;
	private Collection<HardGoal> hardGoals;
	
	public Component(String name) {
		this.editorServiceName = name;
		hardGoals = new ArrayList<HardGoal>();
	}

	public String getName() {
		return editorServiceName;
	}

	public String getPcmCompositeComponentId() {
		return pcmCompositeComponentId;
	}

	public void setPcmCompositeComponentId(String pcmCompositeComponentId) {
		this.pcmCompositeComponentId = pcmCompositeComponentId;
	}

	public String getPcmFunctionalComponentId() {
		return pcmFunctionalComponentId;
	}

	public void setPcmFunctionalComponentId(String pcmFunctionalComponentId) {
		this.pcmFunctionalComponentId = pcmFunctionalComponentId;
	}

	public Collection<HardGoal> getHardGoals() {
		return hardGoals;
	}	
}
