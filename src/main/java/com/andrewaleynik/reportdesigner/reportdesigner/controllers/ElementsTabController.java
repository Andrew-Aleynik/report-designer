package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ElementsTabController implements Controller {
    private ElementService elementService;

    private Element selectedParentElement;
    @FXML
    private ComboBox<Element> rootElementsComboBox;
    @FXML
    private TreeView<Element> elementsTreeView;


    public ElementsTabController(ElementService elementService) {
        this.elementService = elementService;
    }

    @FXML
    public void initialize() {
        initializeRootElementsComboBox();
        initializeElementsTreeView();
    }

    private void initializeRootElementsComboBox() {
        rootElementsComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Element item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCode() + " - " + item.getName());
                }
            }
        });

        rootElementsComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Element item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCode() + " - " + item.getName());
                }
            }
        });

        rootElementsComboBox.setOnAction(event -> {
            Element selected = rootElementsComboBox.getSelectionModel().getSelectedItem();
            if (selected != null) {
                refreshTreeView(selected);
            }
        });
    }

    private void initializeElementsTreeView() {
        elementsTreeView.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<Element> call(TreeView<Element> param) {
                return new TreeCell<>() {
                    private final HBox hbox = new HBox(5);
                    private final Label codeLabel = new Label();
                    private final Label separator = new Label("-");
                    private final Label nameLabel = new Label();
                    private final Button addButton = new Button("+");

                    {
                        hbox.getChildren().setAll(codeLabel, separator, nameLabel, addButton);
                        separator.setStyle("-fx-text-fill: gray;");
                        addButton.setStyle("-fx-font-weight: bold; -fx-padding: 0 3;");

                        addButton.setOnAction(event -> {
                            Element currentElement = getItem();
                            if (currentElement != null) {
                                showAddChildElementForm(currentElement);
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
        });
    }

    private void refreshTreeView(Element rootElement) {
        TreeItem<Element> root = createTreeItem(rootElement);
        elementsTreeView.setRoot(root);
        root.setExpanded(true);
    }

    private TreeItem<Element> createTreeItem(Element element) {
        TreeItem<Element> item = new TreeItem<>(element);

        if (element.getChildren() != null && !element.getChildren().isEmpty()) {
            for (Element child : element.getChildren()) {
                item.getChildren().add(createTreeItem(child));
            }
        }

        return item;
    }

    @FXML
    private void showAddRootElementForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.ADD_ROOT_ELEMENT_FORM_PATH));
            loader.setControllerFactory(App.getControllerFactory());
            DialogPane dialogPane = loader.load();
            FormController controller = loader.getController();
            controller.setParentController(this);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Добавление корневого элемента");

            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setOnAction(event -> {
                if (controller.handleOk()) {
                    dialog.setResult(ButtonType.OK);
                    dialog.close();
                }
            });

            Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
            cancelButton.setOnAction(event -> {
                dialog.setResult(ButtonType.CANCEL);
                dialog.close();
            });

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                refreshRootElementsComboBox();
            }

        } catch (IOException e) {
            showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    private void showAddChildElementForm(Element parentElement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.ADD_CHILD_ELEMENT_FORM_PATH));
            loader.setControllerFactory(App.getControllerFactory());
            DialogPane dialogPane = loader.load();
            ElementFormController controller = loader.getController();
            controller.setParentElement(parentElement);
            controller.setParentController(this);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Добавление дочернего элемента");

            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setOnAction(event -> {
                if (controller.handleOk()) {
                    dialog.setResult(ButtonType.OK);
                    dialog.close();
                }
            });

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Element currentRoot = rootElementsComboBox.getValue();
                if (currentRoot != null) {
                    refreshTreeView(currentRoot);
                }
            }

        } catch (IOException e) {
            showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    @Override
    public void updateViews() {
        List<Element> rootElements = elementService.getRootElements();
        rootElementsComboBox.getItems().setAll(rootElements);
        refreshRootElementsComboBox();
    }

    private void refreshRootElementsComboBox() {
        Element selected = rootElementsComboBox.getValue();
        rootElementsComboBox.getItems().clear();
        List<Element> rootElements = elementService.getRootElements();
        rootElementsComboBox.getItems().addAll(rootElements);

        if (selected != null && rootElements.contains(selected)) {
            rootElementsComboBox.setValue(selected);
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
