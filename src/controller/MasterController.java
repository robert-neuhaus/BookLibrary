package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;

public class MasterController {
	private static MasterController instance = null;
	private BorderPane borderPane;
	private boolean isChange = false;
	
	private MasterController(){
		
	}
	
	public static MasterController getInstance() {
		if (instance == null) {
			instance = new MasterController();
		}
		return instance;
	}
	
	public void changeView(String fxml, Object controller, Object data) {
		if (getIsChange() == true) {
			alertSave();
		}
		if (getIsChange() == false) {
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
	}
	
	public void exit() {
		if (getIsChange() == true) {
			alertSave();
		}
		if (getIsChange() == false) {
			Platform.exit();
		}
	}
	
	public void alertSave() {
		Boolean isNewBook = false;
		BookDetailController bookDetailController = BookDetailController.getInstance();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		
		alert.setTitle("Unsaved Changes");
		alert.setHeaderText("Unsaved changes have been made to: "
				+ bookDetailController.getBook().getTitle());
		alert.setContentText("Would you like to save changes?");
		ButtonType bttnYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
		ButtonType bttnNo = new ButtonType("No", ButtonBar.ButtonData.OK_DONE);
		ButtonType bttnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(bttnYes, bttnNo, bttnCancel);
		

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == bttnYes){
			if (bookDetailController.getBook().getId() == 0)
				isNewBook = true;
					
			if (bookDetailController.saveBook())
				alertSuccess(isNewBook);
		} else if (result.get() == bttnNo) {
			this.setIsChange(false);
		}
	}
	
	public void alertSuccess(Boolean isNewBook) {	
		Alert alert = new Alert(AlertType.INFORMATION);
		BookDetailController bookDetailController = BookDetailController.getInstance();
		
		alert.setTitle("Save Successful");
		
		if (isNewBook == true) {
			alert.setHeaderText("Successfully Saved: " + bookDetailController.getBook());
		} else {
			alert.setHeaderText("Successfully Updated: " + bookDetailController.getBook());
		}
		
		alert.showAndWait();
	}
	
	public void alertLock() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Save Unsuccessful");
		alert.setHeaderText("Book Record has Changed Since Opened.");
		alert.setContentText("Please Return to Book List to Fetch Most Recent Record.");
		alert.showAndWait();
	}
	
	public void setRootBorderPane(BorderPane menuBorderPane) {
		this.borderPane = menuBorderPane;
	}
	
	public boolean getIsChange() {
		return this.isChange;
	}

	public void setIsChange(boolean isChange) {
		this.isChange = isChange;
	}
}
