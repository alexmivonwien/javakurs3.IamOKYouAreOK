package at.alex.ok.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import at.alex.ok.model.enums.Status;

@Entity
@Table(name = "Assignment")
@Access(AccessType.FIELD)
public class Assignment implements Challengeable, Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", updatable=false, nullable=false)
	private int id;
	
	@JoinColumn(name = "challenge_id", nullable=false)
	@ManyToOne( optional = false, cascade=CascadeType.MERGE )
	private Challenge challenge;

	@JoinColumn(name = "user_id", nullable=false)
	@ManyToOne( optional = false )
	private User assignedTo;
	private Date dateAssigned;
	private Date dateDue;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	@OneToMany (fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = AssignmentHistory.class, mappedBy = "assignment")
	private List <AssignmentHistory> assignmentHistory = new ArrayList<AssignmentHistory>();

	private String lastFilePath;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLastFilePath() {
		return lastFilePath;
	}
	public void setLastFilePath(String filePath) {
		this.lastFilePath = filePath;
	}
	public User getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(User assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	public Challenge getChallenge() {
		return challenge;
	}
	public void setChallenge(Challenge challenge) {
		this.challenge = challenge;
	}
	
	@Override
	public boolean equals (Object another){
		if (another == null || ! (another instanceof Assignment))
			return false;
		
		Assignment anotherAssignment = (Assignment)another;
		
		return anotherAssignment.getChallenge().getName().equals(this.getChallenge().getName())
				&& anotherAssignment.getStatus().equals(this.getStatus())
				&& anotherAssignment.getAssignedTo().equals(this.getAssignedTo())
				&& anotherAssignment.getDateAssigned().equals(this.getDateAssigned())
				&& anotherAssignment.getDateDue().equals(this.getDateDue());
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Date getDateAssigned() {
		return dateAssigned;
	}
	
	public void setDateAssigned(Date dateAssigned) {
		this.dateAssigned = dateAssigned;
	}
	
	public Date getDateDue() {
		return dateDue;
	}
	
	public void setDateDue(Date dateDue) {
		this.dateDue = dateDue;
	}
	
	public List<AssignmentHistory> getAssignmentHistory() {
		return assignmentHistory;
	}
	
	public void setAssignmentHistory(List<AssignmentHistory> assignmentHistory) {
		this.assignmentHistory = assignmentHistory;
	}
	
}
