package controller;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputControl;

public class OnChangeListener {

	public OnChangeListener() {
		// TODO Auto-generated constructor stub
	}

	public static void setOnChangeListener(Control control, Button btnSave, Object controller) {
		if (control instanceof ComboBox<?>)
			((ComboBox<?>)control).valueProperty().addListener((observable, oldValue, newValue) -> {
				    btnSave.setDisable(false);
				    if (controller instanceof AuthorDetailController) {
				    	MasterController.getInstance().setIsAuthorChange(true);
				    } else if (controller instanceof BookDetailController) {
				    	MasterController.getInstance().setIsBookChange(true);
				    }
				});
		else if (control instanceof TextInputControl)
			((TextInputControl)control).textProperty().addListener((observable, oldValue, newValue) -> {
				    btnSave.setDisable(false);
				    if (controller instanceof AuthorDetailController) {
				    	MasterController.getInstance().setIsAuthorChange(true);
				    } else if (controller instanceof BookDetailController) {
				    	MasterController.getInstance().setIsBookChange(true);
				    }
				});
		else if (control instanceof DatePicker)
			((DatePicker)control).valueProperty().addListener((observable, oldValue, newValue) -> {
				    btnSave.setDisable(false);
				    if (controller instanceof AuthorDetailController) {
				    	MasterController.getInstance().setIsAuthorChange(true);
				    } else if (controller instanceof BookDetailController) {
				    	MasterController.getInstance().setIsBookChange(true);
				    }
				});
	}
}
