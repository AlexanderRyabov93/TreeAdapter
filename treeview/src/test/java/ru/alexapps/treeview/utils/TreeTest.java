package ru.alexapps.treeview.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.alexapps.treeview.exceptions.NodeNotFoundException;
import ru.alexapps.treeview.exceptions.RemoveRootNodeException;

import static org.junit.Assert.*;
import static ru.alexapps.treeview.utils.testutils.TestUtils.*;

public class TreeTest {

    @Test(expected = IllegalStateException.class)
    public void constructorTest_root_node_should_throw_exception() {
        new Tree<>(new TestTreeNode(0, 0));
}
    @Test
    public void constructorTest_root_node_should_create_valid_tree() {
        Tree<TestTreeNode> tree = new Tree<>(new TestTreeNode(0, 1));
        assertEquals(1, tree.size());
    }

    @Test
    public void sortByLft_should_return_correctList() {
        List<TestTreeNode> tree = prepareTestData(new int[]{4, 2, 1, 0, 0, 1});
        List<TestTreeNode> sortedTree = Tree.sortByLft(tree);
        int prevLft = -1;
        for (TestTreeNode node : sortedTree) {
            assertTrue(node.getLft() > prevLft);
            prevLft = node.getLft();
        }
    }

    @Test
    public void getDescendants_should_return_empty_list() {
        //Create tree with only one node
        List<TestTreeNode> tree = prepareTestData(new int[]{});
        assertEquals(0, Tree.getDescendants(tree, 0, 1).size());
    }

    @Test
    public void getDescendants_should_return_list_with_correct_nodes() {
        List<TestTreeNode> tree = prepareTestData(new int[]{5, 2});
        List<TestTreeNode> descendants = Tree.getDescendants(tree, 1, 6);
        assertEquals(2, descendants.size());
        assertEquals(descendants.get(0), new TestTreeNode(2, 3));
        assertEquals(descendants.get(1), new TestTreeNode(4, 5));
    }

    @Test
    public void isTreeValid_should_return_false_tree_empty() {
        assertFalse(Tree.isTreeValid(new ArrayList<>()));
    }

    @Test
    public void isTreeValid_should_return_true_tree_has_only_one_node() {
        assertTrue(Tree.isTreeValid(prepareTestData(new int[]{})));
    }

    @Test
    public void isTreeValid_should_return_true_tree_has_many_nodes() {
        assertTrue(Tree.isTreeValid(prepareTestData(new int[]{5, 4, 7, 2, 4})));
    }

    @Test
    public void isTreeValid_should_return_false_wrong_root_lft() {
        List<TestTreeNode> tree = prepareTestData(new int[]{5, 4, 7, 2, 4});
        //Set wrong lft to root node
        tree.get(tree.size() - 1).setLft(87897);
        assertFalse(Tree.isTreeValid(tree));
    }

    @Test
    public void isTreeValid_should_return_false_missed_node() {
        List<TestTreeNode> tree = prepareTestData(new int[]{5, 4, 7, 2, 4});
        tree.remove(1);
        assertFalse(Tree.isTreeValid(tree));
    }

    @Test
    public void getVisibleNodes_root_node_collapsed() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        List<TestTreeNode> visibleNodes = tree.getVisibleNodes();
        assertEquals(1, visibleNodes.size());
    }

    @Test
    public void getVisibleNodes_root_node_expanded() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2});
        //Set root node expanded
        nodes.get(nodes.size() - 1).setExpanded(true);
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        List<TestTreeNode> visibleNodes = tree.getVisibleNodes();
        assertEquals(3, visibleNodes.size());
    }

    @Test
    public void getVisibleNodes_all_nodes_expanded() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 5, 2});
        nodes.forEach(node -> {
            node.setExpanded(true);
        });
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        List<TestTreeNode> visibleNodes = tree.getVisibleNodes();
        assertEquals(2 + 5 + 2 + 1, visibleNodes.size());
    }
    @Test
    public void getNodeByLftRgt_should_return_null() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        assertNull(tree.getNodeByLftRgt(1, 2));
    }
    @Test
    public void getNodeByLftRgt_should_return_node() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        assertEquals(nodes.get(0), tree.getNodeByLftRgt(0, 1));
    }
    @Test(expected = IllegalArgumentException.class)
    public void getParent_should_throw_exception_wrong_args() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        //No node with such indexes
        tree.getParent(1,2);
    }
    @Test
    public void getParent_should_return_null() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        //Rot node hasn't parent
        assertNull(tree.getParent(0,1));
    }
    @Test
    public void getParent_should_return_root() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{1, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        assertEquals(nodes.get(nodes.size() - 1), tree.getParent(1,4));
    }
    @Test
    public void getParent_should_return_second_node() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{1, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        assertEquals(nodes.get(1), tree.getParent(2,3));
    }
    @Test(expected = IllegalArgumentException.class)
    public void getAncestors_should_throw_exception() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        //No node with such indexes
        tree.getAncestors(-1,-1);
    }
    @Test
    public void getAncestors_should_return_empty_list() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        //Rot node hasn't ancestors
        assertEquals(0, tree.getAncestors(0,7).size());
    }
    @Test
    public void getAncestors_should_return_list_with_one_item() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        List<TestTreeNode> ancestors = tree.getAncestors(1,4);
        assertEquals(1, ancestors.size());
        assertEquals(nodes.get(nodes.size() - 1), ancestors.get(0));
    }
    @Test
    public void getAncestors_should_return_list_with_two_items() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        List<TestTreeNode> ancestors = tree.getAncestors(2,3);
        assertEquals(2, ancestors.size());
        assertEquals(nodes.get(nodes.size() - 1), ancestors.get(0));
    }
    @Test(expected = NodeNotFoundException.class)
    public void deleteNode_shouldThrowException() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        //No node with such indexes
        tree.deleteNode(-1,-1);
    }
    @Test(expected = RemoveRootNodeException.class)
    public void deleteNode_should_throw_exception() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        //Try to remove root node
        tree.deleteNode(0, 7);

    }
    @Test
    public void deleteNode_should_delete_2_nodes_and_update_others() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        Tree.TreeUpdate<TestTreeNode> treeUpdate = tree.deleteNode(1, 4);
        assertEquals(0, treeUpdate.inserted.size());
        assertEquals(2,treeUpdate.deleted.size());
        assertEquals(new TestTreeNode(2, 3), treeUpdate.deleted.get(0));
        assertEquals(new TestTreeNode(1, 4), treeUpdate.deleted.get(1));
        assertEquals(2, treeUpdate.updated.size());
        assertEquals(new TestTreeNode(0,3), treeUpdate.updated.get(0));
        assertEquals(new TestTreeNode(1,2), treeUpdate.updated.get(1));
    }
    @Test(expected = NodeNotFoundException.class)
    public void getChildren_should_throw_exception() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        tree.getChildren(-1, -1);
    }
    @Test
    public void getChildren_should_return_2_nodes() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        List<TestTreeNode> children = tree.getChildren(0, 7);
        assertEquals(2, children.size());
        assertEquals(new TestTreeNode(1,4), children.get(0));
        assertEquals(new TestTreeNode(5,6), children.get(1));
    }
    @Test
    public void getChildren_should_return_1_node() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        List<TestTreeNode> children = tree.getChildren(1, 4);
        assertEquals(1, children.size());
        assertEquals(new TestTreeNode(2,3), children.get(0));
    }
    @Test(expected = NodeNotFoundException.class)
    public void addNode_should_throw_exception_node_not_found() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        tree.addNode(new TestTreeNode(0, 0), -1, -1, 0);
    }
    @Test(expected = IllegalArgumentException.class)
    public void addNode_should_throw_exception_invalid_index() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        tree.addNode(new TestTreeNode(0, 0), 0, 7, -1);
    }
    @Test
    public void add_node_last_child_of_root() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        Tree.TreeUpdate<TestTreeNode> treeUpdate = tree.addNode(new TestTreeNode(0, 0), 0, 7, 2);
        assertEquals(0, treeUpdate.deleted.size());
        assertEquals(1, treeUpdate.updated.size());
        assertEquals(new TestTreeNode(0, 9), treeUpdate.updated.get(0));
        assertEquals(1, treeUpdate.inserted.size());
        assertEquals(new TestTreeNode(7, 8), treeUpdate.inserted.get(0));
    }
    @Test
    public void add_node_first_child_of_root() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        Tree.TreeUpdate<TestTreeNode> treeUpdate = tree.addNode(new TestTreeNode(0, 0), 0, 7, 0);
        assertEquals(0, treeUpdate.deleted.size());
        assertEquals(4, treeUpdate.updated.size());
        assertEquals(new TestTreeNode(0, 9), treeUpdate.updated.get(0));
        assertEquals(new TestTreeNode(3, 6), treeUpdate.updated.get(1));
        assertEquals(new TestTreeNode(4, 5), treeUpdate.updated.get(2));
        assertEquals(new TestTreeNode(7, 8), treeUpdate.updated.get(3));
        assertEquals(1, treeUpdate.inserted.size());
        assertEquals(new TestTreeNode(1, 2), treeUpdate.inserted.get(0));
    }
    @Test
    public void add_node_to_empty_parent() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        Tree.TreeUpdate<TestTreeNode> treeUpdate = tree.addNode(new TestTreeNode(0, 0), 5, 6, 0);
        assertEquals(0, treeUpdate.deleted.size());
        assertEquals(2, treeUpdate.updated.size());
        assertEquals(new TestTreeNode(0, 9), treeUpdate.updated.get(0));
        assertEquals(new TestTreeNode(5, 8), treeUpdate.updated.get(1));
        assertEquals(1, treeUpdate.inserted.size());
        assertEquals(new TestTreeNode(6, 7), treeUpdate.inserted.get(0));
    }
    @Test
    public void getRoot_should_return_correct_node() {
        List<TestTreeNode> nodes = prepareTestData(new int[]{2, 1});
        Tree<TestTreeNode> tree = new Tree<>(nodes);
        assertEquals(new TestTreeNode(0, 7), tree.getRoot());
    }


}

