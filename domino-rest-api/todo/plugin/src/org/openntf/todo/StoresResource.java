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
package org.openntf.todo;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.openntf.domino.Session;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.das.utils.ErrorHelper;
import com.ibm.domino.osgi.core.context.ContextInfo;

/**
 * @author Paul Withers
 * @since 1.0.0
 *
 *        Sample REST endpoint Resource class URL will be
 *        <em>protocol://server.host.name/optionalDbPath/odaDemoServlet/rest/helloworld</em>
 *
 *        <ul>
 *        <li>Protocol will be "http" or "https" depending on server configuration</li>
 *        <li>OptionalDbPath is optional and, if included, gives a database context in which the REST service runs,
 *        accessible via {@link ContextInfo#getUserDatabase()}</li>
 *        <li>"todoApp" is pulled from the plugin.xml's "contextRoot" setting for the
 *        "com.ibm.pvc.webcontainer.application" endpoint</li>
 *        <li>"rest" is the servlet-mapping in the web.xml</li>
 *        <li>* is @Path added to everything in this class</li>
 *
 */
@Path("/stores")
public class StoresResource {

	/**
	 * Basic endpoint, using path of class with no additional URL path
	 *
	 * @return Json response with "Hello World " + current user + current
	 *         database path; or error
	 */
	@SuppressWarnings("restriction")
	@GET
	public Response getStores() {
		final JsonJavaObject jjo = new JsonJavaObject();
		try {
			final Session session = Factory.getSession(SessionType.CURRENT);
			final Session serverSession = Factory.getSession(SessionType.NATIVE);
			String message = "Hello World";
			if (session != null) {
				message += " " + session.getEffectiveUserName();
				final lotus.domino.Database db = ContextInfo.getUserDatabase();
				if (null == db) {
					message += ", no db";
				} else {
					message += ", db " + db.getFilePath();
				}
			}
			jjo.put("message", message);
			message = "Hello World";
			if (serverSession != null) {
				message += " " + serverSession.getEffectiveUserName();
			}
			jjo.put("serverMessage", message);
			final ResponseBuilder builder = Response.ok(jjo.toString(), MediaType.APPLICATION_JSON);
			return builder.build();
		} catch (final Exception e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * A restricted endpoint, which the user must be authenticated to use, so
	 * "...helloworld/restricted"
	 *
	 * @return Response with username or HTTP 403 error
	 */
	@GET
	@Path("/restricted")
	public Response mustBeAuthenticated() {
		final Session session = Factory.getSession(SessionType.CURRENT);
		final JsonJavaObject jjo = new JsonJavaObject();
		if ("Anonymous".equals(session.getEffectiveUserName())) {
			throw new WebApplicationException(ErrorHelper
					.createErrorResponse("You are not authorized to access this endpoint", Status.FORBIDDEN));
		} else {
			jjo.put("Username", session.getEffectiveUserName());
			return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
		}

	}

	/**
	 * An endpoint to test including a url parameter and sending Json data, so
	 * "...helloworld/" + param
	 *
	 * @param msg
	 *            String the url parameter
	 * @param body
	 *            String the Json data passed
	 * @return Json object echoing url parameter, Json body as string and Json
	 *         body passed
	 * @throws JsonException
	 *             Exception if parsing the Json data
	 */
	@POST
	@Path("/{param}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postMessage(@PathParam("param") final String msg, final String body) throws JsonException {
		final String output = "POST: " + msg;
		final JsonJavaObject jjo = new JsonJavaObject();
		jjo.put("ParamMessage", output);
		jjo.put("jsonObjectAsString", body);
		final Map<String, Object> jsonAsObj = (Map<String, Object>) JsonParser.fromJson(JsonJavaFactory.instance, body);
		for (final String key : jsonAsObj.keySet()) {
			jjo.put(key, jsonAsObj.get(key));
		}
		return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
	}

}