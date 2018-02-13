package org.openntf.todo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.openntf.domino.ACL;
import org.openntf.domino.ACL.Level;
import org.openntf.domino.ACLEntry;
import org.openntf.domino.Database;
import org.openntf.domino.Session;
import org.openntf.domino.design.DatabaseDesign.DbProperties;
import org.openntf.domino.design.DesignColumn;
import org.openntf.domino.design.DesignColumn.SortOrder;
import org.openntf.domino.design.DesignView;
import org.openntf.domino.design.impl.DatabaseDesign;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.xots.Xots;
import org.openntf.todo.exceptions.DatabaseModuleException;
import org.openntf.todo.httpService.StoreLoader;
import org.openntf.todo.model.DatabaseAccess;
import org.openntf.todo.model.DatabaseAccess.AccessLevel;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.Store.StoreType;

public class ToDoStoreFactory {
	private Map<String, Store> stores = new ConcurrentHashMap<String, Store>();
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

	/**
	 * Add an NSF as a Store object to stores ConcurrentHashMap and writes to ToDo Catalog
	 * 
	 * @param db
	 *            NSF to add
	 */
	public Store addStore(Database db) {
		Store store = new Store(db);
		synchronized (stores) {
			stores.put(store.getReplicaId(), store);
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
	public Store getStore(Session sess, String key) {
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

	public Store createToDoNSF(Session sess, String title, String name, StoreType type) throws DatabaseModuleException {
		try {
			// Create database using name or user's name if a Personal store
			if (StoreType.PERSONAL.equals(type)) {
				name = Utils.getPersonalStoreName(sess);
			}
			Database db = sess.createBlankDatabase(Store.TODO_PATH + type.getValue() + "/" + name);
			DatabaseDesign dbDesign = (DatabaseDesign) db.getDesign();
			HashMap<DbProperties, Boolean> props = new HashMap<DbProperties, Boolean>();
			props.put(DbProperties.USE_JS, false);
			props.put(DbProperties.NO_URL_OPEN, false);
			props.put(DbProperties.ENHANCED_HTML, true);
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
			ACLEntry servers = acl.createACLEntry("LocalDomainServers", Level.MANAGER);
			servers.setCanDeleteDocuments(true);
			servers.enableRole("Admin");
			acl.createACLEntry("OtherDomainServers", Level.NOACCESS);
			ACLEntry admins = acl.createACLEntry("LocalDomainAdmins", Level.MANAGER);
			admins.setCanDeleteDocuments(true);
			admins.enableRole("Admin");
			acl.createACLEntry("Anonymous", Level.NOACCESS);
			acl.createACLEntry("-Default-", Level.NOACCESS);

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
			throw new DatabaseModuleException(e.getMessage());
		}
	}

	public void addAccess(ACL acl, String username, DatabaseAccess access) throws DatabaseModuleException {
		try {
			ACLEntry user = acl.createACLEntry(username, Level.NOACCESS);
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
			throw new DatabaseModuleException(e.getMessage());
		}
	}

}
