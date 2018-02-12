package org.openntf.todo.model;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.openntf.domino.Database;
import org.openntf.domino.Document;
import org.openntf.domino.Session;
import org.openntf.domino.utils.DominoUtils;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

public class Store implements Serializable {
	private static final long serialVersionUID = 1L;
	private String replicaId;
	private String title;
	private String name;
	public final static String TODO_PATH = "openntf/todos/";

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

	public Store(Document doc) {
		setReplicaId(doc.getItemValueString("replicaId"));
		setName(doc.getItemValueString("name"));
		setTitle(doc.getItemValueString("title"));
		String typeFromDoc = doc.getItemValueString("type");
		for (StoreType type : StoreType.values()) {
			if (type.getValue().equals(typeFromDoc)) {
				setType(type);
				break;
			}
		}
		return;
	}

	/**
	 * Converts NSF to a Store object, serializing to ToDo catalog
	 * 
	 * @param db
	 */
	public Store(Database db) {
		setName(db.getFilePath().toLowerCase());
		setTitle(db.getTitle());
		setReplicaId(db.getReplicaID());
		String[] cats = StringUtils.split(db.getCategories(), ",");
		for (String cat : cats) {
			for (StoreType type : StoreType.values()) {
				if (StringUtils.equalsIgnoreCase(type.getValue(), cat)) {
					setType(type);
					break;
				}
			}
		}
		serializeToCatalog();
		return;
	}

	/**
	 * Writes database values to document in ToDo catalog
	 */
	public void serializeToCatalog() {
		Session sess = Factory.getSession(SessionType.NATIVE);
		Database todoCatalog = sess.getDatabase(Store.TODO_PATH + "catalog.nsf");
		Document doc = todoCatalog.getDocumentByUNID(DominoUtils.toUnid(getReplicaId()));
		if (null == doc) {
			doc = todoCatalog.createDocument();
			doc.setUniversalID(DominoUtils.toUnid(getReplicaId()));
		}
		doc.replace("replicaId", getReplicaId());
		doc.replace("name", getName());
		doc.replace("title", getTitle());
		doc.replace("type", getType().getValue());
		doc.save();
	}

	/**
	 * 16 character hexadecimal string corresponding to NSF&#39;s replica id
	 * 
	 * @return replicaId
	 **/
	public String getReplicaId() {
		return replicaId;
	}

	public Store setReplicaId(String replicaId) {
		this.replicaId = replicaId;
		return this;
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
