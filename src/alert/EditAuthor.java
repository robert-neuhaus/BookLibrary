package alert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import controller.BookDetailController;
import gateway.AuthorTableGateway;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.FlowPane;
import model.Author;
import model.AuthorBook;

public class EditAuthor {
	
	private AuthorBook authorBook;

	public EditAuthor(AuthorBook authorBook, String mode) {
		Alert alert = new Alert(AlertType.NONE);
		ButtonType btnApply = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
		ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		FlowPane flowPane = new FlowPane();
		ComboBox<Author> cmboBxAuthors = new ComboBox<Author>();
		Label lblAuthor = new Label();
		TextField txtFldRoyalty = new TextField();
		AuthorTableGateway authorTableGateway = null;
		BigDecimal royalty = new BigDecimal(0);
		AuthorBook newAuthorBook = (AuthorBook) authorBook.copy();
		
		try {
			authorTableGateway = AuthorTableGateway.getInstance();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		List<AuthorBook> authorBooks = 
				BookDetailController.getInstance().getAuthorBooks();
		ObservableList<Author> authorList = null;
		try {
			authorList = FXCollections.observableArrayList(
					authorTableGateway.getAuthors());
			for (int i = 0; i < authorList.size(); i++) {
				int aID = authorList.get(i).getId();
				for (AuthorBook ab : authorBooks) {
					int abID = ab.getAuthor().getId();
					if (i > -1 && aID == abID && abID != 0) {
						authorList.remove(i);
						i--;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		try {			
			if (mode.equals("edit")) {
				alert.setTitle("Edit Author");
				lblAuthor.setText(newAuthorBook.toString());
				txtFldRoyalty.setText(newAuthorBook.getRoyalty().toString());
				flowPane.getChildren().addAll(lblAuthor, txtFldRoyalty);
				newAuthorBook.setNewRecord(false);
			} else if (mode.equals("add")){
				alert.setTitle("Add Author");
				cmboBxAuthors.setItems(authorList);
				cmboBxAuthors.getSelectionModel().selectFirst();
				flowPane.getChildren().addAll(cmboBxAuthors, txtFldRoyalty);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		flowPane.setHgap(15);
		
		alert.getButtonTypes().setAll(btnApply, btnCancel);
		alert.getDialogPane().contentProperty().set(flowPane);
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get().equals(btnApply)) {
			
			try {
				royalty = new BigDecimal(txtFldRoyalty.getText());
			} catch (NumberFormatException ne) {
				
			}
			
			if (mode.equals("add")) {
					newAuthorBook.setAuthor(cmboBxAuthors.getSelectionModel().getSelectedItem());
			}
			
			newAuthorBook.setRoyalty(royalty);
			
			this.authorBook = newAuthorBook;
		}		
	}
	
	public AuthorBook getAuthorBook() {
		return this.authorBook;
	}
}
