package com.andrewaleynik.reportdesigner.reportdesigner.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;
import java.util.function.Function;

public final class JavaFxControls {

    private JavaFxControls() {
    }

    public static <T> void bindComboBoxDisplay(ComboBox<T> comboBox, Function<T, String> displayText) {
        comboBox.setCellFactory(param -> createListCell(displayText));
        comboBox.setButtonCell(createListCell(displayText));
    }

    private static <T> ListCell<T> createListCell(Function<T, String> displayText) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : displayText.apply(item));
            }
        };
    }

    public static <T> TableColumn<T, String> textColumn(String title, Function<T, String> valueExtractor) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> {
            T value = cellData.getValue();
            String text = value == null ? "" : nullToEmpty(valueExtractor.apply(value));
            return new SimpleStringProperty(text);
        });
        return column;
    }

    public static <T> TableColumn<T, Void> actionsColumn(Consumer<T> onEdit, Consumer<T> onDelete) {
        TableColumn<T, Void> column = new TableColumn<>("Действия");
        column.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = createActionButton("Редактировать", null);
            private final Button deleteButton = createActionButton("Удалить", "-fx-text-fill: red;");

            {
                editButton.setOnAction(event -> invokeAction(onEdit));
                deleteButton.setOnAction(event -> invokeAction(onDelete));
            }

            private void invokeAction(Consumer<T> action) {
                T item = getTableView().getItems().get(getIndex());
                if (item != null && action != null) {
                    action.accept(item);
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
        return column;
    }

    public static void configureConstrainedTable(TableView<?> tableView) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);
    }

    private static Button createActionButton(String label, String extraStyle) {
        Button button = new Button(label);
        String style = "-fx-font-size: 10px; -fx-padding: 2 5;";
        if (extraStyle != null) {
            style += " " + extraStyle;
        }
        button.setStyle(style);
        return button;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
