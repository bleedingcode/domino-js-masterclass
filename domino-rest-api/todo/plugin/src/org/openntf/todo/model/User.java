package org.openntf.todo.model;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.openntf.todo.domino.Utils;
import org.openntf.todo.exceptions.DataNotAcceptableException;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username = null;
	private DatabaseAccess access = null;

	public User(String username) {
		setUsername(username);
		DatabaseAccess access = new DatabaseAccess();
	}

	/**
	 * If Domino authentication is in operation for REST access, this property will be ignored for the CurrentUser. If
	 * not, the REST service will use the username passed, replacing &#39;@&#39; with &#39;_&#39;, append a computed OU,
	 * and use the server&#39;s O.
	 * 
	 * @return username
	 **/
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		// Modified from Swagger, to handle different authentication factories
		this.username = Utils.getAsUsername(username);
	}

	/**
	 * Get access
	 * 
	 * @return access
	 **/
	public DatabaseAccess getAccess() {
		return access;
	}

	public void setAccess(DatabaseAccess access) {
		this.access = access;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		User user = (User) o;
		return Objects.equals(this.username, user.username) && Objects.equals(this.access, user.access);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, access);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class User {\n");

		sb.append("    username: ").append(toIndentedString(username)).append("\n");
		sb.append("    access: ").append(toIndentedString(access)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}

	public boolean isValidForUpdate() throws DataNotAcceptableException {
		if (StringUtils.isEmpty(getUsername())) {
			throw new DataNotAcceptableException("Username is missing");
		}
		if (null == getAccess()) {
			throw new DataNotAcceptableException("No database access has been provided for " + getUsername());
		}
		if (null == getAccess().getLevel()) {
			throw new DataNotAcceptableException("No database access level has been requested for " + getUsername());
		}
		return true;
	}

}
