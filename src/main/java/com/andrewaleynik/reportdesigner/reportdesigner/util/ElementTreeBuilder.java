package com.andrewaleynik.reportdesigner.reportdesigner.util;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;

import java.util.Comparator;
import java.util.TreeSet;

public final class ElementTreeBuilder {

    public static final Comparator<Element> ELEMENT_COMPARATOR = Comparator
            .comparing(Element::getLevel)
            .thenComparing(Element::getName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(Element::getCode, String.CASE_INSENSITIVE_ORDER);

    private ElementTreeBuilder() {
    }

    public static TreeSet<Element> buildFromRoot(Element root) {
        TreeSet<Element> treeSet = new TreeSet<>(ELEMENT_COMPARATOR);
        if (root != null) {
            addElementRecursively(treeSet, root);
        }
        return treeSet;
    }

    private static void addElementRecursively(TreeSet<Element> treeSet, Element element) {
        treeSet.add(element);
        for (Element child : element.getChildren()) {
            addElementRecursively(treeSet, child);
        }
    }
}
