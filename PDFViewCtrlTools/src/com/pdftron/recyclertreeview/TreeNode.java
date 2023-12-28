package com.pdftron.recyclertreeview;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/TellH/RecyclerTreeView
 * Modified by PDFTron
 */

public class TreeNode<T extends LayoutItemType> implements Cloneable {
    private T content;
    private TreeNode<T> parent;
    private List<TreeNode<T>> childList;
    private boolean isExpand;
    private boolean isLocked;
    //the tree high
    public int height = UNDEFINE;

    public static final int UNDEFINE = -1;

    public TreeNode(@NonNull T content) {
        this.content = content;
        this.childList = new ArrayList<>();
    }

    public int getHeight() {
        if (isRoot())
            height = 0;
        else
            height = parent.getHeight() + 1;
        return height;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return childList == null || childList.isEmpty();
    }

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public List<TreeNode<T>> getChildList() {
        return childList;
    }

    public void setChildList(List<TreeNode<T>> childList) {
        this.childList.clear();
        for (TreeNode<T> treeNode : childList) {
            addChild(treeNode);
        }
    }

    public TreeNode<T> addChild(TreeNode<T> node) {
        if (childList == null)
            childList = new ArrayList<>();
        childList.add(node);
        node.parent = this;
        return this;
    }

    public TreeNode<T> addChildAtPos(int position, TreeNode<T> node) {
        if (childList == null)
            childList = new ArrayList<>();
        if (position >= childList.size()) {
            childList.add(node);
        } else {
            childList.add(position, node);
        }
        node.parent = this;
        return this;
    }

    public TreeNode<T> setChildAtPos(int position, TreeNode<T> node) {
        if (childList == null)
            childList = new ArrayList<>();
        if (position >= childList.size()) {
            childList.add(node);
        } else {
            childList.set(position, node);
        }
        node.parent = this;
        return this;
    }

    public boolean toggle() {
        isExpand = !isExpand;
        return isExpand;
    }

    public void collapse() {
        if (isExpand) {
            isExpand = false;
        }
    }

    public void collapseAll() {
        if (childList == null || childList.isEmpty()) {
            return;
        }
        for (TreeNode<T> child : this.childList) {
            child.collapseAll();
        }
    }

    public void expand() {
        if (!isExpand) {
            isExpand = true;
        }
    }

    public void expandAll() {
        expand();
        if (childList == null || childList.isEmpty()) {
            return;
        }
        for (TreeNode<T> child : this.childList) {
            child.expandAll();
        }
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public TreeNode<T> lock() {
        isLocked = true;
        return this;
    }

    public TreeNode<T> unlock() {
        isLocked = false;
        return this;
    }

    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "content=" + this.content.toString() +
                ", parent=" + (parent == null ? "null" : parent.getContent().toString()) +
                ", childList=" + (childList == null ? "null" : childList) +
                ", isExpand=" + isExpand +
                '}';
    }

    @Override
    public TreeNode<T> clone() throws CloneNotSupportedException {
        super.clone();
        TreeNode<T> clone = new TreeNode<>(this.content);
        clone.isExpand = this.isExpand;
        clone.childList = this.childList;
        clone.parent = this.parent;
        return clone;
    }
}