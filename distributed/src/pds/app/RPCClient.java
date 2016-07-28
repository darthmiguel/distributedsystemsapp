package pds.app;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Thread containing the methods used by the client node
 *
 */
public class RPCClient implements Runnable
{
	//local port where the client is running
	private int clientPortNumber;

	/**
	 * Constructor that receives the port number in the local machine
	 * and calls the initClient method
	 * @param port Port number
	 */
	public RPCClient(int port)
	{
		clientPortNumber = port;
		initClient();

	}

	public void stopClient()
	{

		DistributedNetwork instance = DistributedNetwork.getInstance(); //get the instance
		instance.isInstanceRunning(false); //set instance property to false
	}


	private void initClient()
	{
		
		String ownIpAddress=null;
		try {
			ownIpAddress = InetAddress.getLocalHost().getHostAddress();
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}

		DistributedNetwork instance = DistributedNetwork.getInstance();
		instance.setThisMachineIpnPort(new NetworkNode(ownIpAddress, clientPortNumber));
		System.out.println("Local IP address: " + instance.getThisMachineIpnPort().getIPnPort());
		instance.getListNetworkNodes().add(instance.getThisMachineIpnPort());
	}


	public void run()
	{
		try{
		DistributedNetwork instance = DistributedNetwork.getInstance();
		System.out.println("Client " + instance.getThisMachineIpnPort().getIPnPort() + " is running");
		long startTime = 0; // starting time
		long lapsedTime = 0; // lapsed time since the process started
		int numberOfInstances = 0; // number of instances used by the client 
		while (instance.isInstanceRunning())
		{
			if (instance.isInstanceOnline())
			{
//				start command was typed or received from other node
				if (instance.isInstanceStarted())
				{

					if (lapsedTime == 0)
						startTime = new Date().getTime();
					
					System.out.println("Loop Start to Send Message..");
					//wait a random amount of time
					int maxNumber = 3500;
					int minNumber = 0;
					Random random = new Random();
					int randomNumber = random.nextInt((maxNumber - minNumber) + 1) + minNumber;
					Long randomNumberLong = Long.parseLong(Integer.toString(randomNumber));
					Thread.sleep(randomNumberLong.longValue());
					
					if (instance.getDistributedMEA() == 1001) // Ricart & Agrawala
					{
						RichartAgrawala agrawala = DistributedNetwork.getRicartAgrawala();
						if (agrawala.sendRequest())
						{
							System.out.println(instance.getThisMachineIpnPort().getURLForXMLRPC() + " sends request to access the resource");
							String masterNodeUrl = "";
							for(NetworkNode node : instance.getListNetworkNodes()){
								if(node.getMasterNodeProcessId() == 1){
									masterNodeUrl = node.getURLForXMLRPC();
									break;
								}
							}
							
						   if (!masterNodeUrl.equals("")){
							   XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
						        try 
						        {
						        	config.setServerURL(new URL(masterNodeUrl));
						        	
						            XmlRpcClient client = new XmlRpcClient();
						            client.setConfig(config);
						            Object[] params = new Object[]{};
						            instance.setMasterString((String)client.execute("CentralizedMutualExclusion.getMasterString", params));
						            System.out.println("Previous master string: " + instance.getMasterString());
						            params = new Object[]{instance.getThisMachineIpnPort().getURLForXMLRPC(), instance.getMasterString() + "-R&A-" + instance.getThisMachineIpnPort().getprocessId() + "-" + (++numberOfInstances) + "-"};
						            client.execute("CentralizedMutualExclusion.appendValuesToMasterString", params);
						        } catch (XmlRpcException e) 
						        {
						            System.out.println("XML RPC threw an exception." + e.getMessage()); 
						        } catch (MalformedURLException e) {
						        	System.out.println("The url was wrong."); 
						        }
							  
						   }

						}
						agrawala.releaseResource();
					}

					 if (instance.getDistributedMEA() == 1002) //Centralized Mutual Exclusion
					 {
//						
						String masterNodeUrl = "";
						
						for(NetworkNode node: instance.getListNetworkNodes()){
							if(node.getMasterNodeProcessId()==1){
								masterNodeUrl = node.getURLForXMLRPC();
								break;
							}
								
						}
						if (!masterNodeUrl.equals("")){
						   System.out.println("Before requesting master node");
//						   
						 
						   instance.setIsWaiting(true);
						   Object[] returnedString = null;
						   XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
					        try 
					        {
					        	config.setServerURL(new URL(masterNodeUrl));
					        	
					            XmlRpcClient client = new XmlRpcClient();
					            client.setConfig(config);
					            Object[] params = new Object[]{instance.getThisMachineIpnPort().getURLForXMLRPC()};
					            returnedString = (Object[]) client.execute("CentralizedMutualExclusion.requestToMasterString", params);
					        } catch (XmlRpcException e) 
					        {
					            System.out.println("XML RPC threw an exception." + e.getMessage()); 
					        } catch (MalformedURLException e) {
					        	System.out.println("The url was wrong."); 
					        }
//						   
						   
						   if (returnedString[0].equals("NotFree"))
						   {
							   System.out.println("Master node is busy, node " + instance.getThisMachineIpnPort().getIPnPort() + " starts waiting");
							   while (instance.isInstanceWaiting())
							   {
								   ;
							   }
						   }
						   else if (returnedString[0].equals("Free"))
						   {
							   instance.setMasterString((String)returnedString[1]);

						   }
						   System.out.println("Old masterString: " + instance.getMasterString());
						   
						   config = new XmlRpcClientConfigImpl();
						   String appendedRepliedString = null;
					        try 
					        {
					        	config.setServerURL(new URL(masterNodeUrl));
					        	
					            XmlRpcClient client = new XmlRpcClient();
					            client.setConfig(config);
					            Object[] params = new Object[]{instance.getThisMachineIpnPort().getURLForXMLRPC(), instance.getMasterString() + "-CME-" + instance.getThisMachineIpnPort().getprocessId() + "-"  + (++numberOfInstances) + "-"};
					            appendedRepliedString = (String) client.execute("CentralizedMutualExclusion.appendValuesToMasterString", params);
					        } catch (XmlRpcException e) 
					        {
					            System.out.println("XML RPC threw an exception." + e.getMessage()); 
					        } catch (MalformedURLException e) {
					        	System.out.println("The url was wrong."); 
					        }
						   
						   System.out.println("New master string after appending: " + appendedRepliedString);

							}

					 }

//					
					lapsedTime = new Date().getTime() - startTime;
					 //if the sending messages lasts more than 20s
					if (lapsedTime >= 20000)
					{
						System.out.println("20 seconds of sending messages passed");
						instance.setIsWaiting(true);
//						
						String masterNodeUrl = "";
						for(NetworkNode node: instance.getListNetworkNodes()){
							if(node.getMasterNodeProcessId() == 1){
								masterNodeUrl = node.getURLForXMLRPC();
								break;
							}
						}
						
						XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
				        try 
				        {
				        	config.setServerURL(new URL(masterNodeUrl));
				        	
				            XmlRpcClient client = new XmlRpcClient();
				            client.setConfig(config);
				            Object[] params = new Object[]{instance.getThisMachineIpnPort().getURLForXMLRPC()};
				            client.execute("CentralizedMutualExclusion.finishNotification", params);
				        } catch (Exception e) 
				        {
				        	e.printStackTrace();
				            System.out.println("XML RPC exception: " + e.getMessage()); 
				        }
				        
						
						System.out.println("Waiting for the nodes to stop their requests...");
						while (instance.isInstanceWaiting())
                        {
                            ;
                        }
						System.out.println("FINAL MASTER STRING: " + instance.getMasterString());
						System.out.println("This instance with process Id: " + instance.getThisMachineIpnPort().getprocessId() + " has appended the master string in " + numberOfInstances + " occasions");
						instance.isInstanceStarted(false);
						startTime = 0;
						lapsedTime = 0;
						System.out.println("The process has finished!");
						System.exit(0);
						

					}
				}
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
