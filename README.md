# TreeAdapter for RecyclerView in Android

<img src="https://github.com/AlexanderRyabov93/TreeAdapter/blob/screenshots/screenshots/TreeWork.gif" alt="Tree" width="350" height="600">

### Gradle:
[![](https://jitpack.io/v/AlexanderRyabov93/TreeAdapter.svg)](https://jitpack.io/#AlexanderRyabov93/TreeAdapter)

Step 1. Add the JitPack repository in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
Step 2. Add the dependency
```
implementation 'com.github.AlexanderRyabov93:TreeAdapter:1.1.0'
```

##  Usage

1. Create your TreeItem based on TreeNode
```java
public class TreeItem extends TreeNode {
  // Define some fields, methods
}
```
2. Create your Adapter based on TreeAdapter
```java
public class MyAdapter extends TreeAdapter<MyAdapter.MyViewHolder, TreeItem> {

    public MyAdapter (@NonNull Context context) {
        super(context);
    }
   //Override onCreateViewHolder as usual
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderholder, int position) {
        // Use getNodeAtPosition to find correct tree node
        TreeItem item = getNodeAtPosition(position);
        // You can use this helper method to set different padding to items on different depth
        wrapItemWithPadding(holder.itemView, position);
        // All other code to bind viewHolder
   }
}
```
3. Set Adapter to RecyclerView
```java
   MyAdapter adapter = new MyAdapter(context);
   recyclerView.setAdapter(adapter);
   //You can set data as List of TreeNode, or as complete Tree
   adapter.setData(data);
```
##  Overview
The `Tree` class stores the nodes as [Nested Set](https://en.wikipedia.org/wiki/Nested_set_model)  and provides methods for interacting with them.
You can crete tree and add nodes to it like this:
```java
   //Create tree with root node
   Tree<TreeItem> tree = new Tree<TreeItem>(new TreeItem(0, 1));
   //Add child to root node
   Tree.TreeUpdate<TreeItem> update = tree.addNode(new TreeItem(), tree.getRoot());
   //Add child to just inserted node
   tree.addNode(new TreeItem(), update.inserted.get(0));
```
See [demo app](https://github.com/AlexanderRyabov93/TreeAdapter/tree/master/app/src/main/java/ru/alexapps/treeviewexample) for more examples of usage
