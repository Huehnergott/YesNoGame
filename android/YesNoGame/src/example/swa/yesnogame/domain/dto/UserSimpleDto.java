package example.swa.yesnogame.domain.dto;

/**
 * Entity class. Immutable. Cloneable. Comparable. Equals on name (key).
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class UserSimpleDto {

	Long Id;
	String Name;

	public UserSimpleDto(Long id, String name) {
		super();
		this.Id = id;
		this.Name = name;
	}

	public Long getId() {
		return this.Id;
	}

	public String getName() {
		return this.Name;
	}

	public void setId(Long id) {
		this.Id = id;
	}

	public void setName(String name) {
		this.Name = name;
	}

}
