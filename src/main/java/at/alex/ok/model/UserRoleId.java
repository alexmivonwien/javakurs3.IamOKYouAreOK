package at.alex.ok.model;

import java.io.Serializable;

public class UserRoleId implements Serializable {

	private int user_id;
	private int role_id;

	private static final long serialVersionUID = 1L;

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

	public boolean equals(Object another) {

		if (another == null || !(another instanceof UserRoleId)) {
			return false;
		}

		UserRoleId anotherUserRoleId = (UserRoleId) another;

		return (this.user_id == anotherUserRoleId.getUser_id() && this.role_id == anotherUserRoleId
				.getRole_id());

	}

	public int hashCode() {
		return this.user_id ^ this.role_id;
	}
}
