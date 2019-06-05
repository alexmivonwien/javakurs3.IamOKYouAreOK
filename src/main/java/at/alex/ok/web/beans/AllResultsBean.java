package at.alex.ok.web.beans;


import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import at.alex.ok.model.Assignment;
import at.alex.ok.model.Challenge;
import at.alex.ok.model.Result;
import at.alex.ok.services.ChallengeService;
import at.alex.ok.web.model.LazyResultDataModel;
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

@Named ("allResultsBean")
@javax.faces.view.ViewScoped
// javax.faces.bean.ViewScoped is deprecated as per JSF 2.2
// javax.faces.bean.ManagedBean is deprecated as per JSF 2.2
public class AllResultsBean extends AllChallengeableBean {
	
	public static final String SELECTED_ASSIGNMENT_ID_PARAM = "assId";

	private LazyResultDataModel dataModel;

	private Challenge filterResultsByChallenge;

	private boolean myResultsOnly = false;

	@Inject
	private ChallengeService challengeService;

	public static final String SELECTED_RESULT_PARAM = "selectedResult";

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
			filterResultsByChallenge = (Challenge) extCtx.getFlash().get(
					AllChallengesBean.SELECTED_CHALLENGE_PARAM);

			this.dataModel = new LazyResultDataModel(
					filterResultsByChallenge, (!StringUtils.isEmpty(filterByUseriD) ? 
							filterByUseriD  : null));
		}
	}
	
	private void initMyAssignmentsOnly(String filterByUseriD) {
		ExternalContext extCtx = FacesContext.getCurrentInstance()
				.getExternalContext();

		if (extCtx.getFlash().get(MY_ASSIGNMENTS_ONLY) == null) {

			Integer loggedInUser = (Integer)extCtx.getSessionMap().get(LoginBean.SESSION_ATTRIBUTE_USERID);
			this.myResultsOnly = loggedInUser != null
					&& (loggedInUser.toString().equals(filterByUseriD));
			extCtx.getFlash().put(MY_ASSIGNMENTS_ONLY,
					new Boolean(this.myResultsOnly));

		} else {
			this.myResultsOnly = (Boolean) extCtx.getFlash().get(
					MY_ASSIGNMENTS_ONLY);
		}
	}

	public boolean isMyResultsOnly() {
		return myResultsOnly;
	}

	public Challenge getFilterResultsByChallenge() {
		return filterResultsByChallenge;
	}

	public LazyResultDataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(LazyResultDataModel dataModel) {
		this.dataModel = dataModel;
	}

	public Result getSelectedResult() {
		return (Result) super.getSelectedChallengeable();
	}

	@Override
	public  boolean getEditButtonVisible() {
		return false;
	}
	
	public void setSelectedResult(Result selectedResult) {
		super.setSelectedChallengeable(selectedResult);
	}

	public void viewResultHistory(ActionEvent actionEvent) {
		if (getSelectedResult() == null) {
			NavigationUtils.addMessage("Please select a result first!",
					FacesMessage.SEVERITY_INFO);
		} else {
			processButton("viewAssignmentHistory");
		}
	}

	public String processButton(String buttonID) {

		if (getSelectedResult() == null) {
			NavigationUtils.addMessage("Please select a result first!",
					FacesMessage.SEVERITY_INFO);

			return null;
		}

		if ("viewAssignmentHistory".equals(buttonID)) {
//			Assignment assignmentWithHistory = challengeService.getAssignementFetchHistory(getSelectedResult().getAssignmentId());
//			this.setSelectedAssignment(assignmentWithHistory);
//
//			NavigationUtils.flashAndRedirect(SELECTED_ASSIGNMENT_HISTORY_PARAM, assignmentWithHistory.getAssignmentHistory(), "/pages/assignmentHistory.jsf");
		}

		return null;

	}

}
