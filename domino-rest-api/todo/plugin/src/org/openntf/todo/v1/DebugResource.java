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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openntf.todo.ToDoUtils;
import org.openntf.todo.domino.Utils;
import org.openntf.todo.model.User;

import com.google.gson.Gson;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaObject;

@Path("/v1/debug")
public class DebugResource {
	private final String DEBUG_KEY = "sseqdcof4fq472so10us7ck7r0";

	@Path("/toggleProfiling")
	@GET
	public Response toggleProfiling(@Context HttpServletRequest request) {
		if (!validateKey(request)) {
			return Response.status(401).build();
		}
		JsonJavaObject jjo = new JsonJavaObject();
		jjo.put("profiling", ToDoUtils.toggleProfiling());
		return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Path("/personalStoreName")
	@GET
	public Response getPersonalStoreName(@Context HttpServletRequest request) {
		if (!validateKey(request)) {
			return Response.status(401).build();
		}
		JsonJavaObject jjo = new JsonJavaObject();
		jjo.put("username", Utils.getCurrentUsername());
		jjo.put("storename", Utils.getPersonalStoreName());
		return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Path("/{store}/prepopulate")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response prepopulateStore(@PathParam(value = "store") String store, final String body) throws JsonException {
		// TODO: Get the store matching the key or return error

		// TODO: Validate body
		Gson gson = new Gson();
		User[] users = gson.fromJson(body, User[].class);

		// TODO: Delete all data from said database and recreate as dummy data
		return null;
	}

	private boolean validateKey(HttpServletRequest request) {
		String verificationKey = request.getHeader("X-DEBUG-KEY");
		return DEBUG_KEY.equals(verificationKey);
	}

}
