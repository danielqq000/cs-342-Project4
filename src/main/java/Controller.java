import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class Controller { //implements Initializable {
    @FXML
    private VBox root;
    @FXML
    private Menu exitMenu;
    @FXML
    private MenuItem exitItem;
    @FXML
    private ListView clientsList;
    @FXML
    private ListView selectClientsList;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    /*
    @FXML
    private Label clientsLabel;
    @FXML
    private Label messagesLabel;
    @FXML
    private Label selectClientsLabel;
    @FXML
    private Label clientsLowLabel;
    @FXML
    private Label selectClientsLowLabel;
    */

    private String message = ""; //static?


    public void handleSend(ActionEvent e) throws IOException {
        message = messageField.getText();
        // if message empty, don't send

    }

    public void handleExit(ActionEvent e) throws IOException {
        System.out.println("EXIT");
        System.exit(0);
    }

}
