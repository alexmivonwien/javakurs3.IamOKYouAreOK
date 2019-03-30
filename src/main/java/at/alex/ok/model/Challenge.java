package at.alex.ok.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import at.alex.ok.model.enums.Category;
import at.alex.ok.model.enums.Level;

@Entity
@Access(AccessType.FIELD)
public class Challenge implements Challengeable, Serializable {

	private static final long serialVersionUID = 1L;

	public static final int SHORT_DESCR_LENGHT = 50;

	public static final String SHORT_DESCR_ENDSTR = "... ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	private String name;

	@Enumerated(EnumType.STRING)
	private Category category;
	
	@Enumerated(EnumType.STRING)
	private Level level;

	private String description;


	@JoinColumn(name = "user_id", nullable=false)
	@ManyToOne( optional = false, cascade=CascadeType.MERGE )
	private User createdBy;

	private Date createdOn;
	
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getDescription() {
		return description;
	}

	public String getDescriptionShort() {
		if (description ==null || description.length() <= SHORT_DESCR_LENGHT) {
			return description;
		}
		return description.substring(0,
				SHORT_DESCR_LENGHT - SHORT_DESCR_ENDSTR.length())
				+ SHORT_DESCR_ENDSTR;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Challenge getChallenge(){
		return this;
	};
}
