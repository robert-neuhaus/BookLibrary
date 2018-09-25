package controller;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Book;

public class BookDetailController {

	private static Logger logger = LogManager.getLogger();
	
	@FXML private TextField txtFldTtl;
	@FXML private TextArea txtAreaSmmry;
	@FXML private TextField txtFldYrPblshd;
	@FXML private TextField txtFldIsbn;
	@FXML private TextField txtFldDtAdded;
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
		txtFldTtl.setText(book.getTitle());
		
		txtAreaSmmry.setText(book.getSummary());
		txtAreaSmmry.setWrapText(true);
		
		txtFldYrPblshd.setText(book.getYearPublished());
		
		txtFldDtAdded.setText(book.getDateAdded());
		
		txtFldIsbn.setText(book.getIsbn());
	}

}
