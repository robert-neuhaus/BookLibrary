package controller;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.Book;

public class BookDetailController {

	private static Logger logger = LogManager.getLogger();
	
	@FXML private Label lblTitle;
	@FXML private Label lblAuthor;
	@FXML private Label lblSummary;
	@FXML private Label lblYearPublished;
	@FXML private Label lblISBN;
	@FXML private Label lblDateAdded;
	@FXML private Button btnSave;
	
	private Book book;
	
	public BookDetailController(Book book) {
		this.book = book;
	}
	
	@FXML public void handleButtonAction(ActionEvent action) throws IOException {
		Object source = action.getSource();
		if(source == btnSave) {
			logger.info("Saved");
		}
	}
	
	public void initialize() {
		lblTitle.setText(book.getTitle());
		lblTitle.setWrapText(true);
		
		lblAuthor.setText(book.getAuthor());
		lblAuthor.setWrapText(true);
		
		lblSummary.setText(book.getSummary());
		lblSummary.setWrapText(true);
		
		lblYearPublished.setText(book.getYearPublished());
		lblYearPublished.setWrapText(true);
		
		lblDateAdded.setText(book.getDateAdded());
		lblDateAdded.setWrapText(true);
		
		lblISBN.setText(book.getISBN());
		lblISBN.setWrapText(true);
	}

}
