package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import javafx.stage.Stage;

public abstract class AbstractDialogController implements DialogController {

    protected Stage dialogStage;
    protected boolean saved;

    protected void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    protected void markSavedAndClose() {
        saved = true;
        closeDialog();
    }

    protected void markCancelledAndClose() {
        saved = false;
        closeDialog();
    }

    @Override
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public boolean isSaved() {
        return saved;
    }
}
