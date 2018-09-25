package model;

import java.util.ArrayList;
import java.util.List;

public class BookList {
	private static List<Book> books = new ArrayList<Book>();
	private static BookList instance = null;
	
	public static BookList getInstance() {
		if (instance == null) {
			instance = new BookList();
			books.add(new Book("The Plunger", "Diego Gonzales", 
					"The Plunger is back at it again, plunging his way to the top of the charts "
					+ "in this thrilling crime drama.", "2018", "123456780", "Today"));
			books.add(new Book("Rise of the Necromancer", "James Goldsmith", 
					"The Necromancer is back at it again, necromancing his way to the top of the charts "
					+ "in this thrilling crime drama.", "2018", "123456781", "Today"));
			books.add(new Book("Moby Dick", "Herman Melville", 
					"Moby Dick is back at it again, moby-dicking his way to the top of the charts "
					+ "in this thrilling crime drama.", "1851", "123456782", "Today"));
			books.add(new Book("Pyrotechnic Life Hacks", "Tyler Rasmussen", 
					"The pyrotechnic is back at it again, pyrotechnicing his way to the top of the charts "
					+ "in this thrilling crime drama.", "1851", "123456782", "Today"));
			books.add(new Book("Art of the Script Kiddy", "Sabrina Mosher", 
					"The script kiddy is back at it again, script-kiddying his way to the top of the charts "
					+ "in this thrilling crime drama.", "1851", "123456782", "Today"));
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
