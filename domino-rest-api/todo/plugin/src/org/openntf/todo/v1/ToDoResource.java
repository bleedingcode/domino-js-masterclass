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

import org.apache.commons.lang3.StringUtils;
import org.openntf.todo.domino.ToDoStoreFactory;
import org.openntf.todo.domino.Utils;
import org.openntf.todo.exceptions.DataNotAcceptableException;
import org.openntf.todo.exceptions.DocumentNotFoundException;
import org.openntf.todo.exceptions.InvalidMetaversalIdException;
import org.openntf.todo.exceptions.StoreNotFoundException;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.json.ResultParser;
import org.openntf.todo.model.DatabaseAccess;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.ToDo;
import org.openntf.todo.model.User;

import com.ibm.commons.util.io.json.JsonJavaObject;

@Path("/v1/todo")
@SuppressWarnings("unchecked")
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
			ToDo todo = new ResultParser<ToDo>(ToDo.class).parse(body);
			todo.validateForUpdate();
			todo = ToDoStoreFactory.getInstance().serializeToStore(store, todo);

			RequestBuilder builder = new RequestBuilder(ToDo.class);
			return Response.ok(builder.buildJson(todo), MediaType.APPLICATION_JSON).build();
		} catch (DataNotAcceptableException de) {
			de.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(de.getMessage()).build());
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.STORE_NOT_FOUND_OR_ACCESS_ERROR).build());
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(
					Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
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
			ToDo todo = ToDoStoreFactory.getInstance().getToDoFromMetaversalId(metaversalId);

			RequestBuilder builder = new RequestBuilder(ToDo.class);
			return Response.ok(builder.buildJson(todo), MediaType.APPLICATION_JSON).build();
		} catch (InvalidMetaversalIdException ie) {
			ie.printStackTrace();
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity(ToDoStoreFactory.INVALID_METAVERSAL_ID_ERROR).build());
		} catch (DocumentNotFoundException dde) {
			dde.printStackTrace();
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity(ToDoStoreFactory.DOCUMENT_NOT_FOUND_ERROR).build());
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.STORE_NOT_FOUND_OR_ACCESS_ERROR).build());
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(
					Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
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
			ToDo todo = new ResultParser<ToDo>(ToDo.class).parse(body);
			todo = todo.compareAndUpdateFromPrevious();
			todo = ToDoStoreFactory.getInstance().updateToDo(todo);

			RequestBuilder builder = new RequestBuilder(ToDo.class);
			return Response.ok(builder.buildJson(todo), MediaType.APPLICATION_JSON).build();
		} catch (InvalidMetaversalIdException ie) {
			ie.printStackTrace();
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity(ToDoStoreFactory.INVALID_METAVERSAL_ID_ERROR).build());
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.STORE_NOT_FOUND_OR_ACCESS_ERROR).build());
		} catch (DocumentNotFoundException dde) {
			dde.printStackTrace();
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity(ToDoStoreFactory.DOCUMENT_NOT_FOUND_ERROR).build());
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(
					Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
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
			Store store = ToDoStoreFactory.getInstance().getStore(Utils.getReplicaIdFromMetaversalId(metaversalId));
			DatabaseAccess dbAccess = ToDoStoreFactory.getInstance().queryAccess(store, Utils.getCurrentUsername());
			if (!dbAccess.getAllowDelete()) {
				throw new WebApplicationException(Status.FORBIDDEN);
			}
			
			JsonJavaObject jjo = new JsonJavaObject();
			jjo.put("success", ToDoStoreFactory.getInstance().deleteToDoDoc(metaversalId));
			return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
		} catch (InvalidMetaversalIdException ie) {
			ie.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.INVALID_METAVERSAL_ID_ERROR).build());
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
			User newUser = new ResultParser<User>(User.class).parse(body);
			if (StringUtils.isEmpty(newUser.getUsername())) {
				throw new WebApplicationException(
						Response.status(Status.BAD_REQUEST).entity("Username must be supplied").build());
			}
			ToDo todo = ToDoStoreFactory.getInstance().getToDoFromMetaversalId(metaversalId);
			todo.setAssignedTo(Utils.getAsUsername(newUser.getUsername()));
			ToDoStoreFactory.getInstance().updateToDo(todo);

			JsonJavaObject jjo = new JsonJavaObject();
			jjo.put("success", true);
			return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
		} catch (InvalidMetaversalIdException ie) {
			ie.printStackTrace();
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity(ToDoStoreFactory.INVALID_METAVERSAL_ID_ERROR).build());
		} catch (DocumentNotFoundException dde) {
			dde.printStackTrace();
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
	 * @return Response returned ToDo
	 */
	@PUT
	@Path("/{toDoId}/complete")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response completeToDo(@PathParam(value = "toDoId") final String metaversalId) {
		try {
			ToDo todo = ToDoStoreFactory.getInstance().getToDoFromMetaversalId(metaversalId);
			todo.setStatus(ToDo.Status.COMPLETE);
			ToDoStoreFactory.getInstance().updateToDo(todo);

			JsonJavaObject jjo = new JsonJavaObject();
			jjo.put("success", true);
			return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
		} catch (InvalidMetaversalIdException ie) {
			ie.printStackTrace();
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity(ToDoStoreFactory.INVALID_METAVERSAL_ID_ERROR).build());
		} catch (DocumentNotFoundException dde) {
			dde.printStackTrace();
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
	 * @return Response returned ToDo
	 */
	@PUT
	@Path("/{toDoId}/reopen")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reopenToDo(@PathParam(value = "toDoId") final String metaversalId) {
		try {
			ToDo todo = ToDoStoreFactory.getInstance().getToDoFromMetaversalId(metaversalId);
			todo.setStatus(ToDo.Status.NEW);
			ToDoStoreFactory.getInstance().updateToDo(todo);

			JsonJavaObject jjo = new JsonJavaObject();
			jjo.put("success", true);
			return Response.ok(jjo.toString(), MediaType.APPLICATION_JSON).build();
		} catch (InvalidMetaversalIdException ie) {
			ie.printStackTrace();
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity(ToDoStoreFactory.INVALID_METAVERSAL_ID_ERROR).build());
		} catch (DocumentNotFoundException dde) {
			dde.printStackTrace();
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

}
