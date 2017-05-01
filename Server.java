import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;
import java.math.BigInteger;
 
public class Server extends Thread {
	
   private ServerSocket serverSocket;
   private String hostName;
   private String netsPath;
   private String tuplesPath;
   private String backupPath;
   private String hostAddress;
   private String port;
   public Server(ServerSocket serverSocket, String hostName, String hostAddress, String port, String netsPath, String tuplesPath, String backupPath) throws IOException {
      this.serverSocket = serverSocket;
      this.hostName = hostName;
      this.hostAddress = hostAddress;
      this.port = port;
      this.netsPath = netsPath;
      this.tuplesPath = tuplesPath;
      this.backupPath = backupPath;
   }
 
   public void run() {
      while(true) {
         try {
            Socket server = serverSocket.accept();
            DataInputStream in = new DataInputStream(server.getInputStream());
            String str = in.readUTF();
            String matched = ""; // store the result match: If "", no match else "match" equals the matched tuple
            
            // The command is "add host"
            if(str.startsWith("add")) {
                FileWriter fw = new FileWriter(netsPath);
        		BufferedWriter bw = new BufferedWriter(fw);
        		bw.write(str.substring(3));
        		bw.close();
        		DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(hostName);
		    	server.close();
		    	continue;
            }
         // The command is "set original tuples"
            if(str.startsWith("setTu")) {
            	FileWriter fw = new FileWriter(tuplesPath);
        		BufferedWriter bw = new BufferedWriter(fw);
        		bw.write(str.substring(5));
        		bw.close();
        		DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(hostName);
		    	server.close();
		    	continue;
            }
         // The command is "set backup tuples"
            if(str.startsWith("setBu")) {
            	FileWriter fw = new FileWriter(backupPath);
        		BufferedWriter bw = new BufferedWriter(fw);
        		bw.write(str.substring(5));
        		bw.close();
        		DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(hostName);
		    	server.close();
		    	continue;
            }
         // The command is "get netsfile"
            if(str.startsWith("nets")) {
            	FileReader fr = new FileReader(netsPath);
		    	BufferedReader br = new BufferedReader(fr);
		    	String netsFile = "";
		    	String tmp = "";
		    	if((tmp = br.readLine()) == null) {
		    		netsFile = hostName + " " + hostAddress + " " + port;
		    	}else{
			    	while(tmp != null) {
			    		netsFile += tmp + "\n";
			    		tmp = br.readLine();
			    	}
		    	}
		    	br.close();
		    	DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(netsFile);
		    	server.close();
		    	continue;
            }
            
            // The command is "get tuples"
            if(str.startsWith("tuples")) {
            	FileReader fr = new FileReader(tuplesPath);
		    	BufferedReader br = new BufferedReader(fr);
	    		String tmp = "";
	    		String tuplesFile = "";
		    	while((tmp = br.readLine()) != null) {
		    		if(!tmp.equals("\n")) {
		    			tuplesFile += tmp + "\n";
		    		}
		    	}
		    	br.close();
		    	DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(tuplesFile);
		    	server.close();
		    	continue;
            }
            
            // The command is "get backup tuples"
            if(str.startsWith("backup")) {
            	FileReader fr = new FileReader(backupPath);
		    	BufferedReader br = new BufferedReader(fr);
	    		String tmp = "";
	    		String tuplesFile = "";
		    	while((tmp = br.readLine()) != null) {
		    		if(!tmp.equals("\n")) {
		    			tuplesFile += tmp + "\n";
		    		}
		    	}
		    	br.close();
		    	DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(tuplesFile);
		    	server.close();
		    	continue;
            }
            
            // The command is "delete hosts"
            if(str.startsWith("delete")) {
            	File dir = new File("/tmp/xwei1/linda/" + hostName);
            	deleteDir(dir);
		    	DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(hostName);
		    	server.close();
		    	System.out.println("This host has been deleted");
		    	continue;
            }
            
            // The command is "out tuple on original host"
            if(str.startsWith("out")) { //The command is "out"
                FileWriter fw = new FileWriter(tuplesPath, true);
        		BufferedWriter bw = new BufferedWriter(fw);
        		bw.write(str.substring(3));
        		bw.write("\n");
        		bw.close();
        		fw.close();
        		DataOutputStream out = new DataOutputStream(server.getOutputStream());
        		out.writeUTF("Successfully put tuples on " + hostName);
        		server.close();
        		continue;
            }
            
            // The command is "out tuple on backup host"
            if(str.startsWith("obu")) { //The command is "out"
                FileWriter fw = new FileWriter(backupPath, true);
        		BufferedWriter bw = new BufferedWriter(fw);
        		bw.write(str.substring(3));
        		bw.write("\n");
        		bw.close();
        		fw.close();
        		DataOutputStream out = new DataOutputStream(server.getOutputStream());
        		out.writeUTF("Successfully put backup tuples on " + hostName);
        		server.close();
        		continue;
            }
            
            // The command is "remove tuple on original host"
            if(str.startsWith("ino")) { //The command is "in exact match"
            	// Read the tuples to see whether there is a match
            	FileReader fr = new FileReader(tuplesPath);
		    	BufferedReader br = new BufferedReader(fr);
		    	String strTowrite = "";
		    	String tmp = null;
		    	boolean found = false;
		    	while((tmp = br.readLine()) != null) {
		    		if(!isEqual(tmp, str.substring(3)) || found) {
		    			strTowrite += tmp + "\n";
		    		}else{
		    			found = true;
		    			matched = "Tuple deleted from machine: " + hostName;
		    		}
		    	}
		    	br.close();
                FileWriter fw = new FileWriter(tuplesPath);
        		BufferedWriter bw = new BufferedWriter(fw);
        		bw.write(strTowrite);
        		bw.close();
        		fw.close();
        		
        		DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(matched);
		    	server.close();
		    	continue;
            }
            
            // The command is "remove tuple on backup host"
            if(str.startsWith("inu")) { 
            	FileReader fr = new FileReader(backupPath);
		    	BufferedReader br = new BufferedReader(fr);
		    	String strTowrite = "";
		    	String tmp = null;
		    	boolean found = false;
		    	while((tmp = br.readLine()) != null) {
		    		if(!isEqual(tmp, str.substring(3)) || found) {
		    			strTowrite += tmp + "\n";
		    		}else{
		    			found = true;
		    			matched = "Tuple deleted from backup machine: " + hostName;
		    		}
		    	}
		    	br.close();
                FileWriter fw = new FileWriter(backupPath);
        		BufferedWriter bw = new BufferedWriter(fw);
        		bw.write(strTowrite);
        		bw.close();
        		fw.close();
        		
        		DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(matched);
		    	server.close();
		    	continue;
            }
            
            // The command is "read tuple on original host"
            if(str.startsWith("rdo")) { //The command is "read exact match"
            	// Read the tuples to see whether there is a match
            	FileReader fr = new FileReader(tuplesPath);
		    	BufferedReader br = new BufferedReader(fr);
		    	String tmp = null;
		    	while((tmp = br.readLine()) != null) {
		    		if(isEqual(tmp, str.substring(3))) {
		    			System.out.println("Tuple found on machine: " + hostName);
		    			System.out.print("linda>");
		    			matched = "Tuple on this machine: " + hostName;
		    			break;
		    		}
		    	}
		    	br.close();
		    	DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(matched);
		    	server.close();
		    	continue;
            }
            
            // The command is "read tuple on backup host"
            if(str.startsWith("rdu")) { 
            	FileReader fr = new FileReader(backupPath);
		    	BufferedReader br = new BufferedReader(fr);
		    	String tmp = null;
		    	while((tmp = br.readLine()) != null) {
		    		if(isEqual(tmp, str.substring(3))) {
		    			System.out.println("Tuple found on backup machine: " + hostName);
		    			System.out.print("linda>");
		    			matched = "Tuple on this machine: " + hostName;
		    			break;
		    		}
		    	}
		    	br.close();
		    	DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(matched);
		    	server.close();
		    	continue;
            }
            
            // The command is "broadcast tuple on original host"
            if(str.startsWith("bro")) {
            	FileReader fr = new FileReader(tuplesPath);
		    	BufferedReader br = new BufferedReader(fr);
		    	String tmp = null;
		    	while((tmp = br.readLine()) != null) {
		    		if(isMatch(tmp, str.substring(3))) {
		    			matched = tmp;
		    			break;
		    		}
		    	}
		    	br.close();
		    	DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(matched);
		    	server.close();
		    	continue;
            }
            
            // The command is "broadcast tuple on backup host"
            if(str.startsWith("brb")) { 
            	FileReader fr = new FileReader(backupPath);
		    	BufferedReader br = new BufferedReader(fr);
		    	String tmp = null;
		    	while((tmp = br.readLine()) != null) {
		    		if(isMatch(tmp, str.substring(3))) {
		    			matched = tmp;
		    			break;
		    		}
		    	}
		    	br.close();
		    	DataOutputStream out = new DataOutputStream(server.getOutputStream());
		    	out.writeUTF(matched);
		    	server.close();
		    	continue;
            }
            
            // The command is "check status of host"
            if(str.startsWith("check")) {
            	server.close();
            	continue;
            }
         }catch(IOException e){
            e.printStackTrace();
            break;
         }
      }
   }
   public boolean deleteDir(File dir) {
	   if(dir.isDirectory()) {
		   String[] children = dir.list();
		   for(int i = 0; i < children.length; ++i) {
			   boolean success = deleteDir(new File(dir, children[i]));
			   if(!success) {
				   return false;
			   }
		   }
	   }
	   return dir.delete();
   }

/*
    * Check whether two strings are the same by using hashing value
    */
   boolean isEqual(String str1, String str2) {
		try {
			MessageDigest md =  MessageDigest.getInstance("MD5");
			md.update(str1.getBytes());
			byte[] bArr= md.digest();
			BigInteger number1 = new BigInteger(1, bArr);
			
			md.update(str2.getBytes());
			bArr= md.digest();
			BigInteger number2 = new BigInteger(1, bArr);
			
			return number1.equals(number2);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}
   
   /*
    * Check whether current string matches the pattern
    */
   boolean isMatch(String str1, String str2) {
		String[] arr1 = str1.split(" ");
		String[] arr2 = str2.split(" ");
		if(arr1.length != arr2.length) {
			return false;
		}
		for(int i = 0; i < arr1.length; ++i) {
			if(!arr2[i].contains("?")) {
				if(isInteger(arr2[i]) || isFloat(arr2[i])) {
					if(!arr1[i].equals(arr2[i])) {
						return false;
					}
				}else{
					if(!arr1[i].equals(arr2[i])) {
						return false;
					}
				}
			}else{
				if(arr2[i].contains("int")) {
					if(!isInteger(arr1[i])) {
						return false;
					}
				}else if(arr2[i].contains("float")) {
					if(!isFloat(arr1[i])) {
						return false;
					}
				}else{
					if(isInteger(arr1[i]) || isFloat(arr1[i])) {
						return false;
					}
				}
			}
		}
		return true;
	}
   
   /*
    * Called by isMatch to check whether a string is Integer
    */
   
  boolean isInteger(String data) {
	   try{
		   Integer.parseInt(data);
	   }catch(NumberFormatException e) {
		   return false;
	   }catch(NullPointerException e) {
		   return false;
	   }
	   return true;
  }
  /*
   * Called by isMatch to check whether a string is float
   */
  boolean isFloat(String data) {
	   return data.contains(".");
  }
}
