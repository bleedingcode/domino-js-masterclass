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

import com.ibm.commons.util.io.json.JsonJavaObject;

/**
 * @author Paul Withers
 * @since 1.0.0
 *
 */
@Path("/v1/stores")
public class StoresResource {

	/**
	 * Basic endpoint, using path of class with no additional URL path
	 *
	 * @return Json response with "Hello World " + current user + current
	 *         database path; or error
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