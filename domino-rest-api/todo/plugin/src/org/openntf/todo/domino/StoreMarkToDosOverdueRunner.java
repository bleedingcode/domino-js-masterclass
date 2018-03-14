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
import org.openntf.domino.xots.Tasklet;
import org.openntf.domino.xots.Tasklet.Context;
import org.openntf.domino.xots.XotsUtil;
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.ToDo;
import org.openntf.todo.model.ToDo.Status;
import org.openntf.todo.v1.ToDosResource.ViewType;

/**
 * @author Paul Withers
 * 
 *         Xots tasklet to mark tasks overdue
 *
 */
@Tasklet(session = Tasklet.Session.NATIVE, context = Context.PLUGIN)
public class StoreMarkToDosOverdueRunner extends AbstractXotsRunnable {
	String replicaId;
	String nextUrl;

	/**
	 * Constructor, with replicaId for this Store and next URL to call passed in
	 * 
	 * @param replicaId
	 *            Store NSF replicaId
	 * @param nextUrl
	 *            URL to call on completion
	 */
	public StoreMarkToDosOverdueRunner(String replicaId, String nextUrl) {
		this.replicaId = replicaId;
		this.nextUrl = nextUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			// Get the Store NSF, get view by date
			Database db = Factory.getSession(SessionType.NATIVE).getDatabase(replicaId);
			View view = db.getView(ViewType.DATE.name());
			ViewNavigator nav = view.createViewNav();
			List<ToDo> todos = new ArrayList<ToDo>();
			Date dt = new Date();
			for (ViewEntry ent : nav) {
				// If due before now and Active, set to Overdue. Else abort
				if (dt.after(ent.getColumnValue("byDueDate", Date.class))) {
					Document doc = ent.getDocument();
					String thisStatus = doc.getItemValueString("status");
					if (Status.ACTIVE.getValue().equals(thisStatus)) {
						doc.replaceItemValue("status", Status.OVERDUE.getValue());
						doc.save();
						ToDo todo = ToDoStoreFactory.getInstance().deserializeToDoFromDoc(doc);
						todos.add(todo);
					}
				} else {
					break;
				}
			}

			// Post all ToDos that were marked overdue to Next URL
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
			// End HTTP connection
			client.getConnectionManager().shutdown();
		}
	}

}
