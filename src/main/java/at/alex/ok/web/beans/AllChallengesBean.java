package at.alex.ok.web.beans;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import at.alex.ok.model.Challenge;
import at.alex.ok.web.model.LazyChallengeDataModel;
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
 * @author Alex-Mi
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

@Named ("allChallengesBean")
@javax.faces.view.ViewScoped
// javax.faces.bean.ViewScoped is deprecated as per JSF 2.2
// javax.faces.bean.ManagedBean is deprecated as per JSF 2.2
public class AllChallengesBean extends AllChallengeableBean {

	public static final String SELECTED_CHALLENGE_PARAM = "selectedChallenge";
	
	public static final String SELECTED_CHALLENGE_ID_PARAM = "challId";

	private LazyChallengeDataModel dataModel;

	@PostConstruct
	private void init() {
		this.dataModel = new LazyChallengeDataModel();
	}

	public void createAssignment(ActionEvent actionEvent) {
		if (this.getSelectedChallenge() == null) {
			NavigationUtils.addMessage("Please select a challenge first!",
					FacesMessage.SEVERITY_INFO);
		} else {
			processButton("createAssignment");
		}
	}

	public LazyChallengeDataModel getDataModel() {
		return this.dataModel;
	}

	public void setDataModel(LazyChallengeDataModel dataModel) {
		this.dataModel = dataModel;
	}

	public Challenge getSelectedChallenge() {
		return (Challenge) super.getSelectedChallengeable();
	}

	public void setSelectedChallenge(Challenge selectedChallenge) {
		super.setSelectedChallengeable(selectedChallenge);
	}

	public String processButton(String buttonID) {

		if ("createAssignment".equals(buttonID)) {
			
			Properties requestParameters = new Properties();
			requestParameters.put(SELECTED_CHALLENGE_ID_PARAM, this.getSelectedChallenge().getId());

			NavigationUtils.flashAndRedirect(SELECTED_CHALLENGE_PARAM,
					this.getSelectedChallenge(), "/pages/edit/assignment.jsf", requestParameters);

		} else if ("viewAssignments".equals(buttonID)) {

			NavigationUtils.flashAndRedirect(SELECTED_CHALLENGE_PARAM,
					this.getSelectedChallenge(), "/pages/assignments.jsf");

		} else if ("editChallengeable".equals(buttonID)) {

			NavigationUtils.flashAndRedirect(SELECTED_CHALLENGE_PARAM,
					this.getSelectedChallenge(), "/pages/edit/challenge.jsf");

		}

		return null;
	}

}
