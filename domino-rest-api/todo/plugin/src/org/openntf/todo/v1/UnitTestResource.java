/*******************************************************************************
 * Copyright 2018 Paul Withers
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.openntf.todo.v1;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openntf.todo.ToDoUtils;
import org.openntf.todo.domino.Utils;
import org.openntf.todo.exceptions.DataNotAcceptableException;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.DatabaseAccess;
import org.openntf.todo.model.DatabaseAccess.AccessLevel;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.Store.StoreType;
import org.openntf.todo.model.User;

import com.google.gson.Gson;

/**
 * @author Paul Withers
 * 
 *         Endpoints for unit testing. This does not interact with the backend database, just with dummy Java objects
 *         and converting to/from JSON. All endpoints check to ensure the DEBUG_KEY has been passed (see
 *         {@link #validateKey(HttpServletRequest)}.
 *
 */
@Path("/v1/unitTests")
public class UnitTestResource {
	private final String DEBUG_KEY = "sseqdcof4fq472so10us7ck7r0";

	/**
	 * Creates two dummy Store objects and returns them
	 * 
	 * @param request
	 *            current request
	 * @return Response containing the two Stores
	 */
	@Path("/storeTest")
	@GET
	public Response testStoresList(@Context HttpServletRequest request) {
		try {
			if (!validateKey(request)) {
				return Response.status(401).build();
			}
			List<Store> stores = new ArrayList<Store>();
			Store store1 = new Store();
			store1.setName(ToDoUtils.getStoreFilePath(Utils.getPersonalStoreName(), StoreType.PERSONAL));
			store1.setReplicaId("12345678123456781234567812345678");
			store1.setTitle("My Personal Store");
			store1.setType(StoreType.PERSONAL);
			Store store2 = new Store();
			store2.setName(ToDoUtils.getStoreFilePath("Test Store", StoreType.TEAM));
			store2.setReplicaId("87654321876543218765432187654321");
			store2.setTitle("Test Team Store");
			store2.setType(StoreType.TEAM);
			stores.add(store1);
			stores.add(store2);

			// Build JsonObject
			String json = ToDoUtils.getGson().toJson(stores);
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} catch (final Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Creates a Store object from passed content and returns it
	 * 
	 * @param request
	 *            current request
	 * @param body
	 *            containing Store object
	 * @return Response containing updated and validated Store object
	 */
	@POST
	@Path("/createStore")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createStoreTest(@Context HttpServletRequest request, final String body) {
		try {
			if (!validateKey(request)) {
				return Response.status(401).build();
			}
			Store passedStore = ToDoUtils.getGson().fromJson(body, Store.class);
			if (null == passedStore.getTitle()) {
				Response.status(Status.BAD_REQUEST).entity("Expected title in body").build();
			}

			if (null == passedStore.getType()) {
				Response.status(Status.BAD_REQUEST).entity("type should be 'Personal' or 'Team'").build();
			} else if (StoreType.TEAM.equals(passedStore.getType())) {
				if (null == passedStore.getName()) {
					Response.status(Status.BAD_REQUEST).entity("Expected name in body").build();
				}
				passedStore.setName(ToDoUtils.getStoreFilePath(passedStore.getName(), StoreType.TEAM));
			} else {
				passedStore.setName(ToDoUtils.getStoreFilePath(Utils.getPersonalStoreName(), StoreType.PERSONAL));
			}

			passedStore.setReplicaId("12345678123456781234567812345678");

			// Build JsonObject
			String json = ToDoUtils.getGson().toJson(passedStore);
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Tests returning a User object with DatabaseAccess for the current user
	 * 
	 * @param request
	 *            current request
	 * @return Response for User object
	 */
	@Path("/userTest")
	@GET
	public Response testUser(@Context HttpServletRequest request) {
		try {
			if (!validateKey(request)) {
				return Response.status(401).build();
			}

			User user = new User(Utils.getCurrentUsername());
			DatabaseAccess access = new DatabaseAccess();
			access.setDbName(Utils.getPersonalStoreName());
			access.setAllowDelete(true);
			access.setLevel(AccessLevel.ADMIN);
			access.setReplicaId("12345678123456781234567812345678");
			user.setAccess(access);

			// Build JsonObject
			String json = ToDoUtils.getGson().toJson(user);
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} catch (final Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @param request
	 *            current request
	 * @param body
	 *            containing User objects with Database Access
	 * @return Response containing the same Users
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@POST
	@Path("/receiveUsers")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response receiveUsersTest(@Context HttpServletRequest request, final String body) {
		try {
			if (!validateKey(request)) {
				return Response.status(401).build();
			}
			Gson gson = ToDoUtils.getGson();
			User[] passedUsers = gson.fromJson(body, User[].class);
			for (User user : passedUsers) {
				if (user.validateForUpdate()) {
					System.out.println("Valid - " + user.getUsername());
				} else {
					System.out.println("Not valid - " + user.getUsername());
				}
			}

			// Build JsonObject
			RequestBuilder builder = new RequestBuilder(User.class);
			String json = builder.buildJson(passedUsers);
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} catch (DataNotAcceptableException d) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(d.getMessage()).build());
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Verifies that the X-DEBUG-KEY header param matches the DEBUG_KEY variable
	 * 
	 * @param request
	 *            current request
	 * @return whether or not they match
	 */
	private boolean validateKey(HttpServletRequest request) {
		String verificationKey = request.getHeader("X-DEBUG-KEY");
		return DEBUG_KEY.equals(verificationKey);
	}

}
