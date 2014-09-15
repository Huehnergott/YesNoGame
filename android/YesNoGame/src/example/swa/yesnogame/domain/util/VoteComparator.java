package example.swa.yesnogame.domain.util;

import java.util.Comparator;

import example.swa.yesnogame.domain.simple.VoteSimple;

/**
 * Utility class. Comparator class for a vote. Provides an order by value.
 * Highest value first.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class VoteComparator implements Comparator<VoteSimple> {

	@Override
	public int compare(VoteSimple lhs, VoteSimple rhs) {
		int ret = Integer.valueOf(-lhs.getVoteValue()).compareTo(
				Integer.valueOf(-rhs.getVoteValue()));
		return ret;
	}

}
