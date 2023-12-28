package com.pronovos.pdf.utils;

public class AddPunchList {
    String content;
    Integer punchNumber;
    Integer status;

    public AddPunchList() {
    }

    public Integer getPunchNumber() {
        return punchNumber;
    }

    public void setPunchNumber(Integer punchNumber) {
        this.punchNumber = punchNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
