package org.openntf.todo.todo.cloudant.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.openntf.todo.todo.cloudant.Util;
import org.openntf.todo.todo.cloudant.exceptions.DatabaseModuleException;
import org.openntf.todo.todo.cloudant.exceptions.StoreNotFoundException;
import org.openntf.todo.todo.cloudant.model.Store;
import org.openntf.todo.todo.cloudant.model.User;
import org.openntf.todo.todo.cloudant.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/v1/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @ApiOperation(value = "Get mine store")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return mine store"),
            @ApiResponse(code = 400, message = "Store not found"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.GET, value = "/mine")
    public ResponseEntity<?> getMyStore() {
        String myStorePath = Util.constructDatabaseName(Util.retrieveAuthenticationToken(), Store.StoreType.PERSONAL);
        try {
            Store store = storeService.getStore(myStorePath);
            return new ResponseEntity<>(store, HttpStatus.OK);
        } catch (StoreNotFoundException se) {
            log.error(se.getMessage());
            return new ResponseEntity<>(se.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Post a new store")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return the new store"),
            @ApiResponse(code = 400, message = "Title, type or name are not provided"),
            @ApiResponse(code = 409, message = "Store already exist"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createStore(@RequestBody Store passedStore) {
        try {
            //Validate the input
            if (passedStore.getTitle() == null) {
                return new ResponseEntity<>("Expected title in body", HttpStatus.BAD_REQUEST);
            }

            if (null == passedStore.getType()) {
                return new ResponseEntity<>("Type should be 'Personal' or 'Team'", HttpStatus.BAD_REQUEST);
            } else if (Store.StoreType.TEAM.equals(passedStore.getType())) {
                if (null == passedStore.getName()) {
                    return new ResponseEntity<>("Expected name in body", HttpStatus.BAD_REQUEST);
                }
                passedStore.setName(Util.constructDatabaseName(passedStore.getName(), Store.StoreType.TEAM));
            } else {
                passedStore.setName(Util.constructDatabaseName(Util.retrieveAuthenticationToken(), Store.StoreType.PERSONAL));
            }

            if (storeService.checkStoreExists(passedStore.getName())) {
                return new ResponseEntity<>(
                        "A store already exists with the name. (For personal stores, the username overrides the name passed)", HttpStatus.CONFLICT);
            }

            // Create store

            Store store = storeService.createStore(passedStore.getTitle(), passedStore.getName(),
                    passedStore.getType());
            store.setReplicaId(store.getName());
            return new ResponseEntity<>(store, HttpStatus.OK);

        } catch (DatabaseModuleException de) {
            return new ResponseEntity<>(de.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Update the title of the store")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return the updated store"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.PATCH, value="/{store}/updateTitle")
    public ResponseEntity<?> updateTitle(final @PathVariable(value = "store") String storeKey,
                                         final @RequestHeader(value = "title") String title){

        try {
            Store store = storeService.getStore(storeKey);
            store.setTitle(title);

            return new ResponseEntity<>(store, HttpStatus.OK);
        } catch (StoreNotFoundException se) {
            return new ResponseEntity<>("The store could not be found with the name passed", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Return the store")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return the store"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.GET, value="/{store}")
    public ResponseEntity<?> getStoreInfo(final @PathVariable(value = "store") String storeKey){

        try {

            Store store = storeService.getStore(Util.determineStoreTypeAndReturnDatabaseName(storeKey));

            return new ResponseEntity<>(store, HttpStatus.OK);
        } catch (StoreNotFoundException se) {
            return new ResponseEntity<>("The store could not be found with the name passed", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Return the user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return the user"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.GET, value="/{store}/access", consumes = "application/json")
    public ResponseEntity<?> queryAccess(final @PathVariable(value = "store") String storeKey){

        try {
            String username = Util.retrieveAuthenticationToken();
            User user = new User(username);
            user.setAccess(storeService.queryAccess(Util.determineStoreTypeAndReturnDatabaseName(storeKey), username));

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Update access for the user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return the user"),
            @ApiResponse(code = 500, message = "Internal error, go check the logs")})
    @RequestMapping(method = RequestMethod.POST, value="/{store}/access")
    public ResponseEntity<?> updateAccess(final @PathVariable(value = "store") String storeKey, @RequestBody User[] users){

        try {
            for(User user: users){
                user.validateForUpdate();
                for(String accessRight : user.getAccess()) {
                    storeService.updateAccess(Util.determineStoreTypeAndReturnDatabaseName(storeKey), user.getUsername(), accessRight);
                }
            }

            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
