/*******************************************************************************
 * Copyright 2018 Paul Withers
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.openntf.todo.domino;

import java.util.ArrayList;
import java.util.Date;
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
import org.openntf.domino.DateRange;
import org.openntf.domino.Document;
import org.openntf.domino.DocumentCollection;
import org.openntf.domino.Session;
import org.openntf.domino.View;
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
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.json.ResultParser;
import org.openntf.todo.model.DatabaseAccess;
import org.openntf.todo.model.DatabaseAccess.AccessLevel;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.Store.StoreType;
import org.openntf.todo.model.ToDo;
import org.openntf.todo.model.ToDo.Priority;
import org.openntf.todo.model.User;
import org.openntf.todo.v1.ToDosResource.ViewType;

/**
 * @author Paul Withers
 * 
 *         The class handles all Domino-related code for ToDos
 *
 */
public class ToDoStoreFactory {
	private Map<String, Store> stores = null;
	public static String STORE_NOT_FOUND_OR_ACCESS_ERROR = "The store could not be found with the name or replicaId passed, or you do not have access to that store";
	public static String DOCUMENT_NOT_FOUND_ERROR = "The ToDo with that ID could not be found";
	public static String USER_NOT_AUTHORIZED_ERROR = "You are not authorized to perform this operation";
	public static String INVALID_METAVERSAL_ID_ERROR = "The value passed is not a valid metaversal id";
	private static ToDoStoreFactory INSTANCE;

	/**
	 * @return ToDoStoreFactory instance
	 */
	public static ToDoStoreFactory getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new ToDoStoreFactory();
		}
		return INSTANCE;
	}

	/**
	 * Load all stores from ToDo Catalog into memory. This uses a ConcurrentHashMap because we're never expecting there
	 * to be too many. If we needed more, we'd use a Cache - either using a memcache or Ehcache server, or within the
	 * HttpServer using a Google Guava LoadingCache.
	 * 
	 * @return ConcurrentHashMap of Store objects, with replicaID as key
	 */
	public Map<String, Store> getStores() {
		if (null == stores) {
			loadStores();
		}
		return stores;
	}

	/**
	 * Get all stores the current User has access to
	 * 
	 * @return List of Stores
	 */
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
	 * @return Store corresponding to the relevant NSF
	 */
	public Store addStore(Database db) {
		Store store = initialiseStoreFromDatabase(db);
		synchronized (stores) {
			stores.put(store.getReplicaId(), store);
		}
		return store;
	}

	/**
	 * Converts NSF to a Store object, serializing to ToDo catalog
	 * 
	 * @param db
	 *            NSF to create a Store object from
	 * @return Store corresponding to the relevant NSF
	 */
	public Store initialiseStoreFromDatabase(Database db) {
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

	/**
	 * Writes a Store to the catalog database. Note: we write the json to a single field called json, because it's not
	 * needed in a view
	 * 
	 * @param store
	 *            Store to write to the catalog database
	 */
	public void serializeStoreToCatalog(Store store) {
		Session sess = Factory.getSession(SessionType.NATIVE);
		Database todoCatalog = sess.getDatabase(Store.TODO_PATH + "catalog.nsf");
		Document doc = todoCatalog.getDocumentByUNID(DominoUtils.toUnid(store.getReplicaId()));
		if (null == doc) {
			doc = todoCatalog.createDocument();
			doc.setUniversalID(DominoUtils.toUnid(store.getReplicaId()));
		}
		RequestBuilder<Store> builder = new RequestBuilder<Store>(Store.class);
		doc.replaceItemValue("json", builder.buildJson(store));
		doc.save();
	}

	/**
	 * Takes the Notes Document and extracts the json field, using Gson to convert it to a Store
	 * 
	 * @param doc
	 *            the document in the catalog database
	 * @return Store object
	 */
	public Store deserializeStoreFromDoc(Document doc) {
		String json = doc.getItemValueString("json");
		Store store = new ResultParser<Store>(Store.class).parse(json);
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

	/**
	 * Gets a store based on replica ID or filepath, running as the current user
	 * 
	 * @param key
	 *            Store NSF's replicaId or filepath
	 * @return Store
	 * @throws StoreNotFoundException
	 *             if it can't be found
	 */
	public Store getStore(String key) throws StoreNotFoundException {
		Store store = getStoreUnchecked(Factory.getSession(SessionType.CURRENT), key);
		if (null == store) {
			throw new StoreNotFoundException();
		}
		return store;
	}

	/**
	 * Gets a Store based on replica ID or filepath, running as the server
	 * 
	 * @param key
	 *            Store NSF's replicaId or filepath
	 * @return Store
	 * @throws StoreNotFoundException
	 *             if it can't be found
	 */
	public Store getStoreAsNative(String key) throws StoreNotFoundException {
		Store store = getStoreUnchecked(Factory.getSession(SessionType.NATIVE), key);
		if (null == store) {
			throw new StoreNotFoundException();
		}
		return store;
	}

	/**
	 * Validation method, to check whether a Store NSF already exists, running as the server
	 * 
	 * @param key
	 *            Store NSF's replicaId or filepath
	 * @return boolean whether NSF exists or not
	 */
	public boolean checkStoreExists(String key) {
		Store store = getStoreUnchecked(Factory.getSession(SessionType.NATIVE), key);
		return store != null;
	}

	/**
	 * Gets a collection of ToDos from the By Date view
	 * 
	 * @param store
	 *            Store from which to get the collection
	 * @param viewType
	 *            ViewType, always By Date
	 * @param startDate
	 *            for dueDate field of ToDos
	 * @param endDate
	 *            for dueDate field of ToDos
	 * @return List of ToDos
	 * @throws DatabaseModuleException
	 *             error if Store or View could not be found
	 */
	public List<ToDo> getToDoCollectionRange(Store store, ViewType viewType, Date startDate, Date endDate)
			throws DatabaseModuleException {
		DateRange key = Factory.getSession(SessionType.CURRENT).createDateRange(startDate, endDate);
		return getToDoCollection(store, viewType, key);
	}

	/**
	 * Gets a collection of ToDos based on a key. . If we wanted to improve performance then, depending on memory, we
	 * could use some kind of cache. We would need to manage the size, so a Google Guava LoadingCache would make sense
	 * using the metaversalId as the key (loading from the Document) or a memcache / Ehcache server.
	 * 
	 * @param store
	 *            Store from which to get the collection
	 * @param viewType
	 *            ViewType to identify which view in the NSF
	 * @param key
	 *            Object with which to get collection
	 * @return List of ToDos
	 * @throws DatabaseModuleException
	 *             error if Store or View could not be found
	 */
	public List<ToDo> getToDoCollection(Store store, ViewType viewType, Object key) throws DatabaseModuleException {
		List<ToDo> todos = new ArrayList<ToDo>();
		try {
			Database db = Factory.getSession(SessionType.CURRENT).getDatabase(store.getReplicaId());
			View view = getView(db, viewType);
			DocumentCollection dc = view.getAllDocumentsByKey(key);
			for (Document doc : dc) {
				ToDo todo = deserializeToDoFromDoc(doc);
				todos.add(todo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseModuleException(ToDoUtils.getErrorMessage(e));
		}
		return todos;
	}

	/**
	 * Gets the relevant view based on the ViewType passed from the NSF passed
	 * 
	 * @param db
	 *            Database ToDo instance
	 * @param viewType
	 *            ViewType to identify which view to get
	 * @return View from the ToDo instance NSF
	 * @throws DatabaseModuleException
	 *             error if we can't get a view for the relevant ViewType (defensive coding)
	 */
	public View getView(Database db, ViewType viewType) throws DatabaseModuleException {
		if (viewType.equals(ViewType.STATUS)) {
			return db.getView("byStatus");
		} else if (viewType.equals(ViewType.ASSIGNEE)) {
			return db.getView("byAssignee");
		} else if (viewType.equals(ViewType.PRIORITY)) {
			return db.getView("byPriority");
		} else if (viewType.equals(ViewType.DATE)) {
			return db.getView("byDueDate");
		}
		throw new DatabaseModuleException("Unable to find view for " + viewType.name());
	}

	/**
	 * Creates a new ToDo to the relevant Store, returning the ToDo object
	 * 
	 * @param store
	 *            Store object from which to get the NSF
	 * @param todo
	 *            ToDo to write to the Store
	 * @return ToDo serialized and updated as required (e.g. with metaversalId)
	 * @throws DataNotAcceptableException
	 *             error if Store not passed
	 * @throws StoreNotFoundException
	 *             error if Store couldn't be found as current user
	 */
	public ToDo serializeToStore(Store store, ToDo todo) throws DataNotAcceptableException, StoreNotFoundException {
		Document doc = null;
		if (null == store) {
			throw new DataNotAcceptableException("Store must be supplied");
		}
		Database db = Factory.getSession(SessionType.CURRENT).getDatabase(store.getReplicaId());
		doc = db.createDocument();
		todo = updateDocFromToDo(doc, todo);
		return todo;
	}

	/**
	 * Updates an existing ToDo
	 * 
	 * @param todo
	 *            ToDo to update (PATCH)
	 * @return stored ToDo
	 * @throws StoreNotFoundException
	 *             error if Store NSF could not be found
	 * @throws DocumentNotFoundException
	 *             error if document could not be found (e.g. already deleted)
	 * @throws InvalidMetaversalIdException
	 *             error if the metaversalId passed is not valid
	 */
	public ToDo updateToDo(ToDo todo)
			throws StoreNotFoundException, DocumentNotFoundException, InvalidMetaversalIdException {
		Document doc = getToDoDoc(todo.getMetaversalId());
		updateDocFromToDo(doc, todo);
		return todo;
	}

	/**
	 * Updates the relevant Notes Document from the ToDo. If we wanted to improve performance then, depending on memory,
	 * we could use some kind of cache. We would need to manage the size, so a Google Guava LoadingCache would make
	 * sense using the metaversalId as the key (loading from the Document) or a memcache / Ehcache server.
	 * 
	 * @param doc
	 *            Notes Document
	 * @param todo
	 *            ToDo being passed
	 * @return processed ToDo
	 */
	private ToDo updateDocFromToDo(Document doc, ToDo todo) {
		boolean isNew = false;
		if (doc.isNewNote()) {
			doc.replaceItemValue("Form", "ToDo");
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

	/**
	 * Gets a ToDo based on the metaversalId passed
	 * 
	 * @param metaversalId
	 *            String replicaId + UNID
	 * @return ToDo deserialized from the document
	 * @throws StoreNotFoundException
	 *             error if Store NSF could not be found
	 * @throws DocumentNotFoundException
	 *             error if document could not be found (e.g. already deleted)
	 * @throws InvalidMetaversalIdException
	 *             error if the metaversalId passed is not valid
	 */
	public ToDo getToDoFromMetaversalId(String metaversalId)
			throws StoreNotFoundException, DocumentNotFoundException, InvalidMetaversalIdException {
		Document doc = getToDoDoc(metaversalId);

		ToDo todo = deserializeToDoFromDoc(doc);
		return todo;
	}

	/**
	 * Gets a ToDo Notes Document based on a metaversalId
	 * 
	 * @param metaversalId
	 *            passed String replicaId + UNID
	 * @return Document for the relevant metaversalId
	 * @throws StoreNotFoundException
	 *             error if Store NSF could not be found
	 * @throws DocumentNotFoundException
	 *             error if document could not be found (e.g. already deleted)
	 * @throws InvalidMetaversalIdException
	 *             error if the metaversalId passed is not valid
	 */
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

	/**
	 * Takes the Notes Document and converts to a ToDo. We store data in separate fields because we display the content
	 * in a view (although we could store the full json in a single field and just store view fields separately - then
	 * use Gson to deserialize)
	 * 
	 * @param doc
	 *            Notes Document
	 * @return ToDo deserialized from the document
	 */
	public ToDo deserializeToDoFromDoc(Document doc) {
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

	/**
	 * Deletes a ToDo Notes Document
	 * 
	 * @param metaversalId
	 *            passed String replicaId + UNID
	 * @return boolean success or failure
	 * @throws StoreNotFoundException
	 *             error if Store NSF could not be found
	 * @throws DocumentNotFoundException
	 *             error if document could not be found (e.g. already deleted)
	 * @throws InvalidMetaversalIdException
	 *             error if the metaversalId passed is not valid
	 */
	public boolean deleteToDoDoc(String metaversalId)
			throws StoreNotFoundException, DocumentNotFoundException, InvalidMetaversalIdException {
		Document doc = getToDoDoc(metaversalId);
		return doc.remove(true);
	}

	/**
	 * Load all stores from ToDo Catalog or creates ToDo Catalog database, done via HttpService 30 seconds after server
	 * starts or first HTTP call, if sooner
	 */
	public void loadStores() {
		if (stores != null) {
			return;
		}
		Future<Map<String, Store>> result = Xots.getService().submit(new StoreLoader());
		stores = new ConcurrentHashMap<String, Store>();

		// There is only one
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

	/**
	 * Marks ToDos due before now as Overdue in all ToDo NSFs
	 * 
	 * @param nextUrl
	 *            URL to send list of updated ToDos to
	 * @throws DatabaseModuleException
	 *             error if Store could not be found
	 */
	public void markOverdue(String nextUrl) throws DatabaseModuleException {
		try {
			for (String replicaId : getStores().keySet()) {
				Xots.getService().submit(new StoreMarkToDosOverdueRunner(replicaId, nextUrl));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseModuleException(ToDoUtils.getErrorMessage(e));
		}
	}

	/**
	 * Changes the title of a Store NSF
	 * 
	 * @param store
	 *            Store to update
	 */
	public void updateToDoNSFTitle(Store store) {
		Database db = Factory.getSession(SessionType.NATIVE).getDatabase(store.getReplicaId());
		db.setTitle(store.getTitle());
	}

	/**
	 * Creates a ToDo NSF instance
	 * 
	 * @param title
	 *            for the new ToDo NSF
	 * @param name
	 *            filepath for the new ToDo NSF
	 * @param type
	 *            StoreType, Personal or Team
	 * @return Store corresponding to ToDo NSF
	 * @throws DatabaseModuleException
	 *             error if the ToDo NSF could not be created
	 */
	public Store createToDoNSF(String title, String name, StoreType type) throws DatabaseModuleException {
		try {
			// Create a blank NSF and set basic properties
			Database db = Factory.getSession(SessionType.NATIVE)
					.createBlankDatabase(name);
			db.setTitle(title);
			db.setCategories(type.getValue());
			db.setListInDbCatalog(false);

			// Some properties can only be set via DXL. Get the DatabaseDesign and set them.
			DatabaseDesign dbDesign = (DatabaseDesign) db.getDesign();
			HashMap<DbProperties, Boolean> props = new HashMap<DbProperties, Boolean>();
			props.put(DbProperties.USE_JS, false);
			props.put(DbProperties.NO_URL_OPEN, true);
			props.put(DbProperties.SHOW_IN_OPEN_DIALOG, false);
			dbDesign.setDatabaseProperties(props);
			dbDesign.save();

			// Create views
			// Create byStatus view
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

			// Create byAssignee view
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

			// Create byPriority view
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

			// Create byDueDate view
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

			// Set ACL access - LocalDomainAdmins, LocalDomainServers, Anonymous
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

			// Add current user to access
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

	/**
	 * Update ACL for a Store based on the User passed. Runs as server
	 * 
	 * @param store
	 *            Store to update
	 * @param user
	 *            User object containing username and updated DatabaseAccess
	 * @throws DatabaseModuleException
	 *             error if ToDo instance NSF can't be retrieved
	 */
	public void updateAccess(Store store, User user) throws DatabaseModuleException {
		Database db = Factory.getSession(SessionType.NATIVE).getDatabase(store.getReplicaId());
		ACL acl = db.getACL();
		addAccess(acl, user.getUsername(), user.getAccess());
	}

	/**
	 * Updates the ACL for a given username and DatabaseAccess
	 * 
	 * @param acl
	 *            ACL object for the ToDo instance NSF
	 * @param username
	 *            for the new ACL entry
	 * @param access
	 *            DatabaseAccess object to use to set access levels
	 * @throws DatabaseModuleException
	 *             if ACL could not be set
	 */
	public void addAccess(ACL acl, String username, DatabaseAccess access) throws DatabaseModuleException {
		try {
			// Get or create ACLEntry
			ACLEntry user = acl.getEntry(username);
			if (null == user) {
				user = acl.createACLEntry(username, Level.NOACCESS);
				user.setUserType(ACLEntry.TYPE_PERSON);
			}

			// Update ACL level
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

			// Set delete rights
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

	/**
	 * Check access for specific username for a ToDo instance NSF
	 * 
	 * @param store
	 *            to which to query access
	 * @param username
	 *            for whom to query
	 * @return DatabaseAccess object for the relevant user
	 */
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

	/**
	 * Checks whether the user has [Admin] role for the ToDo instance NSF
	 * 
	 * @param store
	 *            for the ToDo instance
	 * @param username
	 *            to check for
	 * @return boolean whether or not the user has the [Admin] role
	 */
	public boolean userIsAdmin(Store store, String username) {
		Database db = Factory.getSession(SessionType.NATIVE).getDatabase(store.getReplicaId());
		Vector<String> roles = db.queryAccessRoles(username);
		return roles.contains("[Admin]");
	}

}
