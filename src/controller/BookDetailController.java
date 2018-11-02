package controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exception.validationException;
import gateway.BookTableGateway;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import model.Book;
import model.Publisher;

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
	@FXML private Label	lblLastModified;
	@FXML private ComboBox<Publisher> cmboBxPublisher;
	@FXML private Button btnSave;
	
	private Book book;
	
	private static BookDetailController instance = null;
	
	public BookDetailController() {
		//this.book = book;
		//MasterController.getInstance().setBook(book);
	}
	
	public static BookDetailController getInstance() {
		if (instance == null) {
			instance = new BookDetailController();
		}
		return instance;
	}
	
	@FXML public void handleButtonAction(ActionEvent action) {
		Object source = action.getSource();
		if(source == btnSave) {
			int isNewBook = 1;
			
			logger.info("Save Clicked");
			lblStatus.setText("");
			markValidAll();
			
			try {
				if (saveBook() == true) {
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
					try {
						this.setBook(BookTableGateway.getInstance().getBook(this.getBook().getId()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					initialize();
				}
									
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
//				MasterController.getInstance().setIsChange(false);
			}catch (Exception se) {
				lblStatus.setText("Failed to save changes to database.");
				lblStatus.setStyle("-fx-text-fill: red;");
//				MasterController.getInstance().setIsChange(false);
			}						
			btnSave.setDisable(true);
		}
	}
	
	public Boolean saveBook() throws Exception {
		if (this.getBook().getId() == 0 
					|| this.getBook().getLastModified().equals(
					BookTableGateway.getInstance().getLastModified(this.getBook().getId()))) {
				this.book.save(txtFldTtl.getText(), txtAreaSmmry.getText(),
					txtFldYrPblshd.getText(), txtFldIsbn.getText(), cmboBxPublisher.getSelectionModel().getSelectedItem());
				return true;
			} else {
				MasterController.getInstance().alertLock();
				return false;
			}
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
		
		if (book.getLastModified() != null)
			lblLastModified.setText(book.getLastModified().toString());
		
		ObservableList<Publisher> publishers;
		try {
			publishers = FXCollections.observableArrayList(
					BookTableGateway.getInstance().getPublishers());
			cmboBxPublisher.setItems(publishers);
			cmboBxPublisher.getSelectionModel().select(book.getPublisher());
			setOnChangeListener(cmboBxPublisher);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		btnSave.setDisable(true);
		
	}
	
	public void setOnChangeListener(Control control) {
		if (control instanceof ComboBox<?>)
			((ComboBox<?>)control).valueProperty().addListener((observable, oldValue, newValue) -> {
				    btnSave.setDisable(false);
				    MasterController.getInstance().setIsChange(true);
				});
		if (control instanceof TextInputControl)
			((TextInputControl)control).textProperty().addListener((observable, oldValue, newValue) -> {
				    btnSave.setDisable(false);
				    MasterController.getInstance().setIsChange(true);
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
	
	public void setBook(Book book) {
		this.book = book;
	}
	
	public Book getBook() {
		return this.book;
	}

}
