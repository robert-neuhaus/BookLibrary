package model;

import javafx.scene.control.Control;
import javafx.scene.control.Label;

public class InvalidField {
	private Control control;
	private String message;
	private Label label;
	
	public InvalidField(Control control, Label label, String message) {
		this.control = control;
		this.message = message;
		this.label = label;
	}
	
	public Control getControl() {
		return this.control;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public Control getLabel() {
		return this.label;
	}
}
