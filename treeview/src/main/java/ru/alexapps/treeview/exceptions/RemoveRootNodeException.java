package ru.alexapps.treeview.exceptions;

public class RemoveRootNodeException extends RuntimeException {

    public RemoveRootNodeException() {
        super("Can't remove root node");
    }
}
