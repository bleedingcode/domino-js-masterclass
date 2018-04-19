package org.openntf.todo.todo.cloudant.service;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Index;
import com.cloudant.client.api.model.Permissions;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.views.Key;
import lombok.extern.slf4j.Slf4j;
import org.openntf.todo.todo.cloudant.model.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CloudantService {
    private static final String SERVICE_NAME = "CloudantOpenNTF";

    @Value("${cloudant.username}")
    private String account;
    private String password;
    private String username;
    
    private boolean connected = false;
    
    private static final String BEAN_NAME = "CloudantService";

    @Autowired
    private CloudantClient cloudant;

    private Database databaseCatalog;
    
    public CloudantService() {
    	
    }

    @PostConstruct
    public void connect() {
        loadDatabases();
        connected = true;
    }
    
    public void testConnection() {
        if (!connected) {
            connect();
        }
        try {
            List<Index> indices = databaseCatalog.listIndices();
            connected = !indices.isEmpty();
        } catch (Exception e) {
            connected = false;
        }
    }
    
    public boolean isConnected() {
        return connected;
    }

    private Database initSpecificDatabase(String databaseName){
        return cloudant.database(databaseName, true);
    }

    public List<String> loadDatabases(){
        List<String> dbs = new ArrayList<>();
        try {
            dbs = cloudant.getAllDbs();
        } catch (Exception e) {
                log.error(e.getMessage());
            }
        return dbs;
    }
    
    /*
     * Database connectors
     */
    
    public void switchDatabase(String db, boolean create) {
        cloudant.database(db, create);
    }

    public void createDatabase(String db) {
        switchDatabase(db, true);

    }

    public void createDesignDocument(String dbName, Map<String, Object> view_ddoc){
        Database db = cloudant.database(dbName, false);
        db.save(view_ddoc);
    }

    public void addAccess(String dbName, String username, java.util.EnumSet<Permissions> permissions){
        Database db = cloudant.database(dbName, false);
        db.setPermissions(username, permissions);
        db.setPermissions(account, EnumSet.<Permissions>of(Permissions._admin));
    }

    public EnumSet<Permissions> queryAccess(String dbName, String username){
        Database db = cloudant.database(dbName, false);
        Map<java.lang.String,java.util.EnumSet<Permissions>> permissions = db.getPermissions();

        if(permissions.containsKey(username)){
            return permissions.get(username);
        }

        return null;
    }
    
    /*
     * Document connectors
     */
    
    public Object findDocumentByID(Class<?> cls, String documentId, String databaseName) {
        try {
            Database database = initSpecificDatabase(databaseName);
            return database.find(cls, documentId);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
    
    public List<?> findAllDocuments(Class<?> cls, String databaseName) {
        try {
            Database database = initSpecificDatabase(databaseName);
            return database.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(cls);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
    
    public void removeDocument(Object obj, String databaseName) {
        try {
            Database database = initSpecificDatabase(databaseName);
            database.remove(obj);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    
    public Response saveDocument(Object obj, String databaseName) {
        Response resp = null;
        try {
            Database database = initSpecificDatabase(databaseName);
            resp = database.save(obj);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return resp;
    }
    
    public Response updateDocument(Object obj, String databaseName) {
        Response resp = null;
        try {
            Database database = initSpecificDatabase(databaseName);
            resp = database.update(obj);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return resp;
    }
    
    public void saveDocuments(final List<?> docs, String databaseName) {
        try {
            Database database = initSpecificDatabase(databaseName);
            database.bulk(docs);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    
    public void updateDocuments(final List<?> docs, String databaseName) {
        try {
            Database database = initSpecificDatabase(databaseName);
            database.bulk(docs);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    
    public void deleteDocuments(final List<?> docs, String databaseName) {
        try {
            Database database = initSpecificDatabase(databaseName);
            for(final Object obj : docs){

                database.remove(obj);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    
    public Response saveStandaloneAttachment(final InputStream inputStream,
            final String name, final String contentType, final String docId,
            final String docRev, String databaseName) {
        try {
            Database database = initSpecificDatabase(databaseName);
            return database.saveAttachment(inputStream,
                    name, contentType, docId, docRev);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
    
    /**
     * 
     * @param cls
     * @param designDoc
     * @param viewName
     * @param keyType
     * @param limit
     * @return
     */
    public List<?> findAllDocumentFromView(final Class<?> cls,
            final String designDoc, final String viewName,
            final String keyType, final int limit, String databaseName) {
        try {
            Database database = initSpecificDatabase(databaseName);
            return database.getViewRequestBuilder(designDoc,viewName)
                    .newRequest(Key.Type.STRING, Object.class)
                    .limit(limit)
                    .includeDocs(true)
                    .build()
                    .getResponse()
                    .getDocsAs(cls);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param cls
     * @param designDoc
     * @param viewName
     * @return
     */
    public List<?> findAllDocumentFromView(final Class<?> cls,
                                           final String designDoc, final String viewName,
                                           String databaseName) {
        try {
            Database database = initSpecificDatabase(databaseName);
            return database.getViewRequestBuilder(designDoc,viewName)
                    .newRequest(Key.Type.STRING, Object.class)
                    .includeDocs(true)
                    .build()
                    .getResponse()
                    .getDocsAs(cls);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
    /**
     * 
     * @param cls
     * @param designDoc
     * @param viewName
     * @param keyType
     * @param limit
     * @param startKey
     * @param endKey
     * @return
     */
    public List<?> findAllDocumentFromViewKeys(final Class<?> cls,
            final String designDoc, final String viewName,
            final String keyType, final int limit, final String startKey,
            final String endKey, String databaseName) {
        try {
            Database database = initSpecificDatabase(databaseName);
            return database.getViewRequestBuilder(designDoc,viewName)
                    .newRequest(Key.Type.STRING, Object.class)
                    .startKey(startKey)
                    .endKey(endKey)
                    .limit(limit)
                    .includeDocs(true)
                    .build()
                    .getResponse()
                    .getDocsAs(cls);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
    
    /*
     * Query
     */
    
    public List<Index> allIndices(String databaseName) {
        Database database = initSpecificDatabase(databaseName);
        return (List<Index>) database.listIndices();
    }
    
    public List<?> search(final String searchIndexId, final Class<?> cls,
            final Integer queryLimit, final String query, String databaseName) {
        
        try {
            Database database = initSpecificDatabase(databaseName);
            return database.search(searchIndexId)
                    .limit(queryLimit)
                    .includeDocs(true)
                    .query(query, cls);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
