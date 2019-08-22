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

class KastelEditor2PCMHandler extends AbstractHandler implements IHandler {
	
	val GOAL_MODEL_FILE_ENDING = ".json";
	val PCM_REPOSITORY_FILE_ENDING = ".repository";
	val TRACKING_FILE_ENDING = ".json";
	
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
			val fileName = file.name.split(GOAL_MODEL_FILE_ENDING).get(0);
			val projectPath = file.project.locationURI.path.replaceFirst("/", "") + "/";
			processReadGoalModel(goalModelReader, projectPath, fileName);
		}
	}
	
	def processReadGoalModel(KASTELGoalModelReader goalModelReader, String projectPath, String fileName){
		val pcmRepositoryModelPath = projectPath + fileName + PCM_REPOSITORY_FILE_ENDING;
		var goalModelToPCMTransformer = new GoalModelToPCMElementTransformator();
		goalModelToPCMTransformer.generateRepositoryModel(goalModelReader, pcmRepositoryModelPath);
		goalModelToPCMTransformer.savePCMModel();
		
		var trackingFile = new File(projectPath  + fileName + "_Tracking" + TRACKING_FILE_ENDING);
		goalModelReader.saveTrackingFile(trackingFile);
	}
	
	
}