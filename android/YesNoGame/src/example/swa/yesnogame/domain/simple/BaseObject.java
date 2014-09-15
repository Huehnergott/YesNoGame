package example.swa.yesnogame.domain.simple;

/**
 * For convenience reasons we add a static counter for the mock here. All domain
 * objectsd should inherit from this base class.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public abstract class BaseObject {

	private static long sequence = 1000l;

	public static long nextId() {
		return sequence++;
	}
}
