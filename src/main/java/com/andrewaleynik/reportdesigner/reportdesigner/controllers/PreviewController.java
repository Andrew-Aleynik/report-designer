package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class PreviewController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreviewController.class);
    private Stage dialogStage;
    private File currentPdfFile;
    private PDDocument currentDocument;
    @FXML
    private VBox pagesContainer;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPdfFile(File pdfFile) {
        this.currentPdfFile = pdfFile;
        if (pdfFile == null || !pdfFile.exists()) {
            AlertFactory.showError("Ошибка", "Ошибка загрузки файла");
            return;
        }

        try {
            if (currentDocument != null) {
                currentDocument.close();
            }

            currentDocument = PDDocument.load(pdfFile);
            renderPdfPages();

        } catch (Exception e) {
            AlertFactory.showError("Ошибка",
                    "Не удалось загрузить документ: " + e.getMessage());
        }
    }

    private void renderPdfPages() {
        try {
            pagesContainer.getChildren().clear();

            PDFRenderer renderer = new PDFRenderer(currentDocument);
            int totalPages = currentDocument.getNumberOfPages();

            for (int page = 0; page < totalPages; page++) {
                BufferedImage bufferedImage = renderer.renderImageWithDPI(page, 150);
                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);

                ImageView imageView = new ImageView(fxImage);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(800);

                Label pageLabel = new Label("Страница " + (page + 1));
                pageLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");
                pagesContainer.getChildren().addAll(pageLabel, imageView);
            }
        } catch (Exception e) {
            AlertFactory.showError("Ошибка рендеринга",
                    "Не удалось выполнить рендеринг: " + e.getMessage());
        }
    }

    @FXML
    public void handleSave() {
        if (currentPdfFile == null || !currentPdfFile.exists()) {
            AlertFactory.showError("Ошибка сохранения", "PDF файл не найден или не был сгенерирован");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить отчет как PDF");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        String defaultFileName = generateDefaultFileName();
        fileChooser.setInitialFileName(defaultFileName);

        File saveFile = fileChooser.showSaveDialog(dialogStage);
        if (saveFile != null) {
            try {
                Files.copy(currentPdfFile.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                AlertFactory.showInfo("Отчет сохранен",
                        "Файл успешно сохранен:\n" + saveFile.getAbsolutePath());

            } catch (Exception e) {
                AlertFactory.showError("Ошибка при сохранении файла",
                        "Не удалось сохранить файл: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handlePrint() {
    }

    @FXML
    public void handleClose() {
        if (currentPdfFile != null && currentPdfFile.exists()) {
            Optional<ButtonType> result = AlertFactory.showConfirmation(
                    "Закрытие предпросмотра",
                    "Вы уверены, что хотите закрыть предпросмотр?",
                    "Временный файл будет удален."
            );

            if (result.isPresent() && result.get() == ButtonType.OK) {
                cleanupTempFile();
                closeDialog();
            }
        } else {
            closeDialog();
        }
    }

    private String generateDefaultFileName() {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        return "отчет_" + timestamp + ".pdf";
    }

    private void cleanupTempFile() {
        closeDocument();
        deleteTempFile();
    }

    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    public void forceClose() {
        cleanupTempFile();
        closeDialog();
    }

    public File getCurrentPdfFile() {
        return currentPdfFile;
    }

    public boolean isPdfFileAvailable() {
        return currentPdfFile != null && currentPdfFile.exists();
    }

    private void closeDocument() {
        if (currentDocument != null) {
            try {
                currentDocument.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteTempFile() {
        if (currentPdfFile != null && currentPdfFile.exists()) {
            try {
                currentPdfFile.delete();
            } catch (SecurityException e) {
                LOGGER.error("Не удалось удалить временный файл: ", e);
            }
        }
    }
}
