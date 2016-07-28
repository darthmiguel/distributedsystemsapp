package pds.app;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import pds.utils.NodeEntityComparator;

public final class DistributedNetwork
{
	private ArrayList<Integer> lstProcessId;
	private static RichartAgrawala ricartAgrawala = new RichartAgrawala();
	private static final DistributedNetwork instance = new DistributedNetwork();
	//----------Agrawala-----------//
	private static final Object networkMutualLock = new Object();
	private int distributedMutualExclusionAlgorithm = 0;
	private boolean waitFlag = false;
	private static String masterString = "";
	/** 
	 Agarwala code
	*/
	private static boolean isInstanceOnline = false;
	private boolean isInstanceStarted = false; // variable to indicate if the instance has started
	private boolean isInstanceRunning = true;
	private static final Object networkMutualLocking = new Object();
	private boolean assignId = false;
	private static NetworkNode thisMachineIpPort; // ip and port of the local node
	private NetworkNode firstActiveNode; // first network node to be online
	private static ArrayList<NetworkNode> networkNodes = new ArrayList<NetworkNode>(); // arraylist of all nodes in the network
	private int processId;
	private Scanner scanner;
	private DistributedNetwork()
	{

	}
	
	/**
	 * @return the activeNodes
	 */
	public ArrayList<NetworkNode> getListNetworkNodes() {
		return networkNodes;
	}

	/**
	 * @param networkNodes the activeNodes to set
	 */
	public void setListNetworkNodes(ArrayList<NetworkNode> _activeNodes) {
		networkNodes = _activeNodes;
	}

	public static DistributedNetwork getInstance()
	{
		return instance;
	}

	//----------Agarwala-----------//
	public Object getmutualLock()
	{
		return networkMutualLock;
	}

	public int getDistributedMEA()
	{
		synchronized (networkMutualLock)
		{
			return distributedMutualExclusionAlgorithm;
		}
	}
	public void setDistributedMEA(int value)
	{
		synchronized (networkMutualLock)
		{
			distributedMutualExclusionAlgorithm = value;
		}
	}

	public String getMasterString()
	{
		synchronized (networkMutualLock)
		{
			return masterString;
		}
	}
	public void setMasterString(String value)
	{
		synchronized (networkMutualLock)
		{
			masterString = value;
		}
	}


	public boolean isInstanceWaiting()
	{
		synchronized (networkMutualLock)
		{
			return waitFlag;
		}
	}
	public void setIsWaiting(boolean value)
	{
		synchronized (networkMutualLock)
		{
			waitFlag = value;
		}
	}

	//----------------//

	public boolean isInstanceRunning()
	{
		synchronized (networkMutualLocking)
		{
			return isInstanceRunning;
		}
	}
	public void isInstanceRunning(boolean value)
	{
		synchronized (networkMutualLocking)
		{

			isInstanceRunning = value;
		}
	}

	public boolean isInstanceOnline()
	{
		synchronized (networkMutualLocking)
		{
			return isInstanceOnline;
		}
	}
	public void isInstanceOnline(boolean value)
	{
		synchronized (networkMutualLocking)
		{
			isInstanceOnline = value;
		}
	}
	

	public static RichartAgrawala getRicartAgrawala()
	{
		return ricartAgrawala;
	}
	public static void setRicartAgrawala(RichartAgrawala value)
	{
		synchronized (networkMutualLock)
		{
			ricartAgrawala = value;
		}
	}

	public boolean getAssignId()
	{
		synchronized (networkMutualLocking)
		{
			return assignId;
		}
	}
	public void setAssignId(boolean value)
	{
		synchronized (networkMutualLocking)
		{
			assignId = value;
		}
	}
	public NetworkNode getThisMachineIpnPort()
	{
		synchronized (networkMutualLocking)
		{
			return thisMachineIpPort;
		}
	}
	public void setThisMachineIpnPort(NetworkNode value)
	{
		synchronized (networkMutualLocking)
		{
			thisMachineIpPort = value;
		}
	}

	public NetworkNode getFirstActiveNode()
	{
		synchronized (networkMutualLocking)
		{
			return firstActiveNode;

		}
	}
	public void setFirstActiveNode(NetworkNode value)
	{
		synchronized (networkMutualLocking)
		{
			firstActiveNode = value;
		}
	}


	public boolean isInstanceStarted()
	{
		return isInstanceStarted;
	}
	public void isInstanceStarted(boolean value)
	{
		isInstanceStarted = value;
	}


	public int joinNodeToNetwork()
	{
		try
		{
			synchronized (networkMutualLocking)
			{
				DistributedNetwork instance = DistributedNetwork.getInstance();

				if (!instance.isInstanceOnline())
				{
					System.out.println("Enter IP-address of node to connect");
					scanner = new Scanner(System.in);
					String ipAddress = scanner.nextLine();
					System.out.println("Enter the port of node to connect");
					int portOfNodeToConnectTo = Integer.parseInt(scanner.nextLine());
					instance.setFirstActiveNode(new NetworkNode(ipAddress, portOfNodeToConnectTo));
					
		            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		            try {
		                config.setServerURL(new URL(instance.getFirstActiveNode().getURLForXMLRPC())); 
		            } catch (MalformedURLException e) {
		                e.printStackTrace();
		            }
		            XmlRpcClient client = new XmlRpcClient();
		            client.setConfig(config);

		            Object[] params = new Object[] {};
		            
		            Object[] listOfNodes = (Object[]) client.execute("Node.getListOfActiveNode", params);
					for (int i = 0; i < listOfNodes.length; i++)
					{
						//save that IPs returned from the main node
						NetworkNode newObj = new NetworkNode(listOfNodes[i].toString());
						int k = 0;
						while (k < instance.getListNetworkNodes().size() && instance.getListNetworkNodes().get(k).compare(newObj) < 0)
						{
							k++;
						}

						instance.getListNetworkNodes().add(k, newObj);
					}

					instance.isInstanceOnline(true);

					for (NetworkNode h : instance.getListNetworkNodes())
					{
						if (!h.equals(instance.getThisMachineIpnPort()))
						{
							XMLRPCUtility.joinNodeOverRPC(h.getURLForXMLRPC());
						}
					}

					return 1;
				}
				else
				{
					System.out.println("Client is already online!");
					return 0;
				}
			}
		} //try block ends here
		catch (RuntimeException ex)
		{
			System.out.println(ex.getMessage().toString());
			return 0;
		}catch (Exception e) {
			System.out.println(e.getMessage().toString());
			return 0;
		}
		
	}

	public void getStatusCurrentNodes()
	{
		try
		{
				DistributedNetwork instance = DistributedNetwork.getInstance();
				if (instance.isInstanceOnline())
				{
					if (instance.getListNetworkNodes().size() > 0)
					{
					   
						XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			            try {
			                config.setServerURL(new URL(instance.getThisMachineIpnPort().getURLForXMLRPC())); 
			            } catch (MalformedURLException e) {
			                e.printStackTrace();
			            }
			            XmlRpcClient client = new XmlRpcClient();
			            client.setConfig(config);

			            Object[] params = new Object[] {};
			            
			            Object[] lstAllListOfThatObject = (Object[]) client.execute("Node.getActiveNodeList", params); 
						
							System.out.println("----Node " + instance.getThisMachineIpnPort().getURLForXMLRPC() + " is going to Check all Machines in Our Network----");
							System.out.println();
							System.out.println(instance.getThisMachineIpnPort().getURLForXMLRPC() + " is Processing Request ----");
							System.out.println("------------------");
							
							for (int i = 0; i < lstAllListOfThatObject.length; i++)
							{
								System.out.println(lstAllListOfThatObject[i].toString());
							}
							System.out.println("------------------");

					}
					else
					{
						System.out.println(" Currently, there isn't any Node in the network");
					}
				}
				else
				{
					System.out.println("Node is not avalible in the network");
				}
		} 
		catch (RuntimeException ex)
		{
			System.out.println(ex.getMessage().toString());
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}

	}
	
	

	public boolean startsElectionProcess(int nodeId, ArrayList<Integer> lstNodes, int unknownId)
	{
		
		try
		{
			if (lstNodes.size() > 0)
			{ //lstNode MEANS WE HAVE SOME VALUES IN OUR LIST EXIT
				for (int i = 0; i < lstNodes.size(); i++)
				{
					if (lstNodes.get(i) > nodeId)
					{
						System.out.println("Node " + nodeId + " Sends \"ELECTION\" Message To Node " + lstNodes.get(i));
						System.out.println("Node " + lstNodes.get(i) + " Response back \"I AM ALIVE (OK) \" To Node " + nodeId);

					}
				} // FORLOOP ENDS HERE---
				System.out.println("Node " + nodeId + " Sends  \"ELECTION\" Message To  Node " + unknownId);
				System.out.println(" WAITING FOR THE RESPONSE----");
				System.out.println("-------------------------------------------------------------------------");
				return true;
			}
			else
			{
				System.out.println("Node " + nodeId + " Send  \"ELECTION \" Message TO  Node " + unknownId);
				System.out.println("---WAITING FOR RESPONSE---");
				System.out.println();
				System.out.println("Node " + nodeId + " is SELECTED AS A MASTER NODE");
				System.out.println();
				return false;
			}



		}
		catch (RuntimeException ex)
		{
			System.out.println(ex.getMessage().toString());
			return false;
		}

	}

	public void signOffNodeFromNetwork()
	{
		try
		{

		DistributedNetwork instance = DistributedNetwork.getInstance();
		if (instance.isInstanceOnline())
		{

			if (instance.getThisMachineIpnPort().getMasterNodeProcessId() == 1) // IT MEANS THAT CURRENT NODE IS MASTER NODE --
			{
				int removedMasterNodeId = instance.getThisMachineIpnPort().getprocessId(); // REMOVE MASTER NODE ID WHICH IS GOING TO REMOVE
				instance.setFirstActiveNode(new NetworkNode(instance.getThisMachineIpnPort().getIp(), instance.getThisMachineIpnPort().getPort()));
				int k = 0;
				while (k < instance.getListNetworkNodes().size() && !instance.getListNetworkNodes().get(k).getIPnPort().equals(instance.getThisMachineIpnPort().getIPnPort()))
				{
					k++;
				}
				if (k < instance.getListNetworkNodes().size())
				{
					NetworkNode obj = instance.getListNetworkNodes().get(k);
					instance.getListNetworkNodes().remove(obj);
				}

				System.out.println("New Master Node Selection Starts");
				//MASTER NODE IS REMOVED NOW WE CHECK THAT ANY MORE NODE LEFT IN OUR LIST THEN WE START SELECT NEW MASTER 
			   NetworkNode objFailureDetectionNode = instance.getListNetworkNodes().get(0); //NODE WHICH DETECTS FAILURE OF MASTER NODE
			   
			   if (objFailureDetectionNode != null) // IF FAULURENODE IS NOT NULL MEAN WE HAVE SOME VALUES IN OUR REMAINIG LIST
			   {
				   System.out.println("Node" + objFailureDetectionNode.getprocessId() + " detects the failure of Master Node Now it's Starts Bully Algortithm ");
				   lstProcessId = new ArrayList<Integer>(); // THIS LIST CONTAINS ID OF NODES WHICH HAVE HIGHER VALUES THEN CURRENT NODE VALUE
				   ArrayList<Integer> lstOriginal = new ArrayList<Integer>(); // ORIGINAL LIST OF INTERGER THAT CONTAINS ALL NODE VALUES INCLUDING CURRENT NODE VALUE
				   if (instance.getListNetworkNodes().size() > 0) // HERE WE CHECK THAT OUR REMAINING NODE LIST COTAINS MORE THEN ONE VALUE TO START BULLY ALGORITHM OR NOT ---
				   {
					   for (NetworkNode item : instance.getListNetworkNodes())
					   {
						   if (item.getprocessId() > objFailureDetectionNode.getprocessId())
						   {
							   lstProcessId.add(item.getprocessId());
						   }
					   } //FOREACH LOOP ENDS HERE

					   lstOriginal.add(objFailureDetectionNode.getprocessId());
					   lstOriginal.addAll(lstProcessId);

					   // NOW WE SELECT MASTER NODE 

					   for (int i = 0; i < lstOriginal.size(); i++)
					   {
						   boolean result = startsElectionProcess(lstOriginal.get(i), lstProcessId, removedMasterNodeId);
						   if (result)
						   {
							   lstProcessId.remove(lstOriginal.get(i + 1));
						   }
						   else // IN CASE OF FAILURE WE GET FALSE RESULT THEN IT MEANS CURRENT NODE IS OUR MASTER NODE BECAUSE IT DID NOT GET RESPONSE (TIME OUT)
						   { 
							   ArrayList<NetworkNode> temp = new ArrayList<NetworkNode>();
							   for(NetworkNode ne: instance.getListNetworkNodes()){
								   if(ne.getprocessId() == lstOriginal.get(i))
									   temp.add(ne);
							   }
							   

							   for (NetworkNode data : temp)
							   {
								   data.setMasterNodeProcessId(1); //Now MASTER NODE IS SET
								   processId = lstOriginal.get(i);
							   } //INNER FOREACH ENDS HERE---


						   } //ELSE CASE ENDS HERE---
					   } //FOR LOOP ENDS HERE

					   for (NetworkNode h : instance.getListNetworkNodes())
					   {
						   if (!h.equals(instance.getThisMachineIpnPort()))
						   {
							   XMLRPCUtility.disconnectNodeOverRPC(h.getURLForXMLRPC());
							   XMLRPCUtility.updateMasterNodeOverRPC(h.getURLForXMLRPC(), processId);
						   }
								// MEAN CURRENT NODE IS MASTER NODE THEN UPDATE ON XML-RPC 


					   }

					   instance.getListNetworkNodes().clear();
					   System.out.println("Node " + instance.getThisMachineIpnPort().getprocessId() + " has Left the Network ");
					   
					   instance.isInstanceOnline(false);


				   } // IF CONDTION CHECKED THAT ACTIVE NODE CONTAIN MORE THEN 1 ITEM THEN WE START BULLY ALGO OK MESSAGE PROCESS
				   else
				   {
					   System.out.println("Current Node is our Master Node because No more Node left in our Network");
					   instance.getThisMachineIpnPort().setMasterNodeProcessId(1);
				   }
			   }
			   else
			   {
				   System.out.println("NO MORE NODE REMAINS IN OUR NETWORK ----- !");

			   }


			} //IF CONDTION OF MASTER NODE DELETION NOT TRUE MEAN CURRENT NODE IS NOT MASTER NODE THEN ELSE PART EXECUTED
			else
			{
				for (NetworkNode h : instance.getListNetworkNodes())
				{
					if (!h.equals(instance.getThisMachineIpnPort()))
					{
						XMLRPCUtility.disconnectNodeOverRPC(h.getURLForXMLRPC());
					}
				}

				instance.getListNetworkNodes().clear();
				instance.getListNetworkNodes().add(instance.getThisMachineIpnPort()); // ADDED // WE NEED THIS BECAUSE WE MUST BE IN LIST FOR REJOINING
				instance.isInstanceOnline(false);
			}

		} //IF CONDITION OF NODE ONLINE ENDS HERE
		else
		{
			System.out.println("Client is already offline.");
		}

		} //try BLOCK ENDS HERE---->
		catch (RuntimeException ex)
		{
		System.out.println(ex.getMessage().toString());
		}
	}



	public void startBullyAlgorithm(){
		try{
			DistributedNetwork instance = DistributedNetwork.getInstance();
			synchronized (networkMutualLocking){
				if (instance.isInstanceOnline())
				{

					if (!instance.isInstanceStarted())
					{
						instance.setFirstActiveNode(new NetworkNode(instance.getThisMachineIpnPort().getIp(), instance.getThisMachineIpnPort().getPort()));
						
			            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			            try {
			                config.setServerURL(new URL(instance.getFirstActiveNode().getURLForXMLRPC())); 
			            } catch (MalformedURLException e) {
			                e.printStackTrace();
			            }
			            XmlRpcClient client = new XmlRpcClient();
			            client.setConfig(config);

			            Object[] params = new Object[] {};
			            
			            Object[] listOfNodes = (Object[]) client.execute("Node.getListOfActiveNode", params); 
//						
						int Id = 0;
//						
						
						String[] lstIDnPort = new String[listOfNodes.length];
						int index = 0;
						for (int i = 0; i < listOfNodes.length; i++)
						{
							NetworkNode newObj = new NetworkNode(listOfNodes[i].toString());
							for(NetworkNode node: instance.getListNetworkNodes()){
								if(node.getIPnPort().equals(newObj.getIPnPort())){
									node.setprocessId(++Id);
									//set the URL with XMLRPC along with the process id
									lstIDnPort[index] = node.getURLForXMLRPC() + "-" + node.getprocessId();
									index++;
									break;
								}
								
							}

						}
						//getting highest Id's in the list 
						NetworkNode highestNodeEntity = Collections.max(instance.getListNetworkNodes(), new NodeEntityComparator());
						int highestNodeId = highestNodeEntity.getprocessId();
						
						//the node with the highest process id will be the master node
						for(NetworkNode node: instance.getListNetworkNodes()){
							if(node.getprocessId() == highestNodeId){
								node.setMasterNodeProcessId(1);
								break;
							}
						}
						instance.setAssignId(true); 
						//Update the highest node Id over the whole network
						for (NetworkNode h : instance.getListNetworkNodes())
						{
							if (!h.equals(instance.getThisMachineIpnPort()))
								XMLRPCUtility.setProcessIdOverRPC(h.getURLForXMLRPC(), lstIDnPort, highestNodeId);
						}

					   //SHOW INFORMATION ABOUT MASTER NODE SELECTION ON CURRENT MACHINE


						for (NetworkNode item : instance.getListNetworkNodes())
						{
							 if (item.getMasterNodeProcessId() == 1)
								 System.out.println(item.getURLForXMLRPC() + " Currently ! is our Master Node which Node Id " + item.getprocessId());
							 else
								 System.out.println(item.getURLForXMLRPC().toString() + " Node " + String.valueOf(item.getprocessId()));
						}


						instance.isInstanceOnline(true);


					System.out.println("Please select one of the Mutual Exclusion Algorithms:");
					System.out.println("\"R\" for Ricart & Agrawala and \"C\" for Centralized Mutual Exclusion ");
					scanner = new Scanner(System.in);
					String userInput = scanner.nextLine();
				   // HERE WE START ONE OF OUR MUTUAL EXCLUSION ALGORTIHM 
					if (userInput != null){

						 if (userInput.equals("R"))
						 {
							System.out.println("---Richart Agarwala Starts -----");
							distributedMutualExclusionAlgorithm = 1001;
							for (NetworkNode h : instance.getListNetworkNodes()){
								if (!h.equals(instance.getThisMachineIpnPort()))
									XMLRPCUtility.setNStartAlgorithmOverRPC(h.getURLForXMLRPC(), 1001);
							}
						 }
						 else if (userInput.equals("C"))
						 {
							 System.out.println("Centralised Mutual Exclusion Starts Working");
							 distributedMutualExclusionAlgorithm = 1002;
							 for (NetworkNode h : instance.getListNetworkNodes())
							 {
								 if (!h.equals(instance.getThisMachineIpnPort()))
									 XMLRPCUtility.setNStartAlgorithmOverRPC(h.getURLForXMLRPC(), 1002);
							 }
						 }
						 instance.isInstanceStarted(true);
					}else{
						System.out.println("Sending messages already started.");
					}
				}
				else
					System.out.println("Start Process is in progress Please wait---.");
			} // IF CHECK NODE ISONLINE==TRUE ENDS HERE--
			else
				System.out.println("Node is offline.");
		}
	}catch (RuntimeException | XmlRpcException ex){
		System.out.println(ex.getClass());
	}
}

}

