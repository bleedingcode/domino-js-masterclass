package org.openntf.todo;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;

@Path("/debug")
public class DebugResource {
	private final String DEBUG_KEY = "sseqdcof4fq472so10us7ck7r0";

	@Path("/toggleProfiling")
	@GET
	public Response toggleProfiling(@Context HttpServletRequest request) {
		if (!validateKey(request)) {
			return Response.status(401).build();
		}
		JsonJavaObject jjo = new JsonJavaObject();
		jjo.put("profiling", Utils.toggleProfiling());
		return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Path("/personalStoreName")
	@GET
	public Response getPersonalStoreName(@Context HttpServletRequest request) {
		if (!validateKey(request)) {
			return Response.status(401).build();
		}
		JsonJavaObject jjo = new JsonJavaObject();
		jjo.put("username", Factory.getSession(SessionType.CURRENT).getEffectiveUserName());
		jjo.put("storename", Utils.getPersonalStoreName(Factory.getSession(SessionType.CURRENT)));
		return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
	}

	@Path("/{store}/prepopulate")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response prepopulateStore(@PathParam(value = "store") String store, final String body) throws JsonException {
		// TODO: Get the store matching the key or return error

		// TODO: Validate body
		final JsonJavaObject jjo = new JsonJavaObject();
		final Map<String, Object> bodyAsObj = (Map<String, Object>) JsonParser.fromJson(JsonJavaFactory.instance, body);
		// Expecting an array, so the above code may not work

		// TODO: Delete all data from said database and recreate as dummy data
		return null;
	}

	private boolean validateKey(HttpServletRequest request) {
		String verificationKey = request.getHeader("X-TODO-USER-KEY");
		return DEBUG_KEY.equals(verificationKey);
	}

}
