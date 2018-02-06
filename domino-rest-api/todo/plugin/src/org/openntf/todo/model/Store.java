package org.openntf.todo.model;

import java.io.Serializable;
import java.util.Objects;

public class Store implements Serializable {
	private static final long serialVersionUID = 1L;

	private String replicaId = null;

	private String title = null;

	private String name = null;

	/**
	 * ToDo type, see enum&#39;
	 */
	public enum StoreType {
		PERSONAL("Personal"), TEAM("Team");

		private String value;

		private StoreType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	private StoreType type = null;

	/**
	 * 16 character hexadecimal string corresponding to NSF&#39;s replica id
	 * 
	 * @return replicaId
	 **/
	public String getReplicaId() {
		return replicaId;
	}

	public Store title(String title) {
		this.title = title;
		return this;
	}

	/**
	 * Display name for store, e.g. a project name or an individual user for personal ToDos
	 * 
	 * @return title
	 **/
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Store name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * File name for the NSF, appended to the folder name where all ToDo stored are stored
	 * 
	 * @return name
	 **/
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Store type(StoreType type) {
		this.type = type;
		return this;
	}

	/**
	 * ToDo type, see enum&#39;
	 * 
	 * @return type
	 **/
	public StoreType getType() {
		return type;
	}

	public void setType(StoreType type) {
		this.type = type;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Store store = (Store) o;
		return Objects.equals(this.replicaId, store.replicaId) && Objects.equals(this.title, store.title)
				&& Objects.equals(this.name, store.name) && Objects.equals(this.type, store.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(replicaId, title, name, type);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Store {\n");

		sb.append("    replicaId: ").append(toIndentedString(replicaId)).append("\n");
		sb.append("    title: ").append(toIndentedString(title)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("    type: ").append(toIndentedString(type)).append("\n");
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
