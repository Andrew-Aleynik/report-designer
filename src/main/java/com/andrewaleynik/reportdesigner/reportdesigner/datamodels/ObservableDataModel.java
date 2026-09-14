package com.andrewaleynik.reportdesigner.reportdesigner.datamodels;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class ObservableDataModel {

    private final List<Consumer<ObservableDataModel>> listeners = new CopyOnWriteArrayList<>();

    public void onChange(Runnable listener) {
        if (listener == null) {
            return;
        }
        listeners.add(source -> listener.run());
    }

    public void onChange(Consumer<ObservableDataModel> listener) {
        if (listener == null) {
            return;
        }
        listeners.add(listener);
    }

    public void removeChangeListener(Consumer<ObservableDataModel> listener) {
        listeners.remove(listener);
    }

    protected void fireChanged() {
        for (Consumer<ObservableDataModel> listener : listeners) {
            listener.accept(this);
        }
    }
}
