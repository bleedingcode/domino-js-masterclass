package org.openntf.todo.domino;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.openntf.domino.ACL;
import org.openntf.domino.ACL.Level;
import org.openntf.domino.ACLEntry;
import org.openntf.domino.Database;
import org.openntf.domino.Database.DBPrivilege;
import org.openntf.domino.Document;
import org.openntf.domino.Session;
import org.openntf.domino.design.DatabaseDesign.DbProperties;
import org.openntf.domino.design.DesignColumn;
import org.openntf.domino.design.DesignColumn.SortOrder;
import org.openntf.domino.design.DesignView;
import org.openntf.domino.design.impl.DatabaseDesign;
import org.openntf.domino.utils.DominoUtils;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.xots.Xots;
import org.openntf.todo.ToDoUtils;
import org.openntf.todo.exceptions.DataNotAcceptableException;
import org.openntf.todo.exceptions.DatabaseModuleException;
import org.openntf.todo.exceptions.DocumentNotFoundException;
import org.openntf.todo.exceptions.InvalidMetaversalIdException;
import org.openntf.todo.exceptions.StoreNotFoundException;
import org.openntf.todo.model.DatabaseAccess;
import org.openntf.todo.model.DatabaseAccess.AccessLevel;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.Store.StoreType;
import org.openntf.todo.model.ToDo;
import org.openntf.todo.model.ToDo.Priority;
import org.openntf.todo.model.User;

public class ToDoStoreFactory {
	private Map<String, Store> stores = new ConcurrentHashMap<String, Store>();
	public static String STORE_NOT_FOUND_OR_ACCESS_ERROR = "The store could not be found with the name or replicaId passed, or you do not have access to that store";
	public static String DOCUMENT_NOT_FOUND_ERROR = "The ToDo with that ID could not be found";
	public static String USER_NOT_AUTHORIZED_ERROR = "You are not authorized to perform this operation";
	public static String INVALID_METAVERSAL_ID_ERROR = "The value passed is not a valid metaversal id";
	private static ToDoStoreFactory INSTANCE;

	public static ToDoStoreFactory getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new ToDoStoreFactory();
		}
		return INSTANCE;
	}

	/**
	 * Load all stores from ToDo Catalog into memory
	 * 
	 * @return ConcurrentHashMap of Store objects, with replicaID as key
	 */
	public Map<String, Store> getStores() {
		if (null == stores) {
			stores = new ConcurrentHashMap<String, Store>();
		}
		return stores;
	}

	public List<Store> getStoresForCurrentUser() {
		String username = Factory.getSession(SessionType.CURRENT).getEffectiveUserName();

		List<Store> userStores = new ArrayList<Store>();
		Map<String, Store> stores = ToDoStoreFactory.getInstance().getStores();
		for (String key : stores.keySet()) {
			Store store = stores.get(key);
			DatabaseAccess access = queryAccess(store, username);
			if (!access.getLevel().equals(AccessLevel.NO_ACCESS)) {
				userStores.add(store);
			}
		}

		return userStores;
	}

	/**
	 * Add an NSF as a Store object to stores ConcurrentHashMap and writes to ToDo Catalog
	 * 
	 * @param db
	 *            NSF to add
	 */
	public Store addStore(Database db) {
		Store store = createStoreFromDatabase(db);
		synchronized (stores) {
			stores.put(store.getReplicaId(), store);
		}
		return store;
	}

	/**
	 * Converts NSF to a Store object, serializing to ToDo catalog
	 * 
	 * @param db
	 */
	public Store createStoreFromDatabase(Database db) {
		Store store = new Store();
		store.setName(Utils.getDbName(db));
		store.setTitle(db.getTitle());
		store.setReplicaId(db.getReplicaID());
		String[] cats = StringUtils.split(db.getCategories(), ",");
		for (String cat : cats) {
			for (StoreType type : StoreType.values()) {
				if (StringUtils.equalsIgnoreCase(type.getValue(), cat)) {
					store.setType(type);
					break;
				}
			}
		}
		serializeStoreToCatalog(store);
		return store;
	}

	public void serializeStoreToCatalog(Store store) {
		Session sess = Factory.getSession(SessionType.NATIVE);
		Database todoCatalog = sess.getDatabase(Store.TODO_PATH + "catalog.nsf");
		Document doc = todoCatalog.getDocumentByUNID(DominoUtils.toUnid(store.getReplicaId()));
		if (null == doc) {
			doc = todoCatalog.createDocument();
			doc.setUniversalID(DominoUtils.toUnid(store.getReplicaId()));
		}
		doc.replaceItemValue("replicaId", store.getReplicaId());
		doc.replaceItemValue("name", store.getName());
		doc.replaceItemValue("title", store.getTitle());
		doc.replaceItemValue("type", store.getType().getValue());
		doc.save();
	}

	public Store createStoreFromDoc(Document doc) {
		Store store = new Store();
		store.setReplicaId(doc.getItemValueString("replicaId"));
		store.setName(doc.getItemValueString("name"));
		store.setTitle(doc.getItemValueString("title"));
		String typeFromDoc = doc.getItemValueString("type");
		for (StoreType type : StoreType.values()) {
			if (type.getValue().equals(typeFromDoc)) {
				store.setType(type);
				break;
			}
		}
		return store;
	}

	/**
	 * Get a Store object for the current session based on a Key
	 * 
	 * @param sess
	 *            relevant Domino Session
	 * @param key
	 *            ReplicaID or filepath
	 * @return Store object for the NSF or null
	 */
	public Store getStoreUnchecked(Session sess, String key) {
		if (getStores().containsKey(key)) {
			return getStores().get(key);
		} else {
			Database db = sess.getDatabase(key);
			if (null == db) {
				return null;
			} else {
				String repId = db.getReplicaID();
				if (!getStores().containsKey(repId)) {
					addStore(db);
				}

				return getStores().get(repId);
			}
		}
	}

	public Store getStore(String key) throws StoreNotFoundException {
		Store store = getStoreUnchecked(Factory.getSession(SessionType.CURRENT), key);
		if (null == store) {
			throw new StoreNotFoundException();
		}
		return store;
	}

	public Store getStoreAsNative(String key) throws StoreNotFoundException {
		Store store = getStoreUnchecked(Factory.getSession(SessionType.NATIVE), key);
		if (null == store) {
			throw new StoreNotFoundException();
		}
		return store;
	}

	public boolean createStoreDoesStoreExist(String key) {
		Store store = getStoreUnchecked(Factory.getSession(SessionType.NATIVE), key);
		return store != null;
	}

	public ToDo serializeToStore(Store store, ToDo todo)
			throws DataNotAcceptableException, StoreNotFoundException, DocumentNotFoundException {
		Document doc = null;
		if (null == store) {
				throw new DataNotAcceptableException("Store must be supplied");
			}
			Database db = Factory.getSession(SessionType.CURRENT).getDatabase(store.getReplicaId());
			doc = db.createDocument();
		todo = updateDocFromToDo(doc, todo);
		return todo;
	}

	public ToDo updateToDo(ToDo todo)
			throws StoreNotFoundException, DocumentNotFoundException, InvalidMetaversalIdException {
		Document doc = getToDoDoc(todo.getMetaversalId());
		updateDocFromToDo(doc, todo);
		return todo;
	}

	private ToDo updateDocFromToDo(Document doc, ToDo todo) {
		boolean isNew = false;
		if (doc.isNewNote()) {
			isNew = true;
		}
		doc.replaceItemValue("taskName", todo.getTaskName());
		doc.replaceItemValue("description", todo.getDescription());
		doc.replaceItemValue("author", Utils.getCurrentUsername());
		if (null == todo.getPriority()) {
			todo.setPriority(Priority.LOW);
		}
		doc.replaceItemValue("priority", todo.getPriority().getValue());
		doc.replaceItemValue("status", todo.getStatus().getValue());
		doc.replaceItemValue("dueDate", todo.getDueDate());
		doc.replaceItemValue("assignedTo", todo.getAssignedTo());
		doc.save();
		if (isNew) {
			todo.setMetaversalId(doc.getMetaversalID());
		}
		return todo;
	}

	public ToDo getToDoFromMetaversalId(String metaversalId)
			throws StoreNotFoundException, DocumentNotFoundException, InvalidMetaversalIdException {
		Document doc = getToDoDoc(metaversalId);

		ToDo todo = new ToDo();
		todo.setMetaversalId(doc.getMetaversalID());
		todo.setAuthor(doc.getAuthors().get(0));
		todo.setTaskName(doc.getItemValueString("taskName"));
		todo.setDescription(doc.getItemValueString("description"));
		todo.setDueDate(doc.getItemValue("dueDate", Date.class));
		todo.setAssignedTo(doc.getItemValueString("assignedTo"));
		String storedPriority = doc.getItemValueString("priority");
		for (Priority priority : Priority.values()) {
			if (priority.getValue().equals(storedPriority)) {
				todo.setPriority(priority);
				break;
			}
		}
		String storedStatus = doc.getItemValueString("status");
		for (ToDo.Status status : ToDo.Status.values()) {
			if (status.getValue().equals(storedStatus)) {
				todo.setStatus(status);
				break;
			}
		}
		return todo;
	}

	public Document getToDoDoc(String metaversalId)
			throws StoreNotFoundException, DocumentNotFoundException, InvalidMetaversalIdException {
		try {
			Utils.validateMetaversalId(metaversalId);
			Document doc = Factory.getSession(SessionType.CURRENT).getDocumentByMetaversalID(metaversalId);
			if (null == doc) {
				throw new DocumentNotFoundException();
			}
			return doc;
		} catch (DocumentNotFoundException de) {
			throw de;
		} catch (Exception e) {
			if (null != getStoreAsNative(StringUtils.left(metaversalId, 16))) {
				throw new DocumentNotFoundException();
			}
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	public boolean deleteToDoDoc(String metaversalId)
			throws StoreNotFoundException, DocumentNotFoundException, InvalidMetaversalIdException {
		Document doc = getToDoDoc(metaversalId);
		return doc.remove(true);
	}

	/**
	 * Load all stores from ToDo Catalog or creates ToDo Catalog database, done via HttpService 30 seconds after server
	 * starts
	 */
	public void loadStores() {
		Future<Map<String, Store>> result = Xots.getService().submit(new StoreLoader());

		// There is only
		synchronized (stores) {
			try {
				stores.clear();
				stores.putAll(result.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateToDoNSFTitle(Store store) {
		Database db = Factory.getSession(SessionType.NATIVE).getDatabase(store.getReplicaId());
		db.setTitle(store.getTitle());
	}

	public Store createToDoNSF(String title, String name, StoreType type) throws DatabaseModuleException {
		try {
			Database db = Factory.getSession(SessionType.NATIVE)
					.createBlankDatabase(name);
			db.setTitle(title);
			db.setCategories(type.getValue());
			db.setListInDbCatalog(false);
			DatabaseDesign dbDesign = (DatabaseDesign) db.getDesign();
			HashMap<DbProperties, Boolean> props = new HashMap<DbProperties, Boolean>();
			props.put(DbProperties.USE_JS, false);
			props.put(DbProperties.NO_URL_OPEN, true);
			props.put(DbProperties.SHOW_IN_OPEN_DIALOG, false);
			dbDesign.setDatabaseProperties(props);
			dbDesign.save();

			// Create views
			DesignView byStatus = dbDesign.createView();
			byStatus.setSelectionFormula("SELECT Form=\"ToDo\"");
			byStatus.setName("byStatus");
			DesignColumn col = byStatus.addColumn();
			col.setCategorized(true);
			col.setSortOrder(SortOrder.ASCENDING);
			col.setTitle("Status");
			col.setItemName("status");
			col = byStatus.addColumn();
			col.setSortOrder(SortOrder.ASCENDING);
			col.setTitle("Due Date");
			col.setItemName("dueDate");
			col = byStatus.addColumn();
			col.setTitle("Task Name");
			col.setItemName("taskName");
			byStatus.save();
			db.getView("byStatus").setDefaultView(true);

			DesignView byAssignee = dbDesign.createView();
			byAssignee.setSelectionFormula("SELECT Form=\"ToDo\"");
			byAssignee.setName("byAssignee");
			col = byAssignee.addColumn();
			col.setCategorized(true);
			col.setSortOrder(SortOrder.ASCENDING);
			col.setTitle("Assignee");
			col.setItemName("assignedTo");
			col = byAssignee.addColumn();
			col.setCategorized(true);
			col.setSortOrder(SortOrder.ASCENDING);
			col.setTitle("Status");
			col.setItemName("status");
			col = byAssignee.addColumn();
			col.setSortOrder(SortOrder.ASCENDING);
			col.setTitle("Due Date");
			col.setItemName("dueDate");
			col = byAssignee.addColumn();
			col.setTitle("Task Name");
			col.setItemName("taskName");
			byAssignee.save();

			DesignView byPriority = dbDesign.createView();
			byPriority.setSelectionFormula("SELECT Form=\"ToDo\"");
			byPriority.setName("byPriority");
			col = byPriority.addColumn();
			col.setCategorized(true);
			col.setSortOrder(SortOrder.ASCENDING);
			col.setTitle("Priority");
			col.setItemName("priority");
			col = byPriority.addColumn();
			col.setCategorized(true);
			col.setSortOrder(SortOrder.ASCENDING);
			col.setTitle("Status");
			col.setItemName("status");
			col = byPriority.addColumn();
			col.setSortOrder(SortOrder.ASCENDING);
			col.setTitle("Due Date");
			col.setItemName("dueDate");
			col = byPriority.addColumn();
			col.setTitle("Task Name");
			col.setItemName("taskName");
			byPriority.save();

			DesignView byDueDate = dbDesign.createView();
			byDueDate.setSelectionFormula("SELECT Form=\"ToDo\"");
			byDueDate.setName("byDueDate");
			col = byDueDate.addColumn();
			col.setSortOrder(SortOrder.ASCENDING);
			col.setTitle("Due Date");
			col.setItemName("dueDate");
			col = byDueDate.addColumn();
			col.setTitle("Task Name");
			col.setItemName("taskName");
			byDueDate.save();

			// Set ACL access
			ACL acl = db.getACL();
			acl.addRole("Admin");
			ACLEntry servers = acl.getEntry("LocalDomainServers");
			if (null == servers) {
				servers = acl.createACLEntry("LocalDomainServers", Level.MANAGER);
			} else {
				servers.setLevel(Level.MANAGER);
			}
			servers.setCanDeleteDocuments(true);
			servers.enableRole("Admin");
			ACLEntry admins = acl.createACLEntry("LocalDomainAdmins", Level.MANAGER);
			admins.setCanDeleteDocuments(true);
			admins.enableRole("Admin");
			acl.createACLEntry("Anonymous", Level.NOACCESS);

			// Add user to access
			DatabaseAccess access = new DatabaseAccess();
			access.setAllowDelete(true);
			access.setDbName(db.getFilePath().toLowerCase());
			access.setLevel(AccessLevel.ADMIN);
			access.setReplicaId(db.getReplicaID());
			addAccess(acl, Factory.getSession(SessionType.CURRENT).getEffectiveUserName(), access);

			// Add to ConcurrentHashMap of stores
			return addStore(db);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseModuleException(ToDoUtils.getErrorMessage(e));
		}
	}

	public void updateAccess(Store store, User user) throws DatabaseModuleException {
		Database db = Factory.getSession(SessionType.NATIVE).getDatabase(store.getReplicaId());
		ACL acl = db.getACL();
		addAccess(acl, user.getUsername(), user.getAccess());
	}

	public void addAccess(ACL acl, String username, DatabaseAccess access) throws DatabaseModuleException {
		try {
			ACLEntry user = acl.createACLEntry(username, Level.NOACCESS);
			user.setUserType(ACLEntry.TYPE_PERSON);
			switch (access.getLevel()) {
			case ADMIN:
				user.setLevel(Level.EDITOR);
				user.enableRole("Admin");
			case EDITOR:
				user.setLevel(Level.EDITOR);
				break;
			case READER:
				user.setLevel(Level.READER);
				break;
			default:
				// No access is fine
			}
			if (access.getAllowDelete()) {
				user.setCanDeleteDocuments(true);
			} else {
				user.setCanDeleteDocuments(false);
			}
			acl.save();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseModuleException(ToDoUtils.getErrorMessage(e));
		}
	}

	public DatabaseAccess queryAccess(Store store, String username) {
		Database db = Factory.getSession(SessionType.NATIVE).getDatabase(store.getReplicaId());
		Vector<String> roles = db.queryAccessRoles(username);
		int level = db.queryAccess(username);
		Set<DBPrivilege> priv = db.queryAccessPrivilegesEx(username);
		DatabaseAccess dbAccess = new DatabaseAccess();
		dbAccess.setReplicaId(store.getReplicaId());
		dbAccess.setDbName(store.getName());
		if (roles.contains("[Admin]")) {
			dbAccess.setLevel(AccessLevel.ADMIN);
		} else if (level == Level.EDITOR.getValue()) {
			dbAccess.setLevel(AccessLevel.EDITOR);
		} else if (level == Level.READER.getValue()) {
			dbAccess.setLevel(AccessLevel.READER);
		} else {
			dbAccess.setLevel(AccessLevel.NO_ACCESS);
		}
		if (priv.contains(DBPrivilege.DELETE_DOCS)) {
			dbAccess.setAllowDelete(true);
		} else {
			dbAccess.setAllowDelete(false);
		}
		return dbAccess;
	}

	public boolean userIsAdmin(Store store, String username) {
		Database db = Factory.getSession(SessionType.NATIVE).getDatabase(store.getReplicaId());
		Vector<String> roles = db.queryAccessRoles(username);
		return roles.contains("[Admin]");
	}

}
