package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.util.ElementTreeBuilder;

import java.io.File;
import java.util.TreeSet;

public class PdfExportService {

    private final ElementsTreePdfExportService elementsTreePdfExportService;

    public PdfExportService(ElementsTreePdfExportService elementsTreePdfExportService) {
        this.elementsTreePdfExportService = elementsTreePdfExportService;
    }

    public File exportElementsTree(Element root) {
        TreeSet<Element> elementsTree = ElementTreeBuilder.buildFromRoot(root);
        return elementsTreePdfExportService.export(elementsTree);
    }
}
