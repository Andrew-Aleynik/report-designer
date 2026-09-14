package com.andrewaleynik.reportdesigner.reportdesigner.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AlertFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlertFactory.class);

    public static final ButtonType OK = new ButtonType("ОК", ButtonBar.ButtonData.OK_DONE);
    public static final ButtonType CANCEL = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
    public static final ButtonType CLOSE = new ButtonType("Закрыть", ButtonBar.ButtonData.CANCEL_CLOSE);

    private AlertFactory() {
    }

    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, null, message, CLOSE);
    }

    public static void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, null, message, CLOSE);
    }

    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, null, message, CLOSE);
    }

    public static Optional<ButtonType> showConfirmation(String title, String header, String message) {
        return showAlert(Alert.AlertType.CONFIRMATION, title, header, message, OK, CANCEL);
    }

    private static Optional<ButtonType> showAlert(Alert.AlertType alertType,
                                                  String title,
                                                  String header,
                                                  String message,
                                                  ButtonType... buttonTypes) {
        try {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(message);
            alert.getButtonTypes().setAll(buttonTypes);

            setupAlert(alert);
            return alert.showAndWait();
        } catch (Exception e) {
            LOGGER.error("Error displaying alert {}: {}", title, e.getMessage());
            return Optional.empty();
        }
    }

    private static void setupAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.setPrefSize(400, 200);
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialogPane.setMinWidth(Region.USE_PREF_SIZE);

        Button okButton = (Button) dialogPane.lookupButton(OK);
        if (okButton != null) {
            okButton.setDefaultButton(true);
        }

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        Image appIcon = getAppIcon();
        if (appIcon != null) {
            stage.getIcons().add(appIcon);
        }
    }

    //TODO setup app image
    private static Image getAppIcon() {
        return null;
    }
}
