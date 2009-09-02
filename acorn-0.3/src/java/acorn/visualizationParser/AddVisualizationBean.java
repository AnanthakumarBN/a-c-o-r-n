/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.visualizationParser;

/**
 *
 * @author Mateusz
 */
import acorn.exception.XmlParseException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.myfaces.custom.fileupload.UploadedFile;

public class AddVisualizationBean {

    private UploadedFile file;
    private String name;
    private String modelName;

    public AddVisualizationBean() {
    }

    public AddVisualizationBean(UploadedFile file, String name, String modelName) {
        if (!name.matches("[ a-zA-Z_0-9]*")) {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_FATAL,
                    "You can use only letters, digits, white spaces and underscores", null);
        }
        String newName = name.replace(' ', '_');
        this.file = file;
        this.name = newName;
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String addVisualization() {
        try {
            VisualizationParser vp = new VisualizationParser(name, modelName, file);
            vp.runParser();

        } catch (XmlParseException xpe) {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_FATAL,
                    xpe.getMessage(), null);
            context.addMessage(null, message);
            return null;
        } catch (Exception e) {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_FATAL,
                    "Please insert XML file", null);
            context.addMessage(null, message);
            return null;
        }
        return "taskList";
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String newName = name.replace(' ', '_');
        this.name = newName;
    }
}
