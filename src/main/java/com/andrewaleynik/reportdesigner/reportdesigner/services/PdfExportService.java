package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;

import java.io.File;
import java.util.TreeSet;

public class PdfExportService {
    private final ElementsTreePdfExportService elementsTreePdfExportService;

    public PdfExportService(ElementsTreePdfExportService elementsTreePdfExportService) {
        this.elementsTreePdfExportService = elementsTreePdfExportService;
    }

    public File exportElementsTree(Element root) {
        TreeSet<Element> elementsTree = buildElementsTree(root);
        return elementsTreePdfExportService.export(elementsTree);
    }

    private TreeSet<Element> buildElementsTree(Element root) {
        TreeSet<Element> treeSet = new TreeSet<>(this::compareElements);

        if (root != null) {
            addElementToTreeSet(treeSet, root);
        }

        return treeSet;
    }

    private void addElementToTreeSet(TreeSet<Element> treeSet, Element element) {
        treeSet.add(element);

        for (Element child : element.getChildren()) {
            addElementToTreeSet(treeSet, child);
        }
    }

    private int compareElements(Element e1, Element e2) {
        int levelCompare = Integer.compare(e1.getLevel(), e2.getLevel());
        if (levelCompare != 0) {
            return levelCompare;
        }

        int nameCompare = e1.getName().compareToIgnoreCase(e2.getName());
        if (nameCompare != 0) {
            return nameCompare;
        }

        return e1.getCode().compareToIgnoreCase(e2.getCode());
    }
}
