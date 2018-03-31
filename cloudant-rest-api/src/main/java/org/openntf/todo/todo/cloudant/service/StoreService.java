package org.openntf.todo.todo.cloudant.service;

import com.cloudant.client.api.model.Permissions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openntf.todo.todo.cloudant.ToDoConstants;
import org.openntf.todo.todo.cloudant.Util;
import org.openntf.todo.todo.cloudant.exceptions.DatabaseModuleException;
import org.openntf.todo.todo.cloudant.exceptions.StoreNotFoundException;
import org.openntf.todo.todo.cloudant.model.Store;
import org.openntf.todo.todo.cloudant.model.ToDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class StoreService {

    @Autowired
    private CloudantService cloudantService;

    private ConcurrentHashMap<String, Store> stores = null;

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

    public List<Store> getStoresForCurrentUser() {
        String username = Util.retrieveAuthenticationToken();
        List<Store> storeList = new ArrayList<>();
        for (Store store : getStores().values()) {
            String dbName = Util.determineStoreTypeAndReturnDatabaseName(store.getName());
            List<String> accesRights = queryAccess(dbName, username);
            if (!accesRights.contains(Store.StoreAccess.NOACCESS.getValue())) {
                storeList.add(store);
            }
        }
        return storeList;
    }

    /**
     * Load all stores from ToDo Catalog or creates ToDo Catalog database, done via HttpService 30 seconds after server
     * starts or first HTTP call, if sooner
     */
    public void loadStores() {
        if (stores != null) {
            return;
        }
        stores = new ConcurrentHashMap<String, Store>();

        // There is only one
        try {
            stores.clear();

            List<String> dbs = cloudantService.loadDatabases();
            for (String dbName : dbs) {
                String storeName = Util.extractStoreName(dbName);
                Store store = new Store();
                store.setName(storeName);
                store.setTitle(StringUtils.capitalize(storeName.replaceAll("_", " ")));
                store.setType(Util.getStoryType(dbName));
                store.setReplicaId(storeName);
                stores.put(dbName, store);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void addStore(Store store) {
        stores.put(store.getName(), store);
    }

    public Store getStore(String key) throws StoreNotFoundException {
        Store store = getStoreUnchecked(key);
        if (null == store) {
            throw new StoreNotFoundException();
        }
        return store;
    }

    /**
     * Get a Store object for the current session based on a Key
     * <p>
     * relevant Domino Session
     *
     * @param key ReplicaID or filepath
     * @return Store object for the NSF or null
     */
    public Store getStoreUnchecked(String key) {
        if (getStores().containsKey(key)) {
            return getStores().get(key);
        } else {
            return null;
        }
    }

    public boolean checkStoreExists(String key) {
        Store store = getStoreUnchecked(key);
        return store != null;
    }

    public Store createStore(String title, String name, Store.StoreType type) throws DatabaseModuleException {

        try {
            Store store = new Store();
            store.setTitle(title);
            store.setName(name);
            store.setType(type);

            cloudantService.createDatabase(name);
            createView(name);
            addAccess(name);
            addStore(store);

            return store;
        } catch (Exception e) {
            throw new DatabaseModuleException(e.getMessage());
        }

    }

    public void addAccess(String name) {
        String username = Util.retrieveAuthenticationToken();
        cloudantService.addAccess(name, username, EnumSet.<Permissions>of(Permissions._admin));
    }

    public void updateAccess(String name, String username, String accessRight) {
        if (accessRight.equals(Store.StoreAccess.READER.getValue())) {
            cloudantService.addAccess(name, username, EnumSet.<Permissions>of(Permissions._reader));
        } else if (accessRight.equals(Store.StoreAccess.EDITOR.getValue())) {
            cloudantService.addAccess(name, username, EnumSet.<Permissions>of(Permissions._writer));
        } else if (accessRight.equals(Store.StoreAccess.ADMIN.getValue())) {
            cloudantService.addAccess(name, username, EnumSet.<Permissions>of(Permissions._admin));
        }

    }

    public List<String> queryAccess(String name, String username) {
        List<String> accessRights = new ArrayList<>();
        EnumSet<Permissions> permissions = cloudantService.queryAccess(name, username);
        if (permissions != null) {
            for (Permissions permission : Permissions.values()) {
                if (permission.name().equals("_reader")) {
                    accessRights.add(Store.StoreAccess.READER.getValue());
                } else if (permission.name().equals("_writer")) {
                    accessRights.add(Store.StoreAccess.EDITOR.getValue());
                } else if (permission.name().equals("_admin")) {
                    accessRights.add(Store.StoreAccess.ADMIN.getValue());
                }
            }
        } else {
            accessRights.add(Store.StoreAccess.NOACCESS.getValue());
        }

        return accessRights;
    }

    public void createView(String dbName) {
        Map<String, Object> view1 = new HashMap<>();
        view1.put("map", "function(doc){emit(doc._id, 1)}");
        //view1.put("reduce", "function(key, value, rereduce){return sum(values)}");

        Map<String, Object> views = new HashMap<>();
        views.put(ToDoConstants.VIEW_TODO, view1);

        Map<String, Object> index1 = new HashMap<>();
        index1.put("analyzer", "standard");
        StringBuilder sb = new StringBuilder();
        sb.append("index(\"author\", doc.author, {\"store\": true});\n ");
        sb.append("index(\"taskname\", doc.taskName, {\"store\": true});\n ");
        sb.append("index(\"description\", doc.description, {\"store\": true});\n ");
        sb.append("index(\"dueDate\", doc.dueDate, {\"store\": true});\n ");
        sb.append("index(\"priority\", doc.priority, {\"store\": true});\n ");
        sb.append("index(\"assignedTo\", doc.assignedTo, {\"store\": true});\n ");
        sb.append("index(\"status\", doc.status, {\"store\": true});\n ");
        index1.put("index", "function (doc) {\n " + sb.toString() + "}");

        Map<String, Object> indexes = new HashMap<>();
        indexes.put(ToDoConstants.FTSEARCH_TODO, index1);

        Map<String, Object> view_ddoc = new HashMap<>();
        view_ddoc.put("_id", ToDoConstants.DESIGN_DOC_TODO);
        view_ddoc.put("views", views);
        view_ddoc.put("language", "javascript");
        view_ddoc.put("indexes", indexes);

        cloudantService.createDesignDocument(dbName, view_ddoc);
    }

    public void markOverdue() {
        for (Store store : getStores().values()) {
            List<ToDo> todos = (List<ToDo>) cloudantService.findAllDocumentFromView(ToDo.class, ToDoConstants.DESIGN_DOC_TODO, ToDoConstants.VIEW_TODO, store.getName());
            for (ToDo todo : todos) {
                if (todo.checkOverdue()) {
                    //save document
                    cloudantService.saveDocument(todo, store.getName());
                }
            }
        }
    }
}
