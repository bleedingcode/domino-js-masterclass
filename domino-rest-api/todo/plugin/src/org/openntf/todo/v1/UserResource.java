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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openntf.todo.domino.Utils;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.User;

import com.ibm.commons.util.io.json.JsonException;

@Path("/v1/user")
public class UserResource {

	/**
	 * Get a user by username
	 * 
	 * @return User object
	 * @throws JsonException
	 */
	@GET
	public Response getCurrentUser() throws JsonException {
		User user = new User(Utils.getCurrentUsername());
		String json = new RequestBuilder(User.class).buildJson(user);
		return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Get a user by username
	 * 
	 * @param username
	 *            String username
	 * @return User object
	 * @throws JsonException
	 *             exception parsing User to json
	 */
	@GET
	@Path("/{username}")
	public Response getUserByName(@PathParam("username") final String username) throws JsonException {
		User user = new User(username);
		String json = new RequestBuilder(User.class).buildJson(user);
		return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

}
