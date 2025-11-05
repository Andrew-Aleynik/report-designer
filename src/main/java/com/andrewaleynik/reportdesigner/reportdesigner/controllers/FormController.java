package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

public interface FormController extends Controller {
    boolean handleOk();

    default void handleCancel() {
    }
}
