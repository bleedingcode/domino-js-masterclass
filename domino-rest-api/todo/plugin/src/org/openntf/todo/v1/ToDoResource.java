/*******************************************************************************
 * Copyright 2018 Paul Withers
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.openntf.todo.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.openntf.todo.model.Store.StoreType;
import org.openntf.todo.model.ToDo;
import org.openntf.todo.model.User;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.domino.httpmethod.PATCH;

/**
 * @author Paul Withers
 * 
 *         Endpoints for an individual ToDo
 *
 */
@Path("/v1/todo")
@SuppressWarnings("unchecked")
public class ToDoResource {

	/**
	 * @param storeKey
	 *            String store replicaId or name
	 * @param body
	 *            JSON object containing ToDo
	 * @return Response returned ToDo or error
	 */
	@POST
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addToDo(@PathParam(value = "key") final String storeKey, final String body) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(storeKey);
			ToDo todo = new ResultParser<ToDo>(ToDo.class).parse(body);
			todo.setAuthor(Utils.getCurrentUsername());
			if (StringUtils.isNotEmpty(todo.getAssignedTo()) || StoreType.PERSONAL.equals(store.getType())) {
				todo.setAssignedTo(todo.getAuthor());
			}
			todo.setStatus(ToDo.Status.NEW);
			todo.validateForUpdate();
			todo = ToDoStoreFactory.getInstance().serializeToStore(store, todo);

			RequestBuilder<ToDo> builder = new RequestBuilder<ToDo>(ToDo.class);
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
	 * @return Response returned ToDo or error
	 */
	@GET
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getToDo(@PathParam(value = "key") final String metaversalId) {
		try {
			ToDo todo = ToDoStoreFactory.getInstance().getToDoFromMetaversalId(metaversalId);

			RequestBuilder<ToDo> builder = new RequestBuilder<ToDo>(ToDo.class);
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
	 * @return Response returned ToDo or error
	 */
	@PATCH
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateToDo(@PathParam(value = "key") final String metaversalId, final String body) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(Utils.getReplicaIdFromMetaversalId(metaversalId));
			ToDo todo = new ResultParser<ToDo>(ToDo.class).parse(body);
			if (StringUtils.isNotEmpty(todo.getAssignedTo())) {
				ToDo oldTodo = ToDoStoreFactory.getInstance().getToDoFromMetaversalId(todo.getMetaversalId());
				if (!StringUtils.equals(todo.getAssignedTo(), oldTodo.getAssignedTo())) {
					if (StoreType.PERSONAL.equals(store.getType())) {
						return Response.status(Status.BAD_REQUEST).entity("Personal ToDos cannot be reassigned")
								.build();
					}
				}
			}
			todo.setMetaversalId(metaversalId);
			todo = todo.compareAndUpdateFromPrevious();
			todo = ToDoStoreFactory.getInstance().updateToDo(todo);

			RequestBuilder<ToDo> builder = new RequestBuilder<ToDo>(ToDo.class);
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
	 * @param metaversalId
	 *            String metaversalId of the ToDo
	 * @return Response {"success": true} or error
	 */
	@DELETE
	@Path("/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteToDo(@PathParam(value = "key") final String metaversalId) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(Utils.getReplicaIdFromMetaversalId(metaversalId));
			DatabaseAccess dbAccess = ToDoStoreFactory.getInstance().queryAccess(store, Utils.getCurrentUsername());
			if (!dbAccess.getAllowDelete()) {
				return Response.status(Status.FORBIDDEN).build();
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
	 * @return Response {"success":true} or error
	 */
	@POST
	@Path("/{toDoId}/reassign")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reassignToDo(@PathParam(value = "toDoId") final String metaversalId, final String body) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(Utils.getReplicaIdFromMetaversalId(metaversalId));
			if (StoreType.PERSONAL.equals(store.getType())) {
				return Response.status(Status.FORBIDDEN).entity("Personal ToDos cannot be reassigned").build();
			}
			User newUser = new ResultParser<User>(User.class).parse(body);
			if (StringUtils.isEmpty(newUser.getUsername())) {
				return Response.status(Status.BAD_REQUEST).entity("Username must be supplied").build();
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
	 * @return Response ["success":true} or error
	 */
	@POST
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
	 * @return Response {"success":true} or error
	 */
	@POST
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
