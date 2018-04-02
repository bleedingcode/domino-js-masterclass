package org.openntf.todo.todo.cloudant.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.openntf.todo.todo.cloudant.model.Store;
import org.openntf.todo.todo.cloudant.model.ToDo;
import org.openntf.todo.todo.cloudant.service.StoreService;
import org.openntf.todo.todo.cloudant.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/v1/todos")
public class ToDosController {

    @Autowired
    private ToDoService todoService;

    @ApiOperation(value = "Find Todo's by status")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return all Todo's by status"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.GET, value = "/{store}/findByStatus", produces = "application/json")
    public ResponseEntity<?> findByStatus(@PathVariable(value = "store") String storeKey, @RequestParam("status") String status) {
        try {
            List<ToDo>todos = todoService.getToDoCollectionStatus(storeKey, status);
            return new ResponseEntity<>(todos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Find Todo's by status and username")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return Todo's by status and username"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.GET, value = "/{store}/findByAssigneeAndStatus", produces = "application/json")
    public ResponseEntity<?> findByAssigneeAndStatus(@PathVariable(value = "store") String storeKey, @RequestParam("status") String status, @RequestParam("username") String username) {
        try {
            List<ToDo>todos = todoService.getToDoCollectionStatusUsername(storeKey, status, username);
            return new ResponseEntity<>(todos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Find Todo's by status and priority")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return Todo's by status and priority"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.GET, value = "/{store}/findByPriorityAndStatus", produces = "application/json")
    public ResponseEntity<?> findByPriorityAndStatus(@PathVariable(value = "store") String storeKey, @RequestParam("status") String status, @RequestParam("priority") String priority) {
        try {
            List<ToDo>todos = todoService.getToDoCollectionStatusPriority(storeKey, status, priority);
            return new ResponseEntity<>(todos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Find Todo's by start and end date")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return Todo's by start and end date"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.GET, value = "/{store}/findByDate", produces = "application/json")
    public ResponseEntity<?> getToDosByDate(@PathVariable(value = "store") String storeKey, @RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
        try {
            List<ToDo>todos = todoService.getToDoCollectionRange(storeKey, startDate, endDate);
            return new ResponseEntity<>(todos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
