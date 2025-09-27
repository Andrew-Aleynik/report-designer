package com.andrewaleynik.reportdesigner.reportdesigner.components.comboboxes;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.ComponentNameHolder;
import com.andrewaleynik.reportdesigner.reportdesigner.Titled;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class SpecificComboBox<T extends Enum<T> & Titled & ComponentNameHolder> extends ComboBox<T> {
    private static final Logger LOGGER = LogManager.getLogger();

    SpecificComboBox() {
        super();
        init();
        setOnAction(e -> addElement());
    }

    private void init() {
        setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });

        setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? getPromptText() : item.getTitle());
            }
        });

        setItems();
    }

    @SuppressWarnings("unchecked")
    private void setItems() {
        try {
            ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
            Type type = superClass.getActualTypeArguments()[0];
            if (type instanceof Class) {
                Class<T> enumClass = (Class<T>) type;
                getItems().setAll(enumClass.getEnumConstants());
            }
        } catch (Exception e) {
            LOGGER.error("Error setting items: ", e);
        }
    }

    private void addElement() {
        if (getValue() == null) {
            return;
        }

        try {
            Node element = App.getComponentLoader().load(getValue());

            Parent parent = getParent();
            if (parent instanceof Pane container) {
                int comboBoxIndex = container.getChildren().indexOf(this);
                if (comboBoxIndex >= 0) {
                    container.getChildren().add(comboBoxIndex, element);
                    Platform.runLater(() -> {
                        if (!getItems().isEmpty()) {
                            setValue(null);
                        }
                    });
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error adding element: ", e);
        }
    }
}
