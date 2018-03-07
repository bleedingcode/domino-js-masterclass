/*
 * Copyright 2018
 *
 * @author Paul Withers (pwithers@intec.co.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package org.openntf.todo.v1;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openntf.todo.domino.ToDoStoreFactory;
import org.openntf.todo.exceptions.DatabaseModuleException;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.ToDo;

import com.ibm.commons.util.io.json.JsonJavaObject;

/**
 * @author Paul Withers
 * @since 1.0.0
 *
 */
/**
 * @author Paul Withers
 *
 *         Endpoints for multiple Stores
 */
@Path("/v1/stores")
public class StoresResource {

	/**
	 * Basic endpoint, using path of class with no additional URL path, getting all stores the current user has access
	 * to
	 *
	 * @return Response with list of stores
	 */
	@GET
	public Response getStores() {
		try {
			List<Store> stores = ToDoStoreFactory.getInstance().getStoresForCurrentUser();

			// Build JsonObject
			RequestBuilder<List<Store>> builder = new RequestBuilder(Store.class);
			return Response.ok(builder.buildJson(stores), MediaType.APPLICATION_JSON).build();
		} catch (final Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	/**
	 * Endpoint to run on a schedule to mark any overdue ToDos as {@link ToDo.Status#OVERDUE} and post them back to a
	 * passed URL.
	 * 
	 * @param nextUrl
	 *            URL to which to post list of ToDos marked overdue
	 * @return Response {"success":true} or error
	 */
	@GET
	@Path("/todos/markOverdue")
	public Response markOverdue(@HeaderParam(value = "updateUrl") String nextUrl) {
		try {
			ToDoStoreFactory.getInstance().markOverdue(nextUrl);

			JsonJavaObject jjo = new JsonJavaObject();
			jjo.put("success", true);
			return Response.status(Status.ACCEPTED).entity(jjo.toString()).build();
		} catch (DatabaseModuleException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

}