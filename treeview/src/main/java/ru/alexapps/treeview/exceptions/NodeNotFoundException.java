package ru.alexapps.treeview.exceptions;

public class NodeNotFoundException extends IllegalArgumentException {

    public NodeNotFoundException(int lft, int rgt) {
        super("Invalid lft = " + lft + " or rgt = " + rgt + ". No nodes found");
    }
}
