package org.openntf.todo.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openntf.todo.ToDoUtils;
import org.openntf.todo.domino.ToDoStoreFactory;
import org.openntf.todo.domino.Utils;
import org.openntf.todo.exceptions.DataNotAcceptableException;
import org.openntf.todo.exceptions.DatabaseModuleException;
import org.openntf.todo.exceptions.StoreNotFoundException;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.json.ResultParser;
import org.openntf.todo.model.DatabaseAccess;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.Store.StoreType;
import org.openntf.todo.model.User;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.domino.httpmethod.PATCH;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Path("/v1/store")
public class StoreResource {

	/**
	 * Get personal store, if it exists. If it doesn't, throw Bad Request error 400
	 * 
	 * @return Response containing Store object for personal store or error
	 */
	@GET
	@Path("/mine")
	public Response getMyStore() {
		try {
			String myStorePath = ToDoUtils.getStoreFilePath(Utils.getPersonalStoreName(), StoreType.PERSONAL);
			Store store = ToDoStoreFactory.getInstance().getStore(myStorePath);
			RequestBuilder builder = new RequestBuilder(Store.class);
			return Response.ok(builder.buildJson(store), MediaType.APPLICATION_JSON).build();
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.STORE_NOT_FOUND_OR_ACCESS_ERROR).build());
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	/**
	 * Create a Store with the passed details
	 * 
	 * @param body
	 *            String json object comprising title, name and type properties
	 * @return Response containing created Store or error
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createStore(final String body) {
		try {
			Store passedStore = new ResultParser<Store>(Store.class).parse(body);
			if (null == passedStore.getTitle()) {
				return Response.status(Status.BAD_REQUEST).entity("Expected title in body").build();
			}

			if (null == passedStore.getType()) {
				return Response.status(Status.BAD_REQUEST).entity("type should be 'Personal' or 'Team'").build();
			} else if (StoreType.TEAM.equals(passedStore.getType())) {
				if (null == passedStore.getName()) {
					return Response.status(Status.BAD_REQUEST).entity("Expected name in body").build();
				}
				passedStore.setName(ToDoUtils.getStoreFilePath(passedStore.getName(), StoreType.TEAM));
			} else {
				passedStore.setName(ToDoUtils.getStoreFilePath(Utils.getPersonalStoreName(), StoreType.PERSONAL));
			}

			if (ToDoStoreFactory.getInstance().checkStoreExists(passedStore.getName())) {
				return Response.status(Status.CONFLICT).entity(
						"A store already exists with the name. (For personal stores, the username overrides the name passed)")
						.build();
			}

			// Create store
			Store store = ToDoStoreFactory.getInstance().createToDoNSF(passedStore.getTitle(), passedStore.getName(),
					passedStore.getType());
			RequestBuilder builder = new RequestBuilder(Store.class);
			return Response.ok(builder.buildJson(store), MediaType.APPLICATION_JSON).build();
		} catch (DatabaseModuleException de) {
			de.printStackTrace();
			throw new WebApplicationException(
					Response.status(Status.INTERNAL_SERVER_ERROR).entity(de.getMessage()).build());
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	/**
	 * Change title for the passed Store
	 * 
	 * @param storeKey
	 *            store id or name
	 * @param title
	 *            new title for the store
	 * @return Response containing updated store or error
	 */
	@PATCH
	@Path("/{store}/updateTitle")
	public Response changeTitle(final @PathParam(value = "store") String storeKey,
			final @HeaderParam(value = "title") String title) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStoreAsNative(storeKey);
			store.setTitle(title);
			ToDoStoreFactory.getInstance().updateToDoNSFTitle(store);
			store.serializeToCatalog();
			RequestBuilder builder = new RequestBuilder(Store.class);
			return Response.ok(builder.buildJson(store), MediaType.APPLICATION_JSON).build();
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity("The store could not be found with the name or replicaId passed").build());
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
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
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
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
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	/**
	 * Updates access for an individual to a store
	 * 
	 * @param storeKey
	 *            store id or name
	 * @param body
	 *            User to update with access level
	 * @return Response containing boolean success or error
	 */
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
			ResultParser<User[]> parser = new ResultParser<User[]>(User[].class);
			User[] newUsers = parser.parse(body);
			for (User user : newUsers) {
				user.validateForUpdate();
				user.setUsername(Utils.getAsUsername(user.getUsername()));
				// Update ACL if required
				DatabaseAccess currAccess = ToDoStoreFactory.getInstance().queryAccess(store, user.getUsername());
				if (!currAccess.getLevel().equals(user.getAccess().getLevel())
						&& !currAccess.getAllowDelete().equals(user.getAccess().getAllowDelete())) {
					ToDoStoreFactory.getInstance().updateAccess(store, user);
				}
			}

			JsonJavaObject jjo = new JsonJavaObject();
			jjo.put("success", true);
			return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
		} catch (DataNotAcceptableException de) {
			de.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(de.getMessage()).build());
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.STORE_NOT_FOUND_OR_ACCESS_ERROR).build());
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

}
