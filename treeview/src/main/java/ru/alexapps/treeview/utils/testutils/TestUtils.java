package ru.alexapps.treeview.utils.testutils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.alexapps.treeview.model.TreeNode;

public class TestUtils {

    /**
     * Generates valid Nested set
     *
     * @param sizes children in each level. Root is always only 1,
     *              [0] value in array is number of it's children,
     *              [1] value in array is number of children of first child
     *              etc...
     * @return list of nodes
     */
    public static List<TestTreeNode> prepareTestData(@NonNull int[] sizes) {
        return prepareTestData(sizes, TestTreeNode::new);
    }
    /**
     * Generates valid Nested set
     *
     * @param sizes children in each level. Root is always only 1,
     *              [0] value in array is number of it's children,
     *              [1] value in array is number of children of first child
     *              etc...
     * @param factory the Factory to create specific node
     * @return list of nodes
     */
    public static <T extends TreeNode> List<T> prepareTestData(@NonNull int[] sizes, TreeNodeFactory<T> factory) {
        return prepareTestData(sizes, 0, 0, 1, factory);
    }

    private static <T extends TreeNode> List<T> prepareTestData(int[] sizes, int childLft, int childrenNumberIndex, int prevChildNumber, TreeNodeFactory<T> factory) {
        List<T> result = new ArrayList<>();
        if (childrenNumberIndex >= sizes.length) {
            result.add(factory.createTreeNode(childLft, childLft + 1));
            return result;
        }
        int childNumber = sizes[childrenNumberIndex];
        for (int i = 0; i < childNumber; i++) {
            result.addAll(prepareTestData(sizes, childLft + result.size() * 2 + 1, childrenNumberIndex + prevChildNumber + i, childNumber, factory));
        }
        //Don't forget add root for all of children
        result.add(factory.createTreeNode(childLft, childLft + result.size() * 2 + 1));
        return result;
    }

    public static class TestTreeNode extends TreeNode {


        public TestTreeNode(int lft, int rgt) {
            super(lft, rgt, false);
        }

        @Override
        @NonNull
        public String toString() {
            return getLft() + "_" + getRgt() + "_" + isExpanded();
        }
    }

    public interface TreeNodeFactory<T extends TreeNode> {
        T createTreeNode(int lft, int rgt);
    }
}
