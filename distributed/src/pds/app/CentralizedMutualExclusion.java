package pds.app;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Class to implement the Centralized Mutual Exclusion algorithm
 */
public class CentralizedMutualExclusion 
{
	
	private static final Object networkMutualLock = new Object();

	/**
	 * XML RPC method that sends a request to the master node
	 * @param clientCompleteURL URL of the client
	 * @return String [] size 2. In position 0, we have whether the master is Free or NotFree
	 * Position 1 the master String if the master node is free, else empty string
	 */
	public String[] requestToMasterString(String clientCompleteURL)
	{
		System.out.println(clientCompleteURL + " sends request To Master");
		Master master = Master.getInstance();
		String[] returnValues = new String[2];
		try
		{
			synchronized (networkMutualLock)
			{
				if (!master.getoccupied().equals(""))
				{
					if (!master.getnodeQueue().contains(clientCompleteURL))
					{
						master.getnodeQueue().offer(clientCompleteURL);
						System.out.println("New client added to the LinkedList: " + clientCompleteURL);
					}

					System.out.println("Currently, the Master Node is busy, " + clientCompleteURL + " is waiting for it");
					LinkedList<String> tmpQueue = new LinkedList<String>(master.getnodeQueue());
					for (String queue : tmpQueue)
					{
						System.out.println("LinkedList value: " + queue.toString());
					}

					returnValues[0] = "NotFree";
					returnValues[1] = "";
					return returnValues;
				}
				else
				{
					System.out.println("Master Node Available");
					if (notifyClientMasterIsFree(clientCompleteURL))
					{
						System.out.println("Master is processing the request from: " + clientCompleteURL);
						returnValues[0] = "Free";
						returnValues[1] = master.getMasterString();
						return returnValues;
					}
					else
					{
						System.out.println("Master Node is now busy");
						returnValues[0] = "NotFree";
						returnValues[1] = "";
						return returnValues;
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			throw e;
		}
	}

	/**
	 * XML-RPC Method to append a random string to the master string
	 * @param nodeFullUrlID client who is appending the master string
	 * @param appendedString appended string
	 * @return new appended string
	 */
	public String appendValuesToMasterString(String nodeFullUrlID, String appendedString)
	{
		Master master = Master.getInstance();
		DistributedNetwork network = DistributedNetwork.getInstance();

		synchronized (networkMutualLock)
		{
			if (!master.getoccupied().equals(nodeFullUrlID))
				System.out.println("Master string is being appended by node: " + nodeFullUrlID);

			master.setMasterString(appendedString);

			if (network.getDistributedMEA() == 1002)
				notifyClientMasterIsFree("");
			return master.getMasterString();
		}
	}

	/**
	 * Notifies the client that the master is free to attend the request.
	 * @param clientNodeFullURL Client Full URL
	 * @return true if the announcement was ok
	 * false if there was a problem
	 */
	public boolean notifyClientMasterIsFree(String clientNodeFullURL)
	{
		Master master = Master.getInstance();
//		String client;

		System.out.println("Sending free notification to client");
		try
		{
			synchronized (networkMutualLock)
			{
				if (master.getnodeQueue().size() != 0)
				{
					master.setoccupied(master.getnodeQueue().poll());
//					client = master.getoccupied();

					XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			        try 
			        {
//			        	config.setServerURL(new URL(client.split("[_]", -1)[0]));
			        	config.setServerURL(new URL(master.getoccupied()));
			        	
			            XmlRpcClient xmlRPCclient = new XmlRpcClient();
			            xmlRPCclient.setConfig(config);
			            Object[] params = new Object[]{false, master.getMasterString()};
			            xmlRPCclient.execute("CentralizedMutualExclusion.setWaitFlagForResource", params);
			        } catch (XmlRpcException e) 
			        {
			            System.out.println("XML RPC threw an exception." + e.getMessage()); 
			        } catch (MalformedURLException e) {
			        	System.out.println("The url was wrong."); 
			        }

					return false;
				}
				else
				{
					System.out.println("Master has processed the request from node: " + clientNodeFullURL + " successfully");;
					master.setoccupied(clientNodeFullURL);
					return true;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			throw e;
		}
	}

	/**
	 * XML-RPC Method that updates the string master in the local node and sets the waiting flag
	 * @param waitFlag variable that tells the current node to wait or no
	 * @param masterString variable that updates the string master in the current instance
	 * @return
	 */
	public boolean setWaitFlagForResource(boolean waitFlag, String masterString)
	{
		DistributedNetwork instance = DistributedNetwork.getInstance();
		synchronized (networkMutualLock)
		{
			instance.setMasterString(masterString);
			instance.setIsWaiting(waitFlag);
		}
		return true;
	}

	/**
	 * XML-RPC Method that returns the master string
	 * @return
	 */
	 public String getMasterString()
	 {
		Master master = Master.getInstance();
		return master.getMasterString();
	 }

	 /**
	  * XML-RPC method that tells the client that the process is finished
	  * @param node client that will be notified
	  * @return
	  */
	public boolean finishNotification(String node)
	{
		DistributedNetwork network = DistributedNetwork.getInstance();
		Master master = Master.getInstance();
		Object mutualLock = Master.getmutualLock();

		
		synchronized (mutualLock)
		{
			System.out.println("Sending finish notification to node: " + node);
			master.setfinishedNodes(master.getfinishedNodes() + 1);
			if (master.getfinishedNodes() == network.getListNetworkNodes().size())
			{
				for (NetworkNode hIPP : network.getListNetworkNodes())
				{
					XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			        try 
			        {
			        	config.setServerURL(new URL(hIPP.getURLForXMLRPC()));
			            XmlRpcClient xmlRPCclient = new XmlRpcClient();
			            xmlRPCclient.setConfig(config);
			            Object[] params = new Object[]{false, master.getMasterString()};
			            xmlRPCclient.execute("CentralizedMutualExclusion.setWaitFlagForResource", params);
			            
			        } catch (XmlRpcException e) {
			        	System.out.println("XML RPC Exception: " + e.getMessage());
			        } catch (MalformedURLException e) {
			        	System.out.println("The url was wrong."); 
			        }
				}
			}
		}
		return true;
		
	}
	


}