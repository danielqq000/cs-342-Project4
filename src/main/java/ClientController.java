import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;


public class ClientController {
	@FXML
	private VBox root;
	@FXML
	private Menu exitMenu;
	@FXML
	private MenuItem exitItem;
	@FXML
	private ListView clientsListView = new ListView<String>();
	@FXML
	private ListView selectClientsListView = new ListView<String>();
	@FXML
	private ListView messagesListView = new ListView<String>();
	@FXML
	private TextField messageField;
	@FXML
	private Button sendButton;

	int counter = 0;
	private Client client;

	ObservableList<String> clientsList = FXCollections.observableArrayList();
	ObservableList<String> selectClientsList = FXCollections.observableArrayList();
	ObservableList<String> messageList = FXCollections.observableArrayList();

	private String message = "";

	public void initialize() {
		client = new Client(this::updateClientList, this::updateMessage);

		// checkbox
		clientsListView.setCellFactory(param -> new ListCell<String>() {
			private final CheckBox checkBox = new CheckBox();

			{
				checkBox.setOnAction(event -> {
					String item = getItem();
					if (item != null) {
						if (checkBox.isSelected()) {
							// Add the selected item to a Set or List
							selectClientsList.add(item);
							selectClientsListView.setItems(selectClientsList);
							System.out.println("Selected item: " + item);
						} else {
							// Remove the deselected item from the Set or List
							selectClientsList.remove(item);
							selectClientsListView.getItems().remove(selectClientsList);
							System.out.println("Deselected item: " + item);
						}
					}
				});
			}

		});
	}

	// update client list
	private void updateClientList(Serializable listdata) {
		ArrayList<Integer> clientlist = (ArrayList<Integer>) listdata;
		List<String> clientStrings = new ArrayList<>();
		for (int i = 0; i < clientlist.size(); i++) {
			clientStrings.add("Client#" + clientlist.get(i));
		}
		Platform.runLater(()-> {
			clientsListView.getItems().setAll(clientStrings);
			selectClientsListView.getItems().setAll(clientStrings);
		});
	}

	// Send a message to selected clients
	private void updateMessage(Serializable messagedata) {
		Platform.runLater(()-> {
			messagesListView.getItems().add(messagedata.toString());
		});
	}

	public void handleSend(ActionEvent e) throws IOException {
		message = messageField.getText();
		messageField.clear();
		// if message empty, don't send
		//client.send(selectClientsList, message);
		if (message == null)
			System.out.println("message");
		if (client == null)
			System.out.println("client");
		if (client.out == null)
			System.out.println("out");

		client.send(message);
		messagesListView.setItems(messageList);
		selectClientsList.clear();

	}

	public void handleExit(ActionEvent e) throws IOException {
		System.out.println("EXIT");
		System.exit(0);
	}

}

