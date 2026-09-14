package com.andrewaleynik.reportdesigner.reportdesigner.util;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.HashMap;
import java.util.Map;

public final class ElementTreeViewHelper {

    private ElementTreeViewHelper() {
    }

    public static void refresh(TreeView<Element> treeView, Element rootElement) {
        if (rootElement == null) {
            treeView.setRoot(null);
            return;
        }

        Map<Element, Boolean> expandedStates = saveExpandedStates(treeView);
        TreeItem<Element> root = createTreeItem(rootElement, expandedStates);
        treeView.setRoot(root);
        restoreExpandedStates(root, expandedStates);
        root.setExpanded(true);
    }

    private static Map<Element, Boolean> saveExpandedStates(TreeView<Element> treeView) {
        Map<Element, Boolean> expandedStates = new HashMap<>();
        if (treeView.getRoot() != null) {
            saveExpandedStatesRecursive(treeView.getRoot(), expandedStates);
        }
        return expandedStates;
    }

    private static void saveExpandedStatesRecursive(TreeItem<Element> item,
                                                    Map<Element, Boolean> expandedStates) {
        if (item != null && item.getValue() != null) {
            expandedStates.put(item.getValue(), item.isExpanded());
            for (TreeItem<Element> child : item.getChildren()) {
                saveExpandedStatesRecursive(child, expandedStates);
            }
        }
    }

    private static TreeItem<Element> createTreeItem(Element element, Map<Element, Boolean> expandedStates) {
        TreeItem<Element> item = new TreeItem<>(element);
        if (expandedStates.containsKey(element)) {
            item.setExpanded(expandedStates.get(element));
        }
        if (element.getChildren() != null) {
            for (Element child : element.getChildren()) {
                item.getChildren().add(createTreeItem(child, expandedStates));
            }
        }
        return item;
    }

    private static void restoreExpandedStates(TreeItem<Element> root, Map<Element, Boolean> expandedStates) {
        if (root != null) {
            restoreExpandedStatesRecursive(root, expandedStates);
        }
    }

    private static void restoreExpandedStatesRecursive(TreeItem<Element> item,
                                                       Map<Element, Boolean> expandedStates) {
        if (item != null && item.getValue() != null) {
            Boolean wasExpanded = expandedStates.get(item.getValue());
            if (wasExpanded != null) {
                item.setExpanded(wasExpanded);
            }
            for (TreeItem<Element> child : item.getChildren()) {
                restoreExpandedStatesRecursive(child, expandedStates);
            }
        }
    }
}
