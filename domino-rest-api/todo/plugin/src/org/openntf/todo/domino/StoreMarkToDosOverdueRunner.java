package org.openntf.todo.domino;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openntf.domino.Database;
import org.openntf.domino.Document;
import org.openntf.domino.View;
import org.openntf.domino.ViewEntry;
import org.openntf.domino.ViewNavigator;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.xots.AbstractXotsRunnable;
import org.openntf.domino.xots.XotsUtil;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.ToDo;
import org.openntf.todo.model.ToDo.Status;
import org.openntf.todo.v1.ToDosResource.ViewType;

public class StoreMarkToDosOverdueRunner extends AbstractXotsRunnable {
	String replicaId;
	String nextUrl;

	public StoreMarkToDosOverdueRunner(String replicaId, String nextUrl) {
		this.replicaId = replicaId;
		this.nextUrl = nextUrl;
	}

	@Override
	public void run() {
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			Database db = Factory.getSession(SessionType.NATIVE).getDatabase(replicaId);
			View view = db.getView(ViewType.DATE.name());
			ViewNavigator nav = view.createViewNav();
			List<ToDo> todos = new ArrayList<ToDo>();
			Date dt = new Date();
			for (ViewEntry ent : nav) {
				if (dt.after(ent.getColumnValue("byDueDate", Date.class))) {
					Document doc = ent.getDocument();
					doc.replaceItemValue("status", Status.OVERDUE.getValue());
					doc.save();
					ToDo todo = ToDoStoreFactory.getInstance().deserializeToDoFromDoc(doc);
					todos.add(todo);
				} else {
					break;
				}
			}

			if (!todos.isEmpty()) {
				HttpPost post = new HttpPost(nextUrl);
				RequestBuilder<List<ToDo>> builder = new RequestBuilder(ToDo.class);
				StringEntity payload = new StringEntity(builder.buildJson(todos));
				post.setEntity(payload);
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() != 200) {
					System.out.println("Operation failed: " + response.getStatusLine().getStatusCode());
				}
			}
		} catch (Exception e) {
			XotsUtil.handleException(e, getContext());
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

}
