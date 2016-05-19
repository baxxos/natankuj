package gui;

import javafx.scene.control.Alert;

public class CustomAlert extends Alert {

	public CustomAlert(Alert.AlertType type, String title, String headerText, String contentText) {
		super(type);
		this.getDialogPane().setMinHeight(150);
		this.getDialogPane().setMinWidth(450);
		this.setContentText(contentText);
		this.setHeaderText(headerText);
		this.setTitle(title);
	}
}
