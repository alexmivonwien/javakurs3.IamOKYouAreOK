package at.alex.ok.web.beans;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;

import at.alex.ok.model.Assignment;
import at.alex.ok.model.Challenge;
import at.alex.ok.services.ChallengeService;
import at.alex.ok.web.model.LazyAssignmentDataModel;
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
 * @author User
 *
 */
@ManagedBean(name = "allAssignmentsBean")
@ViewScoped
public class AllAssignmentsBean extends AllChallengeableBean {
	
	public static final String SELECTED_ASSIGNMENT_ID_PARAM = "assId";

	private LazyAssignmentDataModel dataModel;

	private Challenge filterAssignmentByChallenge;

	private boolean myAssignmentsOnly = false;

	@Inject
	private ChallengeService challengeService;

	public static final String SELECTED_ASSIGNMENT_PARAM = "selectedAssignment";

	public static final String SELECTED_ASSIGNMENT_HISTORY_PARAM = "selectedAssignmentHistory";

	public static final String FILTER_ASSIGNMENTS_BY_USERID = "userId";

	public static final String MY_ASSIGNMENTS_ONLY = "myAssignmentsOnly";

	@PostConstruct
	private void init() {

		ExternalContext extCtx = FacesContext.getCurrentInstance()

		.getExternalContext();

		String filterByUseriD = extCtx.getRequestParameterMap().get(FILTER_ASSIGNMENTS_BY_USERID);

		if (StringUtils.isEmpty(filterByUseriD)) { // no such request parameter, try flash map:
			
			filterByUseriD = (String) extCtx.getFlash().get(FILTER_ASSIGNMENTS_BY_USERID);
		}

		if (!StringUtils.isEmpty(filterByUseriD)) { // found it, 
			
			//1.) put it in flash again:
			//extCtx.getFlash().put(FILTER_ASSIGNMENTS_BY_USERID, filterByUseriD);

			//2.) if logged in user same as fileterBy user:
			initMyAssignmentsOnly(filterByUseriD);
		}

		if (this.dataModel == null) {
			filterAssignmentByChallenge = (Challenge) extCtx.getFlash().get(
					AllChallengesBean.SELECTED_CHALLENGE_PARAM);

			this.dataModel = new LazyAssignmentDataModel(
					filterAssignmentByChallenge, (!StringUtils.isEmpty(filterByUseriD) ? 
							filterByUseriD  : null));
		}
	}
	
	private void initMyAssignmentsOnly(String filterByUseriD) {
		ExternalContext extCtx = FacesContext.getCurrentInstance()
				.getExternalContext();

		if (extCtx.getFlash().get(MY_ASSIGNMENTS_ONLY) == null) {

			Integer loggedInUser = (Integer)extCtx.getSessionMap().get(LoginBean.SESSION_ATTRIBUTE_USERID);
			this.myAssignmentsOnly = loggedInUser != null
					&& (loggedInUser.toString().equals(filterByUseriD));
			extCtx.getFlash().put(MY_ASSIGNMENTS_ONLY,
					new Boolean(this.myAssignmentsOnly));

		} else {
			this.myAssignmentsOnly = (Boolean) extCtx.getFlash().get(
					MY_ASSIGNMENTS_ONLY);
		}
	}

	public boolean isMyAssignmentsOnly() {
		return myAssignmentsOnly;
	}

	public Challenge getFilterAssignmentByChallenge() {
		return filterAssignmentByChallenge;
	}

	public LazyAssignmentDataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(LazyAssignmentDataModel dataModel) {
		this.dataModel = dataModel;
	}

	public Assignment getSelectedAssignment() {
		return (Assignment) super.getSelectedChallengeable();
	}

	public void setSelectedAssignment(Assignment selectedAssignment) {
		super.setSelectedChallengeable(selectedAssignment);
	}

	public void viewAssignmentHistory(ActionEvent actionEvent) {
		if (getSelectedAssignment() == null) {
			NavigationUtils.addMessage("Please select an assignment first!",
					FacesMessage.SEVERITY_INFO);
		} else {
			processButton("viewAssignmentHistory");
		}
	}

	public String processButton(String buttonID) {

		if (getSelectedAssignment() == null) {
			NavigationUtils.addMessage("Please select an assignment first!",
					FacesMessage.SEVERITY_INFO);

			return null;
		}

		if ("viewAssignmentHistory".equals(buttonID)) {
			this.setSelectedAssignment(challengeService.getAssignementFetchHistory(getSelectedAssignment().getId()));

			NavigationUtils.flashAndRedirect(SELECTED_ASSIGNMENT_HISTORY_PARAM,
					getSelectedAssignment().getAssignmentHistory(),
					"/pages/assignmentHistory.jsf");

		} else if ("editChallengeable".equals(buttonID)) {
			
			Properties requestParameters = new Properties();
			requestParameters.put(SELECTED_ASSIGNMENT_ID_PARAM, getSelectedAssignment().getId());


			NavigationUtils.flashAndRedirect(SELECTED_ASSIGNMENT_PARAM,
					getSelectedAssignment(), "/pages/edit/assignment.jsf", requestParameters);
		}

		return null;

	}

}
