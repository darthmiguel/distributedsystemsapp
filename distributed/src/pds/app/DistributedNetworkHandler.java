package pds.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DistributedNetworkHandler implements Runnable{
	
	public RPCClient client;
	public RPCServer server;
	public Thread clientThread;
	public Thread serverThread;
	
	public DistributedNetworkHandler(RPCClient client, RPCServer server){
		this.client=client;
		this.server=server;
		serverThread = new Thread(server);
		serverThread.start();
		clientThread = new Thread(client);
		clientThread.start();
	}
	
	
	/*function to validate input, not finished*/
	public int validateInput(String line){
		String[] s = line.split(" ");
		if (s[0].equals("Join")){
			return 0;
		}
		else if(s[0].equals("Signoff")){
			return 1;
		}
		else if(s[0].equals("StartBully")){
			return 2;
		}
		if (s[0].equals("NodeList"))
        {
            return 3;
        }
        if (s[0].equals("Exit"))
        {
            return 4;
        }
        return -1;
		
	}	
		
	/*method executed by Reader thread*/
	public void run() {
		
		
		
		System.out.println(" Please enter your choice by typing one of the commands below: ");
		System.out.println("      Join                                                ");
		System.out.println("      Signoff                                                 ");
		System.out.println("      StartBully                                              ");
		System.out.println("      NodeList                            ");
		System.out.println("      Exit                                                 ");
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		
		String input="";
		
		while (true){	// in infinite loop wait for any input user may make	
			try {
				input = bufferedReader.readLine(); // read line which user enters
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (input!=null){
				System.out.println("Option chosen: " + input);
				int val=validateInput(input); // validate string which user entered
				DistributedNetwork instance = null;
				switch(val){
				case 0: // if user entered join then send the "Join" command and start the client thread (sending calculation messages)
					instance = DistributedNetwork.getInstance();
					System.out.println("Joining Process Starts  -- ");
				    instance.joinNodeToNetwork();
					break;
				case 1: // if user wrote "Signoff" then stop all the threads and leave
					instance = DistributedNetwork.getInstance();
					System.out.println("Attempting to sign off ... ");
					instance.signOffNodeFromNetwork();
					
					break;
				case 2: // User entered "Start"
					instance = DistributedNetwork.getInstance();
					instance.startBullyAlgorithm();
					break;
				case 3:
					instance = DistributedNetwork.getInstance();
					instance.getStatusCurrentNodes();
					break;
				case 4:
					instance = DistributedNetwork.getInstance();
					System.out.println("Exiting. Goodbye.");
					break;
				default:
					System.out.println("Wrong input");
					break;
				}
			}
		}
		
		
		
	}
}