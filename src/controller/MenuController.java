package controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import model.Book;

public class MenuController {
	
	@FXML private MenuItem menuQuit;
	@FXML private MenuItem menuBookList;
	@FXML private MenuItem menuAddBook;
	
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
	}
	
	public void initialize() {
	}
}
