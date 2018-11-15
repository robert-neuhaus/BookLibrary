package controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exception.ValidationException;
import javafx.scene.control.Label;
import model.InvalidField;

public class ValidationErrors {
	
	private static Logger logger = LogManager.getLogger();
	
	public void ValidationErrors() {
		
	}

	public static void showErrors(ValidationException ve, Label lblStatus) {
		List<InvalidField> exceptions = ve.getCauses();
		lblStatus.setStyle("-fx-text-fill: red;");
		exceptions.get(0).getControl().requestFocus();
		
		for (InvalidField exception : exceptions) {
			logger.info(exception.getMessage());
			lblStatus.setText(lblStatus.getText() + "\n" + exception.getMessage());
			exception.getLabel().getStyleClass().add("invalid_label");
			exception.getControl().getStyleClass().add("invalid_control");
		}
	}

}
