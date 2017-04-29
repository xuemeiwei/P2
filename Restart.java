import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Restart {
	public static ArrayList<String> getNetsFileFromBackup(String netsPath, String hostName, String hostAddress, String port) throws IOException {
		FileReader fr = new FileReader(netsPath);
    	BufferedReader br = new BufferedReader(fr);
    	String tmp = "";
    	ArrayList<String> hosts = new ArrayList<>();
    	while((tmp = br.readLine()) != null) {
    		String[] hostInfo = tmp.split(" ");
    		if(hostInfo[0].equals(hostName)) {
    			hosts.add(hostName + " " + hostAddress + " " + port);
    		}else{
    			hosts.add(tmp);
    		}
    	}
    	br.close();
    	return hosts;
	}
	public static int getHostId(ArrayList<String> hosts, String hostName) {
		for(int i = 0; i < hosts.size(); ++i) {
			if(hosts.get(i).startsWith(hostName)) {
				return i;
			}
		}
		return 0;
	}
	public static String getNetsfileFromHosts(ArrayList<String> hosts) {
		String netsFile = "";
		for(String host: hosts) {
			netsFile += host + "\n";
		}
		return netsFile;
	}
}
