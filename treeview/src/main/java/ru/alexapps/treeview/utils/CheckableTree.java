package ru.alexapps.treeview.utils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.alexapps.treeview.exceptions.NodeNotFoundException;
import ru.alexapps.treeview.model.CheckableTreeNode;

public class CheckableTree<T extends CheckableTreeNode> extends Tree<T> {

    public CheckableTree(T rootNode) {
        super(rootNode);
    }

    public CheckableTree(@NonNull List<T> nodes) {
        super(nodes);
    }

    public TreeUpdate<T> setNodeChecked(@NonNull T node, boolean value) {
        return setNodeChecked(node.getLft(), node.getRgt(), value);
    }
    public TreeUpdate<T>setNodeChecked(int lft, int rgt, boolean value) {
        T treeNode = getNodeByLftRgt(lft, rgt);
        if(treeNode == null) {
            throw new NodeNotFoundException(lft, rgt);
        }
        List<T> updated = new ArrayList<>();
        if(value != treeNode.isChecked()) {
            treeNode.setChecked(value);
            updated.add(treeNode);
        }
        List<T> descendants = getDescendants(lft, rgt);
        descendants.forEach(descendant -> {
            if(descendant.isChecked() != value) {
                descendant.setChecked(value);
                updated.add(descendant);
            }

        });
        List<T> ancestors =  getAncestors(lft, rgt);
        //If any descendant unchecked, all its ancestors unchecked too
        if(!value) {
            ancestors.forEach(ancestor -> {
                if(ancestor.isChecked()) {
                    ancestor.setChecked(false);
                    updated.add(ancestor);
                }
            });
        }else {
            //Reverse list of ancestors to go through it from child to parent, and set checked all nodes, with checked children
            Collections.reverse(ancestors);
            ancestors.forEach(ancestor -> {
                boolean hasUncheckedChild = getChildren(ancestor.getLft(), ancestor.getRgt()).stream().anyMatch(child -> !child.isChecked());
                if(!hasUncheckedChild && !ancestor.isChecked()) {
                    ancestor.setChecked(true);
                    updated.add(ancestor);
                }
            });
        }
        return new TreeUpdate<>(new ArrayList<>(), updated, new ArrayList<>());
    }
}
