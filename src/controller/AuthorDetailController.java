package controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exception.ValidationException;
import gateway.AuthorTableGateway;
import gateway.BookTableGateway;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import model.Author;
import model.AuthorBook;
import model.InvalidField;
import model.Publisher;

public class AuthorDetailController {
	@FXML private TextField txtFldFirstName;
	@FXML private TextField txtFldLastName;
	@FXML private TextField txtFldGender;
	@FXML private DatePicker dtPckrDOB;
	@FXML private TextArea txtAreaWebsite;
	@FXML private Label lblDtAdded;
	@FXML private Label lblStatus;
	@FXML private Label lblFirstName;
	@FXML private Label lblLastName;
	@FXML private Label lblGender;
	@FXML private Label lblDOB;
	@FXML private Label lblWebsite;
	@FXML private Label	lblLastModified;
	@FXML private Label	lblBooks;
	@FXML private Button btnSave;
	@FXML private Button btnAuditTrail;
	@FXML private Button btnDelete;
	@FXML private Button btnEdit;
	@FXML private Button btnAdd;
	@FXML private TableView<AuthorBook> tblVwAuthors;
	
	private static Logger logger = LogManager.getLogger();
	private static AuthorDetailController instance = null;
	private Author author;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public AuthorDetailController() {
		// TODO Auto-generated constructor stub
	}
	
	public static AuthorDetailController getInstance() {
		if (instance == null) {
			instance = new AuthorDetailController();
		}
		
		return instance;
	}
	
	public void initialize() {
		txtFldFirstName.setText(author.getFirstName());
		OnChangeListener.setOnChangeListener(txtFldFirstName, btnSave);
		
		txtFldLastName.setText(author.getLastName());
		OnChangeListener.setOnChangeListener(txtFldLastName, btnSave);
		
		if (author.getDOBDate() != null) {
			dtPckrDOB.setValue(author.getDOBDate());
		}
		OnChangeListener.setOnChangeListener(dtPckrDOB, btnSave);
		
		txtAreaWebsite.setText(author.getWebsite());
		OnChangeListener.setOnChangeListener(txtAreaWebsite, btnSave);
		
		txtFldGender.setText(author.getGender());
		OnChangeListener.setOnChangeListener(txtFldGender, btnSave);
		
		btnSave.setDisable(true);
		MasterController.getInstance().setIsAuthorChange(false);
	}
	
	@FXML public void handleButtonAction(ActionEvent action) {
		Object source = action.getSource();
		
		//Save
		if(source == btnSave) {	
			saveAuthor();
		}
	}
	
	public Boolean saveAuthor() {
		AuthorTableGateway authorTableGateway = null;
		Boolean isNewBook = true;
		logger.info("Save Clicked");
		lblStatus.setText("");
		markValidAll();
		
		try {
			authorTableGateway = AuthorTableGateway.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (isSavable()) {
				if (this.getAuthor().getId() > -1)
					isNewBook = false;
				
				authorTableGateway.updateAuthor(this.author);
				
				if (isNewBook == true) {
					lblStatus.setText("Author added.");				
					logger.info("Author added.");
				} else {
					lblStatus.setText("Author updated.");				
					logger.info("Author updated.");
				}
				lblStatus.setStyle("-fx-text-fill: blue;");
				try {
					this.setAuthor(authorTableGateway.getAuthor(
							this.getAuthor().getId()));
				} catch (Exception e) {
					e.printStackTrace();
				} 
				initialize();
			}
			
		}catch (ValidationException ve) {			
			ValidationErrors.showErrors(ve, lblStatus);
			
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return true;
	}
	
	public Boolean isSavable() throws Exception {
		int id = this.getAuthor().getId();
//		LocalDateTime lastModifiedOld = this.getAuthor().getLastModified();
		
		if (id == -1 /*|| lastModifiedOld.equals(
				BookTableGateway.getInstance().getLastModified(book.getId()))*/) {		
			List<InvalidField> exceptions = validateFields();
			
			if (exceptions.isEmpty()) {
				this.getAuthor().setFirstName(txtFldFirstName.getText());
				this.getAuthor().setLastName(txtFldLastName.getText());
				this.getAuthor().setGender(txtFldGender.getText());
				this.getAuthor().setDOB(dtPckrDOB.getValue());
				this.getAuthor().setWebsite(txtAreaWebsite.getText());
			} else 
				throw new ValidationException(exceptions);			
			
			return true;
		
		} else {
			MasterController masterController = MasterController.getInstance();
//			masterController.alertLock();
			masterController.setIsAuthorChange(false);

			return false;
		}
	}
	
	public List<InvalidField> validateFields() {
		List<InvalidField> exceptions = new ArrayList<InvalidField>();
		int i = 0;
		
		String validation = author.validateLastName(txtFldLastName.getText());
		InvalidField exception;
		if (validation != null){
			exception = new InvalidField(txtFldLastName, lblLastName, validation);
			exceptions.add(exception);
		}
		
		validation = author.validateFirstName(txtFldFirstName.getText());
		if (validation != null) {
			exception = new InvalidField(txtFldFirstName, lblFirstName, validation);
			exceptions.add(exception);
		}
		
		validation = author.validateGender(txtFldGender.getText());
		if (validation != null) {
			exception = new InvalidField(txtFldGender, lblGender, validation);
			exceptions.add(exception);
		}
		
		validation = author.validateDOB(dtPckrDOB.getValue());
		if (validation != null) {
			exception = new InvalidField(dtPckrDOB, lblDOB, validation);
			exceptions.add(exception);
		}
		
		validation = author.validateWebsite(txtAreaWebsite.getText());
		if (validation != null) {
			exception = new InvalidField(txtAreaWebsite, lblWebsite, validation);
			exceptions.add(exception);
		}
		
		return exceptions;
	}
	
	public void markValidAll() {
		txtFldFirstName.getStyleClass().remove("invalid_control");
		txtFldLastName.getStyleClass().remove("invalid_control");
		txtAreaWebsite.getStyleClass().remove("invalid_control");
		txtFldFirstName.getStyleClass().remove("invalid_control");
		txtFldGender.getStyleClass().remove("invalid_control");
		dtPckrDOB.getStyleClass().remove("invalid_control");
		tblVwAuthors.getStyleClass().remove("invalid_control");
		lblDtAdded.getStyleClass().remove("invalid_label");
		lblStatus.getStyleClass().remove("invalid_label");
		lblFirstName.getStyleClass().remove("invalid_label");
		lblLastName.getStyleClass().remove("invalid_label");
		lblGender.getStyleClass().remove("invalid_label");
		lblWebsite.getStyleClass().remove("invalid_label");	
		lblDOB.getStyleClass().remove("invalid_label");	
		lblBooks.getStyleClass().remove("invalid_label");
	}
	
	public void setAuthor(Author author) {
		this.author = author;
	}

	public Author getAuthor() {
		return this.author;
	}
	
}
