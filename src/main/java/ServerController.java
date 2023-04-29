import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.List;

public class ServerController implements Initializable {

	//FXML injected variables
	@FXML
	ListView clientsList;

	@FXML
	ListView messagesList;

	private Server server;

	// Initialize the server
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		server = new Server(this::updateClientList, this::updateMessage);
	}

	// update client list
	private void updateClientList(Serializable listdata) {
		ArrayList<Integer> clientlist = (ArrayList<Integer>) listdata;
		List<String> clientStrings = new ArrayList<>();
		for (int i = 0; i < clientlist.size(); i++) {
			clientStrings.add("Client#" + clientlist.get(i));
		}
		Platform.runLater(()-> {
			clientsList.getItems().setAll(clientStrings);
		});
	}

	// Send a message to selected clients
	private void updateMessage(Serializable messagedata) {
		Platform.runLater(()-> {
			messagesList.getItems().add(messagedata.toString());
		});
	}
}

