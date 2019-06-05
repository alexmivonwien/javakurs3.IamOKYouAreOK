package at.alex.ok.web.beans;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

import at.alex.ok.model.Assignment;
import at.alex.ok.model.Challenge;
import at.alex.ok.model.Challengeable;
import at.alex.ok.model.User;
import at.alex.ok.model.enums.Category;
import at.alex.ok.model.enums.Level;
import at.alex.ok.model.enums.Status;
import at.alex.ok.services.ChallengeService;
import at.alex.ok.services.UserService;
import at.alex.ok.web.utils.NavigationUtils;


/**
 * Unless you're using JSF 2.2 (which is still not out yet at this moment) or
 * MyFaces CODI (which I'd have expected that you would explicitly mention that)
 * the @ViewScoped doesn't work in CDI. This also pretty much matches your
 * problem symptoms.
 * 
 * http://stackoverflow.com/questions/14812238/jsf-view-scoped-bean-
 * reconstructed-multiple-times
 * 
 * 
 * As per JSF 2.2 and higher, @ManagedBean is deprecated. Use @Named together with @javax.faces.view.ViewScoped,
 * @see https://stackoverflow.com/a/4347707/1925356
 * @see @javax.faces.view.ViewScoped documentation:  
 * 
 * When this annotation, along with javax.inject.Named is found on a class, the runtime must place
 * the bean in a CDI scope such that it remains active as long as javax.faces.application.NavigationHandler.handleNavigation 
 * does not cause a navigation to a view with a viewId that is different than theview Id of the current view. Any injections and
 * notifications required by CDI and the Java EE platform must occur as usual at the expected time.
 * 
 * 
 */

@Named ("assignmentBean")
@javax.faces.view.ViewScoped
// javax.faces.bean.ViewScoped is deprecated as per JSF 2.2
// javax.faces.bean.ManagedBean is deprecated as per JSF 2.2
public class AssignmentBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String UPLOAD_FILE_PATH = "C:/Temp/IamOKYouAreOK";

	@Inject
	private ChallengeService challengeService;

	@Inject
	private UserService userService;

	private Challenge selectedChallenge;
	private Assignment selectedAssignment;
	private Status status;
	private User assignedTo;
	private Date dueDate;
	private boolean isExistingAssignment;
	private boolean itemNotExists;
	private String fileId;

	private String assId;
	private String challId;

	private UploadedFile uploadedFile;
	
	public String getModifyType(){
		return ( !itemNotExists && isExistingAssignment ? "Edit " : "Create ");
	}

	public String getAssId() {
		return assId;
	}

	public void setAssId(String assId) {
		this.assId = assId;
	}

	public String getChallId() {
		return challId;
	}

	public void setChallId(String challId) {
		this.challId = challId;
	}

	public void onRowSelect(SelectEvent event) {
		Challenge challenge = (Challenge) event.getObject();
		FacesMessage msg = new FacesMessage("Challenge Selected",
				challenge.getId() + "");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	private int obtainId(Class<? extends Challengeable> challengeable) {
		int challengebleId = -1;

		ExternalContext extContext = FacesContext.getCurrentInstance()
				.getExternalContext();

		String requestParamater = (challengeable.getSimpleName().equals(
				Challenge.class.getSimpleName()) ? AllChallengesBean.SELECTED_CHALLENGE_ID_PARAM
				: AllAssignmentsBean.SELECTED_ASSIGNMENT_ID_PARAM);

		String challengeableIdString = (String) extContext
				.getRequestParameterMap().get(requestParamater);
		try {
			challengebleId = Integer.valueOf(challengeableIdString);
		} catch (NumberFormatException nfe) {
			Logger logger = LogManager.getLogger();
			logger.debug("Invalid Request parameter " + requestParamater, nfe);
		}

		return challengebleId;
	}

	@PostConstruct
	private void init() {
		if (this.selectedChallenge == null && selectedAssignment == null) {

			ExternalContext extContext = FacesContext.getCurrentInstance()
					.getExternalContext();

			Flash flash = extContext.getFlash();

			int challengeId = obtainId(Challenge.class);

			if (challengeId > 0) {

				this.challId = challengeId + "";

				// create a new assignment for a challengeId requested:
				this.selectedChallenge = (Challenge) flash
						.get(AllChallengesBean.SELECTED_CHALLENGE_PARAM);

				if (this.selectedChallenge == null
						|| this.selectedChallenge.getId() != challengeId) {
					this.selectedChallenge = this.challengeService
							.getChallengeable(challengeId, Challenge.class);
				}

				if (this.selectedChallenge != null) {
					// challenge found, so we will create a new assignment:
					return;
				}

			}

			int assignmentId = obtainId(Assignment.class);

			if (assignmentId > 0) {
				this.assId = assignmentId + "";
				// edit an existing assignment for the assignmentId requested:
				this.selectedAssignment = (Assignment) flash
						.get(AllAssignmentsBean.SELECTED_ASSIGNMENT_PARAM);

				if (this.selectedAssignment == null
						|| this.selectedAssignment.getId() != assignmentId) {
					this.selectedAssignment = this.challengeService
							.getChallengeable(assignmentId, Assignment.class);
				}

				if (this.selectedAssignment != null) { // assignment found, so:

					// 2.) Take the rest values from it:
					this.selectedChallenge = selectedAssignment.getChallenge();
					this.isExistingAssignment = true;
					this.dueDate = selectedAssignment.getDateDue();
					this.assignedTo = selectedAssignment.getAssignedTo();
					this.status = selectedAssignment.getStatus();

				} else { // assignment not found, so n
					itemNotExists = true;
				}

			} else {
				itemNotExists = true;
			}
		}
	};


	public boolean isItemNotExists() {
		return itemNotExists;
	}

	public boolean isExistingAssignment() {
		return isExistingAssignment;
	}

	public List<SelectItem> getLevelValues() {
		List<SelectItem> result = new ArrayList<SelectItem>();

		for (int i = 0; i < Level.values().length; i++) {
			result.add(new SelectItem(Level.values()[i], Level.values()[i]
					.getName()));
		}
		return result;
	}

	public List<SelectItem> getStatusValues() {
		List<SelectItem> result = new ArrayList<SelectItem>();

		for (int i = 0; i < Status.values().length; i++) {
			result.add(new SelectItem(Status.values()[i], Status.values()[i]
					.getName()));
		}
		return result;
	}

	public List<SelectItem> getCategoryValues() {
		List<SelectItem> result = new ArrayList<SelectItem>();

		for (int i = 0; i < Category.values().length; i++) {
			result.add(new SelectItem(Category.values()[i],
					Category.values()[i].toString()));
		}
		return result;
	}

	public Level getLevel() {
		return getChallenge().getLevel();
	}

	public Category getCategory() {
		return getChallenge().getCategory();
	}

	public User getAssignedTo() {
		if (assignedTo == null) {
			String remoteUser = FacesContext.getCurrentInstance()
					.getExternalContext().getRemoteUser();
			assignedTo = userService.findByUsernameOrEmail(remoteUser,
					remoteUser);
		}
		return assignedTo;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Challenge getChallenge() {
		return selectedChallenge;
	}

	public void setChallenge(Challenge challenge) {
		this.selectedChallenge = challenge;
	}

	public Date getDueDate() {
		if (dueDate == null){
			dueDate = new Date();
		}
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public ChallengeService getChallengeService() {
		return challengeService;
	}

	public void setChallengeService(ChallengeService challengeService) {
		this.challengeService = challengeService;
	}
	
	
	public String getFileId(){
		
		if (fileId != null)
			return fileId;

		
		String lastSavedFilePath = null;
		
		if ( this.selectedAssignment!=null ){
			lastSavedFilePath = this.selectedAssignment.getLastFilePath();
		}
		
		return lastSavedFilePath;
	}
	
	private Path getDirectoryPath(){
		return Paths.get(UPLOAD_FILE_PATH  + File.separatorChar + "A_" + this.assId );
	}
	
	public void upload(FileUploadEvent event) {

		if ((this.uploadedFile = event.getFile()) == null)
			return;

		try (InputStream input = this.uploadedFile.getInputstream()) {
			
			String extension = FilenameUtils.getExtension(uploadedFile
					.getFileName());
			
			Path targetDirectory = getDirectoryPath();
			
			if ( !Files.exists(targetDirectory)){
				targetDirectory = Files.createDirectories(targetDirectory);
			}
			
			Path targetFile = Paths.get(targetDirectory.toString() + File.separatorChar +  ( selectedAssignment == null ?  Status.Assigned : selectedAssignment.getStatus().toString() + "."  + extension));
			
			Files.copy(input, targetFile, StandardCopyOption.REPLACE_EXISTING);
			
			fileId = targetFile.toString().substring((UPLOAD_FILE_PATH  + File.separatorChar).length(), targetFile.toString().length());
			
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful",
							event.getFile().getFileName() + " is uploaded"));

		} catch (IOException e1) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR I/O",
							e1.getMessage()));
			e1.printStackTrace();
		}

	}

	public String modifyAssignment() {

		Assignment assignment = null;

		if (selectedAssignment == null) {
			// we are creating a new assignment:
			assignment = new Assignment();
			assignment.setDateAssigned(new java.sql.Date(new java.util.Date()
					.getTime()));
			assignment.setChallenge(getChallenge());
			assignment.setDateDue(new java.sql.Date(this.getDueDate().getTime()));
			assignment.setStatus(Status.Assigned);
			assignment.setAssignedTo(getAssignedTo());
			assignment.setLastFilePath(this.fileId);

		} else {
			// we are modifying an existing assignment:
			assignment = selectedAssignment;

			if (this.dueDate != null) {
				assignment
						.setDateDue(new java.sql.Date(this.dueDate.getTime()));
			}

			if (this.getStatus() != null) {
				assignment.setStatus(this.getStatus());
			}
			if (!StringUtils.isEmpty(this.getFileId())) {
				assignment.setLastFilePath(this.getFileId());
			}
		}

		this.challengeService.createOrUpdateAssignment(assignment);

		return NavigationUtils.getDevicePrefix() + "/pages/assignments?faces-redirect=true";
	}
}
