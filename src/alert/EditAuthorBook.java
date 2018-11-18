package alert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import controller.AuthorDetailController;
import controller.BookDetailController;
import gateway.AuthorTableGateway;
import gateway.BookTableGateway;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.FlowPane;
import javafx.stage.WindowEvent;
import model.Author;
import model.AuthorBook;
import model.Book;

public class EditAuthorBook {
	
	private AuthorBook authorBook;
	private Boolean valid = true;

	public EditAuthorBook(AuthorBook authorBook, String mode, String label) {
		Alert alert = new Alert(AlertType.NONE);
		ButtonType btnApply = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
		ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		FlowPane flowPane = new FlowPane();
		ComboBox<Author> cmboBxAuthors = new ComboBox<Author>();
		ComboBox<Book> cmboBxBooks = new ComboBox<Book>();
		Label lblName = new Label();
		TextField txtFldRoyalty = new TextField();
		AuthorTableGateway authorTableGateway = null;
		BookTableGateway bookTableGateway = null;
		BigDecimal royalty = new BigDecimal(0);
		AuthorBook newAuthorBook = (AuthorBook) authorBook.copy();
		
		alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
			public void handle(DialogEvent e) {  
			    if(!valid) {
			        e.consume();
			    }
			}
		});
		
		try {
			authorTableGateway = AuthorTableGateway.getInstance();
			bookTableGateway = BookTableGateway.getInstance();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		
		List<AuthorBook> authorBooks = null;
		ObservableList<Author> authorList = null;
		ObservableList<Book> bookList = null;
		
		if (mode.equals("Add Book")) {
			try {			
				authorList = FXCollections.observableArrayList(
						authorTableGateway.getAuthors());
				authorBooks = 
						BookDetailController.getInstance().getAuthorBooks();
				
				//Get only the authors not already associated with book
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
		} else if (mode.equals("Add Author")) {
			try {
				bookList = FXCollections.observableArrayList(
						bookTableGateway.getBooks());
				
				authorBooks = 
						AuthorDetailController.getInstance().getAuthorBooks();
				
				//Get only the books not already associated with author
				for (int i = 0; i < bookList.size(); i++) {
					int aID = bookList.get(i).getId();
					for (AuthorBook ab : authorBooks) {
						int abID = ab.getBook().getId();
						if (i > -1 && aID == abID && abID != 0) {
							bookList.remove(i);
							i--;
						}
					}
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	
		try {			
			if (mode.contains("Edit")) {
				alert.setTitle(mode);
				lblName.setText(label);
				txtFldRoyalty.setText(newAuthorBook.getRoyalty().toString());
				flowPane.getChildren().addAll(lblName, txtFldRoyalty);
				newAuthorBook.setNewRecord(false);
			} else if (mode.contains("Add")){
				alert.setTitle(mode);
				if (mode.contains("Book")) {
					cmboBxAuthors.setItems(authorList);
					cmboBxAuthors.getSelectionModel().selectFirst();
					flowPane.getChildren().addAll(cmboBxAuthors, txtFldRoyalty);
				} else if (mode.contains("Author")) {
					cmboBxBooks.setItems(bookList);
					cmboBxBooks.getSelectionModel().selectFirst();
					flowPane.getChildren().addAll(cmboBxBooks, txtFldRoyalty);
				}
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
				valid = false;
			}
			
			if (new BigDecimal(1).compareTo(royalty) == 1 
					|| new BigDecimal(0).compareTo(royalty) == -1) {
				valid = false;
			} else {
				valid = true;
			}
			
			if (mode.contains("Add")) {
				if (mode.equals("Add Book"))
					newAuthorBook.setAuthor(cmboBxAuthors.getSelectionModel().getSelectedItem());
				else if (mode.equals("Add Author"))
					newAuthorBook.setBook(cmboBxBooks.getSelectionModel().getSelectedItem());
			}
			
			newAuthorBook.setRoyalty(royalty);
			
			this.authorBook = newAuthorBook;
		}		
	}
	
	public AuthorBook getAuthorBook() {
		return this.authorBook;
	}
}
