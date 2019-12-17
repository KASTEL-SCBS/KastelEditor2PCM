package edu.kit.kastel.scbs.kastelEditor2PCM;


import java.io.File;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;


public class KastelEditor2PCM implements IApplication{

	private final static String GOAL_MODEL_FILE_ENDING = ".json";
	private final static String PCM_REPOSITORY_FILE_ENDING = ".repository";
	private final static String TRACKING_FILE_ENDING = ".json";
	private final static String GENERATION_DIRECTORY_NAME = "mod-gen";
	
	
	public static void main(String[] args) {
		try {
			CommandLineParameters cliParameters = getConfiguration(args);
			
			if(cliParameters != null && cliParameters.parametersValid()) {
				processGoalModelingEditorModel(cliParameters);
			} 
		} catch (ParseException e) {
			System.out.println("Error in CLI");
			System.out.println("Error: " + e.getMessage().toString());
		}
		
		System.out.println("Done");
	}
	

	@Override
	public Object start(IApplicationContext context) throws Exception {
		Map<?, ?> contextArgs = context.getArguments();
		String[] appArgs = (String[]) contextArgs.get("application.args");
		
		try {
			CommandLineParameters cliParameters = getConfiguration(appArgs);
			
			if(cliParameters != null && cliParameters.parametersValid()) {
				processGoalModelingEditorModel(cliParameters);
			} else {
				System.out.println("Problem with CLI Parameters");
			}
		} catch (ParseException e) {
			System.out.println("Error in CLI");
			System.out.println("Error: " + e.getMessage().toString());
			System.exit(42);
			return 42;
		}
		System.out.println("Done");
		return IApplication.EXIT_OK;
	}


	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	private static void processGoalModelingEditorModel(CommandLineParameters cliParameters){

		KASTELGoalModelReader reader;
		
		File goalModelFile = new File(URI.createFileURI(cliParameters.getEditorFilePath()).toFileString());
		
		
		
		boolean validGoalModel = false;
		
		if(cliParameters.isUsingAdversaries()) {
			reader = new KASTELGoalModelReader_ActorAttackerExtension();
		} else {
			reader = new KASTELGoalModelReader();
		}
		
		validGoalModel = reader.extractKastelEditorModelFromJson(goalModelFile);
	
		
		if(validGoalModel){
			processReadGoalModel(reader, cliParameters.getGenerationPath(), cliParameters);
		}
	}
	
	

	
	protected static CommandLineParameters getConfiguration(String[] args) throws ParseException{

		Options options = new Options();
		
		Option editorFilePathOption = Option.builder("editorFilePath").longOpt("KASTELEditorModelFile").argName("file").hasArg().desc("File that contains the Editor model").required().build();
		Option generationPathOption = Option.builder("generationPath").argName("generationPath").desc("The Path the Models shall be generated into").hasArg().required().build();
		Option useAdversariesOption = Option.builder("useAdversaries").argName("considerAdversaries").desc("Determines wether adversaries should be considered in input or output").hasArg(false).build();
		Option generateJOANAFlowModelOption = Option.builder("generateJOANAFlowModel").argName("generateJOANAFlowModel").desc("Specifies that a JOANA Flow Model should be generated").hasArg(false).build();
		
		
		
		options.addOption(editorFilePathOption);
		options.addOption(generationPathOption);
		options.addOption(useAdversariesOption);
		options.addOption(generateJOANAFlowModelOption);
		
		CommandLineParser parser = new DefaultParser();
		
		CommandLine  commandLine = parser.parse(options, args);

		String filePath = "";
		String generationPath = "";
		
		if(commandLine.hasOption(editorFilePathOption.getOpt())) {
			filePath = commandLine.getOptionValue(editorFilePathOption.getOpt());
		} else {
			System.out.println("Editor File Path not provided");
			return null;
		}
		
		if(commandLine.hasOption(generationPathOption.getOpt())) {
			generationPath = commandLine.getOptionValue(generationPathOption.getOpt());
		} else {
			System.out.println("Generation Path not provided");
			return null;
		}
		
		 
		boolean useAdversaries = commandLine.hasOption(useAdversariesOption.getOpt());
		boolean generateJOANAFlowModel = commandLine.hasOption(generateJOANAFlowModelOption.getOpt());
		
		return new CommandLineParameters(filePath, generationPath, useAdversaries, generateJOANAFlowModel);
	}	
	
	private static void processReadGoalModel(KASTELGoalModelReader goalModelReader, String projectPath, CommandLineParameters cliParameters){
		
		File genDirectoryFile =  new File(projectPath + "/" + GENERATION_DIRECTORY_NAME);
		
		if(!genDirectoryFile.exists()){
			genDirectoryFile.mkdirs();
		}
		
		String pcmRepositoryModelPath = projectPath + "/" + GENERATION_DIRECTORY_NAME + "/" + goalModelReader.getModelName() + PCM_REPOSITORY_FILE_ENDING;
		GoalModelToPCMElementTransformator goalModelToPCMTransformer = new GoalModelToPCMElementTransformator();
		goalModelToPCMTransformer.generateRepositoryModel(goalModelReader, pcmRepositoryModelPath);
		goalModelToPCMTransformer.savePCMModel();
		goalModelToPCMTransformer.saveSystems(projectPath + "/" + GENERATION_DIRECTORY_NAME);
		
		if(cliParameters.isGeneratingJOANAFlowModel()) {
		String joanaFlowModelPath = projectPath + "/" + GENERATION_DIRECTORY_NAME + "/" + goalModelReader.getModelName() + ".joanaflow4palladio";
		JoanaFlow4PCMGenerator joanaFlow4PCMGenerator = new JoanaFlow4PCMGenerator();
		joanaFlow4PCMGenerator.generateModel(goalModelReader, goalModelToPCMTransformer.getRepositoryModel(), joanaFlowModelPath);
		}
		
		if(cliParameters.isUsingAdversaries()) {
		String adversaryModelPath = projectPath + "/" + GENERATION_DIRECTORY_NAME + "/" +goalModelReader.getModelName() + ".adversary";
		AdversaryGenerator adversaryGenerator = new AdversaryGenerator();
		adversaryGenerator.generateAdversaryModel((KASTELGoalModelReader_ActorAttackerExtension)goalModelReader, adversaryModelPath);
		}
		
		File trackingFile = new File(projectPath  + "/" + GENERATION_DIRECTORY_NAME + "/" + goalModelReader.getModelName() + "_Tracking" + TRACKING_FILE_ENDING);
		goalModelReader.saveTrackingFile(trackingFile);
	}


}
