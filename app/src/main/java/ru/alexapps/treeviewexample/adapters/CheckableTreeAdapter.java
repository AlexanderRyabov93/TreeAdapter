package ru.alexapps.treeviewexample.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.alexapps.treeview.utils.testutils.TestUtils;
import ru.alexapps.treeview.view.TreeAdapter;
import ru.alexapps.treeviewexample.R;
import ru.alexapps.treeviewexample.models.TreeItem;

public class CheckableTreeAdapter extends TreeAdapter<CheckableTreeAdapter.TextViewHolder, TreeItem> {

    private CheckableTreeAdapterListener mListener;
    private TreeItem mSelectedItem;

    public CheckableTreeAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        //Use getNodeAtPosition to find correct tree node
        TreeItem item = getNodeAtPosition(position);
        wrapItemWithPadding(holder.itemView, position);
        if(item.equals(mSelectedItem)) {
            holder.itemView.setBackgroundColor(Color.argb(128, 0, 0, 200));
        }else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
        holder.text.setText(item.toString());
        if(isLeaf(position)) {
            holder.expandButton.setVisibility(View.INVISIBLE);
        }else {
            holder.expandButton.setVisibility(View.VISIBLE);
            if(item.isExpanded()) {
                holder.expandButton.setImageResource(R.drawable.ic_keyboard_arrow_down_24px);
            }else {
                holder.expandButton.setImageResource(R.drawable.ic_keyboard_arrow_right_24px);
            }
        }

        if(isRoot(position)) {
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }else {
            holder.deleteButton.setVisibility(View.VISIBLE);
        }

        holder.checkBox.setChecked(item.isChecked());
        holder.expandButton.setOnClickListener(v -> {
            if(mListener != null) {
                mListener.onChangeExpanded(item, !item.isExpanded());
            }
        });
        holder.checkBox.setOnClickListener((v) -> {
            if(mListener != null ) {
                mListener.onChangeChecked(item, holder.checkBox.isChecked());
            }
        });
        holder.deleteButton.setOnClickListener(v -> {
          if(mListener != null) {
              mListener.onDelete(item);
          }
        });
        holder.itemView.setOnClickListener(v -> {
            if(mListener != null) {
                mListener.onClickItem(item);
            }
        });


    }
    public void setListener(@NonNull CheckableTreeAdapterListener listener) {
        mListener = listener;
    }
    public void setSelectedItem(@Nullable TreeItem item) {
        int oldPosition = getNodePosition(mSelectedItem);
        if(oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
        mSelectedItem = item;
        int position = getNodePosition(item);
        if(position != -1) {
            notifyItemChanged(position);
        }
    }


    class TextViewHolder extends RecyclerView.ViewHolder {
        final ImageButton expandButton;
        final CheckBox checkBox;
        final TextView text;
        final ImageButton deleteButton;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            expandButton = itemView.findViewById(R.id.imageButton_expanded);
            checkBox = itemView.findViewById(R.id.checkBox);
            text = itemView.findViewById(R.id.textView_item_name);
            deleteButton = itemView.findViewById(R.id.imageButton_delete);
        }
    }

    public interface CheckableTreeAdapterListener {
        void onChangeExpanded(TreeItem item, boolean value);
        void onChangeChecked(TreeItem item, boolean value);
        void onDelete(TreeItem item);
        void onClickItem(TreeItem item);
    }
}
