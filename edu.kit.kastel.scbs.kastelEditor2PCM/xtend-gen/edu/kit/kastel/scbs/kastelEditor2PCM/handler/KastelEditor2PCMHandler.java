package edu.kit.kastel.scbs.kastelEditor2PCM.handler;

import com.google.common.collect.Iterables;
import edu.kit.kastel.scbs.kastelEditor2PCM.AdversaryGenerator;
import edu.kit.kastel.scbs.kastelEditor2PCM.GoalModelToPCMElementTransformator;
import edu.kit.kastel.scbs.kastelEditor2PCM.JoanaFlow4PCMGenerator;
import edu.kit.kastel.scbs.kastelEditor2PCM.KASTELGoalModelReader;
import edu.kit.kastel.scbs.kastelEditor2PCM.KASTELGoalModelReader_ActorAttackerExtension;
import java.io.File;
import java.net.URI;
import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class KastelEditor2PCMHandler extends AbstractHandler implements IHandler {
  private final String GOAL_MODEL_FILE_ENDING = ".json";
  
  private final String PCM_REPOSITORY_FILE_ENDING = ".repository";
  
  private final String TRACKING_FILE_ENDING = ".json";
  
  private final String GENERATION_DIRECTORY_NAME = "gen";
  
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    final ISelection selection = HandlerUtil.getCurrentSelection(event);
    final List<IFile> list = this.getFilteredList(selection);
    if ((list != null)) {
      for (final IFile file : list) {
        this.processGoalModelingEditorModel(file);
      }
    }
    return null;
  }
  
  public CharSequence getPlugInID() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("edu.kit.kastel.scbs.kastelEditor2PCM");
    return _builder;
  }
  
  public List<IFile> getFilteredList(final ISelection selection) {
    if ((selection instanceof IStructuredSelection)) {
      final IStructuredSelection structuredSelection = ((IStructuredSelection) selection);
      List<IFile> files = IterableExtensions.<IFile>toList(Iterables.<IFile>filter(structuredSelection.toList(), IFile.class));
      int _size = files.size();
      boolean _greaterThan = (_size > 0);
      if (_greaterThan) {
        return files;
      }
    }
    return null;
  }
  
  public void processGoalModelingEditorModel(final IFile file) {
    KASTELGoalModelReader_ActorAttackerExtension goalModelReader = new KASTELGoalModelReader_ActorAttackerExtension();
    URI _locationURI = file.getLocationURI();
    File goalModelFile = new File(_locationURI);
    final boolean validGoalModel = goalModelReader.extractKastelEditorModelFromJson(goalModelFile);
    if (validGoalModel) {
      String _replaceFirst = file.getProject().getLocationURI().getPath().replaceFirst("/", "");
      final String projectPath = (_replaceFirst + "/");
      this.processReadGoalModel(goalModelReader, projectPath);
    }
  }
  
  public void processReadGoalModel(final KASTELGoalModelReader goalModelReader, final String projectPath) {
    File genDirectoryFile = new File(((projectPath + "/") + this.GENERATION_DIRECTORY_NAME));
    boolean _exists = genDirectoryFile.exists();
    boolean _not = (!_exists);
    if (_not) {
      genDirectoryFile.mkdirs();
    }
    String _modelName = goalModelReader.getModelName();
    String _plus = ((((projectPath + "/") + this.GENERATION_DIRECTORY_NAME) + "/") + _modelName);
    final String pcmRepositoryModelPath = (_plus + this.PCM_REPOSITORY_FILE_ENDING);
    GoalModelToPCMElementTransformator goalModelToPCMTransformer = new GoalModelToPCMElementTransformator();
    goalModelToPCMTransformer.generateRepositoryModel(goalModelReader, pcmRepositoryModelPath);
    goalModelToPCMTransformer.savePCMModel();
    goalModelToPCMTransformer.saveSystems(((projectPath + "/") + this.GENERATION_DIRECTORY_NAME));
    String _modelName_1 = goalModelReader.getModelName();
    String _plus_1 = ((((projectPath + "/") + this.GENERATION_DIRECTORY_NAME) + "/") + _modelName_1);
    final String joanaFlowModelPath = (_plus_1 + ".joanaflow4palladio");
    JoanaFlow4PCMGenerator joanaFlow4PCMGenerator = new JoanaFlow4PCMGenerator();
    joanaFlow4PCMGenerator.generateModel(goalModelReader, goalModelToPCMTransformer.getRepositoryModel(), joanaFlowModelPath);
    String _modelName_2 = goalModelReader.getModelName();
    String _plus_2 = ((((projectPath + "/") + this.GENERATION_DIRECTORY_NAME) + "/") + _modelName_2);
    String _plus_3 = (_plus_2 + "_Tracking");
    String _plus_4 = (_plus_3 + this.TRACKING_FILE_ENDING);
    File trackingFile = new File(_plus_4);
    goalModelReader.saveTrackingFile(trackingFile);
    String _modelName_3 = goalModelReader.getModelName();
    String _plus_5 = ((((projectPath + "/") + this.GENERATION_DIRECTORY_NAME) + "/") + _modelName_3);
    final String adversaryModelPath = (_plus_5 + ".adversary");
    AdversaryGenerator adversaryGenerator = new AdversaryGenerator();
    adversaryGenerator.generateAdversaryModel(((KASTELGoalModelReader_ActorAttackerExtension) goalModelReader), adversaryModelPath);
  }
}
