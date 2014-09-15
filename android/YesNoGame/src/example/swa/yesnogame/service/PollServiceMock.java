package example.swa.yesnogame.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import example.swa.yesnogame.domain.Poll;
import example.swa.yesnogame.domain.User;
import example.swa.yesnogame.domain.Vote;
import example.swa.yesnogame.domain.simple.PollSimple;
import example.swa.yesnogame.domain.simple.VoteSimple;

/**
 * Mock implementation of the async. PollService interface to test the UI and
 * its behaviour.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class PollServiceMock extends PollServiceBase implements IPollService {

	private static final String TAG = PollServiceMock.class.getSimpleName();

	private static User[] USERS = { new User(1l, "Matze"), new User(2l, "Thomas"), new User(3l, "Robert"),
			new User(4l, "Ron"), new User(5l, "Maggie"), new User(6l, "Peter") };

	private static PollSimple[] POLLS = {
			new PollSimple(1l, "Stuff", "Do you like that stuff ?", 4l, true, new Date().getTime()),
			new PollSimple(2l, "Gaming", "Do you like gaming ?", 3l, true, new Date().getTime()),
			new PollSimple(3l, "Testing", "Are you a good tester ?", 2l, true, new Date().getTime()),
			new PollSimple(4l, "Results", "Do you like quick results? Who has had some?", 2l, false,
					new Date().getTime()), };

	private static VoteSimple[] VOTES = { new VoteSimple(1l, 1l, 1l, 100), new VoteSimple(2l, 1l, 2l, 34),
			new VoteSimple(3l, 1l, 3l, 87), new VoteSimple(4l, 2l, 1l, 12), new VoteSimple(5l, 2l, 2l, 43),
			new VoteSimple(6l, 2l, 3l, 18), new VoteSimple(7l, 3l, 1l, 27), new VoteSimple(8l, 3l, 2l, 44),
			new VoteSimple(9l, 3l, 3l, 60), new VoteSimple(11l, 5l, 4l, 17), new VoteSimple(12l, 3l, 4l, 65),
			new VoteSimple(13l, 2l, 4l, 1), new VoteSimple(14l, 1l, 4l, 50), };

	private static Map<Long, PollSimple> pollSimples;
	private static Map<Long, VoteSimple> voteSimples;
	private static Map<Long, User> users;
	private static Map<Long, Poll> polls;
	private static Map<Long, Vote> votes;

	public PollServiceMock() {
		super();
		init();
	}

	@Override
	public void closePoll(final Poll poll, final IClosePollListener listener) {
		LogStart(TAG, "closePoll");

		Runnable r = new Runnable() {
			@Override
			public void run() {
				LogEnd(TAG, "closePoll");
				if (poll == null) {
					listener.onPollClosed(null);
				} else {
					Long id = poll.getId();
					if (id == null) {
						listener.onPollClosed(null);
					} else {
						Poll removePoll = null;
						Poll newPoll = null;
						for (Poll p : polls.values()) {
							if (id.equals(p.getId())) {
								newPoll = new Poll(p.getId(), p.getTitle(), p.getQuestion(), p.getOwner(), false,
										p.getCreated());
								removePoll = p;
								break;
							}
						}

						// check if poll was closed and return new state
						if (removePoll != null) {
							polls.put(newPoll.getId(), newPoll);
							listener.onPollClosed(newPoll);
						} else {
							listener.onPollClosed(null);
						}
					}
				}
			}
		};
		new Handler().postDelayed(r, 140);
	}

	@Override
	public void createPoll(final Poll poll, final ICreatePollListener listener) {
		LogStart(TAG, "createPoll");

		Runnable r = new Runnable() {
			@Override
			public void run() {
				LogEnd(TAG, "createPoll");
				if (poll == null) {
					listener.onPollCreated(null);
				} else {
					String title = poll.getTitle();
					if (title == null) {
						listener.onPollCreated(null);
					} else {
						// check if we have a poll with same title
						for (Poll existingPoll : polls.values()) {
							if (title.equals(existingPoll.getTitle())) {
								listener.onPollCreated(null);
								return;
							}
						}
						Poll newPoll = new Poll(Poll.nextId(), poll.getTitle(), poll.getQuestion(), poll.getOwner(),
								poll.isOpen(), poll.getCreated());
						polls.put(newPoll.getId(), newPoll);
						listener.onPollCreated(newPoll);
					}
				}
			}
		};
		new Handler().postDelayed(r, 300);
	}

	@Override
	public void createUser(final User user, final ICreateUserListener listener) {
		LogStart(TAG, "createUser");

		Runnable r = new Runnable() {
			@Override
			public void run() {
				LogEnd(TAG, "createUser");
				if ((user == null) || (user.getName() == null)) {
					listener.onUserCreated(null);
				} else {
					// check for user with existing same name
					for (User existingUser : users.values()) {
						if (existingUser.getName() != null) {
							if (existingUser.getName().equals(user.getName())) {
								listener.onUserCreated(null);
								return;
							}
						}
					}
					User newUser = new User(User.nextId(), user.getName());
					users.put(newUser.getId(), newUser);
					listener.onUserCreated(newUser);
				}
			}
		};
		new Handler().postDelayed(r, 100);
	}

	@Override
	public void createVote(final Vote vote, final ICreateVoteListener listener) {
		LogStart(TAG, "createVote");

		Runnable r = new Runnable() {
			@Override
			public void run() {
				LogEnd(TAG, "createVote");
				if (vote == null) {
					listener.onVoteCreated(null);
				} else {
					Vote newVote = new Vote(Vote.nextId(), vote.getUser(), vote.getPoll(), vote.getVoteValue());
					Long pollId = newVote.getPollId();
					if (pollId == null) {
						listener.onVoteCreated(null);
					} else {
						for (Vote vote : votes.values()) {
							if (vote.equals(newVote)) {
								listener.onVoteCreated(null);
								return;
							}
						}
						votes.put(newVote.getId(), newVote);
						listener.onVoteCreated(newVote);
					}
				}
			}
		};
		new Handler().postDelayed(r, 100);
	}

	@Override
	public void findPoll(final Long pollId, final IFindPollListener listener) {
		LogStart(TAG, "findPoll (by id)");

		Runnable r = new Runnable() {
			@Override
			public void run() {
				LogEnd(TAG, "findPoll (by id)");
				if (pollId != null) {
					Poll poll = polls.get(pollId);
					listener.onPollFound(poll);
					return;
				} else {
					listener.onPollFound(null);
				}
			}
		};
		new Handler().postDelayed(r, 300);
	}

	@Override
	public void findPolls(final IFindPollsListener listener) {
		LogStart(TAG, "findPolls");

		Runnable r = new Runnable() {
			@Override
			public void run() {
				LogEnd(TAG, "findPolls");
				listener.onPollsFound(polls.values());
			}
		};
		new Handler().postDelayed(r, 250);
	}

	@Override
	public void findUser(final Long userId, final IFindUserListener listener) {
		LogStart(TAG, "findUser (by id)");

		Runnable r = new Runnable() {
			@Override
			public void run() {
				if (userId != null) {
					User user = users.get(userId);
					LogEnd(TAG, "findUser (by id)", user.toString());
					listener.onUserFound(user);
					return;
				} else {
					LogEnd(TAG, "findUser (by id)", "NULL");
					listener.onUserFound(null);
				}
			}
		};
		new Handler().postDelayed(r, 300);
	}

	@Override
	public void findUser(final String name, final IFindUserListener listener) {
		LogStart(TAG, "findUser (by name)");

		Runnable r = new Runnable() {
			@Override
			public void run() {
				if (name != null) {
					for (User user : users.values()) {
						if (name.equals(user.getName())) {
							LogEnd(TAG, "findUser (by name)" + user.toString());
							listener.onUserFound(user);
							return;
						}
					}
					LogEnd(TAG, "findUser (by name)", "NULL");
					listener.onUserFound(null);
				} else {
					LogEnd(TAG, "findUser (by name)", "NULL");
					listener.onUserFound(null);
				}
			}
		};
		new Handler().postDelayed(r, 300);
	}

	@Override
	public void findUsers(final IFindUsersListener listener) {
		LogStart(TAG, "findUsers");

		Runnable r = new Runnable() {
			@Override
			public void run() {
				LogEnd(TAG, "findUsers");
				listener.onUsersFound(users.values());
			}
		};
		new Handler().postDelayed(r, 250);
	}

	@Override
	public void findVote(final Long userId, final Long pollId, final IFindVoteListener listener) {
		LogStart(TAG, "findVote");

		Runnable r = new Runnable() {
			@Override
			public void run() {
				if ((userId != null) && (pollId != null)) {
					for (Vote vote : votes.values()) {
						if ((userId.equals(vote.getUserId())) && (pollId.equals(vote.getPollId()))) {
							LogEnd(TAG, "findVote", vote.toString());
							listener.onVoteFound(vote);
							return;
						}
					}
				}
				LogEnd(TAG, "findVote", "NULL");
				listener.onVoteFound(null);
			}
		};
		new Handler().postDelayed(r, 200);
	}

	@Override
	public void findVotesByPoll(final Long pollId, final IFindVotesByPollListener listener) {
		LogStart(TAG, "findVotesByPoll");

		Runnable r = new Runnable() {
			@Override
			public void run() {
				if (pollId != null) {
					List<Vote> ret = new ArrayList<Vote>();
					for (Vote vote : votes.values()) {
						if (pollId.equals(vote.getPollId())) {
							// fill in user to vote if accessible
							for (User user : users.values()) {
								if (user.getId().equals(vote.getUserId())) {
									vote = new Vote(vote.getId(), user, vote.getPoll(), vote.getVoteValue());
								}
							}
							ret.add(vote);
						}
					}
					LogEnd(TAG, "findVotesByPoll", ret.toString());
					listener.onVotesFound(ret);
					return;
				}
				LogEnd(TAG, "findVotesByPoll", "NULL");
				listener.onVotesFound(null);
			}
		};
		new Handler().postDelayed(r, 150);
	}

	private void init() {

		users = new HashMap<Long, User>();
		for (int i = 0; i < USERS.length; i++) {
			users.put((long) i, USERS[i]);
		}

		pollSimples = new HashMap<Long, PollSimple>();
		for (int i = 0; i < POLLS.length; i++) {
			pollSimples.put((long) i, POLLS[i]);
		}

		voteSimples = new HashMap<Long, VoteSimple>();
		for (int i = 0; i < VOTES.length; i++) {
			voteSimples.put((long) i, VOTES[i]);
		}

		polls = new HashMap<Long, Poll>();
		for (PollSimple pollSimple : pollSimples.values()) {
			User user = users.get(pollSimple.getOwnerId());
			Poll poll = new Poll(pollSimple.getId(), pollSimple.getTitle(), pollSimple.getQuestion(), user,
					pollSimple.isOpen(), pollSimple.getCreated());
			polls.put(poll.getId(), poll);
		}

		votes = new HashMap<Long, Vote>();
		for (VoteSimple voteSimple : voteSimples.values()) {
			User user = users.get(voteSimple.getUserId());
			Poll poll = polls.get(voteSimple.getPollId());
			Vote vote = new Vote(voteSimple.getId(), user, poll, voteSimple.getVoteValue());
			votes.put(vote.getId(), vote);
		}

	}
}
