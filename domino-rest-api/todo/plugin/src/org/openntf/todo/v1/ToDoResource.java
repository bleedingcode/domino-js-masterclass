package org.openntf.todo.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openntf.todo.domino.ToDoStoreFactory;
import org.openntf.todo.exceptions.DocumentNotFoundException;
import org.openntf.todo.exceptions.StoreNotFoundException;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.ToDo;

@Path("/v1/todo")
public class ToDoResource {

	/**
	 * @param storeKey
	 *            String store replicaId or name
	 * @param body
	 *            JSON object containing ToDo
	 * @return Response returned ToDo
	 */
	@POST
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addToDo(@PathParam(value = "key") final String storeKey, final String body) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(storeKey);

			// TODO: Build collection
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
	 * @param metaversalId
	 *            String metaversalId of the ToDo
	 * @param body
	 *            JSON object containing ToDo
	 * @return Response returned ToDo
	 */
	@GET
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getToDo(@PathParam(value = "key") final String metaversalId) {
		try {
			ToDo todo = ToDoStoreFactory.getInstance().getToDoFromDoc(metaversalId);

			RequestBuilder builder = new RequestBuilder(ToDo.class);
			return Response.ok(builder.buildJson(todo), MediaType.APPLICATION_JSON).build();
		} catch (DocumentNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity(ToDoStoreFactory.DOCUMENT_NOT_FOUND_ERROR).build());
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
	 * @param metaversalId
	 *            String metaversalId of the ToDo
	 * @param body
	 *            JSON object containing ToDo
	 * @return Response returned ToDo
	 */
	@PUT
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateToDo(@PathParam(value = "key") final String metaversalId, final String body) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(metaversalId);
			// TODO: Get ToDos for this Store between dates passed

			// TODO: Build collection
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
	 * @param storeKey
	 *            String metaversalId of the ToDo
	 * @return Response returned ToDo
	 */
	@DELETE
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteToDo(@PathParam(value = "key") final String metaversalId) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(metaversalId);
			// TODO: Get ToDos for this Store between dates passed

			// TODO: Build collection
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
	 * @param metaversalId
	 *            String metaversalId of the ToDo
	 * @param body
	 *            JSON object containing User to reassign to
	 * @return Response returned ToDo
	 */
	@PUT
	@Path("/{toDoId}/reassign")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reassignToDo(@PathParam(value = "toDoId") final String metaversalId, final String body) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(metaversalId);
			// TODO: Get ToDos for this Store between dates passed

			// TODO: Build collection
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
	 * @param metaversalId
	 *            String metaversalId of the ToDo
	 * @return Response returned ToDo
	 */
	@PUT
	@Path("/{toDoId}/complete")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response completeToDo(@PathParam(value = "toDoId") final String metaversalId) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(metaversalId);
			// TODO: Get ToDos for this Store between dates passed

			// TODO: Build collection
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
	 * @param metaversalId
	 *            String metaversalId of the ToDo
	 * @return Response returned ToDo
	 */
	@PUT
	@Path("/{toDoId}/reopen")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reopenToDo(@PathParam(value = "toDoId") final String metaversalId) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(metaversalId);
			// TODO: Get ToDos for this Store between dates passed

			// TODO: Build collection
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

}
