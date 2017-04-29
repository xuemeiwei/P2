# Linux Tuple Space, Part II

This assignment is to implement the distributed model using java. In a distributed environment, Linda provides a conceptually "global" tuple space (TS) which remote processes can sccess the matched tuples in TS by atomix operations (in, rd, inp, rdp, out, eval). "out" will put tuple in specified tuple by hashing the tuple and get the corresponding host. Both "in" and "rd" operations have exact match and variable match. Exact match will go to the sepecified host and variable match will broadcast the message. Check all the hosts and only operate on one host. The difference between "in" and "rd" is that "in" will remove the tuple while "rd" will not. If the tuple wanted is not available, the host will be blocked until tuple is available.

Also Every host is an original host and backup host of the other one. When the original host is down, you can check the backup host.

## Tasks
1. Find avaiable port on current machine;
2. Add and delete other hosts;
3. When host is changed, redistribute the data among all the hosts and corresponding host;
4. Implement the features of "in", "rd", and "out".
5. When a host is down and then restarts, get its data from its backup host. Also since it restarts, its port will change. The hosts information stored in each host should also be updated.

## How to run the program
1. Compile the P2.java by using
```bash
    $> javac P2.java
```
2. Run the P2 on each host and also set the parameter of hostname
```bash
    $>java P2 hostname
```
3. Input the commands you want like "add", "delete", "out", "in", "rd"

## Main Structure

1. P2 is the main program which will call Server, Broadcast, Client.

2. Server.java is implemented using thread which is always running listening to the specified port;

3. Client.java is only called when there is a request from current host.

4. Broadcast.java is thread called by P1 to broadcast the messages from current host to all the hosts. Each request is implemented using a thread.

5. SharedInfo.java is the message shared by all the broadcast thread. When a tuple is found on one host, it will set the SharedInfo and all the hosts will know it's found and all the threads will complete. If no tuple is found, all the threads are running all the time and the main program is waiting until available tuple.

6. MainFunction.java is the main functions dealing with "add", "delete", "out", "in", "rd" command.

7. Hash.java deals with the hash of tuple.

8. Utils.java deals with the functions used by other functions.

9. Restart.java deals with the functions of restarting a host.

10. ErrorHandle.java deals with the checking of input.

## Code Desciption
### 1. P2 contains:
1) Find available port on current machine;
2) If the machine is newly created, create relevant files like "nets.txt", "tuples.txt", "backup.txt";
3) If the machine is restarted, get original data from backup host and backup data from original host;
4) Start its server and wait for client request;
5) Waiting for user's input and do corresponding operation;

### 2. Server contains:
1) Property: serverSocket, hostName, netsPath, tuplesPath, backupPath, hostAddress, port;
2) Method: run() including connects with client, receive message, do corresponding operations, send message to client

### 3. Client contains (only have methods):
1) add(): add host
2) deleteFile(): delete host and its files
3) getTuplesFile(): get original tuples from backup host;
4) getBackupFile(): get backup tuples from original host;
5) getNetsFile(): get netsFile from backup host;
6) ino(): delete tuple from original host;
7) inBackup(): delete backup tuple from backup host;
8) out(): put tuple on original host;
9) outBackup: put backup tuple on backup host;
10) rdo(): read tuple from original host;
11) rdu(): read backup tuple from backup host;
12) setTuples(): set tuples on original host;
13) setBackup(): set backup tuples on original host;

### 4. Broadcast:
1) Properties: sharedInfo, hostAddress, port, tuples, hostname;
2) Method: run() which sends request to server

### 5. SharedInfo contains:
1) Property: flag, hostAddress, port, tuples
2) Method: set()

### 6. MainFunctions:
1) add();
2) delete();
3) in();
4) out();
5) rd();

### 7. Restart:
1) getHostId()
2) getNetsFileFromBackup();
3) getNetsFileFromHosts();
