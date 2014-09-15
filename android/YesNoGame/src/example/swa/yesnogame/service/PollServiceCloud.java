package example.swa.yesnogame.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import example.swa.yesnogame.domain.Poll;
import example.swa.yesnogame.domain.User;
import example.swa.yesnogame.domain.Vote;
import example.swa.yesnogame.domain.dto.PollSimpleDto;
import example.swa.yesnogame.domain.dto.UserSimpleDto;
import example.swa.yesnogame.domain.dto.VoteSimpleDto;
import example.swa.yesnogame.service.util.EntityFormatEnum;
import example.swa.yesnogame.service.util.RequestMethodEnum;
import example.swa.yesnogame.service.util.RequestUrlParams;
import example.swa.yesnogame.service.util.RequestUrlParams.IResponseListener;
import example.swa.yesnogame.service.util.RequestUrlTask;

/**
 * Poll service implementation to be used with the real RESTful endpoint
 * implemented in .NET and hosted in the Amazon cloud.
 * 
 * Big difference to the PHP implementation is the usage of userName and
 * pollTitle as foreign keys to the respective entity classes.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class PollServiceCloud implements IPollService {

	private String BASE_URL = "http://54.165.8.75/SWAPoll/api/";
	private static Gson gson = new Gson();

	@Override
	public void closePoll(final Poll poll, final IClosePollListener listener) {
		// create a "closed" instance
		final Poll closedPoll = new Poll(poll.getId(), poll.getTitle(), poll.getQuestion(), poll.getOwner(), false,
				poll.getCreated());

		PollSimpleDto SimpleDto = new PollSimpleDto(closedPoll.getId(), closedPoll.getCreated(), closedPoll.isOpen(),
				closedPoll.getOwner().getName(), closedPoll.getQuestion(), closedPoll.getTitle());
		String putData = gson.toJson(SimpleDto);
		String url = this.BASE_URL + "Polls/" + closedPoll.getId();
		// make an update and find the changed instance
		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_PUT,
				EntityFormatEnum.FORMAT_JSON_ENCODED, url, new IResponseListener() {

					@Override
					public void onResponse(String text) {
						findPoll(poll.getId(), new IFindPollListener() {

							@Override
							public void onPollFound(Poll poll) {
								listener.onPollClosed(poll);
							}
						});
					}
				});
		params.setPutData(putData);

		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);
	}

	@Override
	public void createPoll(Poll poll, final ICreatePollListener listener) {
		PollSimpleDto SimpleDto = new PollSimpleDto(poll.getId(), poll.getCreated(), poll.isOpen(), poll.getOwner()
				.getName(), poll.getQuestion(), poll.getTitle());
		String postData = gson.toJson(SimpleDto);
		String url = this.BASE_URL + "Polls";
		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_POST,
				EntityFormatEnum.FORMAT_JSON_ENCODED, url, new IResponseListener() {

					@Override
					public void onResponse(String text) {
						final PollSimpleDto SimpleDto = gson.fromJson(text, PollSimpleDto.class);
						if (SimpleDto != null) {
							// request user by id and fill it in to full entity
							String userName = SimpleDto.getOwner();
							findUser(userName, new IFindUserListener() {

								@Override
								public void onUserFound(User user) {
									if (user != null) {
										Poll ret = new Poll(SimpleDto.getId(), SimpleDto.getTitle(), SimpleDto
												.getQuestion(), user, SimpleDto.isIsOpen(), SimpleDto.getCreatedTime());
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
		if ((user != null) && (user.getName() != null)) {
			UserSimpleDto SimpleDto = new UserSimpleDto(user.getId(), user.getName());
			String postData = gson.toJson(SimpleDto);
			String url = this.BASE_URL + "Users";
			RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_POST,
					EntityFormatEnum.FORMAT_JSON_ENCODED, url, new IResponseListener() {

						@Override
						public void onResponse(String text) {
							UserSimpleDto simple = gson.fromJson(text, UserSimpleDto.class);
							User ret = new User(simple.getId(), simple.getName());
							listener.onUserCreated(ret);
						}
					});
			params.setPostData(postData);

			RequestUrlTask task = new RequestUrlTask();
			task.execute(params);
		} else {
			listener.onUserCreated(null);
		}
	}

	@Override
	public void createVote(Vote vote, final ICreateVoteListener listener) {
		Poll votePoll = vote.getPoll();
		User voteUser = vote.getUser();
		if ((voteUser == null) || (votePoll == null)) {
			listener.onVoteCreated(null);
			return;
		}

		VoteSimpleDto SimpleDto = new VoteSimpleDto(vote.getId(), votePoll.getTitle(), voteUser.getName(),
				vote.getVoteValue());
		String postData = gson.toJson(SimpleDto);
		String url = this.BASE_URL + "Votes";

		IResponseListener respListener = new IResponseListener() {

			@Override
			public void onResponse(String text) {
				final VoteSimpleDto SimpleDto = gson.fromJson(text, VoteSimpleDto.class);
				if (SimpleDto != null) {
					// request poll and user by id and fill it in to
					// full entity
					String userName = SimpleDto.getUserName();
					final String pollTitle = SimpleDto.getPollTitle();
					findUser(userName, new IFindUserListener() {

						@Override
						public void onUserFound(final User user) {
							if (user != null) {
								findPolls(new IFindPollsListener() {

									@Override
									public void onPollsFound(Collection<Poll> polls) {
										if (polls != null) {
											Poll poll = null;
											for (Poll p : polls) {
												if (pollTitle.equals(p.getTitle())) {
													poll = p;
													break;
												}
											}
											if (poll != null) {
												Vote ret = new Vote(SimpleDto.getId(), user, poll, SimpleDto
														.getVoteValue());
												listener.onVoteCreated(ret);
											} else {
												listener.onVoteCreated(null);
											}
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
		};

		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_POST,
				EntityFormatEnum.FORMAT_JSON_ENCODED, url, respListener);
		params.setPostData(postData);

		RequestUrlTask task = new RequestUrlTask();
		task.execute(params);

	}

	@Override
	public void findPoll(Long pollId, final IFindPollListener listener) {
		String url = this.BASE_URL + "Polls/" + pollId;
		RequestUrlParams params = new RequestUrlParams(RequestMethodEnum.METHOD_GET, url, new IResponseListener() {

			@Override
			public void onResponse(String text) {
				final PollSimpleDto SimpleDto = gson.fromJson(text, PollSimpleDto.class);
				if (SimpleDto != null) {
					// request user by id and fill it in to full entity
					String userName = SimpleDto.getOwner();
					findUser(userName, new IFindUserListener() {

						@Override
						public void onUserFound(User user) {
							if (user != null) {
								Poll ret = new Poll(SimpleDto.getId(), SimpleDto.getTitle(), SimpleDto.getQuestion(),
										user, SimpleDto.isIsOpen(), SimpleDto.getCreatedTime());
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
		String url = this.BASE_URL + "Polls";
		IResponseListener respListener = new IResponseListener() {

			@Override
			public void onResponse(String text) {
				final Collection<PollSimpleDto> list = gson.fromJson(text, new TypeToken<List<PollSimpleDto>>() {
				}.getType());
				if (list != null) {
					// fill in users
					findUsers(new IFindUsersListener() {

						@Override
						public void onUsersFound(Collection<User> users) {
							if (users != null) {
								// build map
								HashMap<String, User> userMap = new HashMap<String, User>(users.size());
								for (User u : users) {
									userMap.put(u.getName(), u);
								}
								// fill to polls
								List<Poll> ret = new ArrayList<Poll>();
								for (PollSimpleDto SimpleDto : list) {
									User owner = userMap.get(SimpleDto.getOwner());
									Poll p = new Poll(SimpleDto.getId(), SimpleDto.getTitle(), SimpleDto.getQuestion(),
											owner, SimpleDto.isIsOpen(), SimpleDto.getCreatedTime());
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
		String url = this.BASE_URL + "Users/" + userId;
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
	public void findUser(final String name, final IFindUserListener listener) {

		if (name != null) {
			// find all and filter local (which is bad performance)
			findUsers(new IFindUsersListener() {

				@Override
				public void onUsersFound(Collection<User> users) {
					if (users != null) {
						for (User user : users) {
							if (name.equals(user.getName())) {
								listener.onUserFound(user);
								return;
							}
						}
						listener.onUserFound(null);
					} else {
						listener.onUserFound(null);
					}
				}
			});
		} else {
			listener.onUserFound(null);
		}
	}

	@Override
	public void findUsers(final IFindUsersListener listener) {
		String url = this.BASE_URL + "Users";
		IResponseListener respListener = new IResponseListener() {

			@Override
			public void onResponse(String text) {
				final Collection<UserSimpleDto> list = gson.fromJson(text, new TypeToken<List<UserSimpleDto>>() {
				}.getType());
				if (list != null) {
					ArrayList<User> ret = new ArrayList<User>();
					for (UserSimpleDto u : list) {
						if (u.getName() != null) {
							ret.add(new User(u.getId(), u.getName()));
						}
					}
					listener.onUsersFound(ret);
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
	public void findVote(final Long userId, Long pollId, final IFindVoteListener listener) {

		IFindVotesByPollListener l = new IFindVotesByPollListener() {

			@Override
			public void onVotesFound(List<Vote> votes) {
				if (votes != null) {
					for (Vote v : votes) {
						if (userId.equals(v.getUserId())) {
							listener.onVoteFound(v);
							return;
						}
					}
				}
				listener.onVoteFound(null);
			}
		};

		findVotesByPoll(pollId, l);
	}

	@Override
	public void findVotesByPoll(final Long pollId, final IFindVotesByPollListener listener) {

		String url = this.BASE_URL + "Votes";
		IResponseListener respListener = new IResponseListener() {

			@Override
			public void onResponse(String text) {
				final Collection<VoteSimpleDto> list = gson.fromJson(text, new TypeToken<List<VoteSimpleDto>>() {
				}.getType());
				if (list != null) {
					// fill in polls
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
											HashMap<String, User> userMap = new HashMap<String, User>(users.size());
											for (User u : users) {
												userMap.put(u.getName(), u);
											}
											// fill to polls
											List<Vote> ret = new ArrayList<Vote>();
											for (VoteSimpleDto SimpleDto : list) {
												if (SimpleDto.getPollTitle().equals(poll.getTitle())) {
													User user = userMap.get(SimpleDto.getUserName());
													Vote v = new Vote(SimpleDto.getId(), user, poll, SimpleDto
															.getVoteValue());
													ret.add(v);
												}
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
