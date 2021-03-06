package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import exception.ValidationException;
import gateway.BookTableGateway;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import model.Book;

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
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Unsaved Changes");
		alert.setHeaderText("Unsaved changes have been made to: "
				+ BookDetailController.getInstance().getBook().getTitle());
		alert.setContentText("Would you like to save changes?");
		ButtonType bttnYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
		ButtonType bttnNo = new ButtonType("No", ButtonBar.ButtonData.OK_DONE);
		ButtonType bttnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(bttnYes, bttnNo, bttnCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == bttnYes){
			try {
				if (saveBook()) {
					alertSuccess();
					this.setIsChange(false);
				}
			} catch (ValidationException ve) {
				if (ve != null)
					BookDetailController.getInstance().showErrors(ve);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (result.get() == bttnNo) {
			this.setIsChange(false);
		}
	}
	
	public void alertSuccess() {	
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Save Successful");
		//TODO: change to detect if update or add
		alert.setHeaderText("Successfully Saved: "
				+ BookDetailController.getInstance().getBook());
		alert.showAndWait();
	}
	
	public void alertLock() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Save Unsuccessful");
		//TODO: change to detect if update or add
		alert.setHeaderText("Book Record has Changed Since Opened.");
		alert.setContentText("Please Return to Book List to Fetch Most Recent Record.");
		alert.showAndWait();
	}
	
	public Boolean saveBook() throws Exception {
		Book oldBook = (Book) BookDetailController.getInstance().getBook().clone();
		
		if (BookDetailController.getInstance().saveBook()) {
			BookTableGateway.getInstance().updateBook(
					BookDetailController.getInstance().getBook());			
			BookDetailController.getInstance().addChanges(oldBook, BookDetailController.getInstance().getBook());
			return true;
		}
		return false;
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
