package org.openntf.todo.todo.cloudant;

public class ToDoConstants {

    public static String DESIGN_DOC_TODO = "_design/todo";
    public static String DESIGN_DOC_TODO_SIMPLE = "todo";
    public static String VIEW_TODO = "todos";
    public static String METADATA_UNID = "unid";
    public static String METADATA_DBNAME = "dbname";
    public static String FTSEARCH_TODO = "ftsearchTodo";
    public static int SEARCH_COUNT = 100;
    public static String METAVERSALID_SEPARATOR = "!!";

    public static String STORE_NOT_FOUND_OR_ACCESS_ERROR = "The store could not be found with the name or replicaId passed, or you do not have access to that store";
    public static String DOCUMENT_NOT_FOUND_ERROR = "The ToDo with that ID could not be found";
    public static String USER_NOT_AUTHORIZED_ERROR = "You are not authorized to perform this operation";
    public static String INVALID_METAVERSAL_ID_ERROR = "The value passed is not a valid metaversal id";

    public static final String SEARCH_INDEX = "todo/ftsearchTodo";
    public static final String SEARCH_QUERYREPLACE = "{QUERY}";
    public static final String SEARCH_QUERYREPLACE_1 = "{QUERY1}";
    public static final String SEARCH_QUERY_STATUS = "status:"+SEARCH_QUERYREPLACE;
    public static final String SEARCH_QUERY_STATUS_AND_PRIORTITY = "status:"+SEARCH_QUERYREPLACE+" AND priority:"+SEARCH_QUERYREPLACE_1;
    public static final String SEARCH_QUERY_STATUS_AND_USERNAME = "status:"+SEARCH_QUERYREPLACE+" AND author:"+SEARCH_QUERYREPLACE_1;
}
