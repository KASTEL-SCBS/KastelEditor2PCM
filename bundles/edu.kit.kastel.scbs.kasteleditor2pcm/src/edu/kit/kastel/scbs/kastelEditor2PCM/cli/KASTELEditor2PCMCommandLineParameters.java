package edu.kit.kastel.scbs.kastelEditor2PCM.cli;

public class KASTELEditor2PCMCommandLineParameters {
	private String editorFilePath;
	private String generationPath;
	private boolean useAdversaries;
	private boolean generateJOANAFlowModel;
	
	public KASTELEditor2PCMCommandLineParameters(String editorFilePath, String generationPath, boolean useAdversaries, boolean generateJOANAFlowModel) {
		this.editorFilePath = editorFilePath;
		this.generationPath = generationPath;
		this.useAdversaries = useAdversaries;
		this.generateJOANAFlowModel = generateJOANAFlowModel;
	}
	
	
	//Returns command line parameters element, however, return of isValid returns false
	public KASTELEditor2PCMCommandLineParameters() {
		this.editorFilePath = "";
		this.generationPath = "";
		this.useAdversaries = false;
		this.generateJOANAFlowModel = false;
	}
	
	public String getEditorFilePath() {
		return editorFilePath;
	}
	
	public String getGenerationPath() {
		return generationPath;
	}
	
	public boolean isUsingAdversaries() {
		return useAdversaries;
	}
	
	public boolean isGeneratingJOANAFlowModel() {
		return generateJOANAFlowModel;
	}
	
	public boolean parametersValid() {
		if(!editorFilePath.isBlank() && !generationPath.isBlank()) {
			return true;
		} 
		
		System.out.println("At least one Path is blank");
		return false;
	}
	
	
	
}
