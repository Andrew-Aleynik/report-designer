package com.andrewaleynik.reportdesigner.reportdesigner.enums;

import com.andrewaleynik.reportdesigner.reportdesigner.ComponentNameHolder;
import com.andrewaleynik.reportdesigner.reportdesigner.Titled;

public enum GroupEnum implements Titled, ComponentNameHolder {
    FUNCTIONS("Группа функций", "FunctionsGroup.fxml"),
    TABLES("Группа матриц", "TableGroup.fxml");

    private final String title;
    private final String componentName;

    GroupEnum(String title, String componentName) {
        this.title = title;
        this.componentName = componentName;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getComponentName() {
        return componentName;
    }
}
