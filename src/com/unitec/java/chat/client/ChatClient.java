package com.unitec.java.chat.client;
	


import javafx.application.Application;
import javafx.stage.Stage;


/**
 * Chat user FX window 
 * @author Kedar
 */
public class ChatClient extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		new ChatRegister(primaryStage);
	}
		
	public static void main(String[] args) {
		launch(args);
	}
	
}
