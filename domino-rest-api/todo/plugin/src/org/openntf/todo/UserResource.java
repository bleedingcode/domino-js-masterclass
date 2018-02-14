package org.openntf.todo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.User;

import com.ibm.commons.util.io.json.JsonException;

@Path("/user")
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
	@Path("/{username}")
	public Response getUserByName(@PathParam("username") final String username) throws JsonException {
		User user = new User(username);
		String json = new RequestBuilder(User.class).buildJson(user);
		return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

}
