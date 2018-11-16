package controller;

import java.util.List;

import gateway.AuthorTableGateway;
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
import model.Author;
import model.Author;

public class AuditTrailAuthorController {
	
	@FXML private TableView<Audit> tblVwAuditTrail;
	@FXML private Label lblTitle;
	@FXML private Button btnBack;
	
	private Author author = null;
	private ObservableList<Audit> auditTrail = null;
	
	public AuditTrailAuthorController(Author author) {
		this.author = author;
	}
	
	public void initialize() throws Exception {
		List<Audit> audits = AuthorTableGateway.getInstance().getAudits(this.getAuthor().getId());
		this.auditTrail = FXCollections.observableArrayList(audits);
		lblTitle.setText("Audit Trail For: " + this.getAuthor().toString());
		tblVwAuditTrail.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("timestamp"));
		tblVwAuditTrail.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("message"));
		tblVwAuditTrail.setItems(this.getAuditTrail());
	}
	
	@FXML public void handleButtonAction(ActionEvent action) {
		Object source = action.getSource();
		
		if (source == btnBack) {
    		try {
				AuthorDetailController.getInstance().setAuthor(
						AuthorTableGateway.getInstance().getAuthor(author.getId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			MasterController.getInstance().changeView(
					"../view/view_authorDetail.fxml", 
					AuthorDetailController.getInstance(), 
					null); 
		}
	}
	
	public void setAuthor(Author author) {
		this.author = author;
	}
	
	public Author getAuthor() {
		return this.author;
	}
	
	public ObservableList<Audit> getAuditTrail() {
		return this.auditTrail;
	}
	
	public void setAuditTrail(ObservableList<Audit> audits) {
		this.auditTrail = audits;
	}
}
