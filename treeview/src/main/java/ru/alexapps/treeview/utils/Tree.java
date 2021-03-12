package ru.alexapps.treeview.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ru.alexapps.treeview.exceptions.RemoveRootNodeException;
import ru.alexapps.treeview.model.TreeNode;
import ru.alexapps.treeview.exceptions.NodeNotFoundException;

public class Tree<T extends TreeNode> {
    List<T> mNodes;

    public Tree(@NonNull T rootNode) {
        this(Collections.singletonList(rootNode));
    }

    public Tree(@NonNull List<T> nodes) {
        if (!isTreeValid(nodes)) throw new IllegalStateException("Tree is not valid");
        this.mNodes = sortByLft(nodes);
    }

    public static <T extends TreeNode> List<T> sortByLft(@NonNull List<T> nodes) {
        return nodes.stream()
                .sorted((node1, node2) -> node1.getLft() - node2.getLft())
                .collect(Collectors.toList());
    }

    public static <T extends TreeNode> boolean isTreeValid(@NonNull List<T> nodes) {
        if (nodes.size() == 0) return false;
        List<? extends TreeNode> sortedNodes = sortByLft(nodes);
        //Node with min lft is root
        TreeNode rootNode = sortedNodes.get(0);
        //Root node lft must be 0
        if (rootNode.getLft() != 0) return false;

        for (TreeNode node : nodes) {
            if (node.getLft() == node.getRgt()) return false;
            final int descendantsSize = getDescendants(nodes, node.getLft(), node.getRgt()).size();
            int expectedSize = (node.getRgt() - node.getLft() - 1) / 2;
            if (descendantsSize != expectedSize) return false;
        }
        return true;
    }

    /**
     * Returns List of nodes which are not descendants of any collapsed node
     *
     * @return All expanded nodes and their descendants
     */
    public List<T> getVisibleNodes() {
        List<T> copyNodes = new ArrayList<>(mNodes);
        for (T node : mNodes) {
            if (!node.isExpanded()) {
                copyNodes.removeAll(getDescendants(node));
            }
        }
        return copyNodes;
    }

    public List<T> getDescendants(T node) {
        return getDescendants(node.getLft(), node.getRgt());
    }

    public List<T> getDescendants(int nodeLft, int nodeRgt) {
        return getDescendants(mNodes, nodeLft, nodeRgt);
    }

    public void resetNodes(List<T> nodes) {
        if (!isTreeValid(nodes)) throw new IllegalStateException("Tree is not valid");
        mNodes.clear();
        mNodes.addAll(sortByLft(nodes));
    }

    /**
     * Returns node by lft and rgt indexes. If not found returns null
     *
     * @param lft the lft index
     * @param rgt the rgt index
     * @return Returns node by lft and rgt indexes
     */
    @Nullable
    public T getNodeByLftRgt(int lft, int rgt) {
        return mNodes.stream()
                .filter(node -> node.getLft() == lft && node.getRgt() == rgt)
                .findAny()
                .orElse(null);
    }

    /**
     * Returns depth of node  with specified indexes
     *
     * @param lft the lft index
     * @param rgt the rgt index
     * @return depth of node
     */
    public int getDepth(int lft, int rgt) {
        return getAncestors(lft, rgt).size();
    }

    /**
     * Returns List of ancestors of node with specified indexes
     *
     * @param lft the lft index
     * @param rgt the rgt index
     * @return Returns List of ancestors
     */
    public List<T> getAncestors(int lft, int rgt) {
        T currentNode = getNodeByLftRgt(lft, rgt);
        if (currentNode == null)
            throw new NodeNotFoundException(lft, rgt);
        return mNodes.stream()
                .filter(node -> node.getLft() < lft && node.getRgt() > rgt)
                .collect(Collectors.toList());
    }

    /**
     * Returns parent of node with specified indexes. If tree has not node with such lft and rgt throws IllegalArgumentException
     *
     * @param lft the lft index
     * @param rgt the rgt index
     * @return Returns parent of node with specified indexes, or null, if node has no parent (in case of root node)
     */
    @Nullable
    public T getParent(int lft, int rgt) {
        T currentNode = getNodeByLftRgt(lft, rgt);
        if (currentNode == null)
            throw new NodeNotFoundException(lft, rgt);
        int index = mNodes.indexOf(currentNode);
        //Go down through array because it is sorted by lft, and parents lft is always less then child lft
        for (int i = index - 1; i >= 0; i--) {
            T node = mNodes.get(i);
            if (node.getLft() < lft && node.getRgt() > rgt)
                return node;
        }
        return null;
    }

    public List<T> getChildren(int lft, int rgt) {
        T currentNode = getNodeByLftRgt(lft, rgt);
        if (currentNode == null)
            throw new NodeNotFoundException(lft, rgt);
        //Nodes already sorted by lft
        List<T> descendants = getDescendants(lft, rgt);
        List<T> children = new ArrayList<>();
        int skipCount;
        for (int i = 0; i < descendants.size(); i += skipCount) {
            //First descendant is child
            T descendant = descendants.get(i);
            //Number of child descendants + 1
            skipCount = (descendant.getRgt() - descendant.getLft() - 1) / 2 + 1;
            children.add(descendant);
        }
        return children;
    }

    /**
     * Removes a node and all of its descendants. Updates indexes in tree after it
     *
     * @param lft the lft index of the node to remove
     * @param rgt the rgt index of the node to remove
     * @return TreeUpdated object with changes in tree (deleted and updated nodes)
     * @see TreeUpdate
     */
    public TreeUpdate<T> deleteNode(int lft, int rgt) {
        T currentNode = getNodeByLftRgt(lft, rgt);
        if (currentNode == null)
            throw new NodeNotFoundException(lft, rgt);
        T root = getRoot();
        if (lft == root.getLft() && rgt == root.getRgt()) {
            throw new RemoveRootNodeException();
        }
        List<T> deleted = getDescendants(currentNode);
        deleted.add(currentNode);
        mNodes.removeAll(deleted);
        List<T> updated = new ArrayList<>();
        final int decrement = deleted.size() * 2;
        for (T node : mNodes) {
            if (node.getRgt() > rgt) {
                node.setRgt(node.getRgt() - decrement);
                if (node.getLft() > lft) {
                    node.setLft(node.getLft() - decrement);
                }
                updated.add(node);
            }
        }
        return new TreeUpdate<>(new ArrayList<>(), updated, deleted);
    }

    /**
     * Adds node to tree
     *
     * @param node   the node to add
     * @param parent the parent node
     * @return TreeUpdated object with changes in tree
     * @see TreeUpdate
     */
    public TreeUpdate<T> addNode(@NonNull T node, @NonNull T parent) {
        return addNode(node, parent.getLft(), parent.getRgt(), 0);
    }

    /**
     * Adds node to tree on specified position.
     *
     * @param node              the node to add
     * @param parentLft         the lft index of the parent node
     * @param parentRgt         the rgt index of the parent node
     * @param indexInsideParent position inside parent (0 - first child)
     * @return TreeUpdated object with changes in tree
     * @see TreeUpdate
     */
    public TreeUpdate<T> addNode(@NonNull T node, int parentLft, int parentRgt, int indexInsideParent) {
        T parentNode = getNodeByLftRgt(parentLft, parentRgt);
        if (parentNode == null)
            throw new NodeNotFoundException(parentLft, parentRgt);
        int nodeLft = parentRgt;
        List<T> children = getChildren(parentLft, parentRgt);
        if (indexInsideParent < 0 || indexInsideParent > children.size())
            throw new IllegalArgumentException("Wrong indexInsideParent = " + indexInsideParent + " total children: " + children.size());
        if (indexInsideParent < children.size()) {
            nodeLft = children.get(indexInsideParent).getLft();
        }
        List<T> updated = new ArrayList<>();
        for (T treeNode : mNodes) {
            if (treeNode.getRgt() >= nodeLft) {
                treeNode.setRgt(treeNode.getRgt() + 2);
                if (treeNode.getLft() >= nodeLft) {
                    treeNode.setLft(treeNode.getLft() + 2);
                }
                updated.add(treeNode);
            }
        }
        List<T> inserted = new ArrayList<>();
        node.setLft(nodeLft);
        node.setRgt(nodeLft + 1);
        inserted.add(node);
        mNodes.add(node);
        mNodes = sortByLft(mNodes);
        return new TreeUpdate<>(inserted, updated, new ArrayList<>());
    }


    public TreeUpdate<T> setExpanded(int lft, int rgt, boolean value) {
        T node = getNodeByLftRgt(lft, rgt);
        if (node == null) throw new NodeNotFoundException(lft, rgt);
        List<T> updated = new ArrayList<>(1);
        if (node.isExpanded() != value) {
            node.setExpanded(value);
            updated.add(node);
        }
        return new TreeUpdate<>(new ArrayList<>(0), updated, new ArrayList<>());
    }

    public int size() {
        return mNodes.size();
    }

    /**
     * Returns root node of the tree
     *
     * @return Returns root node of the tree
     */
    public T getRoot() {
        return mNodes.get(0);
    }

    /**
     * Moves node and all its descendants inside specified parent at specified index
     * @param node node to move
     * @param newParent new parent of node
     * @param newIndex index inside parent node (from 0 to parent node children array size)
     * @return TreeUpdated object with changes in tree
     * @see TreeUpdate
     */
    public TreeUpdate<T> moveNode(T node, T newParent, int newIndex) {
        if (node.getLft() == newParent.getLft() && node.getRgt() == newParent.getRgt()) {
            throw new IllegalArgumentException("You are trying to move node inside itself");
        }
        int oldLft = node.getLft();
        int oldRgt = node.getRgt();
        List<T> newParentChildren = getChildren(newParent.getLft(), newParent.getRgt());
        if (newIndex < 0 || newIndex > newParentChildren.size()) {
            throw new IllegalArgumentException("Illegal new index. Children size = " + newParentChildren.size());
        }
        int newLft;
        if (newIndex == 0) {
            newLft = newParentChildren.get(newIndex).getLft();
        } else {
            newLft = newParentChildren.get(newIndex - 1).getRgt() + 1;
        }
        int movedTreeWidth = node.getRgt() - node.getLft() + 1;
        int moveDistance = newLft - node.getLft();
        int tmppos = node.getLft();
        if (moveDistance < 0) {
            moveDistance -= movedTreeWidth;
            tmppos += movedTreeWidth;
        }

        List<T> updated = new ArrayList<>();
        for (T nodeFromArray : mNodes) {
            int lftBefore = nodeFromArray.getLft();
            int rgtBefore = nodeFromArray.getRgt();
            //create new space for subtree
            if (nodeFromArray.getLft() >= newLft) {
                nodeFromArray.setLft(nodeFromArray.getLft() + movedTreeWidth);
            }
            if (nodeFromArray.getRgt() >= newLft) {
                nodeFromArray.setRgt(nodeFromArray.getRgt() + movedTreeWidth);
            }
            //move subtree into new space
            if (nodeFromArray.getLft() >= tmppos && nodeFromArray.getRgt() < tmppos + movedTreeWidth) {
                nodeFromArray.setRgt(nodeFromArray.getRgt() + moveDistance);
                nodeFromArray.setLft(nodeFromArray.getLft() + moveDistance);
            }
            //remove old space vacated by subtree
            if (nodeFromArray.getLft() > oldRgt) {
                nodeFromArray.setLft(nodeFromArray.getLft() - movedTreeWidth);
            }
            if (nodeFromArray.getRgt() > oldRgt) {
                nodeFromArray.setRgt(nodeFromArray.getRgt() - movedTreeWidth);
            }
            if (lftBefore != nodeFromArray.getLft() || rgtBefore != nodeFromArray.getRgt()) {
                updated.add(nodeFromArray);
            }
        }
        mNodes = sortByLft(mNodes);
        return new TreeUpdate<>(new ArrayList<>(), updated, new ArrayList<>());
    }

    /**
     * Finds all descendants of node with specified lft an rgt
     *
     * @param nodes   all nodes
     * @param nodeLft lft of node
     * @param nodeRgt rgt of node
     * @return List of descendants
     */
    static <T extends TreeNode> List<T> getDescendants(@NonNull List<T> nodes, int nodeLft, int nodeRgt) {
        return nodes.stream()
                .filter(node -> node.getLft() > nodeLft && node.getRgt() < nodeRgt)
                .collect(Collectors.toList());
    }

    public static class TreeUpdate<T> {
        /**
         * List of inserted nodes
         */
        public final List<T> inserted;
        /**
         * List of updated nodes
         */
        public final List<T> updated;
        /**
         * List of deleted nodes
         */
        public final List<T> deleted;

        public TreeUpdate(@NonNull List<T> inserted, @NonNull List<T> updated, @NonNull List<T> deleted) {
            this.inserted = inserted;
            this.updated = updated;
            this.deleted = deleted;
        }


    }

}
