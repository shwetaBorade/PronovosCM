package com.pronovoscm.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.CreateTransfersActivity;
import com.pronovoscm.activity.LoginActivity;
import com.pronovoscm.activity.TransferOverviewDetailsActivity;
import com.pronovoscm.adapter.TransferDetailsAdapter;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.TransferOverviewProvider;
import com.pronovoscm.model.request.transferdetails.TransferDetailRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.transferdelete.TransferDeleteResponse;
import com.pronovoscm.model.response.transferdetail.TransferDetailResponse;
import com.pronovoscm.model.response.transferoverview.TransferData;
import com.pronovoscm.model.response.transferoverview.TransferOverviewResponse;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class TransferOverviewDetailFragment extends Fragment {
    @Inject
    TransferOverviewProvider transferOverviewProvider;

    @BindView(R.id.equipmentsRecyclerView)
    RecyclerView equipmentsRecyclerView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;

    private List<TransferData> transferDataArrayList = new ArrayList<>();
    private TransferDetailsAdapter transferDetailAdapter;
    private LoginResponse loginResponse;
    private String title;
    private int status;
    private int projectId;

    @SuppressLint("ValidFragment")
    public TransferOverviewDetailFragment(int i) {
        status = i;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (NetworkService.isNetworkAvailable(getActivity())) {
            ((TransferOverviewDetailsActivity) getActivity()).setLoading(true);
            noRecordTextView.setText("Loading Transfer Overview");
            noRecordTextView.setVisibility(View.VISIBLE);
        }
    }

    public void loadingText() {
        noRecordTextView.setText("Loading Transfer Overview");
        noRecordTextView.setVisibility(View.VISIBLE);
    }

    public void loadingFail() {
        if (transferDataArrayList == null || transferDataArrayList.size() == 0) {
            if (status == 1) {
                noRecordTextView.setText("There are currently no Requests Awaiting Approval.");
            } else if (status == 0) {
                noRecordTextView.setText("There are currently no Drafts.");
            } else if (status == 2) {
                noRecordTextView.setText("There are currently no Scheduled Transfers.");
            } else if (status == 3) {
                noRecordTextView.setText("There are currently no Transfers Pending Approval.");
            } else if (status == 4) {
                noRecordTextView.setText("There are currently no Transfers Pending Reconciliation.");
            }
            noRecordTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.transfer_detail_view_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        projectId = getActivity().getIntent().getIntExtra("project_id", 0);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (transferDataArrayList.size() == 0) {
            transferDataArrayList.clear();
//            callTransferOverviewAPI(status);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        /*if (getActivity() != null) {
            transferDataArrayList.clear();
//            callTransferOverviewAPI(status);
        }*/

    }


    public void editTransfer(int adapterPosition, String title) {
        if (transferDataArrayList != null && transferDataArrayList.size() > adapterPosition) {
            CustomProgressBar.showDialog(getActivity());
            TransferData transferData = transferDataArrayList.get(adapterPosition);
            callTransferLocationAPI(transferData.getEqTransferRequestsId(), title, transferData.getTransferType());

        }
    }

    /**
     * Get List
     */
    public void callTransferLocationAPI(int transferID, String title, int transferType) {
        TransferDetailRequest transferOverviewRequest = new TransferDetailRequest();
        transferOverviewRequest.setTransferId(transferID);

        transferOverviewProvider.callTransferDetail(transferOverviewRequest, new ProviderResult<TransferDetailResponse>() {
            @Override
            public void success(TransferDetailResponse transferOverviewResponse) {
                CustomProgressBar.dissMissDialog(getContext());
                startActivity(new Intent(getActivity(), CreateTransfersActivity.class).putExtra("project_id", projectId)
                        .putExtra("transfer_option", "New Request")
                        .putExtra(Constants.INTENT_KEY_TRANSFER_TYPE, transferType)
                        .putExtra("details", (Parcelable) transferOverviewResponse.getData()
                                .getDetails()).putExtra("selected_tab",
                                ((TransferOverviewDetailsActivity) getActivity()).getSelectTab()));
                getActivity().finish();
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(getContext());
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                getActivity().finish();
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(getContext());
                ((TransferOverviewDetailsActivity) getActivity()).showMessageAlert(getActivity(), message, getString(R.string.ok));
            }
        }, loginResponse);
    }

    /**
     * Get List
     */
    public void callTransferDelete(int transferID) {
        TransferDetailRequest transferOverviewRequest = new TransferDetailRequest();
        transferOverviewRequest.setTransferId(transferID);

        transferOverviewProvider.callTransferDelete(transferOverviewRequest, new ProviderResult<TransferDeleteResponse>() {
            @Override
            public void success(TransferDeleteResponse transferOverviewResponse) {
                CustomProgressBar.dissMissDialog(getContext());
                if (getActivity() != null) {
                    ((TransferOverviewDetailsActivity) getActivity()).clearAndRefresh();
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(getContext());
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                getActivity().finish();
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(getContext());
                ((TransferOverviewDetailsActivity) getActivity()).showMessageAlert(getActivity(), message, getString(R.string.ok));
            }
        }, loginResponse);
    }

    public void setOffline(Boolean event) {
        if (event) {
            transferDetailAdapter.hidePopUp();
        }
    }


    public void updateData(TransferOverviewResponse transferOverviewResponse) {
        transferDataArrayList.clear();
        transferDataArrayList.addAll(transferOverviewResponse.getData().getTransfers().getTransferData());
        equipmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        transferDetailAdapter = new TransferDetailsAdapter(getActivity(), transferDataArrayList,
                equipmentsRecyclerView, TransferOverviewDetailFragment.this, title, projectId);
        equipmentsRecyclerView.setAdapter(transferDetailAdapter);
        transferDetailAdapter.notifyDataSetChanged();
        if (transferDataArrayList.size() <= 0) {
            if (status == 1) {
                noRecordTextView.setText("There are currently no Requests Awaiting Approval.");
            } else if (status == 0) {
                noRecordTextView.setText("There are currently no Drafts.");
            } else if (status == 2) {
                noRecordTextView.setText("There are currently no Scheduled Transfers.");
            } else if (status == 3) {
                noRecordTextView.setText("There are currently no Transfers Pending Approval.");
            } else if (status == 4) {
                noRecordTextView.setText("There are currently no Transfers Pending Reconciliation.");
            }
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            noRecordTextView.setVisibility(View.GONE);
        }
    }
}
