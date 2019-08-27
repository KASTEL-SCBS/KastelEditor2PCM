package edu.kit.kastel.scbs.kastelEditor2PCM.handler

import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.IHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.commands.ExecutionException
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.core.resources.IFile
import java.util.List
import org.eclipse.jface.viewers.ISelection
import java.io.File
import edu.kit.kastel.scbs.kastelEditor2PCM.KASTELGoalModelReader
import edu.kit.kastel.scbs.kastelEditor2PCM.GoalModelToPCMElementTransformator
import edu.kit.kastel.scbs.kastelEditor2PCM.JoanaFlow4PCMGenerator

class KastelEditor2PCMHandler extends AbstractHandler implements IHandler {
	
	val GOAL_MODEL_FILE_ENDING = ".json";
	val PCM_REPOSITORY_FILE_ENDING = ".repository";
	val TRACKING_FILE_ENDING = ".json";
	val GENERATION_DIRECTORY_NAME = "gen";
	
	override execute(ExecutionEvent event) throws ExecutionException {
		val selection = HandlerUtil.getCurrentSelection(event);
		val list = getFilteredList(selection);
		
		if(list !== null){
			for (file : list) {
				file.processGoalModelingEditorModel;
			}
		}
		return null;
	}
	
	
	def getPlugInID() '''edu.kit.kastel.scbs.kastelEditor2PCM'''
	
	def List<IFile> getFilteredList(ISelection selection){
		if (selection instanceof IStructuredSelection) {
			val structuredSelection = selection as IStructuredSelection
			var files = structuredSelection.toList.filter(typeof(IFile)).toList
			if (files.size > 0) {
				return files
			}
		}
		
		return null;
	}
	
	
	def processGoalModelingEditorModel(IFile file){

		var goalModelReader = new KASTELGoalModelReader();
		var goalModelFile = new File(file.locationURI);
		val validGoalModel = goalModelReader.extractKastelEditorModelFromJson(goalModelFile);
		
		if(validGoalModel){
			val projectPath = file.project.locationURI.path.replaceFirst("/", "") + "/";
			processReadGoalModel(goalModelReader, projectPath);
		}
	}
	
	def processReadGoalModel(KASTELGoalModelReader goalModelReader, String projectPath){
		
		var genDirectoryFile =  new File(projectPath + "/" + GENERATION_DIRECTORY_NAME);
		
		if(!genDirectoryFile.exists){
			genDirectoryFile.mkdirs();
		}
		
		val pcmRepositoryModelPath = projectPath + "/" + GENERATION_DIRECTORY_NAME + "/" + goalModelReader.modelName + PCM_REPOSITORY_FILE_ENDING;
		var goalModelToPCMTransformer = new GoalModelToPCMElementTransformator();
		goalModelToPCMTransformer.generateRepositoryModel(goalModelReader, pcmRepositoryModelPath);
		goalModelToPCMTransformer.savePCMModel();
		
		val joanaFlowModelPath = projectPath + "/" + GENERATION_DIRECTORY_NAME + "/" + "My.joanaflow4palladio";
		var joanaFlow4PCMGenerator = new JoanaFlow4PCMGenerator();
		joanaFlow4PCMGenerator.generateModel(goalModelReader, goalModelToPCMTransformer.repositoryModel, joanaFlowModelPath);
		
		
		var trackingFile = new File(projectPath  + "/" + GENERATION_DIRECTORY_NAME + "/" + goalModelReader.modelName + "_Tracking" + TRACKING_FILE_ENDING);
		goalModelReader.saveTrackingFile(trackingFile);
	}
	
	
}