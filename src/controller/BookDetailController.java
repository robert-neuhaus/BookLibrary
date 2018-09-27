package controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import model.Book;

public class BookDetailController {

	private static Logger logger = LogManager.getLogger();
	
	@FXML private TextField txtFldTtl;
	@FXML private TextArea txtAreaSmmry;
	@FXML private TextField txtFldYrPblshd;
	@FXML private TextField txtFldIsbn;
	@FXML private Label lblDtAdded;
	@FXML private Label lblStatus;
	@FXML private Button btnSave;
	
	private Book book;
	
	public BookDetailController(Book book) {
		this.book = book;
	}
	
	@FXML public void handleButtonAction(ActionEvent action) throws IOException {
		Object source = action.getSource();
		if(source == btnSave) {
			logger.info("Save Clicked");	
			try {
				this.book.save(txtFldTtl.getText(), txtAreaSmmry.getText(),
						txtFldYrPblshd.getText(), txtFldIsbn.getText(), 
						LocalDateTime.parse(lblDtAdded.getText()));
				lblStatus.setText("Book saved.");
				lblStatus.setStyle("-fx-text-fill: blue;");
			} catch (Exception e) {
				logger.info(e.getMessage());
				lblStatus.setText(e.getMessage());
				lblStatus.setStyle("-fx-text-fill: red;");
			}
			
			btnSave.setDisable(true);
		}
	}
	
	public void initialize() {
		txtFldTtl.setText(book.getTitle());
		setOnChangeListener(txtFldTtl);
		
		txtAreaSmmry.setText(book.getSummary());
		txtAreaSmmry.setWrapText(true);
		setOnChangeListener(txtAreaSmmry);
		
		if (Integer.parseInt(book.getYearPublished()) < 0)
			txtFldYrPblshd.setText("");
		else 
			txtFldYrPblshd.setText(book.getYearPublished());
		
		setOnChangeListener(txtFldYrPblshd);
		
		lblDtAdded.setText(book.getDateAdded());
		
		txtFldIsbn.setText(book.getIsbn());
		setOnChangeListener(txtFldIsbn);
		
		btnSave.setDisable(true);
	}
	
	public void setOnChangeListener(TextInputControl txtInpt) {
		txtInpt.textProperty().addListener((observable, oldValue, newValue) -> {
		    btnSave.setDisable(false);
		});
	}

}
