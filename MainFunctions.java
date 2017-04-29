import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class MainFunctions {
	/********************************The command is add********************************************/
	public static void add(String command, String netsPath, String hostName, String hostAddress, String port) throws IOException {
		Set<String> allHosts = Utils.getAllHostInfoIntoSet(netsPath);
		allHosts.add(hostName + " " + hostAddress + " " + port);
		String[] newHostInfo = Utils.getAddHostsInfo(command);//stored as {[h1, a1, p1], [h2, a2, p2]}
		Client client = new Client();
		
		for(int i = 0; i < newHostInfo.length; ++i) {
			String[] currentHostInfo = newHostInfo[i].split(" ");
			String netsFile = client.getNetsfile(currentHostInfo[1], currentHostInfo[2]);
			String[] netsFileArr = netsFile.split("\n");
			for(String item: netsFileArr) {
				if(!item.equals("\n") && item != null) {
					allHosts.add(item);
				}
			}
		}
		//Get the nets file and tuples from all the hosts
		int hostNumber = allHosts.size();
		ArrayList<String> hostsArray = new ArrayList<>();
		String[] dataTobeAllocated = Utils.getDataTobeAllocated(allHosts, hostsArray, hostNumber);
		String netsFile = Utils.getAllNetsFile(hostsArray);
		Utils.updateAllNetsFile(hostsArray, netsFile);
		for(int i = 0; i < hostNumber; ++i) {
    		String[] ori = hostsArray.get(i).split(" ");
			client.setTuples(ori[1], ori[2], dataTobeAllocated[i]);
    		int backupId = Utils.getBackupId(i, hostNumber);
    		String[] backup = hostsArray.get(backupId).split(" ");
    		client.setBackup(backup[1], backup[2], dataTobeAllocated[i]);
    	}
	}
	/********************************The command is delete********************************************/
	public static void delete(String command, String netsPath, String hostName, String hostAddress, String port) throws IOException {
		String[] hostsToBeDeleted =  Utils.getDeleteHostsInfo(command);
    	Set<String> allHosts = Utils.getAllHostInfoIntoSet(netsPath);
    	int hostNumber = allHosts.size();
    	
    	if(hostNumber == 1) {
    		System.out.println("Only one host remains and it can't be deleted");
    	}else{
    		int newHostNumber = hostNumber - hostsToBeDeleted.length;
	    	System.out.println("newHostNumber: " + newHostNumber);
    		
	    	ArrayList<String> hostsArray = new ArrayList<>();
			String[] dataTobeAllocated = Utils.getDataTobeAllocated(allHosts, hostsArray, newHostNumber);
			
			Client client = new Client();
	    	String remainNetsFile = "";
	    	ArrayList<String> remainHosts = new ArrayList<>();
	    	for(String item: allHosts) {
	    		String[] hostInfo = item.split(" ");
	    		int i = 0;
	    		for(i = 0; i < hostsToBeDeleted.length; ++i) {
	    			if(hostInfo[0].equals(hostsToBeDeleted[i])) {
	    				break;
	    			}
	    		}
	    		if(i == hostsToBeDeleted.length) {
	    			remainNetsFile += item + "\n";
	    			remainHosts.add(item);
	    		}else{
	    			client.deleteFile(hostInfo[1], hostInfo[2]);
	    		}
	    	}
	    	hostNumber = newHostNumber;
	    	for(int i = 0; i < newHostNumber; ++i) {
	    		String[] ori = remainHosts.get(i).split(" ");
	    		client.add(ori[1], ori[2], remainNetsFile);
	    		client.setTuples(ori[1], ori[2], dataTobeAllocated[i]);
	    		int backupId = Utils.getBackupId(i, hostNumber);
	    		String[] backup = remainHosts.get(backupId).split(" ");
	    		client.setBackup(backup[1], backup[2], dataTobeAllocated[i]);
	    	}
    	}
	}
	/********************************The command is out********************************************/
	public static void out(String command, String netsPath, String hostName, String hostAddress, String port) throws IOException {
		int hostNumber = Utils.getHostNumber(netsPath);
    	ArrayList<String> hosts = Utils.getAllHostInfoIntoList(netsPath);
    	String strToOut = Utils.preprocess(command);//get the string to store
    	
    	int hostId = Hash.md5(strToOut, hostNumber);
    	String[] targetInfo = hosts.get(hostId).split(" ");
		int backupId = Utils.getBackupId(hostId, hostNumber);
		String[] backupHostInfo = hosts.get(backupId).split(" ");
		
		Client client = new Client();
		if(Utils.checkServerStatus(targetInfo[1], targetInfo[2])) {
			client.out(targetInfo[1], targetInfo[2], strToOut);
			if(Utils.checkServerStatus(backupHostInfo[1], backupHostInfo[2])) {
				client.outBackup(backupHostInfo[1], backupHostInfo[2], strToOut);
			}
		}else{
			System.out.println("connection to original failed and try to connect to backup host");
			client.outBackup(backupHostInfo[1], backupHostInfo[2], strToOut);
		}
	}
	/********************************The command is in********************************************/
	public static void in(String command, String netsPath, String hostName, String hostAddress, String port) throws IOException, InterruptedException{
		int hostNumber = Utils.getHostNumber(netsPath);
    	ArrayList<String> hosts = Utils.getAllHostInfoIntoList(netsPath);
    	String strToIn = Utils.preprocess(command);//get the string to delete
    	
    	if(!strToIn.contains("?")) {//If exact match
    		//Get the host to store the tuple by hashing and send the "out tuple" request to corresponding host
    		int hostId = Hash.md5(strToIn, hostNumber);
	    	String[] targetInfo = hosts.get(hostId).split(" ");
	    	int backupId = Utils.getBackupId(hostId, hostNumber);
    		String[] backupHostInfo = hosts.get(backupId).split(" ");
    		
    		Client client = new Client();
    		if(Utils.checkServerStatus(targetInfo[1], targetInfo[2])) {
    			while(!client.rdo(targetInfo[1], targetInfo[2], strToIn)) {
    				Thread.sleep(1000);
	    		}
    			client.ino(targetInfo[1], targetInfo[2], strToIn);
    			if(Utils.checkServerStatus(backupHostInfo[1], backupHostInfo[2])) {
    				client.inBackup(backupHostInfo[1], backupHostInfo[2], strToIn);
    			}
    		}else{
    			while(!client.rdu(backupHostInfo[1], backupHostInfo[2], strToIn)) {
    				Thread.sleep(1000);
	    		}
    			System.out.println("connection to original failed and try to connect to backup host");
    			client.inBackup(backupHostInfo[1], backupHostInfo[2], strToIn);
    		}
    	}else{
    		/* Create n threads to broadcast the message to all the hosts. 
    		 * If not found, current host will block, waiting for available tuple at all the hosts.
    		 * If found, random host will be chosen and send back the host machine to current host
    		 * Then current machine will delete the tuple
    		 */
    		Thread[] broadcastThread = new BroadcastThread[hostNumber];
    		SharedInfo sharedInfo = new SharedInfo();
    		Utils.broadCast(netsPath, hostNumber, broadcastThread, sharedInfo, strToIn);
    		
			Client client = new Client();
			System.out.println("The tuple to be deleted is: [" + sharedInfo.tuples + "]");
			if(sharedInfo.flag.equals("original")) {
				System.out.println("The tuple will be removed from: " + sharedInfo.hostAddress + ". Port number is: " + sharedInfo.port);
				client.ino(sharedInfo.hostAddress, sharedInfo.port, sharedInfo.tuples);
			}else{
				System.out.println("The tuple will be removed from backup: " + sharedInfo.hostAddress + ". Port number is: " + sharedInfo.port);
				client.inBackup(sharedInfo.hostAddress, sharedInfo.port, sharedInfo.tuples);
			}
    	}
	}
	/********************************The command is rd********************************************/
	public static void rd(String command, String netsPath, String hostName, String hostAddress, String port) throws IOException, InterruptedException {
		int hostNumber = Utils.getHostNumber(netsPath);
    	ArrayList<String> hosts = Utils.getAllHostInfoIntoList(netsPath);
    	String strToRd = Utils.preprocess(command);//get the string to read
    	
    	if(!strToRd.contains("?")) {
    		//Get the host to store the tuple by hashing and send the "out tuple" request to corresponding host
    		int hostId = Hash.md5(strToRd, hostNumber);
	    	String[] targetInfo = hosts.get(hostId).split(" ");
    		int backupId = Utils.getBackupId(hostId, hostNumber);
    		String[] backupHostInfo = hosts.get(backupId).split(" ");
    		
    		Client client = new Client();
    		if(Utils.checkServerStatus(targetInfo[1], targetInfo[2])) {
    			while(!client.rdo(targetInfo[1], targetInfo[2], strToRd)) {
	    			Thread.sleep(1000);
	    		}
    			System.out.println("tuple found on " + targetInfo[0]);;
    		}else{
    			System.out.println("connection to original failed and try to connect to backup host");
    			while(!client.rdu(backupHostInfo[1], backupHostInfo[2], strToRd)) {
    				Thread.sleep(1000);
	    		}
    			System.out.println("tuple found on backup machine " + backupHostInfo[0]);;
    		}
    	}else{
    		Thread[] broadcastThread = new BroadcastThread[hostNumber];
    		SharedInfo sharedInfo = new SharedInfo();
    		Utils.broadCast(netsPath, hostNumber, broadcastThread, sharedInfo, strToRd);
    		if(sharedInfo.flag.equals("original")) {
				System.out.println("The tuple will be read from: " + sharedInfo.hostAddress + ". Port number is: " + sharedInfo.port);
			}else{
				System.out.println("The tuple will be read from backup: " + sharedInfo.hostAddress + ". Port number is: " + sharedInfo.port);
			}
    	}
	}
}
