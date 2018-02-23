package org.openntf.todo;

import org.apache.commons.lang3.StringUtils;
import org.openntf.todo.json.AccessLevelDeserializer;
import org.openntf.todo.json.AccessLevelSerializer;
import org.openntf.todo.json.PriorityDeserializer;
import org.openntf.todo.json.PrioritySerializer;
import org.openntf.todo.json.StatusDeserializer;
import org.openntf.todo.json.StatusSerializer;
import org.openntf.todo.json.StoreTypeDeserializer;
import org.openntf.todo.json.StoreTypeSerializer;
import org.openntf.todo.model.DatabaseAccess.AccessLevel;
import org.openntf.todo.model.Store;
import org.openntf.todo.model.Store.StoreType;
import org.openntf.todo.model.ToDo.Priority;
import org.openntf.todo.model.ToDo.Status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ToDoUtils {
	public static String DEBUG_PREFIX = "[TODO_APP]: ";
	private static boolean PROFILING = false;

	public static void debugPrint(String msg) {
		System.out.println(DEBUG_PREFIX + msg);
	}

	public static long startTimer() {
		return System.currentTimeMillis();
	}

	public static boolean toggleProfiling() {
		PROFILING = !PROFILING;
		System.out.println(DEBUG_PREFIX + "Profiling enabled: " + PROFILING);
		return PROFILING;
	}

	public static void debugTimer(long startTimer, String msg) {
		if (PROFILING) {
			long now = System.currentTimeMillis();
			System.out.println(DEBUG_PREFIX + msg + " completed in " + (now - startTimer) + " milliseconds");
		}
	}

	public static String getStoreFilePath(String name, StoreType type) {
		name = StringUtils.replace(StringUtils.replace(name, "/", "_"), " ", "_");
		return StringUtils.lowerCase(Store.TODO_PATH + type.getValue() + "/" + name + ".nsf");
	}

	public static Gson getGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat("yyyy-MM-dd");
		builder.registerTypeAdapter(AccessLevel.class, new AccessLevelSerializer());
		builder.registerTypeAdapter(AccessLevel.class, new AccessLevelDeserializer());
		builder.registerTypeAdapter(Priority.class, new PrioritySerializer());
		builder.registerTypeAdapter(Priority.class, new PriorityDeserializer());
		builder.registerTypeAdapter(Status.class, new StatusSerializer());
		builder.registerTypeAdapter(Status.class, new StatusDeserializer());
		builder.registerTypeAdapter(StoreType.class, new StoreTypeSerializer());
		builder.registerTypeAdapter(StoreType.class, new StoreTypeDeserializer());
		return builder.create();
	}

	public static String getErrorMessage(Exception e) {
		if (null == e.getMessage()) {
			if (null != e.getCause()) {
				return e.getCause().getMessage();
			} else {
				return "Unexpected error, see stack trace on server";
			}
		} else {
			return e.getMessage();
		}
	}

}
