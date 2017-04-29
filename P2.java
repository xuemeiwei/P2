import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class P2 {
	static String hostName;
	static String hostAddress;
	static String port;
	static int hostNumber;
	static Set<String> allHosts = new HashSet<>();
	
	@SuppressWarnings("static-access")
	P2(String hostName, String hostAddress, String port) {
		this.hostName = hostName;
		this.hostAddress = hostAddress;
		this.port = port;
	}
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		hostName = args[0];
	    Scanner in = new Scanner(System.in);
		/*----Find available port and display it on the screen*/
	    ServerSocket serverSocket =  new ServerSocket(0); 
	    
	    InetAddress addr = InetAddress.getLocalHost();
	    hostAddress = String.valueOf(addr.getHostAddress());
		port = String.valueOf(serverSocket.getLocalPort());
		System.out.println(hostAddress + " at port number: " + port);
		
		System.out.println("Please add this host and port on another machine using:\nadd (" 
		+ hostName + " ," + hostAddress + ", " + port +")");
		
		allHosts.add(hostName + " " + hostAddress + " " +  port);
		
		/*************Create the directories for nets and tuples*****************/
		String dirPath = "/tmp/xwei1/linda/" + hostName;
		String netsPath = dirPath+ "/nets.txt";
		String tuplesPath = dirPath+ "/tuples.txt";
		String backupPath = dirPath+ "/backup.txt";
		
		File file = new File(dirPath);
		if(file.exists()) {
			System.out.println("It's an old Machine");
	    	ArrayList<String> hosts = Restart.getNetsFileFromBackup(netsPath, hostName, hostAddress, port);
	    	hostNumber = hosts.size();
	    	int hostId = Restart.getHostId(hosts, hostName);
	    	int backupId = Utils.getBackupId(hostId, hostNumber);
	    	String[] backupHostInfo = hosts.get(backupId).split(" ");
			Utils.updateTuplesFile(tuplesPath, backupHostInfo[1], backupHostInfo[2]);
			
			int backHostId = Utils.getBackHostId(hostId, hostNumber);
			String[] backHostInfo = hosts.get(backHostId).split(" ");
			Utils.updateBackupFile(backupPath, backHostInfo[1], backHostInfo[2]);
			
			String netsFile = Restart.getNetsfileFromHosts(hosts);
			Utils.updateAllNetsFile(hosts, netsFile);
			
		}else{
			System.out.println("It's a new Machine");
			file.mkdirs();
	 	    file = new File(netsPath);
	 	    file.createNewFile();
	 	    file = new File(tuplesPath);
	 	    file.createNewFile();
		    file = new File(backupPath);
		    file.createNewFile();
		}
		
	    /****************Start Server******************/
	    try{
	    	Thread t = new Server(serverSocket, hostName, hostAddress, port, netsPath, tuplesPath, backupPath);
  	         t.start();
  	    }catch(IOException e){
  	         e.printStackTrace();
  	    }
	    
	    System.out.print("linda>");
	    /**************************************************/
	    
	    String command = in.nextLine();
	    command.trim();
	 
	    while(true) {
	    	
	    	if(!ErrorHandle.checkError(command)) {
	    		System.out.println("Error command. Please input again.");
	    		System.out.print("linda>");
	    		command = in.nextLine();
			    command.trim();
	    		continue;
	    	}
	    	
	    	if(command.startsWith("add")) {
	    		MainFunctions.add(command, netsPath, hostName, hostAddress, port);
		    }
	    	
	    	if(command.startsWith("delete")) {
		    	MainFunctions.delete(command, netsPath, hostName, hostAddress, port);
		    }
	    	
	    	if(command.startsWith("out")) {
		    	MainFunctions.out(command, netsPath, hostName, hostAddress, port);
		    }
	    	
	    	if(command.startsWith("in")) {
		    	MainFunctions.in(command, netsPath, hostName, hostAddress, port);
		    }
	    	if(command.startsWith("rd")) {
		    	MainFunctions.rd(command, netsPath, hostName, hostAddress, port);
		    }
	    	System.out.print("linda>");
		    command = in.nextLine();
		    command.trim();
	    }
	    
	}
	
}
