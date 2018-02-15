package org.openntf.todo.domino;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openntf.domino.ACL;
import org.openntf.domino.ACL.Level;
import org.openntf.domino.ACLEntry;
import org.openntf.domino.Database;
import org.openntf.domino.Document;
import org.openntf.domino.NoteCollection;
import org.openntf.domino.Session;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.xots.AbstractXotsCallable;
import org.openntf.domino.xots.Tasklet;
import org.openntf.domino.xots.Tasklet.Context;
import org.openntf.domino.xots.XotsUtil;
import org.openntf.todo.model.Store;

@Tasklet(session = Tasklet.Session.NATIVE, context = Context.PLUGIN)
public class StoreLoader extends AbstractXotsCallable<Map<String, Store>> {

	@Override
	public Map<String, Store> call() throws Exception {
		try {
			Map<String, Store> retVal = new ConcurrentHashMap<String, Store>();
			Session sess = Factory.getSession(SessionType.NATIVE);
			Database todoCatalog = sess.getDatabase(Store.TODO_PATH + "catalog.nsf");
			if (null == todoCatalog) {
				// Initialise the ToDo Catalog
				todoCatalog = sess.createBlankDatabase(Store.TODO_PATH + "catalog.nsf");
				todoCatalog.setCategories("OpenNTF ToDo");
				todoCatalog.setTitle("OpenNTF ToDo Master");
				ACL acl = todoCatalog.getACL();

				// Set ACL access
				ACLEntry servers = acl.createACLEntry("LocalDomainServers", Level.MANAGER);
				ACLEntry otherServers = acl.createACLEntry("OtherDomainServers", Level.NOACCESS);
				ACLEntry admins = acl.createACLEntry("LocalDomainAdmins", Level.MANAGER);
				acl.createACLEntry("Anonymous", Level.NOACCESS);
				acl.createACLEntry("-Default-", Level.NOACCESS);
				acl.save();
				org.openntf.domino.View v = todoCatalog.createView("NONE");
				v.setSelectionFormula("SELECT @False");
			} else {
				NoteCollection nc = todoCatalog.createNoteCollection(false);
				nc.setSelectDocuments(true);
				nc.buildCollection();
				for (int noteid : nc.getNoteIDs()) {
					Document doc = todoCatalog.getDocumentByID(noteid);
					Store store = ToDoStoreFactory.getInstance().createStoreFromDoc(doc);
					retVal.put(store.getReplicaId(), store);
				}
			}
			return retVal;
		} catch (Exception e) {
			XotsUtil.handleException(e, getContext());
		}
		return null;
	}

}
