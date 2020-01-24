package edu.kit.kastel.scbs.kastelEditor2PCM.extensions;

import java.io.File;

import edu.kit.kastel.scbs.kastelEditor2PCM.Util.IOUtil;

public abstract class GoalModelExtension {
	private final String extensionName;
	
	public GoalModelExtension(String extensionName) {
		this.extensionName = extensionName;
	}
	
	
	public String getExtensionName() {
		return extensionName;
	}
	
	public boolean readExtensionContent(File goalModel) {
		String goalModelStringRepresentation = IOUtil.readFromFile(goalModel);
		
		if(goalModelStringRepresentation.isEmpty()) {
			return false;
		}
		
		processContent(goalModelStringRepresentation);
		
		return true;
		
	}
	
	protected abstract void processContent(String goalModelContent);
}
