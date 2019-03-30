package at.alex.ok.web.beans;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;

import at.alex.ok.model.Challengeable;
import at.alex.ok.web.utils.NavigationUtils;

public abstract class AllChallengeableBean {

	private Challengeable selectedChallengeable;

	public void onRowSelect(SelectEvent event) {
		Challengeable assignment = (Challengeable) event.getObject();
		FacesMessage msg = new FacesMessage(assignment.getChallenge().getName()
				+ " selected");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public boolean getEditButtonVisible() {

		String loggedInUserName = FacesContext.getCurrentInstance()
				.getExternalContext().getRemoteUser();

		boolean result =  this.selectedChallengeable != null
				&& (!StringUtils.isEmpty(loggedInUserName))
				&& loggedInUserName.equalsIgnoreCase(this.selectedChallengeable
						.getChallenge().getCreatedBy().getUsername());
		
		return result;

	}

	public void editChallengeable(ActionEvent actionEvent) {
		if (selectedChallengeable == null) {

			String item = (this instanceof AllChallengesBean ? " a challenge"
					: "an assignment");
			NavigationUtils.addMessage("Please select " + item + " first!",
					FacesMessage.SEVERITY_INFO);
		} else {
			processButton("editChallengeable");
		}
	}

	/**
	 * The action navigation method to be defined in the subclasses
	 * 
	 * @param whatToProcess
	 * @return
	 */
	protected abstract String processButton(String whatToProcess);

	public Challengeable getSelectedChallengeable() {
		return selectedChallengeable;
	}

	public void setSelectedChallengeable(Challengeable selectedChallengeable) {
		this.selectedChallengeable = selectedChallengeable;
	}

}
