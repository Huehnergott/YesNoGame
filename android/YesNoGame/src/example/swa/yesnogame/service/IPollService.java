package example.swa.yesnogame.service;

import java.util.Collection;
import java.util.List;

import example.swa.yesnogame.domain.Poll;
import example.swa.yesnogame.domain.User;
import example.swa.yesnogame.domain.Vote;

/**
 * Definition for an asynchronous data service to retrieve users, polls and
 * votes.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public interface IPollService {

	/**
	 * Callback listener for the "closePoll" method.
	 */
	public interface IClosePollListener {
		void onPollClosed(Poll poll);
	}

	/**
	 * Callback listener for the "createPoll" method.
	 */
	public interface ICreatePollListener {
		void onPollCreated(Poll poll);
	}

	/**
	 * Callback listener for the "createUser" method.
	 */
	public interface ICreateUserListener {
		void onUserCreated(User user);
	}

	/**
	 * Callback listener for the "createVote" method.
	 */
	public interface ICreateVoteListener {
		void onVoteCreated(Vote vote);
	}

	/**
	 * Callback listener for the "findPoll" method.
	 */
	public interface IFindPollListener {
		void onPollFound(Poll poll);
	}

	/**
	 * Callback listener for the "findPolls" method.
	 */
	public interface IFindPollsListener {
		void onPollsFound(Collection<Poll> polls);
	}

	/**
	 * Callback listener for the "findUser" method.
	 */
	public interface IFindUserListener {
		void onUserFound(User user);
	}

	/**
	 * Callback listener for the "findUsers" method.
	 */
	public interface IFindUsersListener {
		void onUsersFound(Collection<User> users);
	}

	/**
	 * Callback listener for the "findVote" method.
	 */
	public interface IFindVoteListener {
		void onVoteFound(Vote vote);
	}

	/**
	 * Callback listener for the "findVotesByPoll" method.
	 */
	public interface IFindVotesByPollListener {
		void onVotesFound(List<Vote> votes);
	}

	/**
	 * Async call. Close the poll.
	 */
	void closePoll(Poll poll, IClosePollListener listener);

	/**
	 * Async call. Create a poll.
	 */
	void createPoll(Poll poll, ICreatePollListener listener);

	/**
	 * Async call. Create a new user.
	 */
	void createUser(User user, ICreateUserListener listener);

	/**
	 * Async call. Create a vote.
	 */
	void createVote(Vote vote, ICreateVoteListener listener);

	/**
	 * Async call. Find a poll.
	 */
	void findPoll(Long pollId, IFindPollListener listener);

	/**
	 * Async call. Find all polls.
	 */
	void findPolls(IFindPollsListener listener);

	/**
	 * Async call. Find a user.
	 */
	void findUser(Long userId, IFindUserListener listener);

	/**
	 * Async call. Find a user by name.
	 */
	void findUser(String name, IFindUserListener listener);

	/**
	 * Async call. Find all users.
	 */
	void findUsers(IFindUsersListener listener);

	/**
	 * Async call. Find a vote.
	 */
	void findVote(Long userId, Long pollId, IFindVoteListener listener);

	/**
	 * Async call. Find votes by poll id.
	 */
	void findVotesByPoll(Long pollId, IFindVotesByPollListener listener);

}
