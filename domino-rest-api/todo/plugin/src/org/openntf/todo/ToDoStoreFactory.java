package org.openntf.todo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.openntf.domino.Database;
import org.openntf.domino.Session;
import org.openntf.domino.xots.Xots;
import org.openntf.todo.httpService.StoreLoader;
import org.openntf.todo.model.Store;

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

}
