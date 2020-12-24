package ru.alexapps.treeview.view;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.alexapps.treeview.model.TreeNode;
import ru.alexapps.treeview.utils.Tree;

public abstract class TreeAdapter<VH extends RecyclerView.ViewHolder, T extends TreeNode> extends RecyclerView.Adapter<VH> {

    private Tree<T> mTree;
    private final float mDensity;
    private int mPaddingDp = 10;

    public TreeAdapter(@NonNull Context context) {
        this(context, new ArrayList<>());

    }

    public TreeAdapter(@NonNull Context context, @NonNull List<T> dataSet) {
        this(context, new Tree<>(dataSet));
    }
    public TreeAdapter(@NonNull Context context, @NonNull Tree<T> tree) {
        mTree = tree;
        mDensity = context.getResources().getDisplayMetrics().density;
    }

    public void setData(@NonNull List<T> dataSet) {
        mTree.resetNodes(dataSet);
    }

    public void setData(@NonNull Tree<T> data) {
        mTree = data;
    }

    @Override
    public int getItemCount() {
        //Only visible nodes needed
        return mTree.getVisibleNodes().size();
    }

    /**
     * Returns nesting depth of node in the tree
     *
     * @param position position of ViewHolder
     * @return Returns nesting depth of node in the tree
     */
    public final int getDepthAtPosition(int position) {
        T node = getNodeAtPosition(position);
        return mTree.getDepth(node.getLft(), node.getRgt());
    }

    public final boolean isNodeExpanded(int position) {
        return getNodeAtPosition(position).isExpanded();
    }

    /**
     * Check if node is leaf of the tree
     * @param position position of ViewHolder
     * @return true if node has no children, false otherwise
     */
    public final boolean isLeaf(int position) {
        T node = getNodeAtPosition(position);
        return mTree.getDescendants(node.getLft(), node.getRgt()).size() == 0;
    }

    /**
     * Check if node is root of the tree
     * @param position position of ViewHolder
     * @return true if node has no ancestors, false otherwise
     */
    public final boolean isRoot(int position) {
        T node = getNodeAtPosition(position);
        return mTree.getAncestors(node.getLft(), node.getRgt()).size() == 0;
    }

    /**
     * Returns visible node in specified position
     *
     * @param position position of ViewHolder
     * @return Node
     */
    protected final T getNodeAtPosition(int position) {
        return mTree.getVisibleNodes().get(position);
    }

    /**
     * Sets padding left to view by its depth in the tree
     * @param view root view of item
     * @param position position of ViewHolder
     */
    protected void wrapItemWithPadding(View view, int position) {
        final int itemDepth = getDepthAtPosition(position);
        view.setPaddingRelative((int) (itemDepth * mDensity * mPaddingDp), 0, 0, 0);

    }

    /**
     * Returns position for node, -1 if node not found
     * @param node element to search for
     * @return Returns position for node
     */
    protected int getNodePosition(@Nullable T node) {
        return mTree.getVisibleNodes().indexOf(node);
    }

    /**
     * Sets padding value
     * @see #wrapItemWithPadding
     * @param paddingInDp
     */
    public void setTreeItemPadding(int paddingInDp) {
        mPaddingDp = paddingInDp;
    }

}
