package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ElementDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class ElementsTabController implements Controller {
    private ElementDataModel elementDataModel;
    @FXML
    private ComboBox<Element> rootElementsComboBox;
    @FXML
    private TreeView<Element> elementsTreeView;


    public ElementsTabController(ElementDataModel elementDataModel) {
        this.elementDataModel = elementDataModel;
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

        rootElementsComboBox.setItems(elementDataModel.getRootElements());
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
            Parent root = loader.load();
            ElementFormController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Добавление корневого элемента");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(rootElementsComboBox.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaved()) {
                rootElementsComboBox.getSelectionModel().select(elementDataModel.getNewElement());
            }
        } catch (IOException e) {
            showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    private void showAddChildElementForm(Element parentElement) {
        try {
            elementDataModel.refreshSelectedParentElement(parentElement);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.ADD_CHILD_ELEMENT_FORM_PATH));
            loader.setControllerFactory(App.getControllerFactory());
            Parent root = loader.load();
            ElementFormController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Добавление дочернего элемента");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(rootElementsComboBox.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);


            dialogStage.showAndWait();

            Element currentRoot = rootElementsComboBox.getValue();
            if (currentRoot != null) {
                refreshTreeView(currentRoot);
            }
        } catch (IOException e) {
            showError("Ошибка при открытии формы", e.getMessage());
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
