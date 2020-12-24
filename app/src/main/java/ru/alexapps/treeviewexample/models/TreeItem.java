package ru.alexapps.treeviewexample.models;

import androidx.annotation.NonNull;

import java.util.Objects;

import ru.alexapps.treeview.model.CheckableTreeNode;

public class TreeItem extends CheckableTreeNode {



    public TreeItem(int lft, int rgt, boolean checked) {
        super(lft, rgt, false, checked);
    }
    @NonNull
    @Override
    public String toString() {
        return getLft() + "_" + getRgt();
    }
}
