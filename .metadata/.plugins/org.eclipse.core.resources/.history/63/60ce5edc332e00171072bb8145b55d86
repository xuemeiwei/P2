import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Utils {
	//Update the netsfile of all hosts
	public static void updateAllNetsFile(ArrayList<String> hosts, String netsFile) {
		Client client = new Client();
		for(String host: hosts) {
			String[] tmp = host.split(" ");
			client.add(tmp[1], tmp[2], netsFile);
		}
	}
	//Get all the hosts information into a set
	public static Set<String> getAllHostInfoIntoSet(String netsPath) throws IOException {
		Set<String> hosts = new HashSet<>();
		FileReader fr = new FileReader(netsPath);
    	BufferedReader br = new BufferedReader(fr);
    	String tmp = null;
		while((tmp = br.readLine()) != null) {
			if(tmp != "\n" && tmp != null) {
				hosts.add(tmp);
			}
		}
		br.close();
		return hosts;
	}
	//Get all the hosts information into a list
	public static ArrayList<String> getAllHostInfoIntoList(String netsPath) throws IOException {
		FileReader fr = new FileReader(netsPath);
    	BufferedReader br = new BufferedReader(fr);
    	ArrayList<String> hosts = new ArrayList<>();
    	String tmp = null;
    	while( (tmp = br.readLine()) != null) {
    		if(!tmp.equals("\n") && tmp != null) {
    			hosts.add(tmp);
    		}
    	}
		br.close();
		return hosts;
	}
	//Get the number of all hosts from netsfile
	public static int getHostNumber(String netsPath) throws IOException {
		FileReader fr = new FileReader(netsPath);
		BufferedReader br = new BufferedReader(fr);
    	String tmp = null;
    	int cnt = 0;
    	while((tmp = br.readLine()) != null) {
    		if(!tmp.equals("\n")) {
    			cnt++;
    		}
    	}
    	br.close();
    	return cnt;
	}
	//Get the backup host id from original host id
	public static int getBackHostId(int hostId, int hostNumber) {
		if(hostNumber == 1) {
			return hostId;
		}else if(hostNumber == 2) {
			return 1 - hostId;
		}else{
			return (hostId - 2 + hostNumber) % hostNumber;
		}
	}
	//Get the original id from backup id
	public static int getBackupId(int hostId, int hostNumber) {
		if(hostNumber == 1) {
			return hostId;
		}else if(hostNumber == 2){
			return 1 - hostId;
		}else{
			return (hostId + 2) % hostNumber;
		}
	}
	
	//Update the original tuples from backup host
	public static void updateTuplesFile(String tuplesPath, String otherHostAddress, String otherHostPort) throws IOException {
		Client client = new Client();
		String netsFile = client.getBackupFile(otherHostAddress, otherHostPort);
		FileWriter fw = new FileWriter(tuplesPath);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(netsFile);
		bw.close();
	}
	
	//Update the backup tuples from original host
	public static void updateBackupFile(String backupPath, String otherHostAddress, String otherHostPort)  throws IOException{
		Client client = new Client();
		String netsFile = client.getTuplesFile(otherHostAddress, otherHostPort);
		FileWriter fw = new FileWriter(backupPath);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(netsFile);
		bw.close();
	}
	
	//Get the add hosts information from command
	public static String[] getAddHostsInfo(String command) {
		String[] tmp = command.split("\\(");
		String[] newHostsInfo = new String[tmp.length - 1];
		for(int i = 1; i < tmp.length; ++i) {
			String eachHost = tmp[i].trim();
			eachHost = eachHost.substring(0, eachHost.length() - 1);
			String[] info = eachHost.split(",");
			newHostsInfo[i - 1] = info[0].trim() + " " + info[1].trim() + " " + info[2].trim();
		}
		return newHostsInfo;
	}
	
	//Get the delete hosts information from command
	public static String[] getDeleteHostsInfo(String command) {
		int start = command.indexOf('(');
    	int end = command.indexOf(')');
    	command = command.substring(start + 1, end).trim();
		String[] tmp = command.split(",");
		String[] deleteHostsInfo = new String[tmp.length];
		for(int i = 0; i < tmp.length; ++i) {
			deleteHostsInfo[i] = tmp[i].trim();
		}
		return deleteHostsInfo;
	}
	
	//Get all the data to be allocated from all the hosts
	public static String[] getDataTobeAllocated(Set<String> hosts, ArrayList<String> hostsArray, int hostNumber) {
		Client client = new Client();
		String[] dataTobeAllocated = new String[hostNumber];
    	for(int i = 0; i < hostNumber; ++i) {
    		dataTobeAllocated[i] = "";
    	}
		for(String item: hosts) {
			hostsArray.add(item);
			String[] hostInfo = item.split(" ");
    		String tuplesOnHost = client.getTuplesFile(hostInfo[1], hostInfo[2]);
    		String[] tuples = tuplesOnHost.split("\n");
    		for(String tuple: tuples) {
    			if(!tuple.equals("\n") && tuples != null) {
    				int hostId = Hash.md5(tuple, hostNumber);
    				dataTobeAllocated[hostId] += tuple + "\n";
    			}
    		}
		}
		return dataTobeAllocated;
	}
	
	//Get the netsfile string from the host list
	public static String getAllNetsFile(ArrayList<String> hostsArray) {
		String netsFile = "";
		for(String host: hostsArray) {
			netsFile += host + "\n";
		}
		return netsFile;
	}
	
	// Process the command
	public static String preprocess(String command){
		int start = command.indexOf('(');
    	int end = command.indexOf(')');
    	String str = command.substring(start + 1, end);
		String res = "";
		String[] tmp = str.split(",");
		for(String item: tmp) {
			res += item.trim() + " ";
		}
		return res;
	}
	
	//Broadcast the tuples to all hosts
	public static void broadCast(String netsPath, int hostNumber, Thread[] broadcastThread, SharedInfo sharedInfo, String strToIn) throws IOException, InterruptedException {
		ArrayList<String> allHosts =  getAllHostInfoIntoList(netsPath);
		for(int i = 0; i < allHosts.size(); ++i) {
			String[] strs = allHosts.get(i).split(" ");
			String otherHostName = strs[0];
    		String otherHostIP = strs[1];
    		String otherHostPortNumber = strs[2];
    		if(!Utils.checkServerStatus(otherHostIP, otherHostPortNumber)) {
    			int backupId = getBackupId(i, allHosts.size());
    			String[] backupInfo = allHosts.get(backupId).split(" ");
    			broadcastThread[i] = new BroadcastThread(sharedInfo, backupInfo[0], backupInfo[1], backupInfo[2], strToIn, "backup");
    		}else{
    			broadcastThread[i] = new BroadcastThread(sharedInfo, otherHostName, otherHostIP, otherHostPortNumber, strToIn, "original");
    		}
		}
		
		// start all the threads
		for(int i = 0; i < hostNumber; ++i) {
			broadcastThread[i].start();
		}
		//If not found, block current host
		while(sharedInfo.tuples.equals("")) {
			Thread.sleep(2000);
		}
	}
	
	//Check whether the server is working or not
	public static boolean checkServerStatus(String hostAddress, String portNumber) {
		try {
			Socket client = new Socket(hostAddress, Integer.valueOf(portNumber));
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF("check");
			client.close();
		} catch (java.net.ConnectException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * File: target file
	 * 777: true true true  - directories.
	 * 666: false,true,true - files.
	 */
	public static void chmod(File file, boolean executable, boolean readable, boolean writable) {
		file.setExecutable(executable,false);
		file.setReadable(readable,false);
		file.setWritable(writable, false);
	}
	
}
