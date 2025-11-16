package com.andrewaleynik.reportdesigner.reportdesigner.util;

import javafx.scene.control.Alert;
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

    private AlertFactory() {
    }

    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, null, message);
    }

    public static void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, null, message);
    }

    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, null, message);
    }

    public static Optional<ButtonType> showConfirmation(String title, String header, String message) {
        return showAlert(Alert.AlertType.CONFIRMATION, title, header, message);
    }

    private static Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String header, String message) {
        try {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(message);

            setupAlert(alert);
            return alert.showAndWait();
        } catch (Exception e) {
            LOGGER.error("Error display alert {}: {}", title, e.getMessage());
            return Optional.empty();
        }
    }

    private static void setupAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.setPrefSize(400, 200);
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialogPane.setMinWidth(Region.USE_PREF_SIZE);

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
