package edu.kit.kastel.scbs.kastelEditor2PCM;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import edu.kit.kastel.scbs.kastelEditor2PCM.Tracking.TrackingGenerator;
import edu.kit.kastel.scbs.kastelEditor2PCM.cli.KASTELEditor2PCMCLI;
import edu.kit.kastel.scbs.kastelEditor2PCM.cli.KASTELEditor2PCMCommandLineParameters;
import edu.kit.kastel.scbs.kastelEditor2PCMedu.kit.kastel.scbs.kastelEditor2PCM.extensions.GoalModelExtension;
import edu.kit.kastel.scbs.kastelEditor2PCMedu.kit.kastel.scbs.kastelEditor2PCM.extensions.RelatingModelGeneration;
import edu.kit.kastel.scbs.kastelEditor2PCMedu.kit.kastel.scbs.kastelEditor2PCM.extensions.ActorAttacker.ActorAttackerExtension;
import edu.kit.kastel.scbs.kastelEditor2PCMedu.kit.kastel.scbs.kastelEditor2PCM.extensions.Confidentiality4CBSE.AdversaryGenerator;
import edu.kit.kastel.scbs.kastelEditor2PCMedu.kit.kastel.scbs.kastelEditor2PCM.extensions.JOANAFlowModel.JOANAFlowGenerator;


public class KastelEditor2PCM implements IApplication{

	private final static String GOAL_MODEL_FILE_ENDING = ".json";
	private final static String PCM_REPOSITORY_FILE_ENDING = ".repository";
	private final static String TRACKING_FILE_ENDING = ".json";
	private final static String GENERATION_DIRECTORY_NAME = "mod-gen";
	
	private Collection<GoalModelExtension> goalModelExtensions;
	private Collection<RelatingModelGeneration> relatedModelGenerations;
	
	
	public static void main(String[] args) {
			
			KASTELEditor2PCMCommandLineParameters cliParameters =  new KASTELEditor2PCMCLI().interrogateCommandLine(args);
			
			if(cliParameters.parametersValid()) {
				processGoalModelingEditorModel(cliParameters);
			}  else {
				System.out.println("Error in CLI");
			}
		
		
		System.out.println("Done");
	}
	

	@Override
	public Object start(IApplicationContext context) throws Exception {
		Map<?, ?> contextArgs = context.getArguments();
		String[] appArgs = (String[]) contextArgs.get("application.args");
		
		System.out.println(System.getProperty("user.dir"));
			KASTELEditor2PCMCommandLineParameters cliParameters = new KASTELEditor2PCMCLI().interrogateCommandLine(appArgs);
			
			
			relatedModelGenerations = new ArrayList<RelatingModelGeneration>();
			goalModelExtensions = new ArrayList<GoalModelExtension>();
			
			if(cliParameters.parametersValid()) {
				processGoalModelingEditorModel(cliParameters);
			} else {
				System.out.println("Error in CLI");
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
	
	private static void processGoalModelingEditorModel(KASTELEditor2PCMCommandLineParameters cliParameters){

		KASTELGoalModelReader reader;
		
		File goalModelFile = new File(URI.createFileURI(cliParameters.getEditorFilePath()).toFileString());
		
		
		
		boolean validGoalModel = false;
		
		reader = new KASTELGoalModelReader();
		
		
		validGoalModel = reader.extractKastelEditorModelFromJson(goalModelFile);
	
		
		if(validGoalModel){
			processReadGoalModel(reader, cliParameters.getGenerationPath(), cliParameters, goalModelFile);
		}
	}
	
	private static void processReadGoalModel(KASTELGoalModelReader goalModelReader, String projectPath, KASTELEditor2PCMCommandLineParameters cliParameters, File goalModel){
		
		File genDirectoryFile =  new File(projectPath + "/" + GENERATION_DIRECTORY_NAME);
		
		if(!genDirectoryFile.exists()){
			genDirectoryFile.mkdirs();
		}
		
		TrackingGenerator trackingGenerator = new TrackingGenerator();
		
		Collection<RelatingModelGeneration> relatingModelGeneration = new ArrayList<RelatingModelGeneration>();
		
		String pcmRepositoryModelPath = projectPath + "/" + GENERATION_DIRECTORY_NAME + "/" + goalModelReader.getModelName() + PCM_REPOSITORY_FILE_ENDING;
		GoalModelToPCMElementTransformator goalModelToPCMTransformer = new GoalModelToPCMElementTransformator();
		goalModelToPCMTransformer.generateRepositoryModel(goalModelReader, pcmRepositoryModelPath);
		goalModelToPCMTransformer.savePCMModel();
		goalModelToPCMTransformer.saveSystems(projectPath + "/" + GENERATION_DIRECTORY_NAME);
		
		if(cliParameters.isGeneratingJOANAFlowModel()) {
			String JOANAFlowModelPath = projectPath + "/" + GENERATION_DIRECTORY_NAME + "/" + goalModelReader.getModelName();
			JOANAFlowGenerator joanaFlowGenerator = new JOANAFlowGenerator(goalModelToPCMTransformer.getRepositoryModel(), cliParameters.usingSimpleJoanaFlowIds());
			joanaFlowGenerator.generateRelatedModel(goalModelReader, JOANAFlowModelPath);
		
			relatingModelGeneration.add(joanaFlowGenerator);
			trackingGenerator.addExtensionTracking(joanaFlowGenerator);
		}
		
		if(cliParameters.isUsingAdversaries()) {
			String adversaryModelPath = projectPath + "/" + GENERATION_DIRECTORY_NAME + "/" +goalModelReader.getModelName();
			
			ActorAttackerExtension actorAttackerExtension = new ActorAttackerExtension("ActorAttackerExtension");
			actorAttackerExtension.readExtensionContent(goalModel);
			AdversaryGenerator adversaryGenerator = new AdversaryGenerator(actorAttackerExtension);
			adversaryGenerator.generateRelatedModel(goalModelReader, adversaryModelPath);
			relatingModelGeneration.add(adversaryGenerator);
			trackingGenerator.addExtensionTracking(adversaryGenerator);
		}
		
		saveRelatingModels(relatingModelGeneration);
		
		String trackingFilePath = projectPath  + "/" + GENERATION_DIRECTORY_NAME + "/" + goalModelReader.getModelName() +"Tracking";
		
		trackingGenerator.writeTracking(goalModelReader, trackingFilePath);
	}
	
	private static void saveRelatingModels(Collection<RelatingModelGeneration> relatingModelGenerators) {
		relatingModelGenerators.forEach( x-> {x.saveRelatingModel();});
	}


}
