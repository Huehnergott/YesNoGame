package example.swa.yesnogame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import example.swa.yesnogame.domain.Vote;
import example.swa.yesnogame.domain.util.VoteComparator;
import example.swa.yesnogame.service.IPollService.IFindVotesByPollListener;

/**
 * An activity to show poll results.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class PollResultActivity extends BaseActivity {

	/**
	 * Timespan to request a refresh of the vote result list
	 */
	private static final long REFRESH_VOTES_TIMESPAN = 30000; // 30sec

	/**
	 * Default adapter on the votes list for the "vote" list view.
	 */
	private ArrayAdapter<Vote> voteAdapter;

	private List<Vote> votes = new ArrayList<Vote>();

	private Timer refreshTimer;

	private Runnable checkVotes = new Runnable() {

		@Override
		public void run() {
			updateVoteListView();
		}
	};

	/**
	 * Initializes the activity.
	 */
	private void init() {
		TextView label = (TextView) findViewById(R.id.result_tvName);
		label.setText(getStringFromIntent(PROP_USERNAME));

		initSetupVoteListView();
		initRegisterButtonListeners();
		initSetupRefreshTimer();
	}

	/**
	 * Registers listeners on all buttons.
	 */
	private void initRegisterButtonListeners() {
		Button cancelButton = (Button) findViewById(R.id.result_btnCancel);

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callMainActivity();
			}
		});
	}

	private void initSetupRefreshTimer() {
		this.refreshTimer = new Timer();
		this.refreshTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// This method is called directly by the timer
				// and runs in the same thread as the timer.

				// We call the method that will work with the UI
				// through the runOnUiThread method.
				PollResultActivity.this.runOnUiThread(PollResultActivity.this.checkVotes);
			}

		}, REFRESH_VOTES_TIMESPAN, REFRESH_VOTES_TIMESPAN);
	}

	/**
	 * Sets up the list of votes.
	 */
	private void initSetupVoteListView() {
		ListView voteView = (ListView) findViewById(R.id.result_listViewVotes);

		// set up adapter
		this.voteAdapter = new ArrayAdapter<Vote>(PollResultActivity.this, android.R.layout.simple_list_item_1,
				this.votes);
		voteView.setAdapter(this.voteAdapter);

		// request items (async)
		updateVoteListView();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poll_result);

		init();
	}

	/**
	 * Updates the vote view. Requests new data and updates on new votes
	 * received.
	 */
	private void updateVoteListView() {

		Long pollId = getLongFromIntent(PROP_POLLID);

		// make an async call and add a callback listener here
		this.pollService.findVotesByPoll(pollId, new IFindVotesByPollListener() {

			@Override
			public void onVotesFound(List<Vote> votes) {
				// sort
				Collections.sort(votes, new VoteComparator());

				// update data
				PollResultActivity.this.votes.clear();
				PollResultActivity.this.votes.addAll(votes);

				// notify changes via adapter
				PollResultActivity.this.voteAdapter.notifyDataSetChanged();
			}

		});
	}
}
