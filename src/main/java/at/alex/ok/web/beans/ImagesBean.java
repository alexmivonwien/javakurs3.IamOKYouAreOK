package at.alex.ok.web.beans;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@javax.enterprise.context.ApplicationScoped
@Named ("imagesBean")
public class ImagesBean {
	
	

	public StreamedContent getUploadedFileAsStream() throws IOException {
		// see
		// http://stackoverflow.com/questions/8207325/display-dynamic-image-from-database-with-pgraphicimage-and-streamedcontent
		
		//System.out.println("Thread Id = " + Thread.currentThread().getId());
		
		ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
		String fileID = extContext.getRequestParameterMap().get("fileId");
			
		if (FacesContext.getCurrentInstance().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {

			// So, we're rendering the HTML. Return a stub StreamedContent so
			// that it will generate right URL.
			return new DefaultStreamedContent();
		} else 
			
		if (! StringUtils.isEmpty(fileID)) {
			Path targetFile  = Paths.get( AssignmentBean.UPLOAD_FILE_PATH  + File.separatorChar + fileID);
			String extension = FilenameUtils.getExtension(targetFile.toString());
			
			ByteArrayContent fileContent = new ByteArrayContent(
					Files.readAllBytes(targetFile), "img/" + extension);

			return fileContent;

		}

		return null;
	}

}
