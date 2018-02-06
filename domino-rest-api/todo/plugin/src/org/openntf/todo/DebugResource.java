package org.openntf.todo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.commons.util.io.json.JsonJavaObject;

@Path("/debug")
public class DebugResource {

	@Path("/toggleProfiling")
	@GET
	public Response toggleProfiling() {
		JsonJavaObject jjo = new JsonJavaObject();
		jjo.put("profiling", Utils.toggleProfiling());
		return Response.ok(jjo, MediaType.APPLICATION_JSON).build();
	}

}
