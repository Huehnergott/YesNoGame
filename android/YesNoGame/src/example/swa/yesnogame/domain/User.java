package example.swa.yesnogame.domain;

import example.swa.yesnogame.domain.simple.UserSimple;

/**
 * User entity. Has same properties as simple class by now.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class User extends UserSimple {

	public User(Long id, String name) {
		super(id, name);
	}
}
