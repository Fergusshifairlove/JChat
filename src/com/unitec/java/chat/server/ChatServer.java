package com.unitec.java.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main chat server.
 * Start the FX application for chat server.
 * 
 * @author Kedar
 *
 */
public class ChatServer extends Application implements Runnable{
	private ServerSocket serverSocket;
	public static final String SERVER_HOST="localhost";
	public static final int SERVER_PORT=5000;	
	
	private TextArea serverLog ;
	HashMap<String,ServerThread> connectedClients = new HashMap<>();
	
	
	@Override
	public void start(Stage primaryStage) {				
		URL fxmlServer = this.getClass().getClassLoader().getResource("Server.fxml");
		try {
			System.out.println("...Chat Server...");
			Pane serverPane = FXMLLoader.<Pane>load(fxmlServer);				
			primaryStage.setTitle("JChat Server");
			primaryStage.setScene( new Scene(serverPane));
			primaryStage.show();			
			
			//Get the control of the text area to show the server logs
			Pane anchorNode = (Pane) serverPane.getChildren().get(0);
			serverLog = (TextArea) anchorNode.getChildren().get(0);

			primaryStage.setOnCloseRequest(event ->{
				connectedClients.forEach((nickname,serverThread) -> {
					serverThread.shutdown(nickname);					
				});	
			});
			
			this.startChatServer();
			
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}
	
	/**
	 * Start server socket and a new thread to listen to new connections.
	 */
	public void startChatServer(){
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			Thread thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void run() {				
		try {			
			while(true){
				log("Waiting for new users...");
				Socket remoteClient = serverSocket.accept();
				log("New user connected");
				ServerThread st = new ServerThread(remoteClient,connectedClients,this);
				st.setDaemon(true);
				st.start();
			}			
		}catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	/**
	 * Log the messaged on the chat server windows text area.
	 * @param msg
	 */
	public void log(String msg){
		serverLog.setText(serverLog.getText() + msg + "\n"); 
	}
}
