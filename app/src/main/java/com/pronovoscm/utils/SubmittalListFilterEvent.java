package com.pronovoscm.utils;

import com.pronovoscm.model.SubmittalStatusEnum;

public class SubmittalListFilterEvent {

    private SubmittalStatusEnum submittalStatusEnum;

    public SubmittalListFilterEvent(SubmittalStatusEnum submittalStatusEnum) {
        this.submittalStatusEnum = submittalStatusEnum;
    }


    public SubmittalStatusEnum getSubmittalStatusEnum() {
        return submittalStatusEnum;
    }


    @Override
    public String toString() {
        return "SubmittalListFilterEvent{" +
                "submittalStatusEnum=" + submittalStatusEnum +
                '}';
    }
}
