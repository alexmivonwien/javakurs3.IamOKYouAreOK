package at.alex.ok.web.beans;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import at.alex.ok.model.Assignment;
import at.alex.ok.model.AssignmentHistory;

@RequestScoped
@Named("assignmentHistoryBean")
public class AssignmentHistoryBean  {

	
	private List <AssignmentHistory> assignmentHistory;
	

	public List<AssignmentHistory> getAssignmentHistory() {
		return assignmentHistory;
	}

	public Assignment getSelectedAssignment() {
		return getAssignmentHistory().get(0).getAssignment();
	}

	@PostConstruct
	private void initInstanceVariables() {
		if (assignmentHistory == null){
			this.assignmentHistory =  (List <AssignmentHistory>)
			FacesContext.getCurrentInstance().getExternalContext().getFlash().get(AllAssignmentsBean.SELECTED_ASSIGNMENT_HISTORY_PARAM);
		}		
	}

}
