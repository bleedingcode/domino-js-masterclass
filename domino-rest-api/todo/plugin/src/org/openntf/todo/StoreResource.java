package org.openntf.todo;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.Store.StoreType;

import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;

@Path("/store")
public class StoreResource {

	@POST
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createStore(final String body) {
		try {
			final JsonJavaObject jjo = new JsonJavaObject();
			final Map<String, Object> bodyAsObj = (Map<String, Object>) JsonParser.fromJson(JsonJavaFactory.instance,
					body);
			if (!Utils.validateBody(bodyAsObj, "title", "name", "type")) {
				Response.status(Status.BAD_REQUEST).entity("Expected title, name and type in body").build();
			}

			StoreType passedType = Utils.validateStoreType((String) bodyAsObj.get("type"));
			if (null == passedType) {
				Response.status(Status.BAD_REQUEST).entity("type should be 'Personal' or 'Team'").build();
			}

			String title = (String) bodyAsObj.get("title");
			String name = (String) bodyAsObj.get("name");
			if (passedType.equals(StoreType.PERSONAL)) {
				name = Utils.getPersonalStoreName(Factory.getSession(SessionType.CURRENT));
			}
			if (null != ToDoStoreFactory.getInstance().getStore(Factory.getSession(SessionType.NATIVE), name)) {
				Response.status(Status.CONFLICT).entity(
						"A store already exists with the name. (For personal stores, the username overrides the name passed)")
						.build();
			}

			// Create store
			Store store = ToDoStoreFactory.getInstance().createToDoNSF(Factory.getSession(SessionType.NATIVE), title,
					name, passedType);
			RequestBuilder builder = new RequestBuilder(Store.class);
			return Response.ok(builder.buildJson(store), MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

}
