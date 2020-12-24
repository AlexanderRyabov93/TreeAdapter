package ru.alexapps.treeviewexample.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ru.alexapps.treeview.utils.CheckableTree;
import ru.alexapps.treeview.utils.Tree;
import ru.alexapps.treeviewexample.models.TreeItem;
import static ru.alexapps.treeview.utils.testutils.TestUtils.*;


public class MainViewModel extends ViewModel {
    private final TreeLiveData mTreeLiveData;
    private final MutableLiveData<TreeItem> mSelectedItemLiveData = new MutableLiveData<>(null);

    public MainViewModel() {
        List<TreeItem> items = prepareTestData(new int[]{3, 5, 7, 3, 3, 3, 4, 6, 7, 6, 9}, ((lft, rgt) -> new TreeItem(lft, rgt, true)));
        mTreeLiveData = new TreeLiveData(new CheckableTree<>(items));
    }

    public void setChecked(int lft, int rgt, boolean value) {
        mTreeLiveData.setChecked(lft, rgt, value);
    }
    public void setExpanded(int lft, int rgt, boolean value) {
        mTreeLiveData.setExpanded(lft, rgt, value);
    }
    public void deleteNode(int lft, int rgt) {
        Tree.TreeUpdate<TreeItem> treeUpdate = mTreeLiveData.deleteNode(lft, rgt);
        if(treeUpdate.deleted.indexOf(mSelectedItemLiveData.getValue()) > -1) {
            //Selected item was removed
            mSelectedItemLiveData.setValue(null);
        }
    }
    public void selectItem(TreeItem item) {
        TreeItem oldSelectedItem = mSelectedItemLiveData.getValue();
        if(oldSelectedItem != null && oldSelectedItem.equals(item)) {
            //Remove selection on second select same item
            mSelectedItemLiveData.setValue(null);
        }else {
            mSelectedItemLiveData.setValue(item);
        }

    }
    public void addNode() {
        TreeItem selectedItem = mSelectedItemLiveData.getValue();
        if(selectedItem == null) throw new IllegalStateException("Can't add child to null node");
        TreeItem newNode = new TreeItem(-1, -1, true);
        if(!selectedItem.isExpanded()) {
            mTreeLiveData.setExpanded(selectedItem.getLft(), selectedItem.getRgt(), true);
        }
        mTreeLiveData.addNode(newNode, selectedItem.getLft(), selectedItem.getRgt());
    }
    public LiveData<CheckableTree<TreeItem>> getTreeLiveData() {
        return mTreeLiveData;
    }
    public LiveData<TreeItem> getSelectedItemLiveData() {
        return mSelectedItemLiveData;
    }


    class TreeLiveData extends LiveData<CheckableTree<TreeItem>> {


        TreeLiveData(CheckableTree<TreeItem> tree) {
            super(tree);
        }
        public void setChecked(int lft, int rgt, boolean value) {
            CheckableTree<TreeItem> tree =  getNonNullValue();
            tree.setNodeChecked(lft, rgt, value);
            setValue(tree);
        }
        public void setExpanded(int lft, int rgt, boolean value) {
            CheckableTree<TreeItem> tree =  getNonNullValue();
            tree.setExpanded(lft, rgt, value);
            setValue(tree);
        }
        public Tree.TreeUpdate<TreeItem> deleteNode(int lft, int rgt) {
            CheckableTree<TreeItem> tree =  getNonNullValue();
            Tree.TreeUpdate<TreeItem> update = tree.deleteNode(lft, rgt);
            setValue(tree);
            return update;
        }
        public void addNode(TreeItem node, int parentLft, int parentRgt) {
            CheckableTree<TreeItem> tree =  getNonNullValue();
            tree.addNode(node, parentLft, parentRgt, 0);
            setValue(tree);
        }
        CheckableTree<TreeItem> getNonNullValue() {
            CheckableTree<TreeItem> value = getValue();
            if(value == null) {
                throw new IllegalStateException("Tree must not be null");
            }
            return value;
        }
    }
}
