package org.openntf.todo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openntf.todo.domino.Utils;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.json.ResultParser;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.Store.StoreType;

@Path("/unitTests")
public class UnitTestResource {
	private final String DEBUG_KEY = "sseqdcof4fq472so10us7ck7r0";

	@Path("/storeTest")
	@GET
	public Response testStoresList(@Context HttpServletRequest request) {
		try {
			if (!validateKey(request)) {
				return Response.status(401).build();
			}
			List<Store> stores = new ArrayList<Store>();
			Store store1 = new Store();
			store1.setName(ToDoUtils.getStoreFilePath(Utils.getPersonalStoreName(), StoreType.PERSONAL));
			store1.setReplicaId("12345678123456781234567812345678");
			store1.setTitle("My Personal Store");
			store1.setType(StoreType.PERSONAL);
			Store store2 = new Store();
			store2.setName(ToDoUtils.getStoreFilePath("Test Store", StoreType.TEAM));
			store2.setReplicaId("87654321876543218765432187654321");
			store2.setTitle("Test Team Store");
			store2.setType(StoreType.TEAM);
			stores.add(store1);
			stores.add(store2);

			// Build JsonObject
			RequestBuilder builder = new RequestBuilder(Store.class);
			String json = builder.buildJson(stores);
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} catch (final Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	@POST
	@Path("/createStore")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createStoreTest(@Context HttpServletRequest request, final String body) {
		try {
			if (!validateKey(request)) {
				return Response.status(401).build();
			}
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

			passedStore.setReplicaId("12345678123456781234567812345678");

			// Build JsonObject
			RequestBuilder builder = new RequestBuilder(Store.class);
			String json = builder.buildJson(passedStore);
			return Response.ok(json, MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean validateKey(HttpServletRequest request) {
		String verificationKey = request.getHeader("X-DEBUG-KEY");
		return DEBUG_KEY.equals(verificationKey);
	}

}
