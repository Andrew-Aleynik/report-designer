package com.andrewaleynik.reportdesigner.reportdesigner.services;

import java.io.File;

public interface ExportService<T> {
    File export(T object);
}
