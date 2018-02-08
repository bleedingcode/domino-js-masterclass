package org.openntf.todo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.openntf.domino.Database;
import org.openntf.domino.Session;
import org.openntf.domino.design.DatabaseDesign.DbProperties;
import org.openntf.domino.design.impl.DatabaseDesign;
import org.openntf.domino.xots.Xots;
import org.openntf.todo.exceptions.DatabaseModuleException;
import org.openntf.todo.httpService.StoreLoader;
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

	public Map<String, Store> getStores() {
		if (null == stores) {
			stores = new ConcurrentHashMap<String, Store>();
		}
		return stores;
	}

	public void addStore(Database db) {
		Store store = new Store(db);
		synchronized (stores) {
			stores.put(store.getReplicaId(), store);
		}
	}

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

	public void loadStores() {
		Future<Map<String, Store>> result = Xots.getService().submit(new StoreLoader());

		// There is only
		synchronized (stores) {
			try {
				stores.clear();
				stores.putAll(result.get());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void createToDoNSF(Session sess, String title, String name, StoreType type) throws DatabaseModuleException {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseModuleException(e.getMessage());
		}
	}

}
