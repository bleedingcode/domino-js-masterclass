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
	 * @param username
	 *            String username
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
	 */
	@GET
	@Path("/{username}")
	public Response getUserByName(@PathParam("username") final String username) throws JsonException {
		User user = new User(username);
		String json = new RequestBuilder(User.class).buildJson(user);
		return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

}
