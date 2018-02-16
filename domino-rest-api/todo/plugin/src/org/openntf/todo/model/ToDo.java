package org.openntf.todo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class ToDo implements Serializable {
	private String metaversalId = null;

	private String author = null;

	private String taskName = null;

	private String description = null;

	private Date dueDate = null;

	private Priority priority = null;

	private String assignedTo = null;

	private Status status = null;

	/**
	 * Priority for the ToDo, see enum
	 */
	public enum Priority {
		LOW("Low"),

		MEDIUM("Medium"),

		HIGH("High");

		private String value;

		private Priority(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	/**
	 * Current status of the ToDo. Set via workflow actions, defaulting to New
	 */
	public enum Status {
		NEW("New"),

		REASSIGNED("Reassigned"),

		COMPLETE("Complete");

		private String value;

		private Status(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	/**
	 * Unique ID across all replicas
	 * 
	 * @return unid
	 **/
	public String getMetaversalId() {
		return metaversalId;
	}

	public void setMetaversalId(String metaversalId) {
		this.metaversalId = metaversalId;
	}

	/**
	 * username of the person who creates the ToDo - set automatically via the API from the currentUser passed across
	 * 
	 * @return author
	 **/
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public ToDo taskName(String taskName) {
		this.taskName = taskName;
		return this;
	}

	/**
	 * Brief description of task
	 * 
	 * @return taskName
	 **/
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public ToDo description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Detailed description of the task
	 * 
	 * @return description
	 **/
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ToDo dueDate(Date dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	/**
	 * Date when the task is due in RFC 3339 section 5.6 format 2018-03-19
	 * 
	 * @return dueDate
	 **/
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public ToDo priority(Priority priority) {
		this.priority = priority;
		return this;
	}

	/**
	 * Priority for the ToDo, see enum
	 * 
	 * @return priority
	 **/
	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public ToDo assignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
		return this;
	}

	/**
	 * Name of the person the ToDo is assigned to. If not set, current user will be used
	 * 
	 * @return assignedTo
	 **/
	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	/**
	 * Current status of the ToDo. Set via workflow actions, defaulting to New
	 * 
	 * @return status
	 **/
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ToDo toDo = (ToDo) o;
		return Objects.equals(this.metaversalId, toDo.metaversalId) && Objects.equals(this.author, toDo.author)
				&& Objects.equals(this.taskName, toDo.taskName)
				&& Objects.equals(this.description, toDo.description) && Objects.equals(this.dueDate, toDo.dueDate)
				&& Objects.equals(this.priority, toDo.priority) && Objects.equals(this.assignedTo, toDo.assignedTo)
				&& Objects.equals(this.status, toDo.status);
	}

	@Override
	public int hashCode() {
		return Objects.hash(metaversalId, author, taskName, description, dueDate, priority, assignedTo,
				status);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ToDo {\n");

		sb.append("    unid: ").append(toIndentedString(metaversalId)).append("\n");
		sb.append("    author: ").append(toIndentedString(author)).append("\n");
		sb.append("    taskName: ").append(toIndentedString(taskName)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    dueDate: ").append(toIndentedString(dueDate)).append("\n");
		sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
		sb.append("    assignedTo: ").append(toIndentedString(assignedTo)).append("\n");
		sb.append("    status: ").append(toIndentedString(status)).append("\n");
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
