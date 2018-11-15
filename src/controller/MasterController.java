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
	private boolean isBookChange = false;
	private boolean isAuthorChange = false;
	
	private MasterController(){
		
	}
	
	public static MasterController getInstance() {
		if (instance == null) {
			instance = new MasterController();
		}
		return instance;
	}
	
	public void changeView(String fxml, Object controller, Object data) {
		if (getIsBookChange() || getIsAuthorChange()) {
			alertSave();
		}
		if (!getIsBookChange() && !getIsAuthorChange()) {
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
		if (getIsBookChange() || getIsAuthorChange()) {
			alertSave();
		}
		if (!getIsBookChange() && !getIsAuthorChange()) {
			Platform.exit();
		}
	}
	
	public void alertSave() {
		Boolean isNewRecord = false;
		Alert alert = new Alert(AlertType.CONFIRMATION);
		
		alert.setTitle("Unsaved Changes");
		alert.setContentText("Would you like to save changes?");
		ButtonType bttnYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
		ButtonType bttnNo = new ButtonType("No", ButtonBar.ButtonData.OK_DONE);
		ButtonType bttnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(bttnYes, bttnNo, bttnCancel);
		
		BookDetailController bookDetailController = BookDetailController.getInstance();
		AuthorDetailController authorDetailController = AuthorDetailController.getInstance();
		
		if (this.getIsBookChange()) {
			alert.setHeaderText("Unsaved changes have been made to: "
					+ bookDetailController.getBook().getTitle());
			if (bookDetailController.getBook().getId() == 0) {
				isNewRecord = true;
			}
		}else if (this.getIsAuthorChange()) {
			alert.setHeaderText("Unsaved changes have been made to: "
					+ authorDetailController.getAuthor().toString());
			if (authorDetailController.getAuthor().getId() == 0) {
				isNewRecord = true;
			}
		}
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == bttnYes){
			if (this.getIsBookChange()) {
				if (bookDetailController.save()) {
					alertSuccess(isNewRecord);
				}
			}else if (this.getIsAuthorChange()) {
				if (authorDetailController.save()) {
					alertSuccess(isNewRecord);
				}
			}
		} else if (result.get() == bttnNo) {
			if(this.getIsBookChange()) {
				this.setIsBookChange(false);
			}else if(this.getIsAuthorChange()) {
				this.setIsAuthorChange(false);
			}
		}
	}
	
	public void alertSuccess(Boolean isNewBook) {	
		Alert alert = new Alert(AlertType.INFORMATION);		
		alert.setTitle("Save Successful");
		
		BookDetailController bookDetailController = BookDetailController.getInstance();
		AuthorDetailController authorDetailController = AuthorDetailController.getInstance();
		
		if (this.getIsBookChange()) {
			if (isNewBook == true) {
				alert.setHeaderText("Successfully Saved: " + bookDetailController.getBook());
			} else {
				alert.setHeaderText("Successfully Updated: " + bookDetailController.getBook());
			}
			
			this.setIsBookChange(false);
		} else if(this.getIsAuthorChange()) {
			if (isNewBook == true) {
				alert.setHeaderText("Successfully Saved: " + authorDetailController.getAuthor());
			} else {
				alert.setHeaderText("Successfully Updated: " + authorDetailController.getAuthor());
			}
			
			this.setIsAuthorChange(false);
		}
		
		alert.showAndWait();
	}
	
	public void alertLock() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Save Unsuccessful");
		
		if (this.getIsBookChange()) {
			alert.setHeaderText("Book Record has Changed Since Opened.");
			alert.setContentText("Please Return to Book List to Fetch Most Recent Record.");
			alert.showAndWait();
			this.setIsBookChange(false);
		} else if (this.getIsAuthorChange()) {
			alert.setHeaderText("Author Record has Changed Since Opened.");
			alert.setContentText("Please Return to Author List to Fetch Most Recent Record.");
			alert.showAndWait();
			this.setIsAuthorChange(false);
		}
	}
	
	public void setRootBorderPane(BorderPane menuBorderPane) {
		this.borderPane = menuBorderPane;
	}
	
	public boolean getIsBookChange() {
		return this.isBookChange;
	}

	public void setIsBookChange(boolean isChange) {
		this.isBookChange = isChange;
	}
	
	public boolean getIsAuthorChange() {
		return this.isAuthorChange;
	}

	public void setIsAuthorChange(boolean isChange) {
		this.isAuthorChange = isChange;
	}
	
}
