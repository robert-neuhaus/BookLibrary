package controller;

import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exception.validationException;
import gateway.BookTableGateway;
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
	@FXML private Label lblTtl;
	@FXML private Label lblSmmry;
	@FXML private Label lblIsbn;
	@FXML private Label lblYrPblshd;
	@FXML private Button btnSave;
	
	private Book book;
	
	public BookDetailController(Book book) {
		this.book = book;
	}
	
	@FXML public void handleButtonAction(ActionEvent action) throws Exception {
		Object source = action.getSource();
		int isNewBook = 1;
		
		if(source == btnSave) {
			logger.info("Save Clicked");
			lblStatus.setText("");
			markValidAll();
			
			try {
				this.book.save(txtFldTtl.getText(), txtAreaSmmry.getText(),
						txtFldYrPblshd.getText(), txtFldIsbn.getText());
				
					if (this.book.getId() > 0)
						isNewBook = 0;
					
					BookTableGateway.getInstance().updateBook(book);
										
					if (isNewBook == 1) {
						lblStatus.setText("Book added.");				
						logger.info("Book added.");
					} else {
						lblStatus.setText("Book updated.");				
						logger.info("Book updated.");
					}
					lblStatus.setStyle("-fx-text-fill: blue;");
					
					
			}catch (validationException ve) {			
				List<Throwable> causes = ve.getCauses();
				Label lblSource;
				TextInputControl txtInptSource = getTxtInptSource(causes.get(0).getMessage());
				txtInptSource.requestFocus();
				lblStatus.setStyle("-fx-text-fill: red;");
				
				for (Throwable cause : causes) {
					logger.info(cause);
					lblStatus.setText(lblStatus.getText() + "\n" + cause.getMessage());
					lblSource = getLblSource(cause.getMessage());
					lblSource.getStyleClass().add("invalid");
					txtInptSource = getTxtInptSource(cause.getMessage());
					txtInptSource.getStyleClass().add("invalid");
				}
			}catch (SQLException se) {
				lblStatus.setText("Failed to save changes to database.");
				lblStatus.setStyle("-fx-text-fill: red;");
				}			
			}			
			btnSave.setDisable(true);
	}
	
	public void initialize() {
		txtFldTtl.setText(book.getTitle());
		setOnChangeListener(txtFldTtl);
		
		txtAreaSmmry.setText(book.getSummary());
		txtAreaSmmry.setWrapText(true);
		setOnChangeListener(txtAreaSmmry);
		
		if (book.getYearPublished() < 0)
			txtFldYrPblshd.setText("");
		else 
			txtFldYrPblshd.setText(Integer.toString(book.getYearPublished()));		
		setOnChangeListener(txtFldYrPblshd);
		
		if (book.getDateAdded() != null)
			lblDtAdded.setText(book.getDateAdded().toString());
		
		txtFldIsbn.setText(book.getIsbn());
		setOnChangeListener(txtFldIsbn);
		
		btnSave.setDisable(true);
		
	}
	
	public void setOnChangeListener(TextInputControl txtInpt) {
		txtInpt.textProperty().addListener((observable, oldValue, newValue) -> {
		    btnSave.setDisable(false);
		});
	}
	
	public TextInputControl getTxtInptSource(String errMsg) {
		if (errMsg.equals("*Unable to read year published.") 
				|| errMsg.equals("*Year published cannot be later than current year."))
			return this.txtFldYrPblshd;
		else if (errMsg.equals("*Title of book must be provided and must be 255 characters or fewer."))
			return this.txtFldTtl;
		else if (errMsg.equals("*Summary must be 65,535 characters or fewer."))
			return this.txtAreaSmmry;
		else if (errMsg.equals("*Year published cannot be later than current year."))
			return this.txtFldYrPblshd;
		else
			return this.txtFldIsbn;		
	}
	
	 public Label getLblSource(String errMsg) {
			if (errMsg.equals("*Unable to read year published.") 
					|| errMsg.equals("*Year published cannot be later than current year."))
				return this.lblYrPblshd;
			else if (errMsg.equals("*Title of book must be provided and must be 255 characters or fewer."))
				return this.lblTtl;
			else if (errMsg.equals("*Summary must be 65,535 characters or fewer."))
				return this.lblSmmry;
			else if (errMsg.equals("*Year published cannot be later than current year."))
				return this.lblYrPblshd;
			else
				return this.lblIsbn;		
		}
	
	
	public void markValidAll() {
		txtFldTtl.getStyleClass().remove("invalid");
		txtAreaSmmry.getStyleClass().remove("invalid");
		txtFldYrPblshd.getStyleClass().remove("invalid");
		txtFldIsbn.getStyleClass().remove("invalid");
		lblDtAdded.getStyleClass().remove("invalid");
		lblStatus.getStyleClass().remove("invalid");
		lblTtl.getStyleClass().remove("invalid");
		lblSmmry.getStyleClass().remove("invalid");
		lblYrPblshd.getStyleClass().remove("invalid");
		lblIsbn.getStyleClass().remove("invalid");		
	}

}
