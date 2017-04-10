package com.unitec.java.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;

import com.unitec.java.chat.server.ChatServer;
import com.unitec.java.chat.server.MessageType;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


/**
 * User chat FX window
 * @author Kedar
 *
 */
public class ChatWindow implements Runnable{
	private Socket client;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private FXMLLoader loader;
	private AnchorPane chatPane;
	private TextArea chatHistory;
	
	private ChatController controller;
	
	private String leftUser="";
	private String newUser="";
	private String nickName;	
		
	
	public ChatWindow(Stage stage,String nickName){
		this.nickName = nickName;
		System.out.println("...ChatWindow..." + nickName);	
		
		URL fxmlChat = this.getClass().getClassLoader().getResource("JChat.fxml");
		try {
			loader = new FXMLLoader(fxmlChat);				
	        chatPane = (AnchorPane) loader.load();
						
			stage.setTitle("JChat - " +  this.nickName);			
			Scene chatScene = new Scene(chatPane);
			stage.setScene(chatScene);
			
			//Handle close of chat window
			stage.setOnCloseRequest(event ->{
				try {					
					dos.writeUTF(MessageType.EXIT_MESSAGE.toString());
					dos.writeUTF(nickName);
				} catch (IOException e) {
					e.printStackTrace();
				}								
			});
			
			Pane anchorNode = (Pane) chatPane.getChildren().get(0);			
			//Chat history
			chatHistory = (TextArea) anchorNode.getChildren().get(0);			
			//Chatters list
			controller = loader.getController();
			
			connectChatServer();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Open socket and connect to the server. Start a new thread to listen to incoming messages.
	 */
	public void connectChatServer(){
		try {			
			
			client = new Socket( ChatServer.SERVER_HOST,ChatServer.SERVER_PORT);
			dis = new DataInputStream(client.getInputStream());
			dos = new DataOutputStream(client.getOutputStream());
			
			controller.setNickName(nickName);
			controller.setDataOutputStream(dos);			
						
			dos.writeUTF(MessageType.REGISTER_CLIENT.toString());
			dos.writeUTF(nickName);

			
			Thread clientThread = new Thread(this);
			clientThread.setName("client-" + nickName);
			clientThread.setDaemon(true);
			clientThread.start();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		MessageType msgType;
		while(true){
			try {
				msgType = MessageType.valueOf(dis.readUTF());				
				switch(msgType)
				{
					case REGISTER_CLIENT:
						System.out.println("REGISTER_CLIENT not allowed ... use REGISTER_BROADCAST");
						break;
						
					case REGISTER_BROADCAST:	

						//Show new person joined message
						if(newUser.length()==0){
							newUser = dis.readUTF();
							appendHistory(newUser + " joined the chat");
						}else{						
							//Add new user to list
							String usersList = dis.readUTF();
							Platform.runLater(()->{																
								controller.updateUserList(usersList);
							});
							newUser="";
						}
						break;
					
					case CHAT_MESSAGE:
						System.out.println("CHAT_MESSAGE not allowed ... use CHAT_BROADCAST");
						break;
						
					case CHAT_BROADCAST:
						appendHistory(dis.readUTF());
						break;
					
					case EXIT_MESSAGE:
						System.out.println("CHAT_MESSAGE not allowed ... use CHAT_BROADCAST");
						break;
						
					case EXIT_BROADCAST:							
								
						if(leftUser.length()==0){
							leftUser = dis.readUTF();
							appendHistory(leftUser + " left the chat");
						}else{
							//Remove user from list
							String usersList = dis.readUTF();
							Platform.runLater(()->{
								controller.updateUserList(usersList);
							});
							leftUser ="";
						}																			
						break;
						
					case PRIVATE_MESSAGE:	
						System.out.println("PRIVATE_MESSAGE not allowed.");
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();				
			}
		}
	}
	
	/**
	 * Add message to the chat history text area.
	 * @param data
	 */
	private void appendHistory(String data){		
		String history = chatHistory.getText() + data + "\n";
		chatHistory.setText(history);
	}
}
