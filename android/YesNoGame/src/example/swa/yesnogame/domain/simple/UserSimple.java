package example.swa.yesnogame.domain.simple;

import android.util.Log;

/**
 * Represents a user in the apllication domain. Entity class. Simple type.
 * Immutable. Comparable. Equals on name (key).
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class UserSimple extends BaseObject implements Cloneable {

	Long id;
	String name;

	public UserSimple(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		UserSimple ret = (UserSimple) super.clone();
		return ret;
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
		UserSimple other = (UserSimple) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public UserSimple getClone() {
		UserSimple ret = null;
		try {
			ret = (UserSimple) clone();
		} catch (CloneNotSupportedException e) {
			Log.e("YesNo", "cloning failed");
		}
		return ret;
	}

	/**
	 * Gets the id (primary key) of the user.
	 * 
	 * @return
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Gets the name of the user.
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
