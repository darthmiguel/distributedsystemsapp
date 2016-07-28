package pds.app;
import java.util.ArrayList;

/**
 * Class that handles the XML-RPC methods for the Node
 * 
 *
 */
public class Node
{

	/**
	 * XML-RPC method to add a node to the network
	 * @param IpnPort
	 * @return
	 */
	public boolean add(String IpnPort)
	{
		
		DistributedNetwork instance = DistributedNetwork.getInstance(); //get the instance

		System.out.println("New node has joined the network.");
		NetworkNode newObj = new NetworkNode(IpnPort);
		int k = 0;

		while (k < instance.getListNetworkNodes().size() && instance.getListNetworkNodes().get(k).compare(newObj) < 0)
		{
			k++;
		}
		instance.getListNetworkNodes().add(k, newObj);
		instance.isInstanceOnline(true); //set instance property online to true

		return true;
	}
	
	/**
	 * XML-RPC Method to remove a node from the network
	 * @param IpnPort
	 * @return
	 */
	public boolean delete(String IpnPort)
	{
		
		DistributedNetwork instance = DistributedNetwork.getInstance(); //get the instance

		System.out.println("New node has signed off the network.");
		//Client.activeNodes.Remove(ipAddress);
		int k = 0;

		while (k < instance.getListNetworkNodes().size() && !instance.getListNetworkNodes().get(k).getIPnPort().equals(IpnPort))
		{
			k++;
		}
		if (k < instance.getListNetworkNodes().size())
		{
			NetworkNode obj = instance.getListNetworkNodes().get(k);
			instance.getListNetworkNodes().remove(obj);
		}
		return true;
	}

	/**
	 * List of all nodes before the execution of the Bully algorithm
	 * @return
	 */
	public String[] getListOfActiveNode()
	{
		DistributedNetwork instance = DistributedNetwork.getInstance();
		String[] result = new String[instance.getListNetworkNodes().size()];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = instance.getListNetworkNodes().get(i).getIPnPort();
		}
		System.out.println("List Passed!");
		return result;
	}


	/**
	 * XML-RPC method that returns the list of nodes after the master node has been selected
	 * @return
	 */
	public String[] getActiveNodeList()
	{
		DistributedNetwork instance = DistributedNetwork.getInstance();
		String[] result = new String[instance.getListNetworkNodes().size()];


		for (int i = 0; i < result.length; i++)
		{
		   
			if (instance.getListNetworkNodes().get(i).getMasterNodeProcessId() == 1)
			{
				result[i] = instance.getListNetworkNodes().get(i).getURLForXMLRPC().toString() + " Node " + instance.getListNetworkNodes().get(i).getprocessId() + " is Our Master Node";
			}
			else
			{
				result[i] = instance.getListNetworkNodes().get(i).getURLForXMLRPC().toString() + " Node " + instance.getListNetworkNodes().get(i).getprocessId();
			}
		}
		return result;
	}

	/**
	 * XML-RPC method that sets the id process to the nodes, used by c#
	 * @param lstIDIPnPort
	 * @param highestNodeId
	 * @return
	 */
	  public boolean setProcessId(String[] lstIDIPnPort, int highestNodeId)
	  {

		try
		{
		DistributedNetwork instance = DistributedNetwork.getInstance();
		System.out.println("---Getting  Master Node Information String---");
		
	  for (int i = 0; i < lstIDIPnPort.length; i++)
	  {
		String[] lstSubIDnPort = new String[2];
		lstSubIDnPort = lstIDIPnPort[i].split("[-]", -1);
		
		ArrayList<NetworkNode> temp = new ArrayList<NetworkNode>();
		for(NetworkNode ne: instance.getListNetworkNodes()){
			if(ne.getURLForXMLRPC().equals(lstSubIDnPort[0]))
				temp.add(ne);
		}
		
		
		for (NetworkNode data : temp)
		{
			data.setprocessId(Integer.parseInt(lstSubIDnPort[1]));
			if (data.getprocessId() == highestNodeId)
			{
				data.setMasterNodeProcessId(1);
			}
			if (data.getMasterNodeProcessId() == 1)
			{
				System.out.println(data.getURLForXMLRPC() + " Currently ! is our Master Node with Node Id " + data.getprocessId());
			}
			else
			{
				System.out.println(data.getURLForXMLRPC() + " Node Id " + data.getprocessId());
			}
		}
	  }
			return true;
		}
		  catch (RuntimeException ex)
		  {
			  System.out.println(ex.getMessage().toString());
			  return false;
		  }
	  }
	  
	  /**
	   * XML-RPC method that sets the id process to the nodes, used by java
	   * @param lstIDIPnPortObject
	   * @param highestNodeId
	   * @return
	   */
	  public boolean setProcessId(Object[] lstIDIPnPortObject, Integer highestNodeId)
	  {
		  try{
			  DistributedNetwork instance = DistributedNetwork.getInstance();
			  System.out.println("---Getting  Master Node Information Object---");
			  String [] lstIDIPnPort = new String[lstIDIPnPortObject.length];
			  int i = 0;
			  //casting the array of objects to array of strings
			  for(Object o: lstIDIPnPortObject){
				  lstIDIPnPort[i] = (String) o;
				  i++;
			  }
			  
			  //taking out the information about the process id for each node
			  //
			  for (i = 0; i <lstIDIPnPort.length; i++){
				  //in this variable we will have in position 0. the URLXMLRPC in position 1. the process id
				  String[] lstSubIDnPort = new String[2];
				  lstSubIDnPort=lstIDIPnPort[i].split("-");
				  
				  for(NetworkNode node: instance.getListNetworkNodes()){
					  if(node.getURLForXMLRPC().equals(lstSubIDnPort[0])){
						  node.setprocessId(Integer.parseInt(lstSubIDnPort[1]));
						  if (node.getprocessId() == highestNodeId){
							  node.setMasterNodeProcessId(1);
							  System.out.println(node.getURLForXMLRPC() + " Currently ! is our Master Node with Node Id " + node.getprocessId());
						  }else
								System.out.println(node.getURLForXMLRPC() + " Node Id " + node.getprocessId());
					  }
				  }
			  }
			return true;
		}
		  catch (RuntimeException ex)
		  {
			  System.out.println(ex.getMessage().toString());
			  return false;
		  }
	  }  

	 /**
	  * XML-RPC that tells the node which algorithms is using
	  * @param value
	  * @return
	  */
	public boolean startAlogrithmbyValue(int value)
	{
		DistributedNetwork instance = DistributedNetwork.getInstance();
		RichartAgrawala agrawala = DistributedNetwork.getRicartAgrawala();

		instance.setDistributedMEA(value);

		if (instance.getDistributedMEA() == 1002)
			System.out.println("Using Centralized Mutual Exclusion algorithm.");
		else if (instance.getDistributedMEA() == 1001){
			System.out.println("Using Ricart & Agrawala Mutual Exclusion algorithm.");
			agrawala.getlamportClock().reset();
		}

		instance.isInstanceStarted(true);
		System.out.println(instance.getThisMachineIpnPort().getURLForXMLRPC() + " Starts the Algorithm !!");

		return true;
	}

	/**
	 * XML-RPC method that updates the new master node
	 * @param processId that will be the new master
	 * @return
	 */
	public boolean updateMasterNode(int processId)
	{
		try
		{
		DistributedNetwork instance = DistributedNetwork.getInstance();
				
		ArrayList<NetworkNode> temp = new ArrayList<NetworkNode>();
		for(NetworkNode ne: instance.getListNetworkNodes()){
			if(ne.getprocessId() == processId)
				temp.add(ne);
				
		}
				
		for (NetworkNode data : temp)
		{
			data.setMasterNodeProcessId(1);
			System.out.println(data.getURLForXMLRPC() + " Currently ! is our Master Node with Node Id " + data.getprocessId());


		}
		return true;
		}
		catch (RuntimeException ex)
		{
			System.out.println(ex.getMessage().toString());
			return false;
		}
	} // updateMasterNode ENDS HERE
	

}