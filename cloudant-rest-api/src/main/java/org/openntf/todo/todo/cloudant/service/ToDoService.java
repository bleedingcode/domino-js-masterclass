package org.openntf.todo.todo.cloudant.service;

import com.cloudant.client.api.model.Permissions;
import com.cloudant.client.api.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openntf.todo.todo.cloudant.ToDoConstants;
import org.openntf.todo.todo.cloudant.Util;
import org.openntf.todo.todo.cloudant.exceptions.DatabaseModuleException;
import org.openntf.todo.todo.cloudant.exceptions.StoreNotFoundException;
import org.openntf.todo.todo.cloudant.model.Store;
import org.openntf.todo.todo.cloudant.model.ToDo;
import org.openntf.todo.todo.cloudant.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ToDoService {

  @Autowired
  private CloudantService cloudantService;

  @Autowired
  private StoreService storeService;

  private ConcurrentHashMap<String, Store> stores = null;

  public List<ToDo> getToDoCollectionStatusPriority(String storeName, String status, String priority){
    String query = Util.constructSearchQuery(ToDoConstants.SEARCH_QUERY_STATUS_AND_PRIORTITY, ToDoConstants.SEARCH_QUERYREPLACE, status);
    query = Util.constructSearchQuery(query, ToDoConstants.SEARCH_QUERYREPLACE_1, priority);
    List<ToDo> todosList = seacrhTodos(storeName, query);
    return todosList;
    /*List<ToDo> todos = getToDoList(dbName);
    return todos.stream()
            .filter( todo -> status.equals(todo.getStatus()) && priority.equals(todo.getPriority().getValue()))
            .collect(Collectors.toList());*/
  }

  public List<ToDo> getToDoCollectionStatusUsername(String storeName, String status, String username){
    String query = Util.constructSearchQuery(ToDoConstants.SEARCH_QUERY_STATUS_AND_USERNAME, ToDoConstants.SEARCH_QUERYREPLACE, status);
    query = Util.constructSearchQuery(query, ToDoConstants.SEARCH_QUERYREPLACE_1, username);
    List<ToDo> todosList = seacrhTodos(storeName, query);
    return todosList;
    /*List<ToDo> todos = getToDoList(dbName);
    return todos.stream()
            .filter( todo -> status.equals(todo.getStatus()) && username.equals(todo.getAuthor()))
            .collect(Collectors.toList());*/
  }

  public List<ToDo> getToDoCollectionStatus(String storeName, String status){
    String query = Util.constructSearchQuery(ToDoConstants.SEARCH_QUERY_STATUS, ToDoConstants.SEARCH_QUERYREPLACE, status);
    List<ToDo> todosList = seacrhTodos(storeName, query);
    return todosList;
  }

  public List<ToDo> getToDoCollectionRange(String dbName, String startDate, String endDate){
    List<ToDo> todos = new ArrayList<>();
    try {
      Calendar cal = Calendar.getInstance();
      String pattern = "yyyy-MM-dd";
      SimpleDateFormat sdf = new SimpleDateFormat(pattern);
      cal.setTime(new Date());
      if (StringUtils.isNotEmpty(startDate)) {
        cal.setTime(sdf.parse(startDate));
      }
      cal.set(Calendar.HOUR, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      Date dtStart = cal.getTime();

      if (StringUtils.isEmpty(endDate)) {
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, 1);
      } else {
        cal.setTime(sdf.parse(endDate));
      }
      cal.set(Calendar.HOUR, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      Date dtEnd = cal.getTime();

      todos = getToDoList(dbName);

      return todos.stream()
              .filter( todo -> todo.getDueDate().before(dtEnd) && todo.getDueDate().after(dtStart))
              .collect(Collectors.toList());
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return todos;
  }

  private List<ToDo> getToDoList(String storeName){
    String dbName = Util.determineStoreTypeAndReturnDatabaseName(storeName);
    List<ToDo> todos =  (List<ToDo>) cloudantService.findAllDocumentFromView(ToDo.class, ToDoConstants.DESIGN_DOC_TODO_SIMPLE, ToDoConstants.VIEW_TODO, dbName);

    return fillinMetaversalId(dbName, todos);
  }

  private List<ToDo> seacrhTodos(String storeName, String query){
    String dbName = Util.determineStoreTypeAndReturnDatabaseName(storeName);
    List<ToDo> todos = (List<ToDo>) cloudantService.search(ToDoConstants.SEARCH_INDEX, ToDo.class, ToDoConstants.SEARCH_COUNT, query, dbName);

    return fillinMetaversalId(dbName, todos);
  }

  private List<ToDo> fillinMetaversalId(String dbName, List<ToDo> todos){
    for(ToDo todo : todos){
      String metaversalId = Util.constructMetaversalId(dbName, todo.getId());
      todo.setMetaversalId(metaversalId);
    }
    return todos;
  }

  public ToDo addToDo(String dbName, ToDo todo){
    Response response = cloudantService.saveDocument(todo, dbName);
    todo.setMetaversalId(Util.constructMetaversalId(dbName, response.getId()));
    todo.setId(response.getId());
    todo.setRev(response.getRev());

    return todo;
  }

  public ToDo updateToDo(String metaversalId, ToDo todo){
    Map<String, String> metaData = Util.parseMetaversalId(metaversalId);
    todo.setId(metaData.get(ToDoConstants.METADATA_UNID));
    Response response = cloudantService.updateDocument(todo, metaData.get(ToDoConstants.METADATA_DBNAME));
    return todo;
  }

  public void deleteToDo(String metaversalId){
    Map<String, String> metaData = Util.parseMetaversalId(metaversalId);
    ToDo todo = getToDoFromMetaversalId(metaversalId);
    cloudantService.removeDocument(todo, metaData.get(ToDoConstants.METADATA_DBNAME));
  }

  public ToDo getToDoFromMetaversalId(String metaversalId){
    Map<String, String> metaData = Util.parseMetaversalId(metaversalId);
    ToDo todo = (ToDo) cloudantService.findDocumentByID(ToDo.class, metaData.get(ToDoConstants.METADATA_UNID), metaData.get(ToDoConstants.METADATA_DBNAME));
    todo.setMetaversalId(metaversalId);
    return todo;
  }

  public void reassignTodo(User user, String metaversalId){
    ToDo todo = getToDoFromMetaversalId(metaversalId);
    todo.setAssignedTo(user.getUsername());
    todo.checkOverdue();
    updateToDo(metaversalId, todo);
  }

  public void completeTodo(String metaversalId){
    ToDo todo = getToDoFromMetaversalId(metaversalId);
    todo.setStatus(ToDo.Status.COMPLETE);
    updateToDo(metaversalId, todo);
  }

  public void reopenTodo(String metaversalId){
    ToDo todo = getToDoFromMetaversalId(metaversalId);
    todo.setStatus(ToDo.Status.ACTIVE);
    todo.checkOverdue();
    updateToDo(metaversalId, todo);
  }
}
