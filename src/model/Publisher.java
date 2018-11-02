package model;

public class Publisher {
	private int publisherId = 0;
	private String publisherName = "Unknown";
	
	public Publisher(int publisherId, String publisherName) {
		this.publisherId = publisherId;
		this.publisherName = publisherName;
	}
	
	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}
	
	public void setPublisherId(int publisherId) {
		this.publisherId = publisherId;
	}
	
	public String getPublisherName() {
		return this.publisherName;
	}
	
	public int getPublisherId() {
		return this.publisherId;
	}
	
	public String toString() {
		return this.getPublisherName();
	}
}
