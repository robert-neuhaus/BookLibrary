package model;

import java.util.ArrayList;
import java.util.List;

public class BookList {
	private static List<Book> books = new ArrayList<Book>();
	private static BookList instance = null;
	
	public static BookList getInstance() {
		if (instance == null) {
			instance = new BookList();
			books.add(new Book(0, "The Plunger", 
					"The Plunger is back at it again, plunging his way to the top of the charts "
					+ "in this thrilling crime drama.", 2018, "123456780"));
			books.add(new Book(1, "Rise of the Necromancer", 
					"The Necromancer is back at it again, necromancing his way to the top of the charts "
					+ "in this thrilling crime drama.", 2018, "123456781"));
			books.add(new Book(2, "Moby Dick", 
					"Moby Dick is back at it again, moby-dicking his way to the top of the charts "
					+ "in this thrilling crime drama.", 1851, "123456782"));
			books.add(new Book(3, "Pyrotechnic Life Hacks",
					"The pyrotechnic is back at it again, pyrotechnicing his way to the top of the charts "
					+ "in this thrilling crime drama.", 1851, "123456782"));
			books.add(new Book(4, "Art of the Script Kiddy",
					"The script kiddy is back at it again, script-kiddying his way to the top of the charts "
					+ "in this thrilling crime drama.", 1851, "123456782"));
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
