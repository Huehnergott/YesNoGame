package example.swa.yesnogame;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import example.swa.yesnogame.service.IPollService;
import example.swa.yesnogame.service.PollServiceProvider;

/**
 * Base activity. Contains all the common base members and methods that we need
 * for our Activities.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public abstract class BaseActivity extends Activity {

	/**
	 * propery name of the userId to be used by Intents
	 */
	public static final String PROP_USERID = "userId";
	/**
	 * propery name of the userName to be used by Intents
	 */
	public static final String PROP_USERNAME = "userName";
	/**
	 * propery name of the pollId to be used by Intents
	 */
	public static final String PROP_POLLID = "pollId";
	/**
	 * propery name of the pollTitle to be used by Intents
	 */
	public static final String PROP_POLLTITLE = "pollTitle";
	/**
	 * propery name of the pollQuestion to be used by Intents
	 */
	public static final String PROP_POLLQUESTION = "pollQuestion";

	/**
	 * name of the preferences set to be used for this application
	 */
	public static final String PREFERENCES_NAME = "YesNoGame";

	/**
	 * poll service instance. Async data retrieval only.
	 */
	protected IPollService pollService = PollServiceProvider.getService();

	/**
	 * Starts an intent to bring the main activity to life.
	 */
	protected void callMainActivity() {
		Intent intent = new Intent(this, example.swa.yesnogame.MainActivity.class);
		startActivity(intent);
	}

	/**
	 * Get a Long-parameter from the intent call.
	 * 
	 * @param property
	 * @return the Long or null
	 */
	protected Long getLongFromIntent(String property) {
		Intent intent = getIntent();
		Long ret = intent.getExtras().getLong(property);
		return ret;
	}

	/**
	 * Get a String-parameter from the intent call.
	 * 
	 * @param property
	 * @return the string or null
	 */
	protected String getStringFromIntent(String property) {
		Intent intent = getIntent();
		String ret = intent.getExtras().getString(property);
		return ret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.poll_result, menu);
		return true;
	}
}
