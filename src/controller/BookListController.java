package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gateway.BookTableGateway;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import model.Book;

public class BookListController implements Initializable {

	private static Logger logger = LogManager.getLogger();
	
	@FXML private ListView<Book> LstVwBookList;
	
	private List<Book> books;
	
	public BookListController(List<Book> books) {
		this.books = books;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<Book> items = null;
		try {
			items = (ObservableList<Book>) BookTableGateway.getInstance().getBooks();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		if (books != null) {
			for(Book book : books) {
				items.add(book);
			}
		}

		LstVwBookList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if(click.getClickCount() == 2) {

                	Book selected = LstVwBookList.getSelectionModel().getSelectedItem();
                   
                	logger.info("double-clicked " + selected);
                	
        			MasterController.getInstance().changeView(
        					"../view/view_bookDetail.fxml", 
        					new BookDetailController(selected), 
        					selected);       			              	
                }
            }
        });
	}
}
