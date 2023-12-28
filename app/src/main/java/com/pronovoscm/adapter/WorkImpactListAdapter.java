package com.pronovoscm.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.DailyWorkImpactActivity;
import com.pronovoscm.fragments.WorkImpactFragment;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.WorkImpact;
import com.pronovoscm.persistence.domain.WorkImpactAttachments;
import com.pronovoscm.persistence.repository.WorkImpactRepository;
import com.pronovoscm.utils.SharedPref;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class WorkImpactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LoginResponse loginResponse;
    private final Activity mActivity;
    private final List<WorkImpact> mWorkImpacts;
    private final RecyclerView workImpactsRecyclerView;
    @Inject
    WorkImpactRepository mmWorkImpactRepository;
    private PopupWindow mPopupWindow;
    private int canEditWorkDetail;
    private int canDeleteWorkDetail;

    public WorkImpactListAdapter(Activity activity, List<WorkImpact> workImpacts, RecyclerView workImpactsRecyclerView) {

        this.mWorkImpacts = workImpacts;
        this.workImpactsRecyclerView = workImpactsRecyclerView;
        mActivity = activity;
        ((PronovosApplication) mActivity.getApplication()).getDaggerComponent().inject(this);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mActivity).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        canEditWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getEditProjectDailyReport();
        canDeleteWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getDeleteProjectDailyReport();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.work_details_item_list, parent, false);

        return new WorkDetailsViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((WorkDetailsViewHolder) holder).bind(mWorkImpacts.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mWorkImpacts != null) {
            return mWorkImpacts.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void hidePopUp() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    public class WorkDetailsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.companeyNameTextView)
        TextView mCompaneyNameTextView;
        //        @BindView(R.id.countTextView)
//        TextView countTextView;
        @BindView(R.id.workSummaryTextView)
        TextView workSummaryTextView;

        @BindView(R.id.locationTextView)
        TextView locationTextView;
        @BindView(R.id.attachmentRecycleView)
        RecyclerView attachmentRecycleView;
        @BindView(R.id.workDetailsView)
        CardView workDetailsView;
        @BindView(R.id.workSummaryView)
        TextView workSummaryView;
        @BindView(R.id.workViewOptions)
        TextView workTextView;
        @BindView(R.id.attachmentTextView)
        TextView attachmentTextView;

        WorkDetailsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(WorkImpact workDetail, int position) {
            mCompaneyNameTextView.setText(workDetail.getCompanyName());
            workSummaryTextView.setText(workDetail.getWorkSummary());
            locationTextView.setText(workDetail.getWorkImpLocation());
            workSummaryView.setText(mActivity.getString(R.string.impact_summary));
            attachmentRecycleView.setLayoutManager(new LinearLayoutManager(attachmentRecycleView.getContext(), RecyclerView.HORIZONTAL, false));
            List<WorkImpactAttachments> workDetailsAttachments = mmWorkImpactRepository.getAttachments(workDetail.getWorkImpactReportIdMobile());
            attachmentRecycleView.setAdapter(new WorkImpactAttachmentAdapter(mActivity, workDetailsAttachments, null, mmWorkImpactRepository));
            if (workDetailsAttachments.size() <= 0) {
                attachmentTextView.setVisibility(View.GONE);
            } else {
                attachmentTextView.setVisibility(View.VISIBLE);

            }
            hidePopUp();
            workTextView.setVisibility(View.GONE);
            workDetailsView.setOnClickListener(view -> {
                FragmentManager fragmentManager = ((AppCompatActivity) workDetailsView.getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                WorkImpactFragment fragment = new WorkImpactFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("projectId", workDetail.getProjectId());
                bundle.putSerializable("workImpactDate", workDetail.getCreatedAt());
                bundle.putLong("workImpactMobileId", workDetail.getWorkImpactReportIdMobile());
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.workContainer, fragment, fragment.getClass().getSimpleName()).addToBackStack(WorkImpactFragment.class.getName());
                fragmentTransaction.commit();
            });

            workTextView.setOnClickListener(v -> {
                workImpactsRecyclerView.scrollToPosition(position);
                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.custom_popup_menu, null);

                mPopupWindow = new PopupWindow(
                        customView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                TextView editMenuOption = customView.findViewById(R.id.editTextView);
                TextView deleteMenuOption = customView.findViewById(R.id.deleteTextView);
                View dividerMenu = customView.findViewById(R.id.dividerMenu);
               /* if (canDeleteWorkDetail != 1) {
                    deleteMenuOption.setVisibility(View.GONE);
                    dividerMenu.setVisibility(View.GONE);
                }*/
                RelativeLayout popupView = customView.findViewById(R.id.popupView);
                if (canDeleteWorkDetail != 1) {
                    deleteMenuOption.setVisibility(View.GONE);
                    dividerMenu.setVisibility(View.GONE);

                    int _100sdp = (int) workTextView.getContext().getResources().getDimension(R.dimen._90sdp);
                    int _50sdp = (int) workTextView.getContext().getResources().getDimension(R.dimen._58sdp);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_100sdp, _50sdp);
                    popupView.setLayoutParams(layoutParams);
                }

                editMenuOption.setOnClickListener(v1 -> {
                    mPopupWindow.dismiss();
                    FragmentManager fragmentManager = ((AppCompatActivity) workDetailsView.getContext()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    WorkImpactFragment fragment = new WorkImpactFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("projectId", workDetail.getProjectId());
                    bundle.putSerializable("workImpactDate", workDetail.getCreatedAt());
                    bundle.putLong("workImpactMobileId", workDetail.getWorkImpactReportIdMobile());

                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.workContainer, fragment, fragment.getClass().getSimpleName()).addToBackStack(WorkImpactFragment.class.getName());
                    fragmentTransaction.commit();
                });

                deleteMenuOption.setOnClickListener(v12 -> {
                    AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
//                        alertDialog.setTitle(getString(R.string.message));
                    alertDialog.setMessage(mActivity.getString(R.string.are_you_sure_you_want_to_delete_this_entry));
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mActivity.getString(R.string.cancel), (dialog, which) -> {
                        dialog.dismiss();
                        mPopupWindow.dismiss();
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, mActivity.getString(R.string.ok), (dialog, which) -> {
                        alertDialog.dismiss();
                        workDetail.setDeletedAt(new Date());
                        workDetail.setIsSync(false);
                        ((DailyWorkImpactActivity) mActivity).updateWorkImpact(workDetail);
                        mPopupWindow.dismiss();
//                                        dailyCrewReportActivity.onBackPressed();
                    });
                    alertDialog.show();
                    Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(ContextCompat.getColor(mActivity, R.color.gray_948d8d));
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                });

                new Handler().postDelayed(() -> {
                    int[] loc_int = new int[2];

                    try {
                        workTextView.getLocationOnScreen(loc_int);
                    } catch (NullPointerException npe) {
                        //Happens when the view doesn't exist on screen anymore.

                    }
                    Rect location = new Rect();
                    location.left = loc_int[0];
                    location.top = loc_int[1];
                    location.right = location.left + v.getWidth();
                    location.bottom = location.top + v.getHeight();


                    //noinspection deprecation
                    mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                    mPopupWindow.setOutsideTouchable(true);
                    mPopupWindow.setFocusable(true);
//                    mPopupWindow.showAsDropDown(v, -158,0);
                    mPopupWindow.showAtLocation(workTextView, Gravity.TOP | Gravity.END, 0, location.top + v.getHeight());
                }, 100);
            });
        }

    }
}
