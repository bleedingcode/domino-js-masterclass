package org.openntf.todo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaObject;

@Path("/user")
public class UserResource {

	@GET
	@Path("/{username}")
	public Response getUserByName(@PathParam("username") final String userName) throws JsonException {
		final JsonJavaObject jjo = new JsonJavaObject();
		String currName = Factory.getSession(SessionType.CURRENT).getEffectiveUserName();
		// With Domino authentication, username is as passed
		if (userName.indexOf("/") > -1) {
			jjo.put("username", userName);
		} else {
			jjo.put("username", "CN=" + userName + currName.substring(currName.indexOf("/")));
		}
		jjo.put("access", new JsonJavaObject());
		return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
	}

}
