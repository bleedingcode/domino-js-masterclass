package org.openntf.todo;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openntf.todo.domino.ToDoStoreFactory;
import org.openntf.todo.exceptions.StoreNotFoundException;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.Store;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Path("/todos")
public class ToDosResource {

	/**
	 * Get all ToDos in a given store for a given status (or unassigned)
	 * 
	 * @param storeKey
	 *            String store id or name
	 * @param status
	 *            String status to look for (or Unassigned)
	 * @return Response containing a collection of ToDos
	 */
	@GET
	@Path("/{store}/findByStatus")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getToDosByStatus(@PathParam(value = "store") String storeKey,
			@QueryParam(value = "status") String status) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(storeKey);
			// TODO: Get ToDos for this Store for the passed status

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
	 * Get all ToDos in a given store for a specific user (or current user) at a given status (or unassigned)
	 * 
	 * @param storeKey
	 *            String store id or name
	 * @param username
	 *            String user to check for (or current user)
	 * @param status
	 *            String status to look for (or Unassigned)
	 * @return Response containing a collection of ToDos
	 */
	@GET
	@Path("/{store}/findByAssigneeAndStatus")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getToDosByAssigneeAndStatus(@PathParam(value = "store") String storeKey,
			@QueryParam(value = "username") String username, @QueryParam(value = "status") String status) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(storeKey);
			// TODO: Get ToDos for this Store for the user (or current) and passed status

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
	 * Get all ToDos in a given store for a specific priority (or high) at a given status (or unassigned)
	 * 
	 * @param storeKey
	 *            String store id or name
	 * @param priority
	 *            String priority to look for (or High)
	 * @param status
	 *            String status to look for (or Unassigned)
	 * @return Response containing a collection of ToDos
	 */
	@GET
	@Path("/{store}/findByPriorityAndStatus")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getToDosByPriorityAndStatus(@PathParam(value = "store") String storeKey,
			@QueryParam(value = "priority") String priority, @QueryParam(value = "status") String status) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(storeKey);
			// TODO: Get ToDos for this Store for the priority (or High) and passed status

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
	 * Get all ToDos in a given store for a specific priority (or high) at a given status (or unassigned)
	 * 
	 * @param storeKey
	 *            String store id or name
	 * @param priority
	 *            String priority to look for (or High)
	 * @param status
	 *            String status to look for (or Unassigned)
	 * @return Response containing a collection of ToDos
	 */
	@GET
	@Path("/{store}/findByDate")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getToDosByDate(@PathParam(value = "store") String storeKey,
			@QueryParam(value = "startDate") String startDate, @QueryParam(value = "endDate") String endDate) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(storeKey);
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
