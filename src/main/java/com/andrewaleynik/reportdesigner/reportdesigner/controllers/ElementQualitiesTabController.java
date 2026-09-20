package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ElementDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
import com.andrewaleynik.reportdesigner.reportdesigner.util.DialogOpener;
import com.andrewaleynik.reportdesigner.reportdesigner.util.FormValidators;
import com.andrewaleynik.reportdesigner.reportdesigner.util.JavaFxControls;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

public class ElementQualitiesTabController {

    private final ElementDataModel elementDataModel;
    private final QualityDataModel qualityDataModel;
    private final PropertyDataModel propertyDataModel;

    @FXML
    private ComboBox<ElementQuality> elementQualitiesComboBox;
    @FXML
    private Label elementNameField;
    @FXML
    private TextField codeField;
    @FXML
    private TextField serviceLifeDaysField;
    @FXML
    private TextField satisfyingCostField;
    @FXML
    private TextField actualCostField;
    @FXML
    private TableView<Property> propertiesTableView;
    @FXML
    private Button deleteButton;

    private boolean suppressAutoSave;

    public ElementQualitiesTabController(ElementDataModel elementDataModel, QualityDataModel qualityDataModel,
                                         PropertyDataModel propertyDataModel) {
        this.elementDataModel = elementDataModel;
        this.qualityDataModel = qualityDataModel;
        this.propertyDataModel = propertyDataModel;
    }

    @FXML
    public void initialize() {
        initializeElementQualitiesComboBox();
        initializePropertiesTableView();
        registerFormChangeListeners();
        elementDataModel.onChange(this::refreshLinkedElementDisplay);
        qualityDataModel.onChange(this::refreshLinkedElementDisplay);
        updateDeleteButtonState();
    }

    private void initializeElementQualitiesComboBox() {
        JavaFxControls.bindComboBoxDisplay(elementQualitiesComboBox, ElementQuality::getCode);

        elementQualitiesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                qualityDataModel.setSelectedQuality(newVal);
                propertyDataModel.setCurrentProperties(qualityDataModel.getSelectedQuality().getProperties());
                populateFormWithQualityData();
            } else {
                qualityDataModel.setSelectedQuality(null);
                propertyDataModel.setCurrentProperties(Collections.emptySet());
                populateFormWithQualityData();
            }
            updateDeleteButtonState();
        });

        elementQualitiesComboBox.setItems(qualityDataModel.getQualities());
    }

    private void initializePropertiesTableView() {
        TableColumn<Property, String> nameColumn = JavaFxControls.textColumn("Название", Property::getName);
        TableColumn<Property, String> groupColumn = JavaFxControls.textColumn("Группа",
                property -> property.getPropertyGroup() != null ? property.getPropertyGroup().getName() : "");
        TableColumn<Property, String> indicatorColumn = JavaFxControls.textColumn("Показатель",
                property -> property.getPropertyIndicator() != null
                        ? property.getPropertyIndicator().getName() : "");
        TableColumn<Property, String> unitColumn = JavaFxControls.textColumn("Единица измерения",
                property -> property.getUnit() != null ? property.getUnit().getName() : "");
        TableColumn<Property, String> criterionValueColumn = JavaFxControls.textColumn(
                "Критерий качества", Property::getQualityCriterionValue);
        TableColumn<Property, Void> actionsColumn = JavaFxControls.actionsColumn(
                this::handleEditProperty,
                this::handleDeleteProperty
        );

        nameColumn.setPrefWidth(160);
        groupColumn.setPrefWidth(120);
        indicatorColumn.setPrefWidth(120);
        unitColumn.setPrefWidth(110);
        criterionValueColumn.setPrefWidth(130);
        actionsColumn.setPrefWidth(100);

        propertiesTableView.getColumns().setAll(
                nameColumn, groupColumn, indicatorColumn, unitColumn, criterionValueColumn, actionsColumn);
        JavaFxControls.configureConstrainedTable(propertiesTableView);
        propertiesTableView.setItems(propertyDataModel.getCurrentProperties());
    }

    private void registerFormChangeListeners() {
        codeField.textProperty().addListener((obs, oldVal, newVal) -> autoSaveIfValid());
        serviceLifeDaysField.textProperty().addListener((obs, oldVal, newVal) -> autoSaveIfValid());
        satisfyingCostField.textProperty().addListener((obs, oldVal, newVal) -> autoSaveIfValid());
        actualCostField.textProperty().addListener((obs, oldVal, newVal) -> autoSaveIfValid());
    }

    private void populateFormWithQualityData() {
        suppressAutoSave = true;
        try {
            ElementQuality quality = qualityDataModel.getSelectedQuality();
            if (quality == null) {
                elementNameField.setText("");
                codeField.clear();
                serviceLifeDaysField.clear();
                satisfyingCostField.clear();
                actualCostField.clear();
                return;
            }

            refreshLinkedElementDisplay();
            codeField.setText(Optional.ofNullable(quality.getCode()).orElse(""));
            serviceLifeDaysField.setText(
                    Optional.ofNullable(quality.getServiceLife())
                            .map(BigDecimal::toString)
                            .orElse(""));
            satisfyingCostField.setText(
                    Optional.ofNullable(quality.getSatisfyingCost())
                            .map(BigDecimal::toString)
                            .orElse(""));
            actualCostField.setText(
                    Optional.ofNullable(quality.getActualCost())
                            .map(BigDecimal::toString)
                            .orElse(""));
        } finally {
            suppressAutoSave = false;
        }
    }

    private void refreshLinkedElementDisplay() {
        ElementQuality quality = elementQualitiesComboBox.getValue();
        if (quality == null) {
            elementNameField.setText("");
            return;
        }
        elementNameField.setText(
                elementDataModel.findElementByQuality(quality)
                        .map(Element::getName)
                        .orElse(""));
    }

    private void updateDeleteButtonState() {
        deleteButton.setDisable(elementQualitiesComboBox.getValue() == null);
    }

    private void autoSaveIfValid() {
        if (suppressAutoSave || qualityDataModel.getSelectedQuality() == null || isFormInvalid()) {
            return;
        }
        persistCurrentQuality();
    }

    private void persistCurrentQuality() {
        ElementQuality quality = qualityDataModel.getSelectedQuality();
        if (quality == null) {
            return;
        }

        quality.setCode(codeField.getText());
        quality.setServiceLife(parseCost(serviceLifeDaysField.getText()));
        quality.setSatisfyingCost(parseCost(satisfyingCostField.getText()));
        quality.setActualCost(parseCost(actualCostField.getText()));

        qualityDataModel.updateQuality(quality);
        qualityDataModel.setSelectedQuality(quality);
        elementDataModel.refreshRootElements();
        updateDeleteButtonState();
    }

    @FXML
    public void handleCreateElementQuality() {
        DialogOpener.<ElementQualityFormController>open(
                App.FxmlPaths.ADD_ELEMENT_QUALITY_SHORT_FORM,
                "Добавление элемента структурной модели",
                elementQualitiesComboBox
        ).ifPresent(result -> {
            if (result.saved()) {
                elementQualitiesComboBox.getSelectionModel().select(qualityDataModel.getNewQuality());
                updateDeleteButtonState();
            }
        });
    }

    @FXML
    public void handleDeleteQuality() {
        qualityDataModel.deleteQuality(elementQualitiesComboBox.getValue());
        qualityDataModel.setSelectedQuality(null);
        elementQualitiesComboBox.getSelectionModel().clearSelection();
        populateFormWithQualityData();
        propertyDataModel.setCurrentProperties(Collections.emptySet());
        updateDeleteButtonState();
    }

    @FXML
    public void showAddPropertyForm() {
        if (elementQualitiesComboBox.getValue() == null) {
            return;
        }
        qualityDataModel.setSelectedQuality(elementQualitiesComboBox.getValue());
        propertyDataModel.setEditingProperty(null);

        DialogOpener.open(
                App.FxmlPaths.ADD_PROPERTY_FORM,
                "Добавление свойства",
                propertiesTableView
        );

        propertyDataModel.setCurrentProperties(qualityDataModel.getSelectedQuality().getProperties());
        persistCurrentQuality();
    }

    @FXML
    public void showInheritPropertyForm() {
        if (elementQualitiesComboBox.getValue() == null) {
            return;
        }
        qualityDataModel.setSelectedQuality(elementQualitiesComboBox.getValue());
        propertyDataModel.setEditingProperty(null);

        DialogOpener.<InheritPropertyFormController>open(
                App.FxmlPaths.INHERIT_PROPERTY_FORM,
                "Наследование свойств",
                propertiesTableView
        ).ifPresent(result -> {
            if (result.saved()) {
                propertyDataModel.setCurrentProperties(qualityDataModel.getSelectedQuality().getProperties());
                persistCurrentQuality();
            }
        });
    }

    private void handleEditProperty(Property property) {
        qualityDataModel.setSelectedQuality(elementQualitiesComboBox.getValue());
        propertyDataModel.setEditingProperty(property);

        DialogOpener.open(
                App.FxmlPaths.ADD_PROPERTY_FORM,
                "Редактирование свойства",
                propertiesTableView
        );

        propertyDataModel.setCurrentProperties(qualityDataModel.getSelectedQuality().getProperties());
        persistCurrentQuality();
    }

    private void handleDeleteProperty(Property property) {
        AlertFactory.showConfirmation(
                "Подтверждение удаления",
                "Удаление свойства",
                "Вы уверены, что хотите удалить это свойство?"
        ).ifPresent(response -> {
            if (response == AlertFactory.OK) {
                ElementQuality currentQuality = qualityDataModel.getSelectedQuality();
                currentQuality.removeProperty(property);
                propertyDataModel.setCurrentProperties(currentQuality.getProperties());
                persistCurrentQuality();
            }
        });
    }

    private boolean isFormInvalid() {
        return FormValidators.isBlankOrTooShort(codeField.getText(), 3)
                || !FormValidators.isOptionalNonNegativeBigDecimal(serviceLifeDaysField.getText())
                || !FormValidators.isOptionalNonNegativeBigDecimal(satisfyingCostField.getText())
                || !FormValidators.isOptionalNonNegativeBigDecimal(actualCostField.getText());
    }

    private BigDecimal parseCost(String value) {
        if (FormValidators.isBlank(value)) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
