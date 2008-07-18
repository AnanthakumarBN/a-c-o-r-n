package acorn.sbmlParser;

import acorn.errorHandling.ErrorBean;
import java.io.ByteArrayInputStream;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author lukasz
 */

public class AddModelBean {   
    private UploadedFile file;
    private String name;
    private String organism;
    private String geneLink;
    
    /** Creates a new instance of AddModelBean */
    public AddModelBean() {
    }
    
    /**
     * Add new model to database
     * @return navigation string
     */
    public String addModel() {        
        SbmlParser handler = new SbmlParser(getName(), getOrganism(), getGeneLink());
        
        try {
            XMLReader xr = XMLReaderFactory.createXMLReader();
            
            xr.setContentHandler(handler);
            xr.setErrorHandler(handler);
            
            xr.parse(new InputSource(new ByteArrayInputStream(file.getBytes())));
        } 
        catch (Exception e) {
            ErrorBean.printMessage(e, null, "Unexpected error while adding model. Please contact the system Administrator.");
            return null;  
        }
        
        if (handler.isError()) {
            FacesContext        context = FacesContext.getCurrentInstance();
            HttpServletRequest  request = (HttpServletRequest)context.getExternalContext().getRequest();
        
            FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_FATAL,
                handler.getMessage(), 
                null);
        
            context.addMessage(null, message);
                
            return null;
        }
        
        return "modelList";
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
        this.name = name;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }

    public String getGeneLink() {
        return geneLink;
    }

    public void setGeneLink(String geneLink) {
        this.geneLink = geneLink;
    }
}
