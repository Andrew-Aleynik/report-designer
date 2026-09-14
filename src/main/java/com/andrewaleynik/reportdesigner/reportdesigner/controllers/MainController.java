package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    private static final String INSTRUCTION_RESOURCE = "/docs/instruction.txt";

    @FXML
    public void handleExit() {
        Platform.exit();
    }

    @FXML
    public void handleInstructions() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Инструкция");
        dialog.setHeaderText("Как пользоваться программой");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.getDialogPane().getButtonTypes().add(AlertFactory.CLOSE);

        TextArea textArea = new TextArea(loadInstructionText());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(520, 420);
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().setPrefSize(560, 500);

        dialog.showAndWait();
    }

    private String loadInstructionText() {
        try (InputStream inputStream = getClass().getResourceAsStream(INSTRUCTION_RESOURCE)) {
            if (inputStream == null) {
                log.error("Instruction file not found: {}", INSTRUCTION_RESOURCE);
                return "Не удалось загрузить инструкцию.";
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load instruction file: {}", e.getMessage(), e);
            return "Не удалось загрузить инструкцию.";
        }
    }
}
