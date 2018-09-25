package assignment1;

import java.net.URL;

import controller.MasterController;
import controller.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//CS 4743 Assignment 1 by Robert Neuhaus, Diego Gonzales
public class Launcher extends Application {
	
	public static void main(String[] args) {
		launch(args);		
	}
	
	public void init() throws Exception {
		super.init();				
		MasterController.getInstance();
	}

	@Override
	public void start(Stage stage) throws Exception {
		URL viewURL = this.getClass().getResource("../view/view_master.fxml");
		FXMLLoader loader = new FXMLLoader(viewURL);
		loader.setController(new MenuController());
		BorderPane rootBorderPane = loader.load();
		MasterController.getInstance().setRootBorderPane((BorderPane) rootBorderPane);
		Scene scene = new Scene(rootBorderPane);
		stage.setScene(scene);
		stage.show();	
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
	}

}
