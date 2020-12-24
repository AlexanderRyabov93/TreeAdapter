package ru.alexapps.treeview.model;

import java.util.Objects;

public class CheckableTreeNode extends TreeNode {
    private boolean mChecked;

    public CheckableTreeNode() {
        this(0, 0, false);
    }
    public CheckableTreeNode(int lft, int rgt, boolean expanded) {
        this(lft, rgt, expanded, false);
    }
    public CheckableTreeNode(int lft, int rgt, boolean expanded, boolean checked) {
        super(lft, rgt, expanded);
        this.mChecked = checked;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CheckableTreeNode)) return false;
        if (!super.equals(o)) return false;
        CheckableTreeNode that = (CheckableTreeNode) o;
        return mChecked == that.mChecked;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mChecked);
    }
}
