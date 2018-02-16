package org.openntf.todo;

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
import org.openntf.todo.json.ResultParser;
import org.openntf.todo.model.DatabaseAccess;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.Store.StoreType;
import org.openntf.todo.model.User;

import com.google.gson.Gson;
import com.ibm.commons.util.io.json.JsonJavaObject;

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
			Store passedStore = new ResultParser<Store>(Store.class).parse(body);
			if (null == passedStore.getTitle()) {
				Response.status(Status.BAD_REQUEST).entity("Expected title in body").build();
			}

			if (null == passedStore.getType()) {
				Response.status(Status.BAD_REQUEST).entity("type should be 'Personal' or 'Team'").build();
			} else if (StoreType.TEAM.equals(passedStore.getType())) {
				if (null == passedStore.getName()) {
					Response.status(Status.BAD_REQUEST).entity("Expected name in body").build();
				}
				passedStore.setName(ToDoUtils.getStoreFilePath(passedStore.getName(), StoreType.TEAM));
			} else {
				passedStore.setName(ToDoUtils.getStoreFilePath(Utils.getPersonalStoreName(), StoreType.PERSONAL));
			}

			if (null != ToDoStoreFactory.getInstance().getStoreAsNative(passedStore.getName())) {
				Response.status(Status.CONFLICT).entity(
						"A store already exists with the name. (For personal stores, the username overrides the name passed)")
						.build();
			}

			// Create store
			Store store = ToDoStoreFactory.getInstance().createToDoNSF(passedStore.getTitle(),
					passedStore.getName(), passedStore.getType());
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

			User user = new User(Utils.getCurrentUsername());
			user.setAccess(ToDoStoreFactory.getInstance().queryAccess(store, user.getUsername()));

			RequestBuilder builder = new RequestBuilder(User.class);
			return Response.ok(builder.buildJson(user), MediaType.APPLICATION_JSON).build();
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
			User currUser = new User(Utils.getCurrentUsername());
			if (!ToDoStoreFactory.getInstance().userIsAdmin(store, currUser.getUsername())) {
				throw new WebApplicationException(
						Response.status(Status.FORBIDDEN).entity(ToDoStoreFactory.USER_NOT_AUTHORIZED_ERROR).build());
			}

			// Extract users to update from body and validate
			Gson gson = new Gson();
			User[] newUsers = gson.fromJson(body, User[].class);
			for (User user : newUsers) {
				if (user.isVaidForUpdate()) {
					// Update ACL if required
					DatabaseAccess currAccess = ToDoStoreFactory.getInstance().queryAccess(store, user.getUsername());
					if (!currAccess.getLevel().equals(user.getAccess().getLevel())
							&& !currAccess.getAllowDelete().equals(user.getAccess().getAllowDelete())) {
						ToDoStoreFactory.getInstance().updateAccess(store, user);
					}
				}
			}

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
