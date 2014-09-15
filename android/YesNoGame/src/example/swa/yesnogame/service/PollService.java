package example.swa.yesnogame.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import example.swa.yesnogame.domain.Poll;
import example.swa.yesnogame.domain.User;
import example.swa.yesnogame.domain.Vote;
import example.swa.yesnogame.domain.simple.PollSimple;
import example.swa.yesnogame.domain.simple.UserSimple;
import example.swa.yesnogame.domain.simple.VoteSimple;
import example.swa.yesnogame.service.util.EntityFormatEnum;
import example.swa.yesnogame.service.util.RequestMethodEnum;
import example.swa.yesnogame.service.util.RequestUrlParams;
import example.swa.yesnogame.service.util.RequestUrlParams.IResponseListener;
import example.swa.yesnogame.service.util.RequestUrlTask;

/**
 * Poll service implementation to be used with the RESTful endpoint based on the
 * PHP implemenation. (see index.php for implementation)
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class PollService implements IPollService {

	private static final String TAG = PollService.class.getSimpleName();

	/**
	 * Base URL of the service to be called.
	 */
	private String BASE_URL = "http://rest.radarworkx.com/api/";

	/**
	 * An instance of the gson parser used for JSON de-/encoding.
	 */
	private static Gson gson = new Gson();

	@Override
	public void closePoll(Poll poll, final IClosePollListener listener) {
		// create a "closed" instance
		final Poll closedPoll = new Poll(poll.getId(), poll.getTitle(), poll.getQuestion(), poll.getOwner(), false,
				poll.getCreated());

		PollSimple simple = closedPoll;
		String putData = gson.toJson(simple);
		String url = this.BASE_URL + "poll/put";
		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_POST,
				EntityFormatEnum.FORMAT_URL_FORM_ENCODED, url, new IResponseListener() {

					@Override
					public void onResponse(String text) {
						PollSimple verify = gson.fromJson(text, PollSimple.class);
						if (verify.getId().equals(closedPoll.getId())) {
							listener.onPollClosed(closedPoll);
						} else {
							listener.onPollClosed(null);
						}
					}
				});
		params.setPutData(putData);

		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);
	}

	@Override
	public void createPoll(Poll poll, final ICreatePollListener listener) {
		PollSimple simple = poll;
		String postData = gson.toJson(simple);
		String url = this.BASE_URL + "poll";
		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_POST,
				EntityFormatEnum.FORMAT_URL_FORM_ENCODED, url, new IResponseListener() {

					@Override
					public void onResponse(String text) {
						final PollSimple simple = gson.fromJson(text, PollSimple.class);
						if (simple != null) {
							// request user by id and fill it in to full entity
							Long userId = simple.getOwnerId();
							findUser(userId, new IFindUserListener() {

								@Override
								public void onUserFound(User user) {
									if (user != null) {
										Poll ret = new Poll(simple.getId(), simple.getTitle(), simple.getQuestion(),
												user, simple.isOpen(), simple.getCreated());
										listener.onPollCreated(ret);
										return;
									} else {
										listener.onPollCreated(null);
										return;
									}
								}
							});
						} else {
							listener.onPollCreated(null);
							return;
						}
					}
				});
		params.setPostData(postData);

		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);
	}

	@Override
	public void createUser(User user, final ICreateUserListener listener) {
		UserSimple simple = user;
		String postData = gson.toJson(simple);
		String url = this.BASE_URL + "user";
		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_POST,
				EntityFormatEnum.FORMAT_URL_FORM_ENCODED, url, new IResponseListener() {

					@Override
					public void onResponse(String text) {
						User user = gson.fromJson(text, User.class);
						listener.onUserCreated(user);
					}
				});
		params.setPostData(postData);

		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);
	}

	@Override
	public void createVote(Vote vote, final ICreateVoteListener listener) {
		VoteSimple simple = vote;
		String postData = gson.toJson(simple);
		String url = this.BASE_URL + "vote";
		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_POST,
				EntityFormatEnum.FORMAT_URL_FORM_ENCODED, url, new IResponseListener() {

					@Override
					public void onResponse(String text) {
						final VoteSimple simple = gson.fromJson(text, VoteSimple.class);
						if (simple != null) {
							// request poll and user by id and fill it in to
							// full entity
							Long userId = simple.getUserId();
							final Long pollId = simple.getPollId();
							findUser(userId, new IFindUserListener() {

								@Override
								public void onUserFound(final User user) {
									if (user != null) {
										findPoll(pollId, new IFindPollListener() {

											@Override
											public void onPollFound(Poll poll) {
												if (poll != null) {
													Vote ret = new Vote(simple.getId(), user, poll, simple
															.getVoteValue());
													listener.onVoteCreated(ret);
												} else {
													listener.onVoteCreated(null);
												}
											}
										});
									} else {
										listener.onVoteCreated(null);
										return;
									}
								}
							});
						} else {
							listener.onVoteCreated(null);
							return;
						}
					}
				});
		params.setPostData(postData);

		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);

	}

	@Override
	public void findPoll(Long pollId, final IFindPollListener listener) {
		String url = this.BASE_URL + "poll/" + pollId;
		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_GET, url, new IResponseListener() {

			@Override
			public void onResponse(String text) {
				final PollSimple simple = gson.fromJson(text, PollSimple.class);
				if (simple != null) {
					// request user by id and fill it in to full entity
					Long userId = simple.getOwnerId();
					findUser(userId, new IFindUserListener() {

						@Override
						public void onUserFound(User user) {
							if (user != null) {
								Poll ret = new Poll(simple.getId(), simple.getTitle(), simple.getQuestion(), user,
										simple.isOpen(), simple.getCreated());
								listener.onPollFound(ret);
								return;
							} else {
								listener.onPollFound(null);
								return;
							}
						}
					});
				} else {
					listener.onPollFound(null);
					return;
				}
			}
		});

		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);
	}

	@Override
	public void findPolls(final IFindPollsListener listener) {
		// logs = gson.fromJson(br, new TypeToken<List<JsonLog>>(){}.getType());
		String url = this.BASE_URL + "poll";
		IResponseListener respListener = new IResponseListener() {

			@Override
			public void onResponse(String text) {
				final Collection<PollSimple> list = gson.fromJson(text, new TypeToken<List<PollSimple>>() {
				}.getType());
				if (list != null) {
					// fill in users
					findUsers(new IFindUsersListener() {

						@Override
						public void onUsersFound(Collection<User> users) {
							if (users != null) {
								// build map
								HashMap<Long, User> userMap = new HashMap<Long, User>(users.size());
								for (User u : users) {
									userMap.put(u.getId(), u);
								}
								// fill to polls
								List<Poll> ret = new ArrayList<Poll>();
								for (PollSimple simple : list) {
									User owner = userMap.get(simple.getOwnerId());
									Poll p = new Poll(simple.getId(), simple.getTitle(), simple.getQuestion(), owner,
											simple.isOpen(), simple.getCreated());
									ret.add(p);
								}
								listener.onPollsFound(ret);
							} else {
								listener.onPollsFound(null);
							}
						}
					});

				} else {
					listener.onPollsFound(null);
				}
			};
		};
		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_GET, url, respListener);
		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);
	}

	@Override
	public void findUser(Long userId, final IFindUserListener listener) {
		String url = this.BASE_URL + "user/" + userId;
		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_GET, url, new IResponseListener() {

			@Override
			public void onResponse(String text) {
				User user = gson.fromJson(text, User.class);
				listener.onUserFound(user);
			}
		});

		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);
	}

	@Override
	public void findUser(String name, final IFindUserListener listener) {
		String url = this.BASE_URL + "user/name/" + name;
		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_GET, url, new IResponseListener() {

			@Override
			public void onResponse(String text) {
				User user = gson.fromJson(text, User.class);
				listener.onUserFound(user);
			}
		});

		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);
	}

	@Override
	public void findUsers(final IFindUsersListener listener) {
		// logs = gson.fromJson(br, new TypeToken<List<JsonLog>>(){}.getType());
		String url = this.BASE_URL + "user";
		IResponseListener respListener = new IResponseListener() {

			@Override
			public void onResponse(String text) {
				final Collection<User> list = gson.fromJson(text, new TypeToken<List<User>>() {
				}.getType());
				if (list != null) {
					listener.onUsersFound(list);
				} else {
					listener.onUsersFound(null);
				}
			}
		};

		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_GET, url, respListener);
		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);
	}

	@Override
	public void findVote(Long userId, Long pollId, final IFindVoteListener listener) {
		String url = this.BASE_URL + "vote/user/" + userId + "/poll/" + pollId;
		IResponseListener respListener = new IResponseListener() {

			@Override
			public void onResponse(String text) {
				try {
					final VoteSimple simple = gson.fromJson(text, VoteSimple.class);

					if (simple != null) {
						// request poll and user by id and fill it in to
						// full entity
						Long userId = simple.getUserId();
						final Long pollId = simple.getPollId();
						findUser(userId, new IFindUserListener() {

							@Override
							public void onUserFound(final User user) {
								if (user != null) {
									findPoll(pollId, new IFindPollListener() {

										@Override
										public void onPollFound(Poll poll) {
											if (poll != null) {
												Vote ret = new Vote(simple.getId(), user, poll, simple.getVoteValue());
												listener.onVoteFound(ret);
											} else {
												listener.onVoteFound(null);
											}
										}
									});
								} else {
									listener.onVoteFound(null);
									return;
								}
							}
						});
					} else {
						listener.onVoteFound(null);
						return;
					}
				} catch (JsonSyntaxException e) {
					Log.e(TAG, "json error: " + e.toString());
					listener.onVoteFound(null);
				}
			}
		};

		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_GET, url, respListener);
		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);

	}

	@Override
	public void findVotesByPoll(final Long pollId, final IFindVotesByPollListener listener) {

		String url = this.BASE_URL + "vote/poll/" + pollId;
		IResponseListener respListener = new IResponseListener() {

			@Override
			public void onResponse(String text) {
				final Collection<VoteSimple> list = gson.fromJson(text, new TypeToken<List<VoteSimple>>() {
				}.getType());
				if (list != null) {
					// fill in poll
					findPoll(pollId, new IFindPollListener() {

						@Override
						public void onPollFound(final Poll poll) {

							if (poll != null) {
								// fill in users
								findUsers(new IFindUsersListener() {

									@Override
									public void onUsersFound(Collection<User> users) {
										if (users != null) {
											// build map
											HashMap<Long, User> userMap = new HashMap<Long, User>(users.size());
											for (User u : users) {
												userMap.put(u.getId(), u);
											}
											// fill to polls
											List<Vote> ret = new ArrayList<Vote>();
											for (VoteSimple simple : list) {
												User user = userMap.get(simple.getUserId());
												Vote v = new Vote(simple.getId(), user, poll, simple.getVoteValue());
												ret.add(v);
											}
											listener.onVotesFound(ret);
										} else {
											listener.onVotesFound(null);
										}
									}
								});
							} else {
								listener.onVotesFound(null);
							}
						}
					});
				} else {
					listener.onVotesFound(null);
				}
			};
		};
		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_GET, url, respListener);
		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);
	}
}
