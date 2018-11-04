package controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exception.ValidationException;
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
import model.Audit;
import model.Book;
import model.InvalidField;
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
	@FXML private Button btnAuditTrail;
	
	private Book book;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
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
			Boolean isNewBook = true;
			Book oldBook = (Book) this.getBook().clone();
			logger.info("Save Clicked");
			lblStatus.setText("");
			markValidAll();
			
			try {
				if (saveBook() == true) {
					if (this.book.getId() > 0)
						isNewBook = false;
					
					BookTableGateway.getInstance().updateBook(book);
					addChanges(oldBook, this.getBook());
														
					if (isNewBook == true) {
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
									
			}catch (ValidationException ve) {			
				List<InvalidField> exceptions = ve.getCauses();
				lblStatus.setStyle("-fx-text-fill: red;");
				exceptions.get(0).getControl().requestFocus();
				for (InvalidField exception : exceptions) {
					logger.info(exception.getMessage());
					lblStatus.setText(lblStatus.getText() + "\n" + exception.getMessage());
					exception.getLabel().getStyleClass().add("invalid");
					exception.getControl().getStyleClass().add("invalid");
				}
			}catch (Exception se) {
				lblStatus.setText("Failed to save changes to database.");
				lblStatus.setStyle("-fx-text-fill: red;");
			}						
			btnSave.setDisable(true);
		}
		
		else if (source == btnAuditTrail) {
			try {
				MasterController.getInstance().changeView("../view/view_auditTrail.fxml", 
						new AuditTrailController(this.getBook()), null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public List<InvalidField> validateFields() {
		List<InvalidField> exceptions = new ArrayList<InvalidField>();
		
		String validation = book.validateTitle(txtFldTtl.getText());
		InvalidField exception;
		if (validation != null){
			exception = new InvalidField(txtFldTtl, lblTtl, validation);
			exceptions.add(exception);
		}
		
		validation = book.validateYearPublished(txtFldYrPblshd.getText());
		if (validation != null) {
			exception = new InvalidField(txtFldYrPblshd, lblYrPblshd, validation);
			exceptions.add(exception);
		}
		
		validation = book.validateIsbn(txtFldIsbn.getText());
		if (validation != null) {
			exception = new InvalidField(txtFldIsbn, lblIsbn, validation);
			exceptions.add(exception);
		}
		
		validation = book.validateSummary(txtAreaSmmry.getText());
		if (validation != null) {
			exception = new InvalidField(txtAreaSmmry, lblSmmry, validation);
			exceptions.add(exception);
		}
		
		return exceptions;
	}
	
	public Boolean saveBook() throws Exception {
		
		if ( this.getBook().getId() == 0 
				|| this.getBook().getLastModified().equals(
					BookTableGateway.getInstance().getLastModified(
							this.getBook().getId()))) {
			
			List<InvalidField> exceptions = validateFields();
			
			if (exceptions.isEmpty()) {
				this.book.setTitle(txtFldTtl.getText());
				this.book.setYearPublished(Integer.parseInt(txtFldYrPblshd.getText()));
				this.book.setIsbn(txtFldIsbn.getText());
				this.book.setSummary(txtAreaSmmry.getText());
				this.book.setPublisher(cmboBxPublisher.getValue());
			}
			else 
				throw new ValidationException(exceptions);
			
			return true;
		} else {
			MasterController.getInstance().alertLock();
			MasterController.getInstance().setIsChange(false);
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
			lblDtAdded.setText(book.getDateAdded().format(formatter));
		
		txtFldIsbn.setText(book.getIsbn());
		setOnChangeListener(txtFldIsbn);
		
		if (book.getLastModified() != null)
			lblLastModified.setText(book.getLastModified().format(formatter));
		
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
		
		if (this.getBook().getId() == 0)
			btnAuditTrail.setDisable(true);
		else
			btnAuditTrail.setDisable(false);
		
		btnSave.setDisable(true);
		MasterController.getInstance().setIsChange(false);
		
	}
	
	public void setOnChangeListener(Control control) {
		if (control instanceof ComboBox<?>)
			((ComboBox<?>)control).valueProperty().addListener((observable, oldValue, newValue) -> {
				    btnSave.setDisable(false);
				    MasterController.getInstance().setIsChange(true);
				});
		else if (control instanceof TextInputControl)
			((TextInputControl)control).textProperty().addListener((observable, oldValue, newValue) -> {
				    btnSave.setDisable(false);
				    MasterController.getInstance().setIsChange(true);
				});
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
	
	public void addChanges(Book oldBook, Book newBook) throws Exception {
		List<Audit> changes = new ArrayList<Audit>();
		
		if (oldBook.getId() == 0) {
			changes.add(new Audit(newBook.getId(), "Book Added."));
		} else {		
			if (!oldBook.getIsbn().equals(newBook.getIsbn()))
				changes.add(new Audit(newBook.getId(), "ISBN changed from " + oldBook.getIsbn() + " to " + newBook.getIsbn() + "."));
			if (!oldBook.getPublisher().equals(newBook.getPublisher()))
				changes.add(new Audit(newBook.getId(), "Publisher changed from " + oldBook.getPublisher() + " to " + newBook.getPublisher() + "."));
			if (!oldBook.getSummary().equals(newBook.getSummary()))
				changes.add(new Audit(newBook.getId(), "Summary changed."));
			if (!oldBook.getTitle().equals(newBook.getTitle()))
				changes.add(new Audit(newBook.getId(), "Title changed from " + oldBook.getTitle() + " to " + newBook.getTitle() + "."));
			if (oldBook.getYearPublished() != newBook.getYearPublished())
				changes.add(new Audit(newBook.getId(), "Year Publisher changed from " + oldBook.getYearPublished() + " to " + newBook.getYearPublished() + "."));
		}
		
		BookTableGateway.getInstance().addAudits(changes);
	}
	
}
