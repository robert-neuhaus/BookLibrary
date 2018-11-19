package controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import alert.EditAuthorBook;
import exception.ValidationException;
import gateway.AuthorTableGateway;
import gateway.BookTableGateway;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Audit;
import model.Author;
import model.AuthorBook;
import model.Book;
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
	@FXML private TableView<AuthorBook> tblVwBooks;
	
	private static Logger logger = LogManager.getLogger();
	private static AuthorDetailController instance = null;
	private Author author;
	
	private ObservableList<AuthorBook> authorBooks;
	private HashMap<AuthorBook, AuthorBook> abChanges = new HashMap<>();
	private LinkedHashMap<AuthorBook, String> abAddDeletes = new LinkedHashMap<>();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
		AuthorDetailController authorDetailController = AuthorDetailController.getInstance();
		
		abChanges.clear();
		abAddDeletes.clear();
		
		txtFldFirstName.setText(author.getFirstName());
		OnChangeListener.setOnChangeListener(txtFldFirstName, btnSave, authorDetailController);
		
		txtFldLastName.setText(author.getLastName());
		OnChangeListener.setOnChangeListener(txtFldLastName, btnSave, authorDetailController);
		
		if (author.getDOBDate() != null) {
			dtPckrDOB.setValue(author.getDOBDate());
		}
		OnChangeListener.setOnChangeListener(dtPckrDOB, btnSave, authorDetailController);
		
		txtAreaWebsite.setText(author.getWebsite());
		OnChangeListener.setOnChangeListener(txtAreaWebsite, btnSave, authorDetailController);
		
		txtFldGender.setText(author.getGender());
		OnChangeListener.setOnChangeListener(txtFldGender, btnSave, authorDetailController);
		
		if (author.getDateAdded() != null)
			lblDtAdded.setText(author.getDateAdded().format(formatter));
		
		if (author.getLastModified() != null)
			lblLastModified.setText(author.getLastModified().format(formatter));
		
		if (this.getAuthor().getId() == -1)
			btnAuditTrail.setDisable(true);
		else
			btnAuditTrail.setDisable(false);
		
		this.authorBooks = FXCollections.observableArrayList(this.getAuthor().getBooks());
		tblVwBooks.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>(
				"bookSimpleString"));
		tblVwBooks.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>(
				"royaltySimpleString"));
	
		tblVwBooks.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                	AuthorBook selected = tblVwBooks.getSelectionModel().getSelectedItem();                   
                	logger.info("double-clicked " + selected);
                	if (selected != null) {
                		EditAuthorBook editAuthor = new EditAuthorBook(selected, "edit", selected.getBook().getTitle());
            			AuthorBook newAuthorBook = editAuthor.getAuthorBook();
            			if (newAuthorBook != null) {
            				authorBooks.set(authorBooks.indexOf(selected), newAuthorBook);
            			}
                	}               		
                }
            }
        });
		
		tblVwBooks.setItems(this.authorBooks);	
		btnSave.setDisable(true);
	}
	
	@FXML public void handleButtonAction(ActionEvent action) {
		Object source = action.getSource();
		AuthorBook selected = null;
		
		if (tblVwBooks.getSelectionModel().getSelectedItem() != null) {
			selected = tblVwBooks.getSelectionModel().getSelectedItem(); 
		}
		
		//Save
		if(source == btnSave) {	
			save();
		}
		
		//Audit Trail
		else if (source == btnAuditTrail) {
			try {
				MasterController.getInstance().changeView("../view/view_auditTrail.fxml", 
						new AuditTrailAuthorController(this.getAuthor()), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//Edit
		else if (source == btnEdit && selected != null) {
			EditAuthorBook editAuthor = new EditAuthorBook(selected, "Edit Author", selected.getBook().getTitle());
			AuthorBook newAuthorBook = editAuthor.getAuthorBook();
			
			if (newAuthorBook != null) {
				
				//Ensures only most recent change to an authorBook is added to change list
				if (this.abChanges.containsValue(selected)) {
					for (AuthorBook key : abChanges.keySet()) {
						if (abChanges.get(key).equals(selected)) {
							abChanges.replace(key, newAuthorBook);
						}
					}
				} else {
					abChanges.put(selected, newAuthorBook);
				}
				
				authorBooks.set(authorBooks.indexOf(selected), newAuthorBook);
				btnSave.setDisable(false);
				MasterController.getInstance().setIsAuthorChange(true);
			}
		}
		
		//Add
		else if (source == btnAdd) {
			AuthorBook newAuthorBook = new AuthorBook();
			newAuthorBook.setAuthor(this.getAuthor());
			EditAuthorBook editAuthor = new EditAuthorBook(newAuthorBook, "Add Author", null);
			newAuthorBook = editAuthor.getAuthorBook();
			if (newAuthorBook != null) {
				authorBooks.add(newAuthorBook);
				abAddDeletes.put(newAuthorBook, "add");
				btnSave.setDisable(false);
				MasterController.getInstance().setIsAuthorChange(true);
			}
		}
		
		//Delete
		else if (source == btnDelete && selected != null) {
			authorBooks.remove(selected);
			abAddDeletes.put(selected, "delete");
			btnSave.setDisable(false);
			MasterController.getInstance().setIsAuthorChange(true);
		}
	}
	
	public Boolean save() {
		AuthorTableGateway authorTableGateway = null;
		Boolean isNewAuthor = true;
		Author oldAuthor = (Author) this.getAuthor().copy();
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
					isNewAuthor = false;
				
				authorTableGateway.updateAuthor(this.author);
				
				//Commit edited authorBook relations to DB
				for (Entry<AuthorBook, AuthorBook> entry : abChanges.entrySet()) {
					authorTableGateway.updateAuthorBook(entry.getValue());
				}
				
				//Commit added and deleted authorBook relations to DB
				for (Entry<AuthorBook, String> entry : abAddDeletes.entrySet()) {
					if (entry.getValue().equals("add")) {
						authorTableGateway.addAuthorBook(entry.getKey());
					}else if (entry.getValue().equals("delete")) {
						authorTableGateway.deleteAuthorBook(entry.getKey());
					}
				}
				
				addAudits(oldAuthor, this.getAuthor(), abChanges, abAddDeletes);
				abChanges.clear();
				abAddDeletes.clear();
				
				if (isNewAuthor == true) {
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
			ValidationException.showErrors(ve, lblStatus);
			
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		MasterController.getInstance().setIsAuthorChange(false);
		btnSave.setDisable(true);
		
		return true;
	}
	
	public Boolean isSavable() throws Exception {
		int id = this.getAuthor().getId();
		LocalDateTime lastModifiedOld = this.getAuthor().getLastModified();
		
		if (id == -1 || lastModifiedOld.equals(
				AuthorTableGateway.getInstance().getLastModified(author.getId()))) {		
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
			masterController.alertLock();

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
		
		if (this.authorBooks.isEmpty()) {
			exception = new InvalidField(tblVwBooks, lblBooks, 
					"At least one author is required.");
			exceptions.add(exception);
		}else {	
			for (AuthorBook ab : authorBooks) {
				if (ab.getRoyalty().compareTo(new BigDecimal(1)) == 1
						|| ab.getRoyalty().compareTo(new BigDecimal(0)) == -1) {			
					exception = new InvalidField(tblVwBooks, lblBooks, 
							"All royalties must be between 0% and 100%.");
					exceptions.add(exception);
					break;
				}
			}
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
		tblVwBooks.getStyleClass().remove("invalid_control");
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
	
	public List<AuthorBook> getAuthorBooks() {
		return this.authorBooks;
	}
	
	public void addAudits(Author oldAuthor, Author newAuthor, HashMap<AuthorBook, AuthorBook> abChanges, 
			LinkedHashMap<AuthorBook, String> abAddDeletes) throws Exception {
		
		List<Audit> authorChanges = new ArrayList<Audit>();
		List<Audit> bookChanges = new ArrayList<Audit>();
		
		if (oldAuthor.getId() == -1) {
			authorChanges.add(new Audit(newAuthor.getId(), "Author Added."));
		} else {		
			if (!oldAuthor.toString().equals(newAuthor.toString()))
				authorChanges.add(new Audit(newAuthor.getId(), 
						"Name changed from " 
						+ oldAuthor.toString()
						+ " to " 
						+ newAuthor.toString() 
						+ "."));
			
			if (!oldAuthor.getGender().equals(newAuthor.getGender()))
				authorChanges.add(new Audit(newAuthor.getId(), 
						"Gender changed from " 
						+ oldAuthor.getGender()
						+ " to " 
						+ newAuthor.getGender()
						+ "."));
			
			if (!oldAuthor.getWebsite().equals(newAuthor.getWebsite()))
				authorChanges.add(new Audit(newAuthor.getId(), 
						"Website changed."));
			
			if (!oldAuthor.getDOB().equals(newAuthor.getDOB()))
				authorChanges.add(new Audit(newAuthor.getId(), 
						"Date of Birth changed from " 
						+ oldAuthor.getDOB() 
						+ " to " 
						+ newAuthor.getDOB() 
						+ "."));
		}
		
		if (!abChanges.isEmpty()) {
			for (Entry<AuthorBook, AuthorBook> entry : abChanges.entrySet()) {
				if (oldAuthor.getId() > -1) {
					authorChanges.add(new Audit(newAuthor.getId(), 
							"Book "
							+ entry.getKey().getBookSimpleString()
							+ " royalty changed from " 
							+ entry.getKey().getRoyaltySimpleString() 
							+ " to "
							+ entry.getValue().getRoyaltySimpleString()
							+ "."));
				}
				
				if (entry.getKey().getBook().getId() > -1) {
					bookChanges.add(new Audit(entry.getKey().getBook().getId(), 
							"Author "
							+ entry.getKey().getAuthorSimpleString()
							+ " royalty changed from " 
							+ entry.getKey().getRoyaltySimpleString() 
							+ " to "
							+ entry.getValue().getRoyaltySimpleString()
							+ "."));
				}
			}
		}
		
		if (!abAddDeletes.isEmpty()) {
			for (Entry<AuthorBook, String> entry : abAddDeletes.entrySet()) {
				
				if (entry.getValue().equals("add")) {
					if (oldAuthor.getId() > -1) {
						authorChanges.add(new Audit(newAuthor.getId(), 
								"Book "
								+ entry.getKey().getBookSimpleString()
								+ " added with royalty " 
								+ entry.getKey().getRoyaltySimpleString() 
								+ "."));
					}					
					bookChanges.add(new Audit(entry.getKey().getBook().getId(), 
							"Author "
							+ this.getAuthor().toString()
							+ " added with royalty " 
							+ entry.getKey().getRoyaltySimpleString() 
							+ "."));
					
				}else if (entry.getValue().equals("delete")) {
					if (oldAuthor.getId() > -1) {
						authorChanges.add(new Audit(newAuthor.getId(), 
								"Book "
								+ entry.getKey().getBookSimpleString()
								+ " removed."));
					}		
					bookChanges.add(new Audit(entry.getKey().getBook().getId(), 
							"Author "
							+ this.getAuthor().toString()
							+ " removed."));
				}
				
			}
		}
				
		BookTableGateway.getInstance().addAudits(bookChanges);
		AuthorTableGateway.getInstance().addAudits(authorChanges);
	}
	
}
