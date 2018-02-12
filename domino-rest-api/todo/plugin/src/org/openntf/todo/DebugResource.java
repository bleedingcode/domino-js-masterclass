package org.openntf.todo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

import com.ibm.commons.util.io.json.JsonJavaObject;

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

	private boolean validateKey(HttpServletRequest request) {
		String verificationKey = request.getHeader("X-TODO-USER-KEY");
		return DEBUG_KEY.equals(verificationKey);
	}

}
