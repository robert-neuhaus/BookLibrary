package model;

import java.time.LocalDateTime;

import javafx.beans.property.SimpleStringProperty;

public class Audit {

	int bookId;
	String auditMsg;
	LocalDateTime dateAdded;
	
	SimpleStringProperty message = new SimpleStringProperty();
	SimpleStringProperty timestamp = new SimpleStringProperty();
	
	public Audit(int bookId, String msg){
		this.bookId = bookId;
		this.auditMsg = msg;
		this.dateAdded = null;
	}
	
	public Audit(int bookId, LocalDateTime ldt, String msg){
		this.bookId = bookId;
		this.auditMsg = msg;
		this.dateAdded = ldt;
		setTimestamp(ldt.toString());
		setMessage(msg);
	}
	
	// Access methods.
	
	public int getBookID() {
		return this.bookId;
	}
	
	public String getAuditMsg() {
		return this.auditMsg;
	}
	
	public LocalDateTime getDateAdded() {
		return this.dateAdded;
	}
	
	public void setBookID(int bookId) {
		this.bookId = bookId;
	}
	
	public void setAuditMsg(String msg) {
		this.auditMsg= msg;
	}
	
	public void setDateAdded(LocalDateTime ldt) {
		this.dateAdded = ldt;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp.set(timestamp);
	}
	
	public void setMessage(String message) {
		this.message.set(message);
	}
	
	public String getTimestamp() {
		return this.timestamp.get();
	}
	
	public String getMessage() {
		return this.message.get();
	}
	
}
