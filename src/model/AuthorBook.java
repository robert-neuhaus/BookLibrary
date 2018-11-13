package model;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javafx.beans.property.SimpleStringProperty;

public class AuthorBook {
	Author author;
	Book book;
	BigDecimal royalty;
	Boolean newRecord = true;
	
	SimpleStringProperty authorSimpleString = new SimpleStringProperty();
	SimpleStringProperty royaltySimpleString = new SimpleStringProperty();
	
	public AuthorBook() {
		this.author = new Author();
		this.book = new Book();
		this.royalty = new BigDecimal(0);
		this.setAuthorSimpleString(this.author.toString());
		this.setRoyaltySimpleString(this.royalty);
	}

	public AuthorBook(Author author, Book book, BigDecimal royalty) {
		this.author = author;
		this.book = book;
		this.royalty = royalty;
		this.setAuthorSimpleString(this.author.toString());
		this.setRoyaltySimpleString(this.royalty);
	}
	
	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
		this.setAuthorSimpleString(author.toString());
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public BigDecimal getRoyalty() {
		return royalty;
	}

	public void setRoyalty(BigDecimal royalty) {
		this.royalty = royalty;
		this.setRoyaltySimpleString(this.royalty);
	}

	public Boolean getNewRecord() {
		return newRecord;
	}

	public void setNewRecord(Boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public void setAuthorSimpleString(String author) {
		this.authorSimpleString.set(author);
	}
	
	public String getAuthorSimpleString() {
		 return this.authorSimpleString.get();
	}
	
	public void setRoyaltySimpleString(BigDecimal royalty) {
		royalty = royalty.multiply(new BigDecimal(10));
		this.royaltySimpleString.set(royalty.toString() + "%");
	}
	
	public String getRoyaltySimpleString() {
		 return this.royaltySimpleString.get();
	}
	
	public String toString() {
		return this.author.getLastName()
				+ ", "
				+ author.getFirstName();
	}

}
