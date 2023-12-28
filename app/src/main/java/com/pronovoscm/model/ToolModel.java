package com.pronovoscm.model;

import com.pronovoscm.utils.photoeditor.ToolType;

public class ToolModel {
    private String mToolName;
    private int mToolIcon;
    private ToolType mToolType;

    public ToolModel(String toolName, int toolIcon, ToolType toolType) {
        mToolName = toolName;
        mToolIcon = toolIcon;
        mToolType = toolType;
    }

    public String getmToolName() {
        return mToolName;
    }

    public void setmToolName(String mToolName) {
        this.mToolName = mToolName;
    }

    public int getmToolIcon() {
        return mToolIcon;
    }

    public void setmToolIcon(int mToolIcon) {
        this.mToolIcon = mToolIcon;
    }

    public ToolType getmToolType() {
        return mToolType;
    }

    public void setmToolType(ToolType mToolType) {
        this.mToolType = mToolType;
    }
}
