package example.swa.yesnogame.domain;

import example.swa.yesnogame.domain.simple.VoteSimple;

/**
 * Represents a vote of a user in a poll. Domain entity. Immutable type.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class Vote extends VoteSimple {

	protected User user;
	protected Poll poll;

	public Vote(Long id, Long userId, Long pollId, int voteValue) {
		super(id, userId, pollId, voteValue);
	}

	public Vote(Long id, User user, Poll poll, int voteValue) {
		super(id, user != null ? user.getId() : null, poll != null ? poll.getId() : null, voteValue);
		this.user = user;
		this.poll = poll;
	}

	public Poll getPoll() {
		return this.poll;
	}

	public User getUser() {
		return this.user;
	}

	private String getUserName() {
		return (this.user != null) ? this.user.getName() : null;
	}

	@Override
	public String toString() {
		int vote = getVoteValue();
		String voteStr;
		switch ((vote - 1) / 10) {
		case 0:
		case 1:
			voteStr = "No!";
			break;
		case 2:
		case 3:
			voteStr = "No";
			break;
		case 4:
			voteStr = "more No";
			break;
		case 5:
			voteStr = "more Yes";
			break;
		case 6:
		case 7:
			voteStr = "Yes";
			break;
		case 8:
		case 9:
			voteStr = "Yes!";
			break;

		default:
			voteStr = "";
		}

		return getUserName() + ": " + voteStr + " " + vote + "%";
	}
}
