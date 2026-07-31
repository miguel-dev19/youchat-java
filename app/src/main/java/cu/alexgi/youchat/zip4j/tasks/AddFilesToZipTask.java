package cu.alexgi.youchat.zip4j.tasks;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cu.alexgi.youchat.zip4j.exception.ZipException;
import cu.alexgi.youchat.zip4j.headers.HeaderWriter;
import cu.alexgi.youchat.zip4j.model.Zip4jConfig;
import cu.alexgi.youchat.zip4j.model.ZipModel;
import cu.alexgi.youchat.zip4j.model.ZipParameters;
import cu.alexgi.youchat.zip4j.progress.ProgressMonitor;

public class AddFilesToZipTask extends AbstractAddFileToZipTask<AddFilesToZipTask.AddFilesToZipTaskParameters> {

  public AddFilesToZipTask(ZipModel zipModel, char[] password, HeaderWriter headerWriter,
                           AsyncTaskParameters asyncTaskParameters) {
    super(zipModel, password, headerWriter, asyncTaskParameters);
  }

  @Override
  protected void executeTask(AddFilesToZipTaskParameters taskParameters, ProgressMonitor progressMonitor)
      throws IOException {

    verifyZipParameters(taskParameters.zipParameters);
    addFilesToZip(taskParameters.filesToAdd, progressMonitor, taskParameters.zipParameters, taskParameters.zip4jConfig);
  }

  @Override
  protected long calculateTotalWork(AddFilesToZipTaskParameters taskParameters) throws ZipException {
    return calculateWorkForFiles(taskParameters.filesToAdd, taskParameters.zipParameters);
  }

  @Override
  protected ProgressMonitor.Task getTask() {
    return super.getTask();
  }

  public static class AddFilesToZipTaskParameters extends AbstractZipTaskParameters {
    private final List<File> filesToAdd;
    private final ZipParameters zipParameters;

    public AddFilesToZipTaskParameters(List<File> filesToAdd, ZipParameters zipParameters, Zip4jConfig zip4jConfig) {
      super(zip4jConfig);
      this.filesToAdd = filesToAdd;
      this.zipParameters = zipParameters;
    }
  }
}
