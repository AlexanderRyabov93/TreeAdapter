package ru.alexapps.treeview.model;

import java.util.Objects;

public class TreeNode {
    private int mLft;
    private int mRgt;
    private boolean mExpanded;

    public TreeNode() {
        this(0, 0, false);
    }
    public TreeNode(int lft, int rgt, boolean expanded) {
        mLft = lft;
        mRgt = rgt;
        mExpanded = expanded;
    }

    public int getLft() {
        return mLft;
    }

    public void setLft(int lft) {
        this.mLft = lft;
    }

    public int getRgt() {
        return mRgt;
    }

    public void setRgt(int rgt) {
        this.mRgt = rgt;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setExpanded(boolean expanded) {
        this.mExpanded = expanded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeNode treeNode = (TreeNode) o;
        return mLft == treeNode.mLft &&
                mRgt == treeNode.mRgt &&
                mExpanded == treeNode.mExpanded;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mLft, mRgt, mExpanded);
    }
}
