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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.openntf.todo.domino.ToDoStoreFactory;
import org.openntf.todo.domino.Utils;
import org.openntf.todo.exceptions.StoreNotFoundException;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.ToDo;
import org.openntf.todo.model.ToDo.Priority;

/**
 * @author Paul Withers
 * 
 *         Endpoint for collections of ToDos
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Path("/v1/todos")
public class ToDosResource {

	/**
	 * @author Paul Withers
	 * 
	 *         Enum for view type, to pass to platform-specific code to identify where to get the collection from
	 *
	 */
	public enum ViewType {
		STATUS, ASSIGNEE, PRIORITY, DATE;
	}

	/**
	 * Get all ToDos in a given store for a given status (or "New")
	 * 
	 * @param storeKey
	 *            String store id or name
	 * @param status
	 *            String status to look for (or New)
	 * @return Response containing a collection of ToDos or error
	 */
	@GET
	@Path("/{store}/findByStatus")
	public Response getToDosByStatus(@PathParam(value = "store") String storeKey,
			@QueryParam(value = "status") String status) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(storeKey);
			if (StringUtils.isEmpty(status)) {
				status = ToDo.Status.NEW.getValue();
			}
			List<ToDo> todos = ToDoStoreFactory.getInstance().getToDoCollection(store, ViewType.STATUS, status);

			RequestBuilder<List<ToDo>> builder = new RequestBuilder(ToDo.class);
			return Response.ok(builder.buildJson(todos), MediaType.APPLICATION_JSON).build();
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.STORE_NOT_FOUND_OR_ACCESS_ERROR).build());
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	/**
	 * Get all ToDos in a given store for a specific user (or current user) at a given status (or New)
	 * 
	 * @param storeKey
	 *            String store id or name
	 * @param username
	 *            String user to check for (or current user)
	 * @param status
	 *            String status to look for (or New)
	 * @return Response containing a collection of ToDos or error
	 */
	@GET
	@Path("/{store}/findByAssigneeAndStatus")
	public Response getToDosByAssigneeAndStatus(@PathParam(value = "store") String storeKey,
			@QueryParam(value = "username") String username, @QueryParam(value = "status") String status) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(storeKey);
			if (StringUtils.isEmpty(username)) {
				username = Utils.getCurrentUsername();
			}
			if (StringUtils.isEmpty(status)) {
				status = ToDo.Status.NEW.getValue();
			}
			List<String> keys = new ArrayList<String>();
			keys.add(username);
			keys.add(status);
			List<ToDo> todos = ToDoStoreFactory.getInstance().getToDoCollection(store, ViewType.ASSIGNEE, keys);

			RequestBuilder<List<ToDo>> builder = new RequestBuilder(ToDo.class);
			return Response.ok(builder.buildJson(todos), MediaType.APPLICATION_JSON).build();
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.STORE_NOT_FOUND_OR_ACCESS_ERROR).build());
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	/**
	 * Get all ToDos in a given store for a specific priority (or High) at a given status (or New)
	 * 
	 * @param storeKey
	 *            String store id or name
	 * @param priority
	 *            String priority to look for (or High)
	 * @param status
	 *            String status to look for (or New)
	 * @return Response containing a collection of ToDos or error
	 */
	@GET
	@Path("/{store}/findByPriorityAndStatus")
	public Response getToDosByPriorityAndStatus(@PathParam(value = "store") String storeKey,
			@QueryParam(value = "priority") String priority, @QueryParam(value = "status") String status) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(storeKey);
			if (StringUtils.isEmpty(priority)) {
				priority = Priority.HIGH.getValue();
			}
			if (StringUtils.isEmpty(status)) {
				status = ToDo.Status.NEW.getValue();
			}
			List<String> keys = new ArrayList<String>();
			keys.add(priority);
			keys.add(status);
			List<ToDo> todos = ToDoStoreFactory.getInstance().getToDoCollection(store, ViewType.PRIORITY, keys);

			RequestBuilder<List<ToDo>> builder = new RequestBuilder(ToDo.class);
			return Response.ok(builder.buildJson(todos), MediaType.APPLICATION_JSON).build();
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.STORE_NOT_FOUND_OR_ACCESS_ERROR).build());
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	/**
	 * Get all ToDos in a given store after a specific date (or today) and before a specific date (or today + 1 year)
	 * 
	 * @param storeKey
	 *            String store id or name
	 * @param startDate
	 *            String startDate to look for or today
	 * @param endDate
	 *            String endDate to look for or today + 1 year
	 * @return Response containing a collection of ToDos or error
	 */
	@GET
	@Path("/{store}/findByDate")
	public Response getToDosByDate(@PathParam(value = "store") String storeKey,
			@QueryParam(value = "startDate") String startDate, @QueryParam(value = "endDate") String endDate) {
		try {
			Store store = ToDoStoreFactory.getInstance().getStore(storeKey);
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			Date dtStart = new Date();
			if (StringUtils.isNotEmpty(startDate)) {
				dtStart = sdf.parse(startDate);
			}
			Date dtEnd = null;
			if (StringUtils.isEmpty(endDate)) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.YEAR, 1);
				dtEnd = cal.getTime();
			} else {
				dtEnd = sdf.parse(endDate);
			}
			List<ToDo> todos = ToDoStoreFactory.getInstance().getToDoCollectionRange(store, ViewType.DATE, dtStart,
					dtEnd);

			RequestBuilder<List<ToDo>> builder = new RequestBuilder(ToDo.class);
			return Response.ok(builder.buildJson(todos), MediaType.APPLICATION_JSON).build();
		} catch (StoreNotFoundException se) {
			se.printStackTrace();
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity(ToDoStoreFactory.STORE_NOT_FOUND_OR_ACCESS_ERROR).build());
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

}
