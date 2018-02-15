package org.openntf.todo;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openntf.todo.domino.ToDoStoreFactory;
import org.openntf.todo.domino.Utils;
import org.openntf.todo.exceptions.StoreNotFoundException;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.Store.StoreType;

import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Path("/store")
public class StoreResource {

	/**
	 * Create a Store with the passed details
	 * 
	 * @param body
	 *            String json object comprising title, name and type properties
	 * @return Response containing created Store or error
	 */
	@POST
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createStore(final String body) {
		try {
			final Map<String, Object> bodyAsObj = (Map<String, Object>) JsonParser.fromJson(JsonJavaFactory.instance,
					body);
			if (!ToDoUtils.validateBody(bodyAsObj, "title", "name", "type")) {
				Response.status(Status.BAD_REQUEST).entity("Expected title, name and type in body").build();
			}

			StoreType passedType = ToDoUtils.validateStoreType((String) bodyAsObj.get("type"));
			if (null == passedType) {
				Response.status(Status.BAD_REQUEST).entity("type should be 'Personal' or 'Team'").build();
			}

			String title = (String) bodyAsObj.get("title");
			String name = (String) bodyAsObj.get("name");
			if (passedType.equals(StoreType.PERSONAL)) {
				name = Utils.getPersonalStoreName();
			}
			if (null != ToDoStoreFactory.getInstance().getStoreAsNative(name)) {
				Response.status(Status.CONFLICT).entity(
						"A store already exists with the name. (For personal stores, the username overrides the name passed)")
						.build();
			}

			// Create store
			Store store = ToDoStoreFactory.getInstance().createToDoNSF(title,
					name, passedType);
			RequestBuilder builder = new RequestBuilder(Store.class);
			return Response.ok(builder.buildJson(store), MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Change title for the passed Store
	 * 
	 * @param storeKey
	 *            store id or name
	 * @param newTitle
	 *            new title for the store
	 * @return Response containing updated store or error
	 */
	@PUT
	@Path("/{store}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeTitle(final @PathParam(value = "store") String storeKey,
			final @QueryParam(value = "newTitle") String newTitle) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStoreAsNative(storeKey);
			store.setTitle(newTitle);
			store.serializeToCatalog();
			RequestBuilder builder = new RequestBuilder(Store.class);
			return Response.ok(builder.buildJson(store), MediaType.APPLICATION_JSON).build();
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity("The store could not be found with the name or replicaId passed").build());
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Get Store object corresponding to a given store
	 * 
	 * @param storeKey
	 *            store id or name
	 * @return Response containing updated store or error
	 */
	@GET
	@Path("/{store}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStoreInfo(final @PathParam(value = "store") String storeKey) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(storeKey);
			RequestBuilder builder = new RequestBuilder(Store.class);
			return Response.ok(builder.buildJson(store), MediaType.APPLICATION_JSON).build();
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.STORE_NOT_FOUND_OR_ACCESS_ERROR).build());
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Queries access for the current user
	 * 
	 * @param storeKey
	 *            store id or name
	 * @return User object corresponding to current username and access level to queried store
	 */
	@GET
	@Path("/{store}/access")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response queryAccess(final @PathParam(value = "store") String storeKey) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStoreAsNative(storeKey);

			// TODO: Create a User object corresponding to the username and access level

			// TODO: Return User object
			RequestBuilder builder = new RequestBuilder(Store.class);
			return Response.ok(builder.buildJson(store), MediaType.APPLICATION_JSON).build();
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.STORE_NOT_FOUND_OR_ACCESS_ERROR).build());
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	@PUT
	@Path("/{store}/access")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateAccess(final @PathParam(value = "store") String storeKey, final String body) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStoreAsNative(storeKey);
			// TODO: Check current user has Admin role access

			// TODO: Extract users to update from body and validate

			// TODO: Update ACL

			JsonJavaObject jjo = new JsonJavaObject();
			jjo.put("success", true);
			return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.STORE_NOT_FOUND_OR_ACCESS_ERROR).build());
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

}
