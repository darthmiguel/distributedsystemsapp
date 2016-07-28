package pds.app;


import java.util.ArrayList;
/**
 * 
 * Class for RicartAgrawala algorithm with XML-RPC methods
 *
 */
public class RichartAgrawala
{
	private static final Object _mutualLock = new Object();
	private LamportLogicClock _lamportClock = new LamportLogicClock();
	private int copiedClock = Integer.MAX_VALUE;
	private int _oKNum = 0;
	private boolean _usingResource = false;
	
	/**
	 * ArrayList with the nodes that cannot use the resource
	 */
	private ArrayList<NetworkNode> listOfNodesQueued = new ArrayList<NetworkNode>();

	public LamportLogicClock getlamportClock()
	{
		return _lamportClock;
	}
	public void setlamportClock(LamportLogicClock value)
	{
		synchronized (_mutualLock)
		{
			_lamportClock = value;
		}
	}

	public int getCopiedClock()
	{
		return copiedClock;
	}
	public void setCopiedClock(int value)
	{
		synchronized (_mutualLock)
		{
			copiedClock = value;
		}
	}

	public int getokNum()
	{
		return _oKNum;
	}
	public void setokNum(int value)
	{
		synchronized (_mutualLock)
		{
			_oKNum = value;
		}
	}

	public boolean getUsingResource()
	{
		return _usingResource;
	}
	public void setUsingResource(boolean value)
	{
		synchronized (_mutualLock)
		{
			_usingResource = value;
		}
	}



	public ArrayList<NetworkNode> getListOfNodesQueued()
	{
		return listOfNodesQueued;
	}
	public void setListOfNodesQueued(ArrayList<NetworkNode> value)
	{
		synchronized (_mutualLock)
		{
			listOfNodesQueued = value;
		}
	}

	public RichartAgrawala()
	{
	}
	
	

	/**
	 * Method t
	 * @return
	 */
	public boolean sendRequest()
	{
		DistributedNetwork network = DistributedNetwork.getInstance();
		RichartAgrawala agrawala = DistributedNetwork.getRicartAgrawala();

		System.out.println("Sending Request -----");
		//change the lamport lock in one
		agrawala.getlamportClock().change();
		//set the copied clock with the current lamport clock
		agrawala.setCopiedClock(agrawala.getlamportClock().getLamportClock());
		System.out.println("Timestamp is: " + agrawala.getCopiedClock());
		agrawala.setokNum(0);
		for (NetworkNode h : network.getListNetworkNodes())
		{
			if (!h.equals(network.getThisMachineIpnPort()))
			{
				System.out.println("Sending Request to:  " +  h.getURLForXMLRPC());
				XMLRPCUtility.sendRequestOverRPC(h, agrawala.getCopiedClock());
			}
		}

		System.out.printf("Ok Count  : %1$s" + "\r\n", agrawala.getokNum());
		System.out.printf("Active nodes: %1$s" + "\r\n", network.getListNetworkNodes().size());

		if (agrawala.getokNum() < network.getListNetworkNodes().size() - 1)
		{
			System.out.println("Wait for the Resource to free...");
		}

		int ok = 0;
		int okNeeded = network.getListNetworkNodes().size() - 1;
		while (ok < okNeeded)
		{
			synchronized (_mutualLock)
			{
				ok = agrawala.getokNum();
			}
		}
		agrawala.setUsingResource(true);
		System.out.println("End of waiting...");

		return true;
	}

	/**
	 * XML-RPC Method when using Ricart & Agrawala algorithm
	 * @param senderLamportClock lamport clock of the sender
	 * @param senderIp the Ip of the sender
	 * @param senderPort the port of the sender
	 * @return
	 */
	public boolean receiveRequest(int senderLamportClock, String senderIp, int senderPort)
	{
		synchronized (_mutualLock)
		{
			DistributedNetwork network = DistributedNetwork.getInstance();
			RichartAgrawala agrawala = DistributedNetwork.getRicartAgrawala();
			System.out.println("Receive request from " + senderIp + ":" + senderPort);
			agrawala.getlamportClock().receiveRequest(senderLamportClock);
			NetworkNode sender = new NetworkNode(senderIp, senderPort);
			int senderId = 0;
			for (NetworkNode hIPP : network.getListNetworkNodes())
			{
				if (hIPP.getURLForXMLRPC().equals(sender.getURLForXMLRPC()))
					senderId = hIPP.getprocessId(); // GET SENDER ID
					break;
			}

			if (agrawala.getUsingResource())
			{
				System.out.println("Using resource: " + agrawala.getUsingResource());
				System.out.println("Adding node: " + senderIp + " :" + senderPort + " to the queue...");
				agrawala.getListOfNodesQueued().add(sender);
			}
			else if ((agrawala.getCopiedClock() == Integer.MAX_VALUE) || 
					(senderLamportClock < agrawala.getCopiedClock()) || 
					(senderLamportClock == agrawala.getCopiedClock() && (senderId < network.getThisMachineIpnPort().getprocessId()))
					)
			{
				System.out.println("sending OK to: " + sender.getURLForXMLRPC());
				XMLRPCUtility.sendOKOverRPC(sender, agrawala.getlamportClock().getLamportClock());
			}
			else
			{
				System.out.println("Timestamp is " + agrawala.getCopiedClock());
				System.out.println("Sender timestamp is " + senderLamportClock);
				System.out.println("Local timestamp is: " + agrawala.getlamportClock().getLamportClock());

				System.out.println("Adding node: " + senderIp + " :" + senderPort + " to the queue");
				agrawala.getListOfNodesQueued().add(sender);
			}

			return true;
		}
	}
	
	/**
	 * XML-RPC Method Receives ok from the sender node
	 * @param IP sender
	 * @param Port sender
	 * @return
	 */
	public boolean receiveOk(String IP, int Port)
	{
		synchronized (_mutualLock)
		{
			System.out.println("Received OK from: " + IP + ":" + Port);
			DistributedNetwork network = DistributedNetwork.getInstance();
			RichartAgrawala agrawala = DistributedNetwork.getRicartAgrawala();
			agrawala.setokNum(agrawala.getokNum() + 1);
			System.out.println("Number of OK: " + agrawala.getokNum());
			if (agrawala.getokNum() == network.getListNetworkNodes().size() - 1)
			{
				agrawala.setUsingResource(true);
				System.out.println("Ok is enough. Go to use the resource. OK Count is : " + agrawala.getokNum());
			}

			return true;
		}
	}

	/**
	 * Release the resource
	 */
	public void releaseResource(){
		RichartAgrawala agrawala = DistributedNetwork.getRicartAgrawala();

		System.out.println("----Release resource...");
		agrawala.setCopiedClock(Integer.MAX_VALUE);
		agrawala.setUsingResource(false);

		while (agrawala.getListOfNodesQueued().size() != 0)
		{
			NetworkNode dequeue = agrawala.getListOfNodesQueued().get(0);
			synchronized (_mutualLock)
			{
				agrawala.getListOfNodesQueued().remove(0);
			}
			System.out.println("Removing from queue node: " + dequeue.getIPnPort());
			XMLRPCUtility.sendOKOverRPC(dequeue, agrawala.getlamportClock().getLamportClock());
			
		}
	}
}