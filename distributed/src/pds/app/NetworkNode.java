package pds.app;

/**
 * Class that represents a Node
 * 
 *
 */
public class NetworkNode
{
	private String ip;
	private int port;
	private int processId;
	private int masterNodeProcessId;
	public void setMasterNodeProcessId(int val)
	{
		this.masterNodeProcessId = val;
	}

	public int getMasterNodeProcessId()
	{
		return masterNodeProcessId;
	}

	public String getIp()
	{
		return ip;
	}
	public void setIp(String _ip)
	{
		ip = _ip;
	}
	public int getPort()
	{
		return port;
	}
	public void setPort(int port)
	{
		this.port = port;
	}
	public void setprocessId(int pId)
	{
		this.processId = pId;
	}
	public int getprocessId()
	{
		return processId;
	}

	public NetworkNode(String _ip, int port)
    {
        ip = _ip;
        this.port = port;
    }
    public NetworkNode(String IpnPort)
    {
        String[] obj = IpnPort.split(":");
        ip = obj[0];
        port = Integer.parseInt(obj[1]);
    }
    public NetworkNode(String IpnPort, int pId, int k)
    {
        String[] obj = IpnPort.split(":");
        ip = obj[0];
        port = Integer.parseInt(obj[1]);
        this.processId = pId;

    }

	public String getIPnPort()
	{
		return ip + ":" + this.port;
	}
	
	public String getURLForXMLRPC()
	{
		return "http://" + ip + ":" + port + "/xmlrpc";
	}
	
	
	public long getHostId()
	{
		String[] parts = ip.split("[.]", -1);
		String ID = "";
		for (int i = 0; i < parts.length; i++)
		{
			ID += parts[i];
		}
		ID += port;
		return Long.parseLong(ID);
	}
	public int compare(NetworkNode obj2)
	{
		if (this.getHostId() < obj2.getHostId())
		{
			return -1;
		}
		if (this.getHostId() == obj2.getHostId())
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}
}