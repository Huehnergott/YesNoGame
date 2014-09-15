package example.swa.yesnogame.domain.simple;

import android.util.Log;

/**
 * Entity class. Simple type. Immutable. Comparable. Equals on userID and pollId
 * (keys).
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class VoteSimple extends BaseObject implements Cloneable {

	Long id;
	Long pollId;
	Long userId;

	int voteValue;

	public VoteSimple(Long id, Long userId, Long pollId, int voteValue) {
		super();
		this.id = id;
		this.userId = userId;
		this.pollId = pollId;
		this.voteValue = voteValue;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VoteSimple other = (VoteSimple) obj;
		if (this.pollId == null) {
			if (other.pollId != null) {
				return false;
			}
		} else if (!this.pollId.equals(other.pollId)) {
			return false;
		}
		if (this.userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!this.userId.equals(other.userId)) {
			return false;
		}

		return true;
	}

	protected VoteSimple getClone(VoteSimple from) {
		VoteSimple ret = null;
		try {
			ret = (VoteSimple) from.clone();
		} catch (CloneNotSupportedException e) {
			Log.e("YesNo", "cloning failed");
		}
		return ret;
	}

	/**
	 * Gets the id of the vote.
	 * 
	 * @return
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Gets the id of the poll of the vote
	 * 
	 * @return
	 */
	public Long getPollId() {
		return this.pollId;
	}

	/**
	 * Gets the id of the user
	 * 
	 * @return
	 */
	public Long getUserId() {
		return this.userId;
	}

	/**
	 * Gets the vote value (0-100)
	 * 
	 * @return
	 */
	public int getVoteValue() {
		return this.voteValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.pollId == null) ? 0 : this.pollId.hashCode());
		result = prime * result + ((this.userId == null) ? 0 : this.userId.hashCode());
		return result;
	}

}
