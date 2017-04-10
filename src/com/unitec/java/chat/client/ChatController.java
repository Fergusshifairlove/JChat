package com.unitec.java.chat.client;

import java.io.DataOutputStream;
import java.io.IOException;

import com.unitec.java.chat.server.MessageType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {
	
	private DataOutputStream dos;
	private String nickName;

	@FXML
    private TextField txtInput;

    @FXML
    private TextArea txtAreaChat;

    @FXML
    private ListView<String> listPerson;

    @FXML
    private Button btnSend;
    
    @FXML
    void sendMessage(ActionEvent event) {
    	sendMessage(txtInput.getText());
    	txtInput.setText("");
    }
    
    void setNickName(String nickName){
    	this.nickName = nickName;
    }
    
    void setDataOutputStream(DataOutputStream dos){
    	this.dos = dos;
    }
    
    private void sendMessage(String msg){
    	try {
    		String selected="All";
    		selected = listPerson.getSelectionModel().getSelectedItem()==null? "All" : listPerson.getSelectionModel().getSelectedItem().toString();	
    		System.out.println(selected);
    		if(!selected.equalsIgnoreCase("All")){
    			dos.writeUTF(MessageType.PRIVATE_MESSAGE.toString());
    			dos.writeUTF(selected);
    			dos.writeUTF(MessageType.PRIVATE_MESSAGE.toString());
    			dos.writeUTF(nickName + " : " + msg);
    			
    		}else{
    			dos.writeUTF(MessageType.CHAT_MESSAGE.toString());
    			dos.writeUTF(nickName + " : " + msg);
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
  
    public void updateUserList(String users){
    	ObservableList<String> updatedUsersList = FXCollections.observableArrayList();
		updatedUsersList.addAll(users.split(","));
		listPerson.setItems(updatedUsersList);
    }
}
