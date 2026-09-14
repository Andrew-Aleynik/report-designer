package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ElementDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.util.DialogOpener;
import com.andrewaleynik.reportdesigner.reportdesigner.util.ElementTreeViewHelper;
import com.andrewaleynik.reportdesigner.reportdesigner.util.JavaFxControls;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

public class ElementsTreeTabController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElementsTreeTabController.class);

    private final ElementDataModel elementDataModel;

    @FXML
    private ComboBox<Element> rootElementsComboBox;
    @FXML
    private TreeView<Element> elementsTreeView;

    public ElementsTreeTabController(ElementDataModel elementDataModel) {
        this.elementDataModel = elementDataModel;
    }

    @FXML
    public void initialize() {
        initializeRootElementsComboBox();
        initializeElementsTreeView();
    }

    private void initializeRootElementsComboBox() {
        JavaFxControls.bindComboBoxDisplay(rootElementsComboBox,
                element -> element.getCode() + " - " + element.getName());

        rootElementsComboBox.setOnAction(event -> {
            Element selected = rootElementsComboBox.getSelectionModel().getSelectedItem();
            if (selected != null) {
                refreshTreeView(selected);
            }
        });

        rootElementsComboBox.setItems(elementDataModel.getRootElements());
    }

    private void initializeElementsTreeView() {
        elementsTreeView.setCellFactory(createElementTreeCellFactory());
    }

    private Callback<TreeView<Element>, TreeCell<Element>> createElementTreeCellFactory() {
        return param -> new TreeCell<>() {
            private final HBox hbox = new HBox(5);
            private final Label codeLabel = new Label();
            private final Label separator = new Label("-");
            private final Label nameLabel = new Label();
            private final Button addButton = new Button("+");
            private final Button editButton = new Button("Редактировать");
            private final Button deleteButton = new Button("Удалить");

            {
                hbox.getChildren().setAll(codeLabel, separator, nameLabel, addButton, editButton, deleteButton);
                separator.setStyle("-fx-text-fill: gray;");
                addButton.setStyle("-fx-font-weight: bold; -fx-padding: 1 3;");

                addButton.setOnAction(event -> {
                    Element currentElement = getItem();
                    if (currentElement != null) {
                        openChildElementForm(currentElement);
                    }
                });

                editButton.setOnAction(event -> {
                    Element currentElement = getItem();
                    if (currentElement != null) {
                        openEditElementForm(currentElement);
                    }
                });

                deleteButton.setOnAction(event -> {
                    Element currentElement = getItem();
                    if (currentElement != null) {
                        LOGGER.debug("Current element: {}", currentElement);
                        deleteElementPreservingRootSelection(currentElement);
                    }
                });
            }

            @Override
            protected void updateItem(Element item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    codeLabel.setText(item.getCode());
                    nameLabel.setText(item.getName());
                    setGraphic(hbox);
                }
            }
        };
    }

    private void refreshTreeView(Element rootElement) {
        ElementTreeViewHelper.refresh(elementsTreeView, rootElement);
    }

    private void deleteElementPreservingRootSelection(Element elementToDelete) {
        Element selectedRoot = rootElementsComboBox.getValue();
        Long selectedRootId = selectedRoot != null ? selectedRoot.getId() : null;
        boolean deletingSelectedRoot = selectedRootId != null
                && selectedRootId.equals(elementToDelete.getId());

        elementDataModel.deleteElement(elementToDelete);
        elementDataModel.refreshRootElements();

        if (deletingSelectedRoot) {
            rootElementsComboBox.getSelectionModel().clearSelection();
            elementsTreeView.setRoot(null);
            return;
        }

        restoreRootSelection(selectedRootId);
    }

    private void restoreRootSelection(Long selectedRootId) {
        if (selectedRootId == null) {
            return;
        }
        elementDataModel.getRootElements().stream()
                .filter(root -> selectedRootId.equals(root.getId()))
                .findFirst()
                .ifPresent(root -> {
                    rootElementsComboBox.getSelectionModel().select(root);
                    refreshTreeView(root);
                });
    }

    @FXML
    private void showAddRootElementForm() {
        elementDataModel.setSelectedParentElement(null);
        elementDataModel.setSelectedEditElement(null);

        DialogOpener.<ElementFormController>open(
                App.FxmlPaths.ADD_ROOT_ELEMENT_FORM,
                "Добавление структурной модели",
                rootElementsComboBox
        ).ifPresent(result -> {
            if (result.saved()) {
                rootElementsComboBox.getSelectionModel().select(elementDataModel.getNewElement());
            }
        });
    }

    @FXML
    public void showPreviewExportToPdf() {
        Element currentRoot = rootElementsComboBox.getValue();
        File pdfFile = elementDataModel.exportElementsTreeToPdf(currentRoot);
        LOGGER.debug("PDF was created");

        DialogOpener.open(
                App.FxmlPaths.EXPORT_PREVIEW,
                "Предпросмотр",
                rootElementsComboBox,
                (PreviewController controller) -> controller.setPdfFile(pdfFile)
        );
    }

    private void openChildElementForm(Element parentElement) {
        elementDataModel.setSelectedParentElement(parentElement);
        elementDataModel.setSelectedEditElement(null);

        Long selectedRootId = Optional.ofNullable(rootElementsComboBox.getSelectionModel().getSelectedItem())
                .map(Element::getId)
                .orElse(null);
        DialogOpener.open(
                App.FxmlPaths.ADD_CHILD_ELEMENT_FORM,
                "Добавление дочернего элемента",
                rootElementsComboBox
        );
        restoreRootSelection(selectedRootId);
    }

    private void openEditElementForm(Element editingElement) {
        elementDataModel.setSelectedParentElement(null);
        elementDataModel.setSelectedEditElement(editingElement);

        Long selectedRootId = Optional.ofNullable(rootElementsComboBox.getSelectionModel().getSelectedItem())
                .map(Element::getId)
                .orElse(null);
        DialogOpener.open(
                App.FxmlPaths.EDIT_ELEMENT_FORM,
                "Редактирование элемента",
                rootElementsComboBox
        );
        restoreRootSelection(selectedRootId);
    }
}
