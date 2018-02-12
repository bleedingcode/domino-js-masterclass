package org.openntf.todo.httpService;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.openntf.todo.ToDoStoreFactory;
import org.openntf.todo.Utils;

import com.ibm.designer.runtime.domino.adapter.ComponentModule;
import com.ibm.designer.runtime.domino.adapter.LCDEnvironment;
import com.ibm.designer.runtime.domino.bootstrap.adapter.HttpServletRequestAdapter;
import com.ibm.designer.runtime.domino.bootstrap.adapter.HttpServletResponseAdapter;
import com.ibm.designer.runtime.domino.bootstrap.adapter.HttpSessionAdapter;

public class HttpService extends com.ibm.designer.runtime.domino.adapter.HttpService {
	public static HttpService INSTANCE;
	public boolean hasRun;

	public static HttpService createInstance(final LCDEnvironment lcdEnv) {
		INSTANCE = new HttpService(lcdEnv);
		return INSTANCE;
		// This runs before ODA starts and ODA won't start until this completes,
		// so don't use any ODA code here
	}

	public static HttpService getInstance() {
		return INSTANCE;
	}

	public HttpService(final LCDEnvironment lcdEnv) {
		super(lcdEnv);
	}

	@Override
	// This method runs 30 seconds after server starts and every 30 seconds
	// thereafter
	public void checkTimeout(final long arg0) {
		if (!hasRun) {
			hasRun = true;
			Utils.toggleProfiling();
			long startTimer = Utils.startTimer();
			try {
				ToDoStoreFactory.getInstance().loadStores();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Utils.debugTimer(startTimer, "Timeout run");
		}
		super.checkTimeout(arg0);
	}

	@Override
	public boolean doService(final String arg0, final String arg1, final HttpSessionAdapter arg2,
			final HttpServletRequestAdapter arg3, final HttpServletResponseAdapter arg4)
			throws ServletException, IOException {
		return false;
	}

	@Override
	public void getModules(final List<ComponentModule> arg0) {

	}

	// This gives the HttpService a priority level
	@Override
	public int getPriority() {
		return 50;
	}

}
