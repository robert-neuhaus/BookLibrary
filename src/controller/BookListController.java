package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gateway.BookTableGateway;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import model.Book;

public class BookListController implements Initializable {

	private static Logger logger = LogManager.getLogger();
	
	@FXML private ListView<Book> LstVwBookList;
	@FXML private Button btnDelete;
	@FXML private Label lblStatus;
	
	public BookListController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setBookList();
		LstVwBookList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                	Book selected = LstVwBookList.getSelectionModel().getSelectedItem();                   
                	logger.info("double-clicked " + selected);
                	if (selected != null)
                		BookDetailController.getInstance().setBook(selected);
	        			MasterController.getInstance().changeView(
	        					"../view/view_bookDetail.fxml", 
	        					BookDetailController.getInstance(), 
	        					selected);       			              	
                }
            }
        });
	}
	
	@FXML private void handleMenuAction(ActionEvent action) throws IOException {
		Object source = action.getSource();
		Book book = LstVwBookList.getSelectionModel().getSelectedItem();
		
		if (source == btnDelete)
			try {
				BookTableGateway.getInstance().deleteBook(book);
				lblStatus.setStyle("-fx-text-fill: blue;");
				lblStatus.setText("Book deleted: " + book.toString());
				setBookList();
			} catch (Exception e) {
				lblStatus.setStyle("-fx-text-fill: red;");
				lblStatus.setText("Failed to delete book: " + book.toString());	
			}
	}
	
	public void setBookList() {
		ObservableList<Book> books = null;
		
		try {
			books = FXCollections.observableArrayList(BookTableGateway.getInstance().getBooks());
		} catch (Exception e) {
			lblStatus.setStyle("-fx-text-fill: red;");
			lblStatus.setText("Failed to fetch books from database. ");	
			btnDelete.setDisable(true);
		}
			
		if (books != null) {
			LstVwBookList.setItems(books);
		}
	}
}
