package controller;

import java.util.List;

import gateway.BookTableGateway;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Audit;
import model.Book;

public class AuditTrailController {
	
	@FXML private TableView<Audit> tblVwAuditTrail;
	@FXML private Label lblBook;
	@FXML private Button btnBack;
	
	private Book book = null;
	private ObservableList<Audit> auditTrail = null;
	
	public AuditTrailController(Book book) {
		this.book = book;	
	}
	
	public void initialize() throws Exception {
		List<Audit> audits = BookTableGateway.getInstance().getAudits(this.getBook().getId());
		this.auditTrail = FXCollections.observableArrayList(audits);
		lblBook.setText("Audit Trail For: " + this.getBook().getTitle());
		tblVwAuditTrail.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("timestamp"));
		tblVwAuditTrail.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("message"));
		tblVwAuditTrail.setItems(this.getAuditTrail());

	}
	
	@FXML public void handleButtonAction(ActionEvent action) {
		Object source = action.getSource();
		
		if (source == btnBack) {
    		try {
				BookDetailController.getInstance().setBook(
						BookTableGateway.getInstance().getBook(book.getId()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MasterController.getInstance().changeView(
					"../view/view_bookDetail.fxml", 
					BookDetailController.getInstance(), 
					null); 
		}
	}
	
	public void setBook(Book book) {
		this.book = book;
	}
	
	public Book getBook() {
		return this.book;
	}
	
	public ObservableList<Audit> getAuditTrail() {
		return this.auditTrail;
	}
	
	public void setAuditTrail(ObservableList<Audit> audits) {
		this.auditTrail = audits;
	}
}
