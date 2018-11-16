package exception;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.Label;
import model.InvalidField;

public class ValidationException extends Exception {

	private static Logger logger = LogManager.getLogger();
	
	private static final long serialVersionUID = 1L;
	List<InvalidField> exceptions = new ArrayList<InvalidField>();

	public ValidationException(List<InvalidField> exceptions) {
		for (InvalidField exception : exceptions)
			this.exceptions.add(exception);
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
	
	public List<InvalidField> getCauses() {
		return this.exceptions;
	}

}
