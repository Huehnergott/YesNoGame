package example.swa.yesnogame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import example.swa.yesnogame.domain.Poll;
import example.swa.yesnogame.domain.User;
import example.swa.yesnogame.domain.Vote;
import example.swa.yesnogame.domain.simple.PollSimple;
import example.swa.yesnogame.domain.simple.UserSimple;
import example.swa.yesnogame.domain.util.PollComparator;
import example.swa.yesnogame.service.IPollService.IClosePollListener;
import example.swa.yesnogame.service.IPollService.ICreateUserListener;
import example.swa.yesnogame.service.IPollService.IFindPollsListener;
import example.swa.yesnogame.service.IPollService.IFindUserListener;
import example.swa.yesnogame.service.IPollService.IFindVoteListener;
import example.swa.yesnogame.ui.YesNoArrayAdapter;

/**
 * The main view of the application, showing polls and options for the user.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class MainActivity extends BaseActivity {

	/**
	 * Refresh timespan for the interval to look for changed polls.
	 */
	private static final long REFRESH_POLLS_TIMESPAN = 30000;

	/**
	 * An adapter which connects the listview in this activity with the polls
	 * data set. Creates and manages the visible "view" components to be shown
	 * by the listview. (each row is a view for itself).
	 */
	private YesNoArrayAdapter<Poll> pollAdapter;

	/**
	 * The list of polls. Will be refreshed via service callbacks.
	 */
	List<Poll> polls = new ArrayList<Poll>();

	/**
	 * The user. (Domain class.)
	 */
	private User user;

	/**
	 * Timer to start a refresh request on the service.
	 */
	private Timer refreshTimer;

	/**
	 * Indicates if Activity is running and a refresh of the listview is
	 * necessary.
	 */
	protected boolean isActivityRunning = false;

	protected Runnable checkPolls = new Runnable() {
		@Override
		public void run() {
			if (MainActivity.this.isActivityRunning) {
				updatePollListView();
			}
		}
	};

	protected void changeUserNameTextView(String name) {
		if (name != null) {
			// replace name in view
			TextView label = (TextView) findViewById(R.id.main_tvName);
			label.setText(name);
		}
	};

	/**
	 * Checks if the app is already registered with a valid user account. If
	 * not, prompts a login dialog and tries to create a user with that name.
	 */
	private void checkUser(final String userName) {
		this.pollService.findUser(userName, new IFindUserListener() {

			@Override
			public void onUserFound(User user) {
				if (user != null) {
					MainActivity.this.user = user;
					MainActivity.this.changeUserNameTextView(user.getName());
					MainActivity.this.updateButtonsState();
				} else {
					MainActivity.this.pollService.createUser(new User(null, userName), new ICreateUserListener() {

						@Override
						public void onUserCreated(User user) {
							if (user == null) {
								// creation failed somehow.
								// Maybe duplicate name.
								// start over
								showLoginDialog();
							} else {
								MainActivity.this.user = user;
								MainActivity.this.changeUserNameTextView(user.getName());
								setUserNameInPrefs(user.getName());
								checkUser(getUserNameFromPrefs());
							}
						}
					});
				}
			}
		});
	}

	private void checkVoteTriggerVotingActivity(final PollSimple poll, final UserSimple user) {
		this.pollService.findVote(user.getId(), poll.getId(), new IFindVoteListener() {

			@Override
			public void onVoteFound(Vote vote) {
				if (vote == null) {
					// user has not yet voted
					switchToVoteActivity(MainActivity.this.user, poll);
				} else {
					// TODO: show error message
				}

			}
		});
	}

	private Poll getSelectedPoll() {
		Poll ret = null;
		try {
			int index = this.pollAdapter.getSelectionIndex();
			ret = this.polls.get(index);
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.e("YesNo", "Poll selection failed", e);
		}
		return ret;
	}

	private Long getUserId() {
		return (this.user == null) ? null : this.user.getId();
	}

	private String getUserNameFromPrefs() {
		// Restore preferences
		SharedPreferences settings = getSharedPreferences("YesNoGame", MODE_PRIVATE);
		String userName = settings.getString("userName", null);
		return userName;
	}

	/**
	 * Initializes the activity
	 */
	private void init() {
		checkUser(getUserNameFromPrefs());

		initButtonListeners();
		initPollListView();
		initRefreshTimer();
	}

	private void initButtonListeners() {
		Button voteButton = (Button) findViewById(R.id.main_btnVote);
		voteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onVoteButtonPressed();

			}
		});

		Button initPollButton = (Button) findViewById(R.id.main_btnInitiatePoll);
		initPollButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onInitPollButtonPressed();
			}
		});

		Button seeResultButton = (Button) findViewById(R.id.main_btnSeeResult);
		seeResultButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSeeResultButtonPressed();
			}
		});
	}

	private void initPollListView() {
		ListView pollView = (ListView) findViewById(R.id.main_listViewPolls);

		// set up choice mode
		pollView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		// set up adapter
		this.pollAdapter = new YesNoArrayAdapter<Poll>(MainActivity.this, android.R.layout.simple_list_item_1,
				this.polls);
		pollView.setAdapter(this.pollAdapter);

		// set up listener
		pollView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> group, View view, int pos, long y) {
				MainActivity.this.pollAdapter.setSelectionIndex(pos);
				MainActivity.this.onSelectedPollChanged(pos);
			}
		});

		// request items (async)
		updatePollListView();
	}

	private void initRefreshTimer() {
		this.refreshTimer = new Timer();
		this.refreshTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// This method is called directly by the timer
				// and runs in the same thread as the timer.

				// We call the method that will work with the UI
				// through the runOnUiThread method.
				MainActivity.this.runOnUiThread(MainActivity.this.checkPolls);
			}

		}, REFRESH_POLLS_TIMESPAN, REFRESH_POLLS_TIMESPAN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	protected void onInitPollButtonPressed() {
		switchToCreatePollActivity();
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.isActivityRunning = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.isActivityRunning = true;
	}

	protected void onSeeResultButtonPressed() {
		PollSimple poll = getSelectedPoll();
		if (poll != null) {
			switchToPollResultActivity(poll);
		}
	}

	protected void onSelectedPollChanged(int pos) {
		PollSimple poll = this.polls.get(pos);
		updateButtonsState(poll);

	}

	protected void onVoteButtonPressed() {
		Poll poll = getSelectedPoll();

		if (poll != null) {
			Long userId = this.user.getId();
			if (userId != null) {
				if (userId.equals(poll.getOwnerId())) {
					// user is owner of poll. vote button pressed means
					// "close poll" in this context
					this.pollService.closePoll(poll, new IClosePollListener() {

						@Override
						public void onPollClosed(Poll poll) {
							updatePollListView();
							// TODO: insert error message if poll could not be
							// closed
						}
					});
				} else {
					checkVoteTriggerVotingActivity(poll, this.user);
				}
			}
		}
	}

	// sets the username in the app preferences
	private void setUserNameInPrefs(String name) {
		SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
		Editor e = settings.edit();
		e.putString("userName", name);
		e.commit();
	}

	/**
	 * Prompt the login dialog to enter a user name.
	 */
	private void showLoginDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.question_title_username);

		// Set up the input
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = input.getText().toString();
				changeUserNameTextView(text);
				checkUser(text);
			}
		});

		builder.show();
	}

	private void switchToCreatePollActivity() {
		Intent intent = new Intent(this, example.swa.yesnogame.InitPollActivity.class);
		intent.putExtra(PROP_USERID, this.user.getId());
		intent.putExtra(PROP_USERNAME, this.user.getName());
		startActivity(intent);
	}

	private void switchToPollResultActivity(PollSimple poll) {
		Intent intent = new Intent(this, example.swa.yesnogame.PollResultActivity.class);
		intent.putExtra(PROP_USERID, this.user.getId());
		intent.putExtra(PROP_POLLID, poll.getId());
		intent.putExtra(PROP_USERNAME, this.user.getName());
		intent.putExtra(PROP_POLLTITLE, poll.getTitle());
		startActivity(intent);
	}

	private void switchToVoteActivity(UserSimple user, PollSimple poll) {
		Intent intent = new Intent(this, example.swa.yesnogame.VoteActivity.class);
		intent.putExtra(PROP_USERID, user.getId());
		intent.putExtra(PROP_USERNAME, user.getName());
		intent.putExtra(PROP_POLLID, poll.getId());
		intent.putExtra(PROP_POLLTITLE, poll.getTitle());
		intent.putExtra(PROP_POLLQUESTION, poll.getQuestion());
		startActivity(intent);
	}

	/**
	 * Update the visibility state of the buttons.
	 * 
	 */
	private void updateButtonsState() {
		Poll poll = getSelectedPoll();
		if (poll != null) {
			updateButtonsState(poll);
		}
	}

	/**
	 * Update the visibility state of the buttons.
	 * 
	 * @param poll
	 */
	private void updateButtonsState(final PollSimple poll) {
		// button state is depending on Poll and user (if he is the moderator of
		// the poll)
		final Button voteButton = (Button) findViewById(R.id.main_btnVote);
		final Button seeResultButton = (Button) findViewById(R.id.main_btnSeeResult);
		Button initiateButton = (Button) findViewById(R.id.main_btnInitiatePoll);

		if (this.user == null) {
			// user type unknown -> disable all buttons
			voteButton.setText(R.string.btn_vote);
			initiateButton.setEnabled(false);
			voteButton.setEnabled(false);
			seeResultButton.setEnabled(false);
		} else {
			// user type known
			initiateButton.setEnabled(true);

			// check if user is moderator
			if (poll.getOwnerId().equals(getUserId())) {
				voteButton.setText(R.string.btn_close_poll);
				voteButton.setEnabled(poll.isOpen());
				// moderator may see result at any time
				seeResultButton.setEnabled(true);
			} else {
				// normal user
				voteButton.setText(R.string.btn_vote);
				this.pollService.findVote(this.user.getId(), poll.getId(), new IFindVoteListener() {

					@Override
					public void onVoteFound(Vote vote) {
						boolean voted = (vote != null);
						voteButton.setEnabled(poll.isOpen() && !voted);
						// user may see result only if poll was closed
						seeResultButton.setEnabled(!poll.isOpen());

					}
				});
			}
		}
	}

	private void updatePollListView() {

		// make an async call and add a callback listener here
		this.pollService.findPolls(new IFindPollsListener() {

			@Override
			public void onPollsFound(Collection<Poll> ret) {
				// sort
				ArrayList<Poll> polls = new ArrayList<Poll>(ret);
				Collections.sort(polls, new PollComparator());

				// update data
				ListView pollView = (ListView) findViewById(R.id.main_listViewPolls);
				MainActivity.this.polls.clear();
				MainActivity.this.polls.addAll(polls);

				// update selection
				@SuppressWarnings("unchecked")
				YesNoArrayAdapter<PollSimple> adapter = (YesNoArrayAdapter<PollSimple>) pollView.getAdapter();
				if ((polls.size() > 0) && (adapter.getSelectionIndex() == Adapter.NO_SELECTION)) {
					adapter.setSelectionIndex(0);
					PollSimple poll = getSelectedPoll();
					if (poll != null) {
						updateButtonsState(poll);
					}
				}

				// notify changes via adapter
				adapter.notifyDataSetChanged();
				updateButtonsState();
			}
		});
	}
}
