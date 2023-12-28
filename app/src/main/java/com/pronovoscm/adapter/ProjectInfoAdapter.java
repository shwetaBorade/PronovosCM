package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.model.response.projectinfo.Info;
import com.pronovoscm.model.response.projectinfo.PjInfo;
import com.pronovoscm.model.response.projectinfo.PjSchedule;
import com.pronovoscm.model.response.projectinfo.PjSite;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProjectInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Integer> mInfoType;
    private Activity mActivity;
    private Info info;

    public ProjectInfoAdapter(Activity mActivity, ArrayList<Integer> mInfoType, Info info) {
        this.mInfoType = mInfoType;
        this.mActivity = mActivity;
        this.info = info;
        setHasStableIds(true);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -1) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.project_info_fragment, parent, false);

            return new ProjectInfoViewHolder(view);
        } else if (viewType == -2) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.project_schedule_fragment, parent, false);

            return new ProjectScheduleViewHolder(view);

        } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.project_site_fragment, parent, false);

            return new ProjectSiteViewHolder(view);

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProjectSiteViewHolder) {
            ((ProjectSiteViewHolder) holder).bind();
        } else if (holder instanceof ProjectScheduleViewHolder) {
            ((ProjectScheduleViewHolder) holder).bind();
        } else {
            ((ProjectInfoViewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        if (mInfoType != null) {
            return mInfoType.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return -1;
        } else if (position == 1) {
            return -2;
        } else {
            return position;
        }
    }

    public void setrefreshInfo(Info result) {
        info = result;
    }


    public class ProjectInfoViewHolder extends RecyclerView.ViewHolder {
        //   info views
        @BindView(R.id.infoCardView)
        CardView infoCardView;
        @BindView(R.id.projectNameTextView)
        TextView projectNameTextView;
        @BindView(R.id.projectNumberTextView)
        TextView projectNumberTextView;
        @BindView(R.id.addressTextView)
        TextView addressTextView;
        @BindView(R.id.address2TextView)
        TextView address2TextView;
        @BindView(R.id.cityTextView)
        TextView cityTextView;
        @BindView(R.id.stateTextView)
        TextView stateTextView;
        @BindView(R.id.zipTextView)
        TextView zipTextView;
        @BindView(R.id.upsDeliveryTextView)
        TextView upsDeliveryTextView;
        @BindView(R.id.notesTextView)
        TextView notesTextView;
        @BindView(R.id.deliveryAddressTextView)
        TextView deliveryAddressTextView;
        @BindView(R.id.deliveryAddress2TextView)
        TextView deliveryAddress2TextView;
        @BindView(R.id.deliveryCityTextView)
        TextView deliveryCityTextView;
        @BindView(R.id.deliveryStateTextView)
        TextView deliveryStateTextView;
        @BindView(R.id.deliveryZipTextView)
        TextView deliveryZipTextView;
        @BindView(R.id.projectSpecialAddress)
        ConstraintLayout projectSpecialAddress;
        private String mapAddress;

        public ProjectInfoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


        private void bind() {
            if (info != null) {
                PjInfo pjInfo = info.getPjInfo();
                if (pjInfo != null) {
                    projectNameTextView.setText(TextUtils.isEmpty(pjInfo.getName()) ? "-" : pjInfo.getName());
                    projectNumberTextView.setText(TextUtils.isEmpty(pjInfo.getProjectNumber()) ? "-" : pjInfo.getProjectNumber());
                    addressTextView.setText(TextUtils.isEmpty(pjInfo.getAddress()) ? "-" : pjInfo.getAddress());
                    address2TextView.setText(TextUtils.isEmpty(pjInfo.getAddress2()) ? "-" : pjInfo.getAddress2());
                    cityTextView.setText(TextUtils.isEmpty(pjInfo.getCity()) ? "-" : pjInfo.getCity());
                    stateTextView.setText(TextUtils.isEmpty(pjInfo.getState()) ? "-" : pjInfo.getState());
                    zipTextView.setText(TextUtils.isEmpty(pjInfo.getZip()) ? "-" : pjInfo.getZip());
                    upsDeliveryTextView.setText(TextUtils.isEmpty(pjInfo.getUpsDelivery()) ? "Same as Project Address" : pjInfo.getUpsDelivery());
                    notesTextView.setText(TextUtils.isEmpty(pjInfo.getSdiNotes()) ? "-" : pjInfo.getSdiNotes());
                    if (TextUtils.isEmpty(pjInfo.getUpsDelivery()) || pjInfo.getUpsDelivery().equalsIgnoreCase("Same as Project Address")) {
                        projectSpecialAddress.setVisibility(View.GONE);
                    } else {
                        projectSpecialAddress.setVisibility(View.VISIBLE);
                        deliveryAddressTextView.setText(TextUtils.isEmpty(pjInfo.getUpsAddress()) ? "-" : pjInfo.getUpsAddress());
                        deliveryAddress2TextView.setText(TextUtils.isEmpty(pjInfo.getUpsAddress2()) ? "-" : pjInfo.getUpsAddress2());
                        deliveryCityTextView.setText(TextUtils.isEmpty(pjInfo.getUpsCity()) ? "-" : pjInfo.getUpsCity());
                        deliveryStateTextView.setText(TextUtils.isEmpty(pjInfo.getUpsState()) ? "-" : pjInfo.getUpsState());
                        deliveryZipTextView.setText(TextUtils.isEmpty(pjInfo.getUpsZip()) ? "-" : pjInfo.getUpsZip());
                    }
                } else {
                    projectNameTextView.setText("-");
                    projectNumberTextView.setText("-");
                    addressTextView.setText("-");
                    address2TextView.setText("-");
                    cityTextView.setText("-");
                    stateTextView.setText("-");
                    zipTextView.setText("-");
                    upsDeliveryTextView.setText("-");
                    notesTextView.setText("-");
                    projectSpecialAddress.setVisibility(View.GONE);
                }

            } else {
                infoCardView.setVisibility(View.GONE);
            }
            if (!addressTextView.getText().toString().startsWith("-")) {
                addressTextView.setPaintFlags(addressTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } else {
                addressTextView.setTextColor(mActivity.getResources().getColor(R.color.gray_535a73));
            }
        }

        @OnClick(R.id.addressTextView)
        public void onAddressClick() {
            if (!addressTextView.getText().toString().startsWith("-")) {
                mapAddress = addressTextView.getText().toString();
                if (!address2TextView.getText().toString().startsWith("-")) {
                    mapAddress = mapAddress + "," + address2TextView.getText().toString();
                }
                if (!cityTextView.getText().toString().startsWith("-")) {
                    mapAddress = mapAddress + "," + cityTextView.getText().toString();
                }
                if (!stateTextView.getText().toString().startsWith("-")) {
                    mapAddress = mapAddress + "," + stateTextView.getText().toString();
                }
                if (!zipTextView.getText().toString().startsWith("-")) {
                    mapAddress = mapAddress + "," + zipTextView.getText().toString();
                }

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q=" + mapAddress));
                mActivity.startActivity(intent);
            }
        }
    }

    public class ProjectScheduleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.scheduleCardView)
        CardView scheduleCardView;
        @BindView(R.id.startDateTextView)
        TextView startDateTextView;
        @BindView(R.id.finishDateTextView)
        TextView finishDateTextView;
        @BindView(R.id.totalDurationTextView)
        TextView totalDurationTextView;


        public ProjectScheduleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


        private void bind() {
            if (info != null) {
                PjSchedule pjSchedule = info.getPjSchedule();

                if (pjSchedule != null) {
                    startDateTextView.setText(TextUtils.isEmpty(pjSchedule.getStartDate()) ? "-" : pjSchedule.getStartDate());
                    finishDateTextView.setText(TextUtils.isEmpty(pjSchedule.getEndDate()) ? "-" : pjSchedule.getEndDate());
                    totalDurationTextView.setText(TextUtils.isEmpty(String.valueOf(pjSchedule.getTotalDuration())) ? "-" : String.valueOf(pjSchedule.getTotalDuration()));
                } else {
                    startDateTextView.setText("-");
                    finishDateTextView.setText("-");
                    totalDurationTextView.setText("-");

                }

            }
        }
    }

    public class ProjectSiteViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.siteCardView)
        CardView siteCardView;
        @BindView(R.id.cameraLinkTextView)
        TextView cameraLinkTextView;
        @BindView(R.id.credentialTextView)
        TextView credentialTextView;
        @BindView(R.id.parkingLocationTextView)
        TextView parkingLocationTextView;
        @BindView(R.id.projectStartDateTextView)
        TextView projectStartDateTextView;
        @BindView(R.id.requiredOrientationTextView)
        TextView requiredOrientationTextView;
        @BindView(R.id.requiredOrientationNotesTextView)
        TextView requiredOrientationNotesTextView;
        @BindView(R.id.requiredPostTextView)
        TextView requiredPostTextView;
        @BindView(R.id.siteReuirementsTextView)
        TextView siteReuirementsTextView;
        @BindView(R.id.siteReuirementsNotesTextView)
        TextView siteReuirementsNotesTextView;
        @BindView(R.id.hiringReuirementsTextView)
        TextView hiringReuirementsTextView;
        @BindView(R.id.hiringReuirementsNotesTextView)
        TextView hiringReuirementsNotesTextView;

        public ProjectSiteViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            if (info != null) {
                PjSite pjSite = info.getPjSite();

                if (pjSite != null) {
                    cameraLinkTextView.setText(TextUtils.isEmpty(pjSite.getCameraLink()) ? "-" : pjSite.getCameraLink());
                    credentialTextView.setText(TextUtils.isEmpty(pjSite.getCameraCredentials()) ? "-" : pjSite.getCameraCredentials());
                    parkingLocationTextView.setText(TextUtils.isEmpty(pjSite.getParkingLocation()) ? "-" : pjSite.getParkingLocation());
                    projectStartDateTextView.setText(TextUtils.isEmpty(pjSite.getProjectStartTime()) ? "-" : pjSite.getProjectStartTime());
                    String orientationNotes = TextUtils.isEmpty(pjSite.getGcOrientationNotes()) ? "" : "\n" + pjSite.getGcOrientationNotes();

                    requiredOrientationTextView.setText(TextUtils.isEmpty(pjSite.getGcOrientation()) ? "-" : pjSite.getGcOrientation() + orientationNotes);
                    requiredOrientationNotesTextView.setVisibility(View.GONE);
                    requiredPostTextView.setText(TextUtils.isEmpty(pjSite.getDrugTest()) ? "-" : pjSite.getDrugTest());
                    String siteNotes = TextUtils.isEmpty(pjSite.getSiteNotes()) ? "" : "\n" + pjSite.getSiteNotes();
                    siteReuirementsTextView.setText(TextUtils.isEmpty(pjSite.getSpecialSite()) ? "-" : pjSite.getSpecialSite() + siteNotes);
                    siteReuirementsNotesTextView.setVisibility(View.GONE);

                    String hiringNotes = TextUtils.isEmpty(pjSite.getHiringNotes()) ? "" : "\n" + pjSite.getHiringNotes();
                    hiringReuirementsTextView.setText(TextUtils.isEmpty(pjSite.getSpecialHiring()) ? "-" : pjSite.getSpecialHiring() + hiringNotes);
                    hiringReuirementsNotesTextView.setVisibility(View.GONE);
                } else {
                    cameraLinkTextView.setText("-");
                    credentialTextView.setText("-");
                    parkingLocationTextView.setText("-");
                    projectStartDateTextView.setText("-");
                    requiredOrientationTextView.setText("-");
                    requiredPostTextView.setText("-");
                    siteReuirementsTextView.setText("-");
                    hiringReuirementsTextView.setText("-");
                }
            }
        }
    }
}
