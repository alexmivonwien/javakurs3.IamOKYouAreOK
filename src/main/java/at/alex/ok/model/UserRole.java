package at.alex.ok.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table (name = "user_role", schema = "iamok")
@IdClass(UserRoleId.class)
public class UserRole {

	@Id
	private int user_id;
	@Id
	private int role_id;

	private String username;
	private String role;
	
	@Override
	public boolean equals(Object another) {
		if (another == null || !(another instanceof UserRole)) {
			return false;
		}
		UserRole anotherUserRole = (UserRole) another;
		return  ( getRole_id() == anotherUserRole.getRole_id() && getUser_id() == anotherUserRole.getUser_id() );
	}

	@Override
	public int hashCode() {
		return getRole_id() ^ getUser_id();
	}


	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getRole_id() {
		return role_id;
	}

	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
