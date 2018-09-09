package assignment1;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class MenuController {
	
	@FXML private MenuItem menuQuit;
	@FXML private MenuItem menuBookList;
	
	@FXML private void handleMenuAction(ActionEvent action) throws IOException {
		Object source = action.getSource();
		if(source == menuQuit) {
			Platform.exit();
		}
		if(source == menuBookList) {
			List<Book> books = BookList.getInstance().getBooks();
			MasterController.getInstance().changeView("view_bookList.fxml", 
					new BookListController(books), null);
			return;
		}
	}
	
	public void initialize() {
	}
}
