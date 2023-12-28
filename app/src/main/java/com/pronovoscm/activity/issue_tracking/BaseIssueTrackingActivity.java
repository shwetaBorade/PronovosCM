package com.pronovoscm.activity.issue_tracking;

import com.pronovoscm.activity.BaseActivity;
import com.pronovoscm.data.NetworkStateProvider;
import com.pronovoscm.data.issuetracking.IssueListResponse;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class BaseIssueTrackingActivity<Response> extends BaseActivity {

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkStateChange(NetworkStateProvider.NetworkStateEnum event) {
        toggleOfflineView(event.isOffline());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IssueListResponse<Response> issueListResponse) {
        dataUpdate(issueListResponse);
    }

    abstract void toggleOfflineView(Boolean event);

    abstract void dataUpdate(IssueListResponse<Response> issueListResponse);
}