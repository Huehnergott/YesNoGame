package example.swa.yesnogame.domain.simple;

import android.util.Log;

/**
 * Entity. Simple type. Immtuable type. Clonable. Comparable. Equals on "title"
 * (key).
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class PollSimple extends BaseObject implements Cloneable {

	Long id;
	Long created;
	boolean isOpen;
	Long ownerId;
	String question;
	String title;

	public PollSimple(Long id, String title, String question, Long ownerId,
			boolean isOpen, Long created) {
		super();
		this.id = id;
		this.title = title;
		this.question = question;
		this.ownerId = ownerId;
		this.isOpen = isOpen;
		this.created = created;
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
		PollSimple other = (PollSimple) obj;
		if (this.title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!this.title.equals(other.title)) {
			return false;
		}
		return true;
	}

	public PollSimple getClone(PollSimple from) {
		PollSimple ret = null;
		try {
			ret = (PollSimple) from.clone();
		} catch (CloneNotSupportedException e) {
			Log.e("YesNo", "cloning failed");
		}
		return ret;
	}

	public Long getCreated() {
		return this.created;
	}

	public Long getId() {
		return this.id;
	}

	public Long getOwnerId() {
		return this.ownerId;
	}

	public String getQuestion() {
		return this.question;
	}

	public String getTitle() {
		return this.title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.title == null) ? 0 : this.title.hashCode());
		return result;
	}

	public boolean isOpen() {
		return this.isOpen;
	}

	@Override
	public String toString() {
		String ret = this.title;
		if (ret == null) {
			ret = "";
		}
		if (ret.length() > 20) {
			ret = ret.substring(0, 19) + "...";
		}
		if (!this.isOpen) {
			ret = "(closed) " + ret;
		}
		return ret;
	}

}
