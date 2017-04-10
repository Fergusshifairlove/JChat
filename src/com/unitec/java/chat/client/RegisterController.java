package com.unitec.java.chat.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Control chat registration actions
 * @author Kedar
 *
 */
public class RegisterController {

	    @FXML
	    private TextField txtNickName;

	    @FXML
	    private Label lblNickName;

	    @FXML
	    private Button btnJoinChat;

	    /**
	     * Handle chat registration
	     * @param event
	     */
	    @FXML
	    void joinChat(ActionEvent event) {
	    	Stage stage = (Stage) btnJoinChat.getScene().getWindow();
	    	//Start the chat window
	    	new ChatWindow(stage,txtNickName.getText());
	    }
}
