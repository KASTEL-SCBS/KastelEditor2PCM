package edu.kit.kastel.scbs.kastelEditor2PCM.handler;

import com.google.common.collect.Iterables;
import edu.kit.kastel.scbs.kastelEditor2PCM.GoalModelToPCMElementTransformator;
import edu.kit.kastel.scbs.kastelEditor2PCM.KASTELGoalModelReader;
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
  
  public boolean processGoalModelingEditorModel(final IFile file) {
    boolean _xblockexpression = false;
    {
      KASTELGoalModelReader goalModelReader = new KASTELGoalModelReader();
      URI _locationURI = file.getLocationURI();
      File goalModelFile = new File(_locationURI);
      final boolean validGoalModel = goalModelReader.extractKastelEditorModelFromJson(goalModelFile);
      boolean _xifexpression = false;
      if (validGoalModel) {
        boolean _xblockexpression_1 = false;
        {
          final String fileName = file.getName().split(this.GOAL_MODEL_FILE_ENDING)[0];
          String _replaceFirst = file.getProject().getLocationURI().getPath().replaceFirst("/", "");
          final String projectPath = (_replaceFirst + "/");
          _xblockexpression_1 = this.processReadGoalModel(goalModelReader, projectPath, fileName);
        }
        _xifexpression = _xblockexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public boolean processReadGoalModel(final KASTELGoalModelReader goalModelReader, final String projectPath, final String fileName) {
    boolean _xblockexpression = false;
    {
      File genDirectoryFile = new File(((projectPath + "/") + this.GENERATION_DIRECTORY_NAME));
      boolean _exists = genDirectoryFile.exists();
      boolean _not = (!_exists);
      if (_not) {
        genDirectoryFile.mkdirs();
      }
      final String pcmRepositoryModelPath = (((((projectPath + "/") + this.GENERATION_DIRECTORY_NAME) + "/") + fileName) + this.PCM_REPOSITORY_FILE_ENDING);
      GoalModelToPCMElementTransformator goalModelToPCMTransformer = new GoalModelToPCMElementTransformator();
      goalModelToPCMTransformer.generateRepositoryModel(goalModelReader, pcmRepositoryModelPath);
      goalModelToPCMTransformer.savePCMModel();
      File trackingFile = new File(((((((projectPath + "/") + this.GENERATION_DIRECTORY_NAME) + "/") + fileName) + "_Tracking") + this.TRACKING_FILE_ENDING));
      _xblockexpression = goalModelReader.saveTrackingFile(trackingFile);
    }
    return _xblockexpression;
  }
}
