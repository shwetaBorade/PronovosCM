package com.pronovoscm.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.activity.DailyCrewReportActivity;
import com.pronovoscm.fragments.CrewFragment;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.CrewList;
import com.pronovoscm.utils.SharedPref;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CrewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<CrewList> mCrewLists;
    private final DailyCrewReportActivity dailyCrewReportActivity;
    private final Context mContext;
    private final RecyclerView crewRecyclerView;
    private final int canEditWorkDetail;
    private final int canDeleteWorkDetail;
    private PopupWindow mPopupWindow;
    private CrewFragment fragment;
    private LoginResponse loginResponse;

    public CrewListAdapter(List<CrewList> crewLists, DailyCrewReportActivity dailyCrewReportActivity, Context context, RecyclerView crewRecyclerView) {

        this.mCrewLists = crewLists;
        this.dailyCrewReportActivity = dailyCrewReportActivity;
        this.mContext = context;
        this.crewRecyclerView = crewRecyclerView;
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        canEditWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getEditProjectDailyReport();
        canDeleteWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getDeleteProjectDailyReport();

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.crew_item_list, parent, false);
        return new CrewListViewHolder(view);

    }

    public void hidePopup() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((CrewListViewHolder) holder).bind(mCrewLists.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mCrewLists != null) {
            return mCrewLists.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void hideKeyBoard() {
        if (fragment != null) {
            fragment.hideKeyBoard();
        }
    }

    public class CrewListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.companeyNameTextView)
        TextView mCompaneyNameTextView;
        @BindView(R.id.tradeTextView)
        TextView mTradeTextView;
        @BindView(R.id.suptTextView)
        TextView suptTextView;
        @BindView(R.id.journeymanTextView)
        TextView journeymanTextView;
        @BindView(R.id.foremanTextView)
        TextView foremanTextView;
        @BindView(R.id.apprenticeTextView)
        TextView apprenticeTextView;
        @BindView(R.id.textViewOptions)
        TextView optionsTextView;
        @BindView(R.id.workDetailsView)
        CardView workDetailsView;

        //        @BindView(R.id.moreMenu)
//        ImageView moreMenu;
        @BindView(R.id.crewItemView)
        ConstraintLayout crewItemView;

        CrewListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(CrewList crewList, int position) {
            suptTextView.setText(String.valueOf(crewList.getSupt()));
            journeymanTextView.setText(String.valueOf(crewList.getJourneyman()));
            foremanTextView.setText(String.valueOf(crewList.getForeman()));
            apprenticeTextView.setText(String.valueOf(crewList.getApprentice()));
            mCompaneyNameTextView.setText(crewList.getCompanyName());
            mTradeTextView.setText(crewList.getTrade());
            crewItemView.setBackgroundColor(ContextCompat.getColor(crewItemView.getContext(), R.color.white));
            dailyCrewReportActivity.position = position;

            hidePopup();
                optionsTextView.setVisibility(View.GONE);
/*
            if (canEditWorkDetail != 1 && canDeleteWorkDetail != 1) {
            } else {
                optionsTextView.setVisibility(View.VISIBLE);
            }
*/

            workDetailsView.setOnClickListener(view -> {
                FragmentManager fragmentManager = ((AppCompatActivity) crewItemView.getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragment = new CrewFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("projectId", crewList.getProjectId());
                bundle.putLong("crewListMobileId", crewList.getCrewReportIdMobile());
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.crewContainer, fragment, fragment.getClass().getSimpleName()).addToBackStack(CrewFragment.class.getName());
                fragmentTransaction.commit();

            });
            optionsTextView.setOnClickListener(v -> {
                crewRecyclerView.scrollToPosition(position);

                LayoutInflater inflater = (LayoutInflater) dailyCrewReportActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.custom_popup_menu, null);

                mPopupWindow = new PopupWindow(
                        customView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                TextView editMenuOption = customView.findViewById(R.id.editTextView);
                TextView deleteMenuOption = customView.findViewById(R.id.deleteTextView);
                View dividerMenu = customView.findViewById(R.id.dividerMenu);
                RelativeLayout popupView = customView.findViewById(R.id.popupView);
                if (canDeleteWorkDetail != 1) {
                    deleteMenuOption.setVisibility(View.GONE);
                    dividerMenu.setVisibility(View.GONE);

                    int _100sdp = (int) optionsTextView.getContext().getResources().getDimension(R.dimen._90sdp);
                    int _50sdp = (int) optionsTextView.getContext().getResources().getDimension(R.dimen._58sdp);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_100sdp, _50sdp);
                    popupView.setLayoutParams(layoutParams);
                }
                editMenuOption.setOnClickListener(v1 -> {
                    mPopupWindow.dismiss();
                    FragmentManager fragmentManager = ((AppCompatActivity) crewItemView.getContext()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragment = new CrewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("projectId", crewList.getProjectId());
                    bundle.putLong("crewListMobileId", crewList.getCrewReportIdMobile());
                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.crewContainer, fragment, fragment.getClass().getSimpleName()).addToBackStack(CrewFragment.class.getName());
                    fragmentTransaction.commit();
                });

                deleteMenuOption.setOnClickListener(v12 -> {
                    AlertDialog alertDialog = new AlertDialog.Builder(dailyCrewReportActivity).create();
                    alertDialog.setMessage(mContext.getString(R.string.are_you_sure_you_want_to_delete_this_entry));
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.cancel), (dialog, which) -> {
                        dialog.dismiss();
                        mPopupWindow.dismiss();
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.ok), (dialog, which) -> {
                        alertDialog.dismiss();
                        crewList.setDeletedAt(new Date());
                        crewList.setIsSync(false);
                        dailyCrewReportActivity.updateCrew(crewList);
                        mPopupWindow.dismiss();
                    });
                    alertDialog.show();
                    Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(ContextCompat.getColor(mContext, R.color.gray_948d8d));
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

                });

                new Handler().postDelayed(() -> {
                    int[] loc_int = new int[2];

                    try {
                        optionsTextView.getLocationOnScreen(loc_int);
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

                    mPopupWindow.showAtLocation(optionsTextView, Gravity.TOP | Gravity.END, 0, location.top + v.getHeight());
                }, 100);
            });


        }

    }
}