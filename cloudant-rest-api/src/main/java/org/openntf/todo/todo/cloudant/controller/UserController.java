package org.openntf.todo.todo.cloudant.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.openntf.todo.todo.cloudant.Util;
import org.openntf.todo.todo.cloudant.model.Store;
import org.openntf.todo.todo.cloudant.model.User;
import org.openntf.todo.todo.cloudant.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private StoreService storeService;

    @ApiOperation(value = "Get current user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return current user"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-TODO-API-KEY", value = "", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "X-TODO-USER-KEY", value = "", required = false, dataType = "string", paramType = "header")
    })
    @RequestMapping(method = RequestMethod.GET, value = "", produces = "application/json")
    public ResponseEntity<?> getCurrentUser() {
        try {
            User user = new User(Util.retrieveAuthenticationToken());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Get user by name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return the user by name"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-TODO-API-KEY", value = "", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "X-TODO-USER-KEY", value = "", required = false, dataType = "string", paramType = "header")
    })
    @RequestMapping(method = RequestMethod.GET, value = "/{username}", produces = "application/json")
    public ResponseEntity<?> getUserByName(final @RequestParam(value = "username") String username) {
        try {
            User user = new User(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
