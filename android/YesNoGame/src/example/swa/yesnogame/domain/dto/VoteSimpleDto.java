package example.swa.yesnogame.domain.dto;

import example.swa.yesnogame.domain.simple.BaseObject;

/**
 * Entity class. Simple type. Cloneable. Comparable. Equals on userID and pollId
 * (keys).
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class VoteSimpleDto extends BaseObject implements Cloneable {

	Long Id;
	String PollTitle;
	String UserName;
	int VoteValue;

	public VoteSimpleDto(Long id, String pollTitle, String userName, int voteValue) {
		super();
		this.Id = id;
		this.PollTitle = pollTitle;
		this.UserName = userName;
		this.VoteValue = voteValue;
	}

	public Long getId() {
		return this.Id;
	}

	public String getPollTitle() {
		return this.PollTitle;
	}

	public String getUserName() {
		return this.UserName;
	}

	public int getVoteValue() {
		return this.VoteValue;
	}

	public void setId(Long id) {
		this.Id = id;
	}

	public void setPollTitle(String pollTitle) {
		this.PollTitle = pollTitle;
	}

	public void setUserName(String userName) {
		this.UserName = userName;
	}

	public void setVoteValue(int voteValue) {
		this.VoteValue = voteValue;
	}

}
