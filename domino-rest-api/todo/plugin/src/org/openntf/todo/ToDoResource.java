package org.openntf.todo;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openntf.todo.domino.ToDoStoreFactory;
import org.openntf.todo.exceptions.StoreNotFoundException;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.Store;

@Path("/todo")
public class ToDoResource {

	/**
	 * @param storeKey
	 *            String store replicaId or name
	 * @param body
	 *            JSON object containing ToDo
	 * @return Response returned ToDo
	 */
	@POST
	@Path("/{store}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addToDo(@PathParam(value = "store") final String storeKey, final String body) {
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
