package at.alex.ok.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A result is an assignment completed by the assignee that is ready to be assessed by a teacher, master, or tutor.
 * Of its attributes, only assessor, dateAssessed, and score can be changed. All other attributes, such as 
 * challenge, assignmentId, dateDue, dateCompleted and filePath are writable only upon creation, and cannot be changed afterwards.
 * The result entity does not refer the parent assignment entity directly in order to avoid selects from the assignment table.
 * 
 * @author User
 *
 */
@Entity
@Table(name = "result")
@Access(AccessType.FIELD)
public class Result implements Challengeable, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", updatable=false, nullable=false)
	private int id;
	
	@JoinColumn(name = "user_id", nullable=false)
	@ManyToOne( optional = false )
	private User ofUser;

	@JoinColumn(name = "assesor_id", nullable=false)
	@ManyToOne( optional = true )
	private User assessor;
	

	@JoinColumn(name = "challenge_id", nullable=false)
	@ManyToOne( optional = false, cascade=CascadeType.MERGE )
	private Challenge challenge;
	
	private String filePath;
	
	/**
	 * The parent assignment entity is not referred directly, to deliberately avoid selects from the assignment table
	 */
	private int assignmentId;

	private Date dateStarted;
	
	private Date dateDue;

	private Date dateCompleted;
	
	private Date dateAssessed;
	
	private int score;
	

	public User getAssessor() {
		return assessor;
	}

	public void setAssessor(User assessor) {
		this.assessor = assessor;
	}

	public Date getDateDue() {
		return dateDue;
	}

	public void setDateDue(Date dateDue) {
		this.dateDue = dateDue;
	}

	public int getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(int assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Date getDateAssessed() {
		return dateAssessed;
	}

	public void setDateAssessed(Date dateAssessed) {
		this.dateAssessed = dateAssessed;
	}

	
	public Date getDateStarted() {
		return dateStarted;
	}

	public void setDateStarted(Date dateStarted) {
		this.dateStarted = dateStarted;
	}

	public Date getDateCompleted() {
		return dateCompleted;
	}

	public void setDateCompleted(Date dateCompleted) {
		this.dateCompleted = dateCompleted;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getOfUser() {
		return ofUser;
	}

	public void setOfUser(User user) {
		this.ofUser = user;
	}

	public Challenge getChallenge() {
		return challenge;
	}

	public void setChallenge(Challenge challenge) {
		this.challenge = challenge;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
