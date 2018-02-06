package org.openntf.todo.httpService;

import com.ibm.designer.runtime.domino.adapter.HttpService;
import com.ibm.designer.runtime.domino.adapter.IServiceFactory;
import com.ibm.designer.runtime.domino.adapter.LCDEnvironment;

public class HttpServiceFactory implements IServiceFactory {

	@Override
	public HttpService[] getServices(final LCDEnvironment lcdEnv) {
		// If you wish a scheduled tasklet to run from Xots, first uncomment the
		// HttpService here, then uncomment the "start" method in
		// HttpService.checkTimeout(), update the "trySchedule" method in
		// ScheduledTask to set the schedule needed and code the "run" method

		final HttpService[] ret = new HttpService[1];
		ret[0] = org.openntf.todo.httpService.HttpService.createInstance(lcdEnv);
		return ret;
	}

}
