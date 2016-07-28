package pds.app;
import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

/**
 * 
 * Thread for the server
 *
 */
public class RPCServer implements Runnable {

	private int port;
	private WebServer webServer;
	
	public RPCServer(int port)
	{
		this.port = port;
	}

	public void stopServer(){
		webServer.shutdown();
	}
	
    @Override
    public void run() {
        webServer = new WebServer(this.port); 

        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
        System.out.println("Server is started----");
        PropertyHandlerMapping phm = new PropertyHandlerMapping();

        try {
            phm.addHandler("Node", Node.class);
            phm.addHandler("RichartAgrawala", RichartAgrawala.class);
            phm.addHandler("CentralizedMutualExclusion", CentralizedMutualExclusion.class);
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }

        xmlRpcServer.setHandlerMapping(phm);
        XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);

        try {
            webServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
