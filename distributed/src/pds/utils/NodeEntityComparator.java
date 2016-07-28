package pds.utils;

import java.util.Comparator;

import pds.app.NetworkNode;

/**
 * 
 * Class that implements a comparator
 *
 */
public class NodeEntityComparator implements Comparator<NetworkNode> {

	/**
	 * Method that implements the comparator for comparing the process id of
	 * two given nodes
	 */
	@Override	
	public int compare(NetworkNode arg0, NetworkNode arg1) {
		Integer id1, id2;
		id1 = new Integer(arg0.getprocessId());
		id2 = new Integer(arg1.getprocessId());
		return id1.compareTo(id2);
	}

}
