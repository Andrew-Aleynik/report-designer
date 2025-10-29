package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

public interface FormController extends Controller {
    void setParentController(Controller controller);
    boolean handleOk();
    default void handleCancel() {};
}
