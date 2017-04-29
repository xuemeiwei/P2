import java.net.*;
import java.io.*;

/*
 * Implement different requests client send to the server
 */
public class Client {
	
	// The command is "add host"
	void add(String hostAddress, String portNumber, String allHostInfo) {
		try {
			Socket client = new Socket(hostAddress, Integer.valueOf(portNumber));
			
			allHostInfo = "add" + allHostInfo;
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF(allHostInfo);
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// The command is "get netsfile"
	String getNetsfile(String otherHostAddress, String otherHostPort) {
		String netsFile = "";
		try {
			Socket client = new Socket(otherHostAddress, Integer.valueOf(otherHostPort));
			
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF("nets");
			
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			netsFile = in.readUTF();
			client.close();
			return netsFile;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// The command is "get tuples"
	String getTuplesFile(String otherHostAddress, String otherHostPort) {
		String netsFile = "";
		try {
			Socket client = new Socket(otherHostAddress, Integer.valueOf(otherHostPort));
			
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF("tuples");
			
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			netsFile = in.readUTF();
			//System.out.println("Tuples return from server is: " + netsFile);
			client.close();
			return netsFile;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// The command is "get backup tuples"
	String getBackupFile(String otherHostAddress, String otherHostPort) {
		String netsFile = "";
		try {
			Socket client = new Socket(otherHostAddress, Integer.valueOf(otherHostPort));
			
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF("backup");
			
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			netsFile = in.readUTF();
			client.close();
			return netsFile;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// The command is "delete hosts"
	void deleteFile(String otherHostAddress, String otherHostPort) {
		try {
			Socket client = new Socket(otherHostAddress, Integer.valueOf(otherHostPort));
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF("delete");
			
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			System.out.println("Successfully delete files on " + in.readUTF());
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// The command is "set original tuples"
	void setTuples(String hostAddress, String portNumber, String tuples) {
		try {
			Socket client = new Socket(hostAddress, Integer.valueOf(portNumber));
			
			tuples = "setTu" + tuples;
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF(tuples);
			
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// The command is "set backup tuples"
	void setBackup(String hostAddress, String portNumber, String tuples) {
		try {
			Socket client = new Socket(hostAddress, Integer.valueOf(portNumber));
			
			tuples = "setBu" + tuples;
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF(tuples);
			
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// The command is "put original tuples"
	void out(String hostAddress, String portNumber, String tuples) {
		try {
			Socket client = new Socket(hostAddress, Integer.valueOf(portNumber));
			
			tuples = "out" + tuples;
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF(tuples);
			
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			System.out.println(in.readUTF());
			client.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// The command is "put backup tuples"
	void outBackup(String hostAddress, String portNumber, String tuples) {
		try {
			Socket client = new Socket(hostAddress, Integer.valueOf(portNumber));
			
			tuples = "obu" + tuples;
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF(tuples);
			
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			System.out.println(in.readUTF());
			
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//The command is "delete original tuples"
	void ino(String hostAddress, String portNumber, String tuples) {
		try {
			Socket client = new Socket(hostAddress, Integer.valueOf(portNumber));
			tuples = "ino" + tuples;
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF(tuples);
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			System.out.println(in.readUTF());
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//The command is "delete backup tuples"
	void inBackup(String hostAddress, String portNumber, String tuples) {
		try {
			Socket client = new Socket(hostAddress, Integer.valueOf(portNumber));
			tuples = "inu" + tuples;
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF(tuples);
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			System.out.println(in.readUTF());
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//The command is "read original tuples"
	boolean rdo(String hostAddress, String portNumber, String tuples) {
		String message = "";
		try {
			Socket client = new Socket(hostAddress, Integer.valueOf(portNumber));
			tuples = "rdo" + tuples;
			
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF(tuples);
			
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			message = in.readUTF();
			client.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message.equals("") ? false:true;
	}
	//The command is "read backup tuples"
	boolean rdu(String hostAddress, String portNumber, String tuples) {
		String message = "";
		try {
			Socket client = new Socket(hostAddress, Integer.valueOf(portNumber));
			tuples = "rdu" + tuples;
			
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF(tuples);
			
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			message = in.readUTF();
			client.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message.equals("") ? false:true;
	}
	
}
