package acorn.model;

import acorn.errorHandling.ErrorBean;
import java.io.ByteArrayInputStream;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author lukasz
 */
public class AddResultsBean {

  private Integer taskID;
  private UploadedFile file;

  /** Creates a new instance of AddResultsBean */
  public AddResultsBean() {
  }

  public String getTaskID() {
    return taskID.toString();
  }

  public void setTaskID(String id) {
    taskID = Integer.valueOf(id);
  }  

  public void addResults() {
    ResultParser handler = new ResultParser(taskID);
    try {
      XMLReader xr = XMLReaderFactory.createXMLReader();
      xr.setContentHandler(handler);
      xr.setErrorHandler(handler);
      xr.parse(new InputSource(new ByteArrayInputStream(file.getBytes())));
    } catch (Exception e) {
      ErrorBean.printMessage(e, null, "Unexpected error while adding results. Please contact the system Administrator.");
      return;
    }
  }

  public UploadedFile getFile() {
    return file;
  }

  public void setFile(UploadedFile file) {
    this.file = file;
  }
}
