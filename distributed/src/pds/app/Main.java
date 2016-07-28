package pds.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Main class that starts the program
 * 
 *
 */
public class Main {

    /*just a main function to initialize classes and reader thread*/
	public static void main(String[] args) throws IOException, UnsupportedEncodingException {
		
		System.out.println();
        System.out.println(" --Welcome to Final Demo from Group # 24-- JAVA Implementation ");
        System.out.println();
        System.out.println(" C# Group Members: Mubashir Mehmood Qureshi & Imuwahen Osazuwa");
        System.out.println(" Java Group Members: Hemaid Ahmed  & Marmol Miguel");
        System.out.println();
        System.out.println("Let's begin the Fun");
        System.out.println("Enter port number (four digits ) to run program on this pc:");
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        int port = Integer.parseInt(bufferedReader.readLine());
        RPCClient client=new RPCClient(port); //initialize client object
        RPCServer server=new RPCServer(port); // initialize server object
        // start reader thread and pass two objects to it
        Thread readerThread=new Thread(new DistributedNetworkHandler(client, server));
        readerThread.start();
    }
}
