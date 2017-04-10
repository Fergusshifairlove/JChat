package com.unitec.java.chat.client;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Render the chat registration FX window
 * @author Kedar
 *
 */
public class ChatRegister {
	public ChatRegister(Stage stage){
		URL fxmlChat = this.getClass().getClassLoader().getResource("Register.fxml");
		try {
			Pane joinPane = FXMLLoader.<Pane>load(fxmlChat);			
			stage.setTitle("Join JChat");
			stage.setScene( new Scene(joinPane));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
