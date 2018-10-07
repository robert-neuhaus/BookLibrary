package model;

import java.util.ArrayList;
import java.util.List;

public class BookList {
	private static List<Book> books = new ArrayList<Book>();
	private static BookList instance = null;
	
	public static BookList getInstance() {
		if (instance == null) {
			instance = new BookList();
		}
		return instance;
	}
	
	public List<Book> getBooks(){
		return books;
	}
	
	public void setBooks(Book book) {
		books.add(book);
	}
}
