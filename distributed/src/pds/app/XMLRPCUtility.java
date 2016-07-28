package pds.app;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Utility class with XML-RPC methods
 * 
 *
 */
public class XMLRPCUtility {
	
	public static void joinNodeOverRPC(String fullURL)
	{
		
		DistributedNetwork instance = DistributedNetwork.getInstance();
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try 
        {
        	config.setServerURL(new URL(fullURL));
        	
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            Object[] params = new Object[]{instance.getThisMachineIpnPort().getIPnPort()};
            client.execute("Node.add", params);
        } catch (XmlRpcException e) 
        {
            System.out.println("XML RPC threw an exception." + e.getMessage()); 
        } catch (MalformedURLException e) {
        	System.out.println("The url was wrong."); 
        }
	}
	
	public static void disconnectNodeOverRPC(String fullURL)
	{
		DistributedNetwork instance = DistributedNetwork.getInstance();
		System.out.println("New node has signed off the network.");
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try 
        {
        	config.setServerURL(new URL(fullURL));
        	
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            Object[] params = new Object[]{instance.getThisMachineIpnPort().getIPnPort()};
            client.execute("Node.delete", params);
        } catch (XmlRpcException e) 
        {
            System.out.println("XML RPC threw an exception." + e.getMessage()); 
        } catch (MalformedURLException e) {
        	System.out.println("The url was wrong."); 
        }
	}
	
	/**
	 * Method that calls Node.setProcessId XML-RPC method to tell the whole network their process Id, and who the master is
	 * @param fullURL URL of the node that will be notified about the 
	 * @param lstIDIpnPort list of all nodes in the network
	 * @param highestNodeId node with the highest id process
	 */
	public static void setProcessIdOverRPC(String fullURL, String[] lstIDIpnPort, int highestNodeId)
	{
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try 
        {
        	config.setServerURL(new URL(fullURL));
        	
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            Object[] params = new Object[]{lstIDIpnPort, highestNodeId};
            client.execute("Node.setProcessId", params);
        } catch (XmlRpcException e) 
        {
            System.out.println("XML RPC threw an exception." + e.getMessage()); 
        } catch (MalformedURLException e) {
        	System.out.println("The url was wrong."); 
        }
	}
	

	public static void updateMasterNodeOverRPC(String fullURL, int processId)
	{
//		Utility instance = Utility.getInstance();
		System.out.println("New node has signed off the network.");
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try 
        {
        	config.setServerURL(new URL(fullURL));
        	
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            Object[] params = new Object[]{processId};
            client.execute("Node.updateMasterNode", params);
        } catch (XmlRpcException e) 
        {
            System.out.println("XML RPC threw an exception." + e.getMessage()); 
        } catch (MalformedURLException e) {
        	System.out.println("The url was wrong."); 
        }
	}
	
	/**
	 * Method to call the XML-RPC method Node.startAlogrithmbyValue to tell the other nodes which algorithm we are using
	 * @param fullURL URL of the node
	 * @param algorithm number
	 */
	public static void setNStartAlgorithmOverRPC(String fullURL, int algorithm)
	{
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try 
        {
        	config.setServerURL(new URL(fullURL));
        	
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            Object[] params = new Object[]{algorithm};
            client.execute("Node.startAlogrithmbyValue", params);
        } catch (XmlRpcException e) 
        {
            System.out.println("XML RPC threw an exception." + e.getMessage()); 
        } catch (MalformedURLException e) {
        	System.out.println("The url was wrong."); 
        }
	}
	
	/**
	 * Method that calls the XML-RPC method RichartAgrawala.receiveOk
	 * @param host
	 * @param clockValue
	 */
	public static void sendOKOverRPC(NetworkNode host, int clockValue)
	{
		
		DistributedNetwork network = DistributedNetwork.getInstance();
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try 
        {
        	config.setServerURL(new URL(host.getURLForXMLRPC()));
        	
            XmlRpcClient xmlRPCclient = new XmlRpcClient();
            xmlRPCclient.setConfig(config);
            Object[] params = new Object[]{network.getThisMachineIpnPort().getIp(), network.getThisMachineIpnPort().getPort()};
            xmlRPCclient.execute("RichartAgrawala.receiveOk", params);
        } catch (XmlRpcException e) 
        {
            System.out.println("XML RPC threw an exception." + e.getMessage()); 
        } catch (MalformedURLException e) {
        	System.out.println("The url was wrong."); 
        }
	}

	/**
	 * Send a request over to the other nodes using XML-RPC method RichartAgrawala.receiveRequest
	 * @param host URL XML-RPC of the host
	 * @param valueLamportClock current value of the sender clock
	 */
	public static void sendRequestOverRPC(NetworkNode host, int valueLamportClock)
	{
		DistributedNetwork network = DistributedNetwork.getInstance();
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try 
        {
        	config.setServerURL(new URL(host.getURLForXMLRPC()));
        	
            XmlRpcClient xmlRPCclient = new XmlRpcClient();
            xmlRPCclient.setConfig(config);
            Object[] params = new Object[]{valueLamportClock, network.getThisMachineIpnPort().getIp(), network.getThisMachineIpnPort().getPort()};
            xmlRPCclient.execute("RichartAgrawala.receiveRequest", params);
        } catch (XmlRpcException e) 
        {
            System.out.println("XML RPC threw an exception." + e.getMessage()); 
        } catch (MalformedURLException e) {
        	System.out.println("The url was wrong."); 
        }
	}

}
