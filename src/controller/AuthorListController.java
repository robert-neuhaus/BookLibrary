package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gateway.AuthorTableGateway;
import gateway.BookTableGateway;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import model.Audit;
import model.Author;
import model.AuthorBook;
import model.Book;

public class AuthorListController implements Initializable {

	private static Logger logger = LogManager.getLogger();
	
	@FXML private ListView<Author> LstVwAuthorList;
	@FXML private Button btnDelete;
	@FXML private Label lblStatus;
	
	public AuthorListController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setAuthorList();
		LstVwAuthorList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                	Author selected = LstVwAuthorList.getSelectionModel().getSelectedItem();                   
                	logger.info("double-clicked " + selected);
                	if (selected != null)
                		AuthorDetailController.getInstance().setAuthor(selected);
	        			MasterController.getInstance().changeView(
	        					"../view/view_authorDetail.fxml", 
	        					AuthorDetailController.getInstance(), 
	        					selected);       			              	
                }
            }
        });
	}
	
	@FXML private void handleMenuAction(ActionEvent action) throws IOException {
		Object source = action.getSource();
		Author author = LstVwAuthorList.getSelectionModel().getSelectedItem();
		
		if (source == btnDelete)
			try {
				//For every book related to this author, add an audit for removal of author
				List<AuthorBook> authorBooks = AuthorTableGateway.getInstance().getBooksForAuthor(author);
				List<Audit> audits = new ArrayList<>();
				for (AuthorBook ab : authorBooks) {
					if (ab.getAuthor().getId() == author.getId()) {
						audits.add(new Audit(ab.getBook().getId(),
							"Author "
							+ author.toString()
							+ " removed."));
					}
				}
				AuthorTableGateway.getInstance().deleteAuthor(author);
				BookTableGateway.getInstance().addAudits(audits);
				lblStatus.setStyle("-fx-text-fill: blue;");
				lblStatus.setText("Author deleted: " + author.toString());
				setAuthorList();
			} catch (Exception e) {
				lblStatus.setStyle("-fx-text-fill: red;");
				lblStatus.setText("Failed to delete author: " + author.toString());	
			}
	}
	
	public void setAuthorList() {
		ObservableList<Author> authors = null;
		
		try {
			authors = FXCollections.observableArrayList(AuthorTableGateway.getInstance().getAuthors());
		} catch (Exception e) {
			lblStatus.setStyle("-fx-text-fill: red;");
			lblStatus.setText("Failed to fetch authors from database. ");	
			btnDelete.setDisable(true);
		}
			
		if (authors != null) {
			LstVwAuthorList.setItems(authors);
		}
	}
}
