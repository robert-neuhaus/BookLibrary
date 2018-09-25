package controller;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MasterController {

	private static MasterController instance = null;
	private BorderPane borderPane;
	
	private MasterController(){
		
	}
	
	public static MasterController getInstance() {
		if (instance == null) {
			instance = new MasterController();
		}
		return instance;
	}
	
	public void changeView(String fxml, Object controller, Object data) {
		URL viewURL = getClass().getResource(fxml);
		FXMLLoader loader = new FXMLLoader(viewURL);
		loader.setController(controller);
		
		try {
			Node contentPane = loader.load();
			borderPane.setCenter(contentPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setRootBorderPane(BorderPane menuBorderPane) {
		this.borderPane = menuBorderPane;
	}

}
