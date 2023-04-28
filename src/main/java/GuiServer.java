
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{


	TextField s1,s2,s3,s4, c1;
	Button serverChoice,clientChoice,b1;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	VBox clientBox;
	HBox clientSelect;
	ComboBox clientList;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection;
	
	ListView<String> listItems, listItems2;
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("The Networked Client/Server GUI Example");
		
		this.serverChoice = new Button("Server");
		this.serverChoice.setStyle("-fx-pref-width: 150px; -fx-pref-height: 80px");

		this.serverChoice.setOnAction(e->{ 
			primaryStage.setScene(sceneMap.get("server"));
			primaryStage.setTitle("Server");
			serverConnection = new Server(data -> {
				Platform.runLater(()->{
					listItems.getItems().add(data.toString());
				});

			});

		});


		this.clientChoice = new Button("Client");
		this.clientChoice.setStyle("-fx-pref-width: 150px; -fx-pref-height: 80px");

		this.clientChoice.setOnAction(e->{
			primaryStage.setScene(sceneMap.get("client"));
			primaryStage.setTitle("Client");
			clientConnection = new Client(data->{
				Platform.runLater(()->{listItems2.getItems().add(data.toString());
				});
			});

			clientConnection.start();
		});

		this.buttonBox = new HBox(80, serverChoice, clientChoice);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(50));
		startPane.setCenter(buttonBox);

		startScene = new Scene(startPane, 400, 200);

		listItems = new ListView<String>();
		listItems2 = new ListView<String>();

		c1 = new TextField();
		b1 = new Button("Send");
		b1.setOnAction(e->{clientConnection.send(c1.getText()); c1.clear();});

		sceneMap = new HashMap<String, Scene>();

		sceneMap.put("server",  createServerGui());
		sceneMap.put("client",  createClientGui());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});



		primaryStage.setScene(startScene);
		primaryStage.show();

	}

	public Scene createServerGui() {

		/*BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");

		pane.setCenter(listItems);

		return new Scene(pane, 600, 600);*/
		try {
			// Read file fxml and draw interface.
			Parent root = FXMLLoader.load(getClass()
					.getResource("/FXML/serverGUI.fxml"));

			//primaryStage.setTitle("My Application");
			Scene s1 = new Scene(root, 900,600);
			s1.getStylesheets().add("/styles/clientStyle.css");
			//s1.getStylesheets().add("/styles/style1.css");
			//primaryStage.setScene(s1);
			//primaryStage.show();
			return s1;

		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		//clientBox = new VBox(10, c1, b1, listItems2);
		//clientBox.setStyle("-fx-background-color: navy");
		//return new Scene(clientBox, 400, 300);
		return null;


	}

	public Scene createClientGui() {
		//clientList = new ComboBox();
		//clientSelect = new HBox(10, clientList, b1);
		try {
			// Read file fxml and draw interface.
			Parent root = FXMLLoader.load(getClass()
					.getResource("/FXML/clientGUI.fxml"));

			//primaryStage.setTitle("My Application");
			Scene s1 = new Scene(root, 900,600);
			s1.getStylesheets().add("/styles/clientStyle.css");
			//s1.getStylesheets().add("/styles/style1.css");
			//primaryStage.setScene(s1);
			//primaryStage.show();
			return s1;

		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		//clientBox = new VBox(10, c1, b1, listItems2);
		//clientBox.setStyle("-fx-background-color: navy");
		//return new Scene(clientBox, 400, 300);
		return null;
	}

}
