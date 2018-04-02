package org.openntf.todo.todo.cloudant.controller;

import com.cloudant.client.api.model.Response;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openntf.todo.todo.cloudant.ToDoConstants;
import org.openntf.todo.todo.cloudant.Util;
import org.openntf.todo.todo.cloudant.exceptions.DocumentNotFoundException;
import org.openntf.todo.todo.cloudant.exceptions.InvalidMetaversalIdException;
import org.openntf.todo.todo.cloudant.exceptions.StoreNotFoundException;
import org.openntf.todo.todo.cloudant.model.Store;
import org.openntf.todo.todo.cloudant.model.ToDo;
import org.openntf.todo.todo.cloudant.model.User;
import org.openntf.todo.todo.cloudant.service.StoreService;
import org.openntf.todo.todo.cloudant.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/v1/todo")
public class ToDoController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private ToDoService todoService;

    @ApiOperation(value = "Creates a ToDo, setting it to Active or Overdue, based on due date")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return the new Todo"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.POST, value = "/{key}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> addToDo(@PathVariable(value = "key") String storeKey, @RequestBody ToDo todo) {
        try {
            String dbName = Util.determineStoreTypeAndReturnDatabaseName(storeKey);
            Store store = storeService.getStore(dbName);
            String username = Util.retrieveAuthenticationToken();
            todo.setAuthor(username);
            if (StringUtils.isEmpty(todo.getAssignedTo()) || Store.StoreType.PERSONAL.equals(store.getType())) {
                todo.setAssignedTo(todo.getAuthor());
            }

            todo.setStatus(ToDo.Status.ACTIVE);
            todo.validateForUpdate();
            todo = todoService.addToDo(dbName, todo);
            return new ResponseEntity<>(todo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Get a ToDo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return the Todo"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.GET, value = "/{key}", produces = "application/json")
    public ResponseEntity<?> getToDo(@PathVariable(value = "key") String metaversalId) {
        try {
            ToDo todo = todoService.getToDoFromMetaversalId(metaversalId);
            return new ResponseEntity<>(todo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Updates a ToDo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return the updated Todo"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.PATCH, value = "/{key}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> updateToDo(@PathVariable(value = "key") String metaversalId, @RequestBody ToDo todo) {
        try {
            Map<String, String> metaData = Util.parseMetaversalId(metaversalId);
                Store store = storeService.getStore(metaData.get(ToDoConstants.METADATA_DBNAME));
                todo.setMetaversalId(metaversalId);
                if (StringUtils.isNotEmpty(todo.getAssignedTo())) {
                    ToDo oldTodo = todoService.getToDoFromMetaversalId(todo.getMetaversalId());
                    todo.setRev(oldTodo.getRev());
                    todo.setId(oldTodo.getId());
                    if (!StringUtils.equals(todo.getAssignedTo(), oldTodo.getAssignedTo())) {
                        if (Store.StoreType.PERSONAL.equals(store.getType())) {
                            return new ResponseEntity<>("Personal ToDos cannot be reassigned", HttpStatus.BAD_REQUEST);
                        }
                        // TODO: For a full app, we would need to check the user has access and, if not, add access
                    }
                }
                //todo = todo.compareAndUpdateFromPrevious();
                todo = todoService.updateToDo(metaversalId, todo);

            return new ResponseEntity<>(todo, HttpStatus.OK);
        } catch (StoreNotFoundException se) {
            return new ResponseEntity<>(ToDoConstants.STORE_NOT_FOUND_OR_ACCESS_ERROR, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Delete a ToDo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{key}")
    public ResponseEntity<?> deleteToDo(@PathVariable(value = "key")final String metaversalId) {
        try {
            Map<String, String> metaData = Util.parseMetaversalId(metaversalId);
            String username = Util.retrieveAuthenticationToken();
            List<String> accessRights = storeService.queryAccess(metaData.get(ToDoConstants.METADATA_DBNAME), username);
            if(accessRights.contains(Store.StoreAccess.NOACCESS.getValue())){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            todoService.deleteToDo(metaversalId);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Reassign a ToDo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return success"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.POST, value = "/{toDoId}/reassign", consumes = "application/json")
    public ResponseEntity<?> reassignToDo(@PathVariable(value = "toDoId") String metaversalId, @RequestBody User user) {
        try {
            Map<String, String> metaData = Util.parseMetaversalId(metaversalId);
            Store store = storeService.getStore(metaData.get(ToDoConstants.METADATA_DBNAME));
            if (Store.StoreType.PERSONAL.equals(store.getType())) {
                return new ResponseEntity<>("Personal ToDos cannot be reassigned", HttpStatus.FORBIDDEN);
            }

            if (StringUtils.isEmpty(user.getUsername())) {
                return new ResponseEntity<>("Username must be supplied", HttpStatus.BAD_REQUEST);
            }

            todoService.reassignTodo(user, metaversalId);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Complete a ToDo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return the success"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.POST, value = "/{toDoId}/complete")
    public ResponseEntity<?> completeToDo(@PathVariable(value = "toDoId") String metaversalId) {
        try {
            todoService.completeTodo(metaversalId);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Reopen a ToDo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return success"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.POST, value = "/{toDoId}/reopen")
    public ResponseEntity<?> reopenToDo(@PathVariable(value = "toDoId") String metaversalId) {
        try {
            todoService.reopenTodo(metaversalId);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
