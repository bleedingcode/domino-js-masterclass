package org.openntf.todo.todo.cloudant;

import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;
import org.openntf.todo.todo.cloudant.model.Store;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

public class Util {

  public static String retrieveAuthenticationToken(){
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Optional<String> token = (Optional<String>) authentication.getPrincipal();
    return token.get();
  }

  public static String constructDatabaseName(String name, Store.StoreType type){
    String databaseName = type.toString()+"_"+StringUtils.replace(StringUtils.replace(name, "/", "_"), " ", "_");
    return databaseName.toLowerCase();
  }

  public static Map<String, String> parseMetaversalId(String metaversalId){
    Map<String, String> map = new HashMap<>();
    if(metaversalId.contains(ToDoConstants.METAVERSALID_SEPARATOR)){
      String[] split = metaversalId.split(ToDoConstants.METAVERSALID_SEPARATOR);
      map.put(ToDoConstants.METADATA_UNID, split[1]);
      map.put(ToDoConstants.METADATA_DBNAME, split[0]);
    }
    return map;
  }

  public static boolean isPersonal(String storeName){
    return storeName.contains(Store.StoreType.PERSONAL.getValue().toLowerCase());
  }

  public static Store.StoreType getStoryType(String storeName){
    if(isPersonal(storeName)){
      return Store.StoreType.PERSONAL;
    }
    return Store.StoreType.TEAM;
  }

  public static String extractStoreName(String storeName){
    String storeType = getStoryType(storeName).getValue().toLowerCase();

    if(storeName.contains(storeType)) {
      return storeName.substring(storeType.length() + 1, storeName.length());
    }
    return storeName;
  }

  public static String toLowerCaseAndRemoveWhitespace(String name){
    return StringUtils.replace(StringUtils.replace(name, "/", "_"), " ", "_").toLowerCase();
  }

  public static boolean matchStoreKeyTheToken(String token, String storeKey){
    return token.equals(storeKey);
  }

  public static String determineStoreTypeAndReturnDatabaseName(String storeKey){
    String username = Util.toLowerCaseAndRemoveWhitespace(Util.retrieveAuthenticationToken());
    String storeKeyLowerCase = Util.toLowerCaseAndRemoveWhitespace(storeKey);
    String dbName = Util.constructDatabaseName(storeKey, Store.StoreType.TEAM);
    if(Util.matchStoreKeyTheToken(username, storeKeyLowerCase)){
      dbName = Util.constructDatabaseName(storeKey, Store.StoreType.PERSONAL);
    }

    return dbName;
  }

  public static String constructSearchQuery(String searchPattern, String placeHolder, String query){
    return searchPattern.replace(placeHolder, query);
  }

  public static String constructMetaversalId(String dbName, String id){
    return dbName+ToDoConstants.METAVERSALID_SEPARATOR+id;
  }
}
