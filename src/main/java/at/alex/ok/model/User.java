package at.alex.ok.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;

@Entity
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_FB_PASSWORD = ":facebook";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="userid_seq")
	@SequenceGenerator(
			name="userid_seq",
			sequenceName="course_sequence",
			allocationSize=1,
			initialValue = 11
		)
	private int id;
	


	@Column
	private String username;

	@Column
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column
	private String password;

	// CascadeType.PERSIST : means that save() or persist() operations cascade
	// to related entities.
	// http://howtodoinjava.com/2014/09/25/hibernate-jpa-cascade-types/
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	@JoinTable(name = "USER_ROLE", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "role_id") })
	private Set<Role> roles;

	public User() {
	}

	public User(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public boolean equals(Object another) {
		if (another == null || !(another instanceof User)) {
			return false;
		}
		User anotherUser = (User) another;
		return this.getUsername().equals(anotherUser.getUsername());
	}

	public int hashCode() {
		return getUsername().hashCode();
	}
	
	@Override
	public String toString(){
		
		return this.username + this.id;
	}

}
