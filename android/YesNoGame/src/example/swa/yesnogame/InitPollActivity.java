package example.swa.yesnogame;

import java.util.Date;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import example.swa.yesnogame.domain.Poll;
import example.swa.yesnogame.domain.User;
import example.swa.yesnogame.service.IPollService.ICreatePollListener;

/**
 * Activity to be used for creating a new poll.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class InitPollActivity extends BaseActivity {

	/**
	 * Change the name shown in the userName textView.
	 * 
	 * @param name
	 */
	protected void changeUserNameTextView(String name) {
		if ((name != null) && (!"".equals(name))) {
			// replace name in view
			TextView label = (TextView) findViewById(R.id.TextViewName);
			label.setText(name);
		}
	}

	/**
	 * Goes back to the main activity when the cancel button is clicked.
	 */
	protected void onCancelButton() {
		callMainActivity();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init_poll);

		registerButtonListeners();
		changeUserNameTextView(getStringFromIntent(PROP_USERNAME));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.init_poll, menu);
		return true;
	}

	/**
	 * Creates a new poll via the poll service on button click.
	 */
	protected void onInitButton() {
		EditText editTitle = (EditText) findViewById(R.id.editTextPollTitle);
		EditText editQuestion = (EditText) findViewById(R.id.editTextPollQuestion);

		String title = editTitle.getText().toString();
		String question = editQuestion.getText().toString();

		if ((title != null) && (question != null) && (title.length() > 0) && (question.length() > 0)) {
			User user = new User(getLongFromIntent(PROP_USERID), getStringFromIntent(PROP_USERNAME));
			Poll newPoll = new Poll(null, title, question, user, true, new Date().getTime() / 1000);
			this.pollService.createPoll(newPoll, new ICreatePollListener() {

				@Override
				public void onPollCreated(Poll poll) {
					InitPollActivity.this.onPollCreated(poll);
				}
			});
		} else {
			// show an error dialog if calling the service failed
			AlertDialog.Builder builder = new Builder(this).setMessage(R.string.msg_empty_field).setPositiveButton(
					android.R.string.ok, null);
			builder.show();
		}
	}

	/**
	 * Method to be called when a poll was created by the service.
	 * 
	 * @param poll
	 */
	protected void onPollCreated(Poll poll) {
		// show an error dialog if the poll creation failed
		if (poll == null) {
			AlertDialog.Builder builder = new Builder(InitPollActivity.this).setMessage(R.string.msg_poll_init_failed)
					.setPositiveButton(android.R.string.ok, null);
			builder.show();
		} else {
			callMainActivity();
		}
	}

	/**
	 * Register the listener methods for the buttons in this activity/form.
	 */
	private void registerButtonListeners() {

		Button btnInitPoll = (Button) findViewById(R.id.buttonInitiatePoll);
		Button btnCancel = (Button) findViewById(R.id.buttonCancel);

		btnInitPoll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InitPollActivity.this.onInitButton();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InitPollActivity.this.onCancelButton();
			}
		});
	}

}
