package example.swa.yesnogame.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * Base class for service implementations. Provides some extended logging to log
 * execution duration of async methods.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public abstract class PollServiceBase {

	private Map<String, Date> startTime = new HashMap<String, Date>();

	protected void LogEnd(String tag, String method) {
		LogEnd(tag, method, "");
	}

	/**
	 * Log exit point out of a method.
	 * 
	 * @param method
	 *            method name
	 * @param tag
	 *            logging tag
	 * @param message
	 *            log message
	 */
	protected void LogEnd(String tag, String method, String message) {
		Date endTime = new Date();
		long duration = endTime.getTime() - this.startTime.get(method).getTime();
		Log.i(tag, "(took " + duration + "ms)" + method + " end  > " + message);
	}

	/**
	 * Log entry point into a method.
	 * 
	 * @param method
	 *            method name
	 * @param tag
	 *            logging tag
	 */
	protected void LogStart(String tag, String method) {
		this.startTime.put(method, new Date());
		Log.i(tag, method + " start");
	}
}
