package com.unitec.java.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Thread to handle incoming messages from the chat clients. 
 * @author Kedar
 *
 */
public class ServerThread extends Thread{

	private String userName;
	private HashMap<String,ServerThread> connectedClients;
	private ChatServer server ;
	private String pvtMsgTo="";
	private volatile boolean chatting = true;
	
	DataInputStream dis;
	DataOutputStream dos;
	
	ServerThread(Socket remoteClient,HashMap<String,ServerThread> connectedClients,ChatServer server){
		this.server = server;
		this.connectedClients = connectedClients;

		try {
			this.dis = new DataInputStream(remoteClient.getInputStream());
			this.dos = new DataOutputStream(remoteClient.getOutputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		MessageType msgType;
		while(chatting){
			try {				
				msgType = MessageType.valueOf(dis.readUTF());
				switch(msgType)
				{
					case REGISTER_CLIENT:						
						userName = dis.readUTF();
						server.log("New user registration " + userName);
						this.setName("server-" + userName);
						connectedClients.put(userName, this);
						
						broadcast(MessageType.REGISTER_BROADCAST.toString());
						broadcast(userName);	
						broadcast(MessageType.REGISTER_BROADCAST.toString());
						broadcastUserList();
						break;
					
					case REGISTER_BROADCAST:	
						server.log("REGISTER_BROADCAST not allowed ... use REGISTER_CLIENT");
						break;
					
					case CHAT_MESSAGE:
						broadcast(MessageType.CHAT_BROADCAST.toString());
						broadcast(dis.readUTF());						
						break;
					
					case CHAT_BROADCAST:	
						server.log("CHAT_BROADCAST not allowed ... use CHAT_MESSAGE");	
						break;
						
					case PRIVATE_MESSAGE:						
						if(pvtMsgTo.length()==0){
							pvtMsgTo = dis.readUTF();
						}else{														
							
							String msg = dis.readUTF();
							ServerThread pvtTo = connectedClients.get(pvtMsgTo);
							pvtTo.dos.writeUTF(MessageType.CHAT_BROADCAST.toString());
							pvtTo.dos.writeUTF("(Private) " + msg);
							
							ServerThread pvtFrom = connectedClients.get(userName);
							pvtFrom.dos.writeUTF(MessageType.CHAT_BROADCAST.toString());
							pvtFrom.dos.writeUTF("(Private) " + msg);
							
							pvtMsgTo="";
						}
						break;
						
					case EXIT_MESSAGE:
						String nickname = dis.readUTF();
						closeConnection(nickname);												
						broadcast(MessageType.EXIT_BROADCAST.toString());
						broadcast(nickname);
						broadcast(MessageType.EXIT_BROADCAST.toString());
						broadcastUserList();
						break;
					
					case EXIT_BROADCAST:	
						server.log("EXIT_BROADCAST not allowed ... use EXIT_MESSAGE");	
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
		}		
	}
	
	/**
	 * Remove the user from the user list and 
	 * close the thread of the removed user.
	 * @param nickname
	 */
	private void closeConnection(String nickname){			
		ServerThread st = connectedClients.get(nickname);
		st.shutdown(nickname);
		connectedClients.remove(nickname);
		
		server.log("User " + nickname + " removed ");		
	}
	

	
	/**
	 * Broadcast the message to all the connected users
	 * @param msg
	 */
	private void broadcast(String msg){
		
		connectedClients.forEach((nickname,serverThread) -> {
			try {
				serverThread.dos.writeUTF(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Get the current connected user list and broadcast to all users.
	 */
	private void broadcastUserList(){
		
		String users = connectedClients.keySet().stream().collect(Collectors.joining(","));
		users = "All," + users;
		broadcast(users);
	}

	/**
	 * Stop the user thread
	 */
	public void shutdown(String nickname){
		server.log("Shutting down..."+ nickname);
		this.chatting = false;
	}
}
