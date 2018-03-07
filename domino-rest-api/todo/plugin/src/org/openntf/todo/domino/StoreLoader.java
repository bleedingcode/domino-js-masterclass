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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openntf.domino.ACL;
import org.openntf.domino.ACL.Level;
import org.openntf.domino.Database;
import org.openntf.domino.Document;
import org.openntf.domino.NoteCollection;
import org.openntf.domino.Session;
import org.openntf.domino.design.DatabaseDesign.DbProperties;
import org.openntf.domino.design.impl.DatabaseDesign;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.xots.AbstractXotsCallable;
import org.openntf.domino.xots.Tasklet;
import org.openntf.domino.xots.Tasklet.Context;
import org.openntf.domino.xots.XotsUtil;
import org.openntf.todo.ToDoUtils;
import org.openntf.todo.model.Store;

/**
 * @author Paul Withers
 * 
 *         Xots tasklet to load stores from ToDo Catalog
 *
 */
@Tasklet(session = Tasklet.Session.NATIVE, context = Context.PLUGIN)
public class StoreLoader extends AbstractXotsCallable<Map<String, Store>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Map<String, Store> call() throws Exception {
		try {
			Map<String, Store> retVal = new ConcurrentHashMap<String, Store>();
			Session sess = Factory.getSession(SessionType.NATIVE);
			Database todoCatalog = sess.getDatabase(Store.TODO_PATH + "catalog.nsf");
			if (null == todoCatalog) {
				// Initialise the ToDo Catalog
				ToDoUtils.debugPrint("Initialising the ToDo Catalog database");
				todoCatalog = sess.createBlankDatabase(Store.TODO_PATH + "catalog.nsf");
				todoCatalog.setCategories("OpenNTF ToDo");
				todoCatalog.setTitle("OpenNTF ToDo Master");
				todoCatalog.setListInDbCatalog(false);
				DatabaseDesign dbDesign = (DatabaseDesign) todoCatalog.getDesign();
				HashMap<DbProperties, Boolean> props = new HashMap<DbProperties, Boolean>();
				props.put(DbProperties.USE_JS, false);
				props.put(DbProperties.NO_URL_OPEN, false);
				props.put(DbProperties.ENHANCED_HTML, true);
				props.put(DbProperties.SHOW_IN_OPEN_DIALOG, false);
				dbDesign.setDatabaseProperties(props);
				dbDesign.save();
				ACL acl = todoCatalog.getACL();

				// Set ACL access
				ToDoUtils.debugPrint("Initialising ACL for the ToDo Catalog database");
				acl.createACLEntry("LocalDomainAdmins", Level.MANAGER);
				acl.createACLEntry("Anonymous", Level.NOACCESS);
				acl.save();

				// Create a view and set SELECT @False - so nothing will show if anyone tries to open in Notes Client
				org.openntf.domino.View v = todoCatalog.createView("NONE");
				v.setSelectionFormula("SELECT @False");
				v.setDefaultView(true);
				ToDoUtils.debugPrint("Completed initialising the ToDo Catalog database");
			} else {
				// Load the stores from the Catalog
				ToDoUtils.debugPrint("Loading ToDo Instances from ToDo Catalog database");
				NoteCollection nc = todoCatalog.createNoteCollection(false);
				nc.setSelectDocuments(true);
				nc.buildCollection();
				for (int noteid : nc.getNoteIDs()) {
					Document doc = todoCatalog.getDocumentByID(noteid);
					// Convert the document to a Store and put in the Map
					Store store = ToDoStoreFactory.getInstance().deserializeStoreFromDoc(doc);
					retVal.put(store.getReplicaId(), store);
				}
				ToDoUtils.debugPrint("Loaded ToDo Instances from ToDo Catalog database");
			}
			return retVal;
		} catch (Exception e) {
			XotsUtil.handleException(e, getContext());
		}
		return null;
	}

}
