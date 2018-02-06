package org.openntf.todo.model;

import java.io.Serializable;
import java.util.Objects;

public class DatabaseAccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private AccessLevel level;
	private String replicaId;
	private String dbName;
	private boolean allowDelete;

	public String getReplicaId() {
		return replicaId;
	}

	public void setReplicaId(String replicaId) {
		this.replicaId = replicaId;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * Application access level. This will be converted to Editor or Reader in Domino ACL
	 * 
	 * @return level
	 **/
	public AccessLevel getLevel() {
		return level;
	}

	public void setLevel(AccessLevel level) {
		this.level = level;
	}

	public DatabaseAccess allowDelete(Boolean allowDelete) {
		this.allowDelete = allowDelete;
		return this;
	}

	/**
	 * Whether or not Delete documents privilege is added
	 * 
	 * @return allowDelete
	 **/
	public Boolean getAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(Boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

	public enum AccessLevel {
		ADMIN("Admin"), READER("Reader"), EDITOR("Editor"), NO_ACCESS("No Access");

		private String label;

		private AccessLevel(String label) {
			this.label = label;
		}

		public String getLabel() {
			return this.label;
		}

		@Override
		public String toString() {
			return String.valueOf(label);
		}

		public static AccessLevel fromValue(String text) {
			for (AccessLevel b : AccessLevel.values()) {
				if (String.valueOf(b.label).equals(text)) {
					return b;
				}
			}
			return null;
		}
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DatabaseAccess userAccess = (DatabaseAccess) o;
		return Objects.equals(this.level, userAccess.level) && Objects.equals(this.allowDelete, userAccess.allowDelete);
	}

	@Override
	public int hashCode() {
		return Objects.hash(level, allowDelete);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class UserAccess {\n");

		sb.append("    level: ").append(toIndentedString(level)).append("\n");
		sb.append("    allowDelete: ").append(toIndentedString(allowDelete)).append("\n");
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

}
