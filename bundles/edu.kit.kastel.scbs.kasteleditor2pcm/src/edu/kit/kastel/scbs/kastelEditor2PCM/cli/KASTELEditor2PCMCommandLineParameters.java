package edu.kit.kastel.scbs.kastelEditor2PCM.cli;

public class KASTELEditor2PCMCommandLineParameters {
	private final String editorFilePath;
	private final String generationPath;
	private final boolean useAdversaries;
	private final boolean generateJOANAFlowModel;
	private final boolean useSimpleJoanaflowIds;
	private final boolean considerAssets;
	
	public KASTELEditor2PCMCommandLineParameters(String editorFilePath, String generationPath, boolean useAdversaries, boolean generateJOANAFlowModel, boolean useSimpleJoanaFlowIds, boolean considerAssets) {
		this.editorFilePath = editorFilePath;
		this.generationPath = generationPath;
		this.useAdversaries = useAdversaries;
		this.generateJOANAFlowModel = generateJOANAFlowModel;
		this.useSimpleJoanaflowIds = useSimpleJoanaFlowIds;
		this.considerAssets = considerAssets;
	}
	
	
	//Returns command line parameters element, however, return of isValid returns false
	public KASTELEditor2PCMCommandLineParameters() {
		this.editorFilePath = "";
		this.generationPath = "";
		this.useAdversaries = false;
		this.generateJOANAFlowModel = false;
		this.useSimpleJoanaflowIds = false;
		this.considerAssets = false;
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
	
	public boolean isConsideringAssets() {
		return considerAssets;
	}
	
	public boolean parametersValid() {
		return !editorFilePath.isBlank() && !generationPath.isBlank();
	}
	
	public boolean usingSimpleJoanaFlowIds() {
		return useSimpleJoanaflowIds;
	}
	
	
	
}
