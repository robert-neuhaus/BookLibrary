package controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import model.Author;
import model.Book;

public class MenuController {
	
	@FXML private MenuItem menuQuit;
	@FXML private MenuItem menuBookList;
	@FXML private MenuItem menuAddBook;
	@FXML private MenuItem menuAuthorList;
	@FXML private MenuItem menuAddAuthor;
	
	@FXML private void handleMenuAction(ActionEvent action) throws IOException {
		Object source = action.getSource();
		if(source == menuQuit) {
			MasterController.getInstance().exit();
		}
		if(source == menuBookList) {
			MasterController.getInstance().changeView("../view/view_bookList.fxml", 
					new BookListController(), null);
			return;
		}
		if(source == menuAddBook) {
			
			Book newBook = new Book();
			
			BookDetailController.getInstance().setBook(newBook);
			MasterController.getInstance().changeView(
					"../view/view_bookDetail.fxml", 
					BookDetailController.getInstance(), 
					newBook);  
		}
		if(source == menuAuthorList) {
			MasterController.getInstance().changeView("../view/view_authorList.fxml", 
					new AuthorListController(), null);
			return;
		}
		if(source == menuAddAuthor) {
			
			Author newAuthor = new Author();
			
			AuthorDetailController.getInstance().setAuthor(newAuthor);
			MasterController.getInstance().changeView(
					"../view/view_authorDetail.fxml", 
					AuthorDetailController.getInstance(), 
					newAuthor);  
		}
	}
	
	public void initialize() {
	}
}
