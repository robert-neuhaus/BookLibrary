package controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import alert.EditAuthor;
import exception.ValidationException;
import gateway.AuthorTableGateway;
import gateway.BookTableGateway;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import model.Audit;
import model.Author;
import model.AuthorBook;
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
	@FXML private Label	lblAuthors;
	@FXML private ComboBox<Publisher> cmboBxPublisher;
	@FXML private Button btnSave;
	@FXML private Button btnAuditTrail;
	@FXML private Button btnDelete;
	@FXML private Button btnEdit;
	@FXML private Button btnAdd;
	@FXML private Button btnApply;
	@FXML private TableView<AuthorBook> tblVwAuthors;
	

	
	private Book book;
	private ObservableList<AuthorBook> authorBooks;
	private HashMap<AuthorBook, AuthorBook> abChanges = new HashMap<>();
	private List<AuthorBook> abAdditions = new ArrayList<>();
	private List<AuthorBook> abDeletions = new ArrayList<>();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	private static BookDetailController instance = null;
	
	public BookDetailController() {

	}
	
	public static BookDetailController getInstance() {
		if (instance == null) {
			instance = new BookDetailController();
		}
		
		return instance;
	}
	
	@FXML public void handleButtonAction(ActionEvent action) {
		Object source = action.getSource();
		AuthorBook selected = null;
		
		if (tblVwAuthors.getSelectionModel().getSelectedItem() != null) {
			selected = tblVwAuthors.getSelectionModel().getSelectedItem(); 
		}
		
		//Save
		if(source == btnSave) {	
			saveBook();
		}
		
		//Audit Trail
		else if (source == btnAuditTrail) {
			try {
				MasterController.getInstance().changeView("../view/view_auditTrail.fxml", 
						new AuditTrailController(this.getBook()), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//Edit
		else if (source == btnEdit && selected != null) {
			EditAuthor editAuthor = new EditAuthor(selected, "edit");
			AuthorBook newAuthorBook = editAuthor.getAuthorBook();
			if (newAuthorBook != null) {
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
				MasterController.getInstance().setIsChange(true);
			}
		}
		
		//Add
		else if (source == btnAdd) {
			AuthorBook newAuthorBook = new AuthorBook();
			newAuthorBook.setBook(this.getBook());
			EditAuthor editAuthor = new EditAuthor(newAuthorBook, "add");
			newAuthorBook = editAuthor.getAuthorBook();
			if (newAuthorBook != null) {
				authorBooks.add(newAuthorBook);
				abAdditions.add(newAuthorBook);
				btnSave.setDisable(false);
				MasterController.getInstance().setIsChange(true);
			}
		}
		
		//Delete
		else if (source == btnDelete && selected != null) {
			authorBooks.remove(selected);
			abDeletions.add(selected);
			btnSave.setDisable(false);
			MasterController.getInstance().setIsChange(true);
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
		
		if (this.authorBooks.isEmpty()) {
			exception = new InvalidField(tblVwAuthors, lblAuthors, 
					"At least one author is required.");
			exceptions.add(exception);
		}
		
		return exceptions;
	}
	
	public Boolean saveBook() {
		Boolean isNewBook = true;
		Book oldBook = (Book) this.getBook().clone();
		AuthorTableGateway authorTableGateway = null;
		logger.info("Save Clicked");
		lblStatus.setText("");
		markValidAll();
		
		try {
			authorTableGateway = AuthorTableGateway.getInstance();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			if (isSavable()) {
				if (this.book.getId() > 0)
					isNewBook = false;
				
				BookTableGateway.getInstance().updateBook(book);
				
				//Commit edited authorBook relations to DB
				for (Entry<AuthorBook, AuthorBook> entry : abChanges.entrySet()) {
					authorTableGateway.updateAuthorBook(entry.getValue());
				}
				
				//Commit added authorBook relations to DB
				for (AuthorBook ab : abAdditions) {
					authorTableGateway.updateAuthorBook(ab);
				}
						
				//Commit deleted authorBook relations to DB
				for (AuthorBook ab : abDeletions) {
					authorTableGateway.deleteAuthorBook(ab);
				}
				
				addAudits(oldBook, this.getBook(), abChanges, abAdditions, abDeletions);
				abChanges.clear();
				abAdditions.clear();
				abDeletions.clear();
				
				if (isNewBook == true) {
					lblStatus.setText("Book added.");				
					logger.info("Book added.");
				} else {
					lblStatus.setText("Book updated.");				
					logger.info("Book updated.");
				}
				lblStatus.setStyle("-fx-text-fill: blue;");
				try {
					this.setBook(BookTableGateway.getInstance().getBook(
							this.getBook().getId()));
				} catch (Exception e) {
					e.printStackTrace();
				} 
				initialize();
			}								
		}catch (ValidationException ve) {			
			showErrors(ve);
			
			return false;
		}catch (Exception se) {
			lblStatus.setText("Failed to save changes to database.");
			lblStatus.setStyle("-fx-text-fill: red;");
			
			return false;
		}		
		
		btnSave.setDisable(true);
		
		return true;
	}
	
	public Boolean isSavable() throws Exception {
		int id = book.getId();
		LocalDateTime lastModifiedOld = book.getLastModified();
		
		if (id == 0 || lastModifiedOld.equals(
				BookTableGateway.getInstance().getLastModified(book.getId()))) {		
			List<InvalidField> exceptions = validateFields();
			
			if (exceptions.isEmpty()) {
				this.book.setTitle(txtFldTtl.getText());
				this.book.setYearPublished(Integer.parseInt(txtFldYrPblshd.getText()));
				this.book.setIsbn(txtFldIsbn.getText());
				this.book.setSummary(txtAreaSmmry.getText());
				this.book.setPublisher(cmboBxPublisher.getValue());
			} else 
				throw new ValidationException(exceptions);			
			
			return true;
		
		} else {
			MasterController masterController = MasterController.getInstance();
			masterController.alertLock();
			masterController.setIsChange(false);

			return false;
		}
	}
	
	public void showErrors(ValidationException ve) {
		List<InvalidField> exceptions = ve.getCauses();
		lblStatus.setStyle("-fx-text-fill: red;");
		exceptions.get(0).getControl().requestFocus();
		
		for (InvalidField exception : exceptions) {
			logger.info(exception.getMessage());
			lblStatus.setText(lblStatus.getText() + "\n" + exception.getMessage());
			exception.getLabel().getStyleClass().add("invalid_label");
			exception.getControl().getStyleClass().add("invalid_control");
		}
	}
	
	public void initialize() {
		
		abChanges.clear();
		abAdditions.clear();
		abDeletions.clear();
		
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
			e1.printStackTrace();
		}
		
		if (this.getBook().getId() == 0)
			btnAuditTrail.setDisable(true);
		else
			btnAuditTrail.setDisable(false);
		
		this.authorBooks = FXCollections.observableArrayList(this.book.getAuthors());
		tblVwAuthors.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>(
				"authorSimpleString"));
		tblVwAuthors.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>(
				"royaltySimpleString"));
	
		tblVwAuthors.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                	AuthorBook selected = tblVwAuthors.getSelectionModel().getSelectedItem();                   
                	logger.info("double-clicked " + selected);
                	if (selected != null) {
                		EditAuthor editAuthor = new EditAuthor(selected, "edit");
            			AuthorBook newAuthorBook = editAuthor.getAuthorBook();
            			if (newAuthorBook != null) {
            				authorBooks.set(authorBooks.indexOf(selected), newAuthorBook);
            			}
                	}               		
                }
            }
        });
		
		tblVwAuthors.setItems(this.authorBooks);	
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
		txtFldTtl.getStyleClass().remove("invalid_control");
		txtAreaSmmry.getStyleClass().remove("invalid_control");
		txtFldYrPblshd.getStyleClass().remove("invalid_control");
		txtFldIsbn.getStyleClass().remove("invalid_control");
		tblVwAuthors.getStyleClass().remove("invalid_control");
		lblDtAdded.getStyleClass().remove("invalid_label");
		lblStatus.getStyleClass().remove("invalid_label");
		lblTtl.getStyleClass().remove("invalid_label");
		lblSmmry.getStyleClass().remove("invalid_label");
		lblYrPblshd.getStyleClass().remove("invalid_label");
		lblIsbn.getStyleClass().remove("invalid_label");	
		lblAuthors.getStyleClass().remove("invalid_label");
	}
	
	public void setBook(Book book) {
		this.book = book;
	}
	
	public Book getBook() {
		return this.book;
	}
	
	public List<AuthorBook> getAuthorBooks() {
		return this.authorBooks;
	}
	
	public void addAudits(Book oldBook, Book newBook, HashMap<AuthorBook, AuthorBook> abChanges, 
			List<AuthorBook> abAdditions, List<AuthorBook> abDeletions) throws Exception {
		
		List<Audit> changes = new ArrayList<Audit>();
		
		if (oldBook.getId() == 0) {
			changes.add(new Audit(newBook.getId(), "Book Added."));
		} else {		
			if (!oldBook.getIsbn().equals(newBook.getIsbn()))
				changes.add(new Audit(newBook.getId(), 
						"ISBN changed from " 
						+ oldBook.getIsbn() 
						+ " to " 
						+ newBook.getIsbn() 
						+ "."));
			
			if (!oldBook.getPublisher().equals(newBook.getPublisher()))
				changes.add(new Audit(newBook.getId(), 
						"Publisher changed from " 
						+ oldBook.getPublisher() 
						+ " to " 
						+ newBook.getPublisher() 
						+ "."));
			
			if (!oldBook.getSummary().equals(newBook.getSummary()))
				changes.add(new Audit(newBook.getId(), 
						"Summary changed."));
			
			if (!oldBook.getTitle().equals(newBook.getTitle()))
				changes.add(new Audit(newBook.getId(), 
						"Title changed from " 
						+ oldBook.getTitle() 
						+ " to " 
						+ newBook.getTitle() 
						+ "."));
			
			if (oldBook.getYearPublished() != newBook.getYearPublished())
				changes.add(new Audit(newBook.getId(), 
						"Year Published changed from " 
						+ oldBook.getYearPublished() 
						+ " to " 
						+ newBook.getYearPublished() 
						+ "."));
			
			if (!abChanges.isEmpty())
				for (Entry<AuthorBook, AuthorBook> entry : abChanges.entrySet()) {
					changes.add(new Audit(newBook.getId(), 
							"Author "
							+ entry.getKey().getAuthorSimpleString()
							+ " royalty changed from " 
							+ entry.getKey().getRoyaltySimpleString() 
							+ " to "
							+ entry.getValue().getRoyaltySimpleString()
							+ "."));
			}
			
			if (!abAdditions.isEmpty())
				for (AuthorBook ab : abAdditions) {
					changes.add(new Audit(newBook.getId(), 
							"Author "
							+ ab.getAuthorSimpleString()
							+ " added with royalty " 
							+ ab.getRoyaltySimpleString() 
							+ "."));
			}
			
			if (!abDeletions.isEmpty())
				for (AuthorBook ab : abDeletions) {
					changes.add(new Audit(newBook.getId(), 
							"Author "
							+ ab.getAuthorSimpleString()
							+ " removed."));
			}
			
		}
		
		BookTableGateway.getInstance().addAudits(changes);
	}
	
}
