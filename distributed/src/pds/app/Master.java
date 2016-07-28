package pds.app;
import java.util.LinkedList;

/**
 * Class to handle the master node
 *
 */
 public final class Master
 {
	private static final Master _master = new Master();
	private static final Object _mutualLock = new Object();
	private static String _masterString = "";
	private LinkedList<String> _nodeQueue = new LinkedList<String>();
	private String _occupied = "";
	private int _finishedNodes = 0;




	private Master()
	{
	}

	public static Master getInstance()
	{
		return _master;
	}

	public static Object getmutualLock()
	{
		return _mutualLock;
	}

	public String getMasterString()
	{
		return _masterString;
	}
	public void setMasterString(String value)
	{
		synchronized (_mutualLock)
		{
			_masterString = value;
		}
	}

	public LinkedList<String> getnodeQueue()
	{
		return _nodeQueue;
	}
	public void setnodeQueue(LinkedList<String> value)
	{
		synchronized (_mutualLock)
		{
			_nodeQueue = value;
		}
	}

	public String getoccupied()
	{
		return _occupied;
	}
	public void setoccupied(String value)
	{
		synchronized (_mutualLock)
		{
			_occupied = value;
		}
	}

	public int getfinishedNodes()
	{
		return _finishedNodes;
	}
	public void setfinishedNodes(int value)
	{
		synchronized (_mutualLock)
		{
			_finishedNodes = value;
		}
	}
 }