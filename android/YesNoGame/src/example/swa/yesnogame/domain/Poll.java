package example.swa.yesnogame.domain;

import example.swa.yesnogame.domain.simple.PollSimple;

/**
 * Entity. Immtuable type. Comparable. Equals on "title" (key). Extends simple
 * class by setting the owner of type User explicitly.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class Poll extends PollSimple {

	User owner;

	public Poll(Long id, String title, String question, Long ownerId, boolean isOpen, Long created) {
		super(id, title, question, ownerId, isOpen, created);
	}

	public Poll(Long id, String title, String question, User owner, boolean isOpen, Long created) {
		super(id, title, question, (owner != null) ? owner.getId() : null, isOpen, created);
		this.owner = owner;
	}

	public User getOwner() {
		return this.owner;
	}
}
