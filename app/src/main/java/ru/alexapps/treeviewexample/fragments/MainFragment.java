package ru.alexapps.treeviewexample.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.alexapps.treeviewexample.R;
import ru.alexapps.treeviewexample.adapters.CheckableTreeAdapter;
import ru.alexapps.treeviewexample.models.TreeItem;
import ru.alexapps.treeviewexample.viewmodels.MainViewModel;

import static ru.alexapps.treeview.utils.testutils.TestUtils.prepareTestData;

public class MainFragment extends Fragment implements CheckableTreeAdapter.CheckableTreeAdapterListener {
    private MainViewModel mainViewModel;

    public MainFragment() {
        super(R.layout.fragment_main);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        FloatingActionButton actionButton = view.findViewById(R.id.floating_action_button);
        CheckableTreeAdapter adapter = new CheckableTreeAdapter(requireContext());
        adapter.setListener(this);
        adapter.setTreeItemPadding(5);
        recyclerView.setAdapter(adapter);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        actionButton.setOnClickListener(v -> {
            mainViewModel.addNode();
        });
        mainViewModel.getTreeLiveData().observe(getViewLifecycleOwner(), tree -> {
            adapter.setData(tree);
            adapter.notifyDataSetChanged();
        });
        mainViewModel.getSelectedItemLiveData().observe(getViewLifecycleOwner(), selectedItem -> {
            adapter.setSelectedItem(selectedItem);
            if(selectedItem == null) {
                actionButton.setVisibility(View.GONE);
            }else {
                actionButton.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onChangeExpanded(TreeItem item, boolean value) {
        mainViewModel.setExpanded(item.getLft(), item.getRgt(), value);
    }

    @Override
    public void onChangeChecked(TreeItem item, boolean value) {
        mainViewModel.setChecked(item.getLft(), item.getRgt(), value);
    }

    @Override
    public void onDelete(TreeItem item) {
        mainViewModel.deleteNode(item.getLft(), item.getRgt());
    }

    @Override
    public void onClickItem(TreeItem item) {
        mainViewModel.selectItem(item);
    }
}