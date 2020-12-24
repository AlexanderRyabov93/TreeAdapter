package ru.alexapps.treeview.utils;

import org.junit.Test;

import java.util.List;

import ru.alexapps.treeview.exceptions.NodeNotFoundException;
import ru.alexapps.treeview.model.CheckableTreeNode;

import static org.junit.Assert.*;
import static ru.alexapps.treeview.utils.testutils.TestUtils.prepareTestData;

public class CheckableTreeTest {
    @Test(expected = NodeNotFoundException.class)
    public void setNodeChecked_should_throw_exception() {
        List<TestCheckableTreeNode> nodes = prepareTestData(new int[] {2, 1}, ((lft, rgt) -> new TestCheckableTreeNode(lft, rgt, false)));
        CheckableTree<TestCheckableTreeNode> tree = new CheckableTree<>(nodes);
        tree.setNodeChecked(-1, -1, false);
    }
    @Test
    public void setNodeChecked_root_should_set_all_checked() {
        List<TestCheckableTreeNode> nodes = prepareTestData(new int[] {2, 1}, ((lft, rgt) -> new TestCheckableTreeNode(lft, rgt, false)));
        CheckableTree<TestCheckableTreeNode> tree = new CheckableTree<>(nodes);
        Tree.TreeUpdate<TestCheckableTreeNode> treeUpdate = tree.setNodeChecked(0, 7, true);
        assertEquals(4, treeUpdate.updated.size());
        assertTrue(treeUpdate.updated.get(0).isChecked());
        assertTrue(treeUpdate.updated.get(1).isChecked());
        assertTrue(treeUpdate.updated.get(2).isChecked());
        assertTrue(treeUpdate.updated.get(3).isChecked());
    }
    @Test
    public void setNodeChecked_should_set_checked_parent() {
        List<TestCheckableTreeNode> nodes = prepareTestData(new int[] {2, 1}, ((lft, rgt) -> new TestCheckableTreeNode(lft, rgt, false)));
        CheckableTree<TestCheckableTreeNode> tree = new CheckableTree<>(nodes);
        Tree.TreeUpdate<TestCheckableTreeNode> treeUpdate = tree.setNodeChecked(2, 3, true);
        //Root node will not be checked because it has one more unchecked child
        assertEquals(2, treeUpdate.updated.size());
        assertEquals(new TestCheckableTreeNode(2,3, true), treeUpdate.updated.get(0));
        assertEquals(new TestCheckableTreeNode(1, 4, true), treeUpdate.updated.get(1));
    }
    @Test
    public void setNodeChecked_should_set_unchecked_all_ancestors() {
        //Create tree with all nodes checked
        List<TestCheckableTreeNode> nodes = prepareTestData(new int[] {2, 1}, ((lft, rgt) -> new TestCheckableTreeNode(lft, rgt, true)));
        CheckableTree<TestCheckableTreeNode> tree = new CheckableTree<>(nodes);
        Tree.TreeUpdate<TestCheckableTreeNode> treeUpdate = tree.setNodeChecked(2, 3, false);
        assertEquals(3, treeUpdate.updated.size());
        assertEquals(new TestCheckableTreeNode(2,3, false), treeUpdate.updated.get(0));
        assertEquals(new TestCheckableTreeNode(0, 7, false), treeUpdate.updated.get(1));
        assertEquals(new TestCheckableTreeNode(1, 4, false), treeUpdate.updated.get(2));
    }
    @Test
    public void setBodeChecked_should_change_nothing_value_same() {
        //Create tree with all nodes checked
        List<TestCheckableTreeNode> nodes = prepareTestData(new int[] {2, 1}, ((lft, rgt) -> new TestCheckableTreeNode(lft, rgt, true)));
        CheckableTree<TestCheckableTreeNode> tree = new CheckableTree<>(nodes);
        //Set node checked (same value)
        Tree.TreeUpdate<TestCheckableTreeNode> treeUpdate = tree.setNodeChecked(2, 3, true);
        assertEquals(0, treeUpdate.updated.size());
    }


    static class TestCheckableTreeNode extends CheckableTreeNode {
        public TestCheckableTreeNode(int lft, int rgt) {
            this(lft, rgt, false);
        }
        public TestCheckableTreeNode(int lft, int rgt, boolean checked) {
            super(lft, rgt, false, checked);
        }
    }

}