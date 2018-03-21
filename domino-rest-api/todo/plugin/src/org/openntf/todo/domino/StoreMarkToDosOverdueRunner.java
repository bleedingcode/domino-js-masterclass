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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

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
import org.openntf.todo.json.RequestBuilder;
import org.openntf.todo.model.ToDo;
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
		try {
			// Get the Store NSF, get view by date
			Database db = Factory.getSession(SessionType.NATIVE).getDatabase(replicaId);
			View view = ToDoStoreFactory.getInstance().getView(db, ViewType.DATE);
			ViewNavigator nav = view.createViewNav();
			List<ToDo> todos = new ArrayList<ToDo>();
			for (ViewEntry ent : nav) {
				// If due before now and Active, set to Overdue. Else abort
				Document doc = ent.getDocument();
				ToDo todo = ToDoStoreFactory.getInstance().deserializeToDoFromDoc(doc);
				if (todo.checkOverdue()) {
					todos.add(todo);
				}
			}

			// Post all ToDos that were marked overdue to Next URL
			System.out.println(nextUrl);
			if (!todos.isEmpty()) {
				URL url = new URL(nextUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
				RequestBuilder<List<ToDo>> builder = new RequestBuilder(ToDo.class);
				OutputStream os = conn.getOutputStream();
				String body = builder.buildJson(todos);
				os.write(body.getBytes());

				conn.connect();
				if (conn.getResponseCode() == 201 || conn.getResponseCode() == 200) {
					System.out.println("Chain request sent successfully");
				} else {
					System.out.println("ERROR SENDING REQUEST: " + conn.getResponseCode());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
