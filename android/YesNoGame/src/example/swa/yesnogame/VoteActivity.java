package example.swa.yesnogame;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import example.swa.yesnogame.domain.Poll;
import example.swa.yesnogame.domain.User;
import example.swa.yesnogame.domain.Vote;
import example.swa.yesnogame.service.IPollService.ICreateVoteListener;

/**
 * An activity to vote on a poll.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class VoteActivity extends BaseActivity {

	protected void onCancelButtonPressed() {
		callMainActivity();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vote);

		setupTextFields();
		registerButtonListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vote, menu);
		return true;
	}

	protected void onVoteButtonPressed() {
		SeekBar seekBar = (SeekBar) findViewById(R.id.vote_seekBar);

		// We need a Vote and a User entity here to create a new vote
		// via the service. This looks incomplete at the first view, But
		// we do only need the keys of user and poll for the
		// service (userId and pollId in php base RESTservice OR
		// userName+polltitle in .NET based RESTservice)
		User u = new User(getLongFromIntent(PROP_USERID), getStringFromIntent(PROP_USERNAME));
		Poll p = new Poll(getLongFromIntent(PROP_POLLID), getStringFromIntent(PROP_POLLTITLE), "", u, true, 1L);
		Vote vote = new Vote(null, u, p, seekBar.getProgress());

		this.pollService.createVote(vote, new ICreateVoteListener() {

			@Override
			public void onVoteCreated(Vote vote) {
				if (vote == null) {
					AlertDialog.Builder builder = new Builder(VoteActivity.this).setMessage(
							R.string.msg_poll_vote_failed).setPositiveButton(android.R.string.ok, null);
					builder.show();
				} else {
					callMainActivity();
				}

			}
		});

	}

	/**
	 * Register the listners for the buttons in our activity.
	 */
	private void registerButtonListeners() {
		Button voteButton = (Button) findViewById(R.id.vote_btnVote);
		voteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onVoteButtonPressed();
			}
		});
		Button cancelButton = (Button) findViewById(R.id.vote_btnCancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onCancelButtonPressed();
			}
		});
	}

	/**
	 * Set up the text in teh text fields (user, poll title and question).
	 */
	private void setupTextFields() {
		// set user name
		TextView userTextView = (TextView) findViewById(R.id.vote_tvName);
		userTextView.setText(getStringFromIntent(PROP_USERNAME));
		// set poll title
		TextView titleTextView = (TextView) findViewById(R.id.vote_tvTitle);
		titleTextView.setText(getStringFromIntent(PROP_POLLTITLE));
		// set poll question
		TextView questionTextView = (TextView) findViewById(R.id.vote_tvQuestion);
		questionTextView.setText(getStringFromIntent(PROP_POLLQUESTION));
	}

}
