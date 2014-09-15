package example.swa.yesnogame.domain.util;

import java.util.Comparator;

import example.swa.yesnogame.domain.simple.PollSimple;

/**
 * Utility class. Comparator class for a poll. Provides an age based order. Open
 * polls first.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class PollComparator implements Comparator<PollSimple> {

	@Override
	public int compare(PollSimple lhs, PollSimple rhs) {
		// check first criterion (open/closed ordering)
		if (lhs.isOpen() && !rhs.isOpen()) {
			return -1;
		}
		if (!lhs.isOpen() && rhs.isOpen()) {
			return 1;
		}

		// check second criterion (creation time ordering)
		return -lhs.getCreated().compareTo(rhs.getCreated());
	}
}
