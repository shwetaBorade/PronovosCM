package com.pronovos.pdf.utils;

public class LinkUriAction {
    private String linkUri;
    private boolean addInBack;

    public LinkUriAction() {
    }

    public String getLinkUri() {
        return this.linkUri;
    }

    public void setLinkUri(String linkUri) {
        this.linkUri = linkUri;
    }

    public boolean isAddInBack() {
        return this.addInBack;
    }

    public void setAddInBack(boolean addInBack) {
        this.addInBack = addInBack;
    }

}
