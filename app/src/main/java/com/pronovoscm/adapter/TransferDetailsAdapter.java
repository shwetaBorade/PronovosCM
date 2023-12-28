package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.fragments.TransferOverviewDetailFragment;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.transferoverview.TransferData;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class TransferDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LoginResponse loginResponse = null;
    private List<TransferData> transferDataList;
    private Activity mActivity;
    private PopupWindow mPopupWindow;
    private RecyclerView equipmentsRecyclerView;
    private TransferOverviewDetailFragment transferOverviewDetailFragment;
    private String title;
    private int projectID;
    private int canEditTransfer;

    public TransferDetailsAdapter(Activity mActivity, List<TransferData> transferDataList,
                                  RecyclerView equipmentsRecyclerView,
                                  TransferOverviewDetailFragment transferOverviewDetailFragment, String title, int projectID) {
        this.transferDataList = transferDataList;
        this.mActivity = mActivity;
        this.title = title;
        this.projectID = projectID;
        this.equipmentsRecyclerView = equipmentsRecyclerView;
        this.transferOverviewDetailFragment = transferOverviewDetailFragment;
        if (mActivity != null) {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(mActivity).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            canEditTransfer = loginResponse.getUserDetails().getPermissions().get(0).getEditProjectTransfers();
        }
    }

    public void hidePopUp() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.transfer_detail_list_item, parent, false);
        return new InventoryCategoryHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((InventoryCategoryHolder) holder).bind();
    }

    @Override
    public int getItemCount() {
        if (transferDataList != null) {
            return transferDataList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;

    }

    public class InventoryCategoryHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.requestTextView)
        TextView requestTextView;
        @BindView(R.id.pickupDateTextView)
        TextView pickupDateTextView;
        @BindView(R.id.pickupTimeTextView)
        TextView pickupTimeTextView;
        @BindView(R.id.dropOffDateTextView)
        TextView dropOffDateTextView;
        @BindView(R.id.dropOffTimeTextView)
        TextView dropOffTimeTextView;
        @BindView(R.id.pickupLocationTextView)
        TextView pickupLocationTextView;
        @BindView(R.id.dropOffLocationTextView)
        TextView dropOffLocationTextView;
        @BindView(R.id.equipmentDetailCardView)
        CardView equipmentDetailCardView;

        @BindView(R.id.textViewOptions)
        TextView optionsTextView;

        public InventoryCategoryHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            TransferData transferData = transferDataList.get(getAdapterPosition());
            requestTextView.setText((transferData.getStatus() > 1 ? "Transfer# : " : "Request# : ") + String.valueOf(transferData.getEqTransferRequestsId()));
            if (transferData.getTransferType() == 1) {
                requestTextView.setText("Transfer# : " + String.valueOf(transferData.getEqTransferRequestsId()));
            } else if (transferData.getTransferType() == 2) {
                requestTextView.setText("Request# : " + String.valueOf(transferData.getEqTransferRequestsId()));
            }
            pickupDateTextView.setText(transferData.getPickupDate());
            pickupTimeTextView.setText(transferData.getPickupTime());
            if (transferData.getStatus() < 3) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Date strDate = null;
                try {
                    if (!TextUtils.isEmpty(transferData.getPickupDate()) && !transferData.getPickupDate().equalsIgnoreCase("TBD")) {
                        strDate = sdf.parse(transferData.getPickupDate());
                        if (new Date().after(strDate)) {
                            requestTextView.setTextColor(ContextCompat.getColor(requestTextView.getContext(), R.color.red));
                        }
                    } else {
                        requestTextView.setTextColor(ContextCompat.getColor(requestTextView.getContext(), R.color.red));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    requestTextView.setTextColor(ContextCompat.getColor(requestTextView.getContext(), R.color.red));
                }
            }
            pickupLocationTextView.setText(transferData.getPickupLocation() != null ? transferData.getPickupLocation().trim() : "");

            dropOffDateTextView.setText(transferData.getDeliveryDate());
            dropOffTimeTextView.setText(transferData.getDropoffTime());
            dropOffLocationTextView.setText(transferData.getDropoffLocation() != null ? transferData.getDropoffLocation().trim() : "");
            optionsTextView.setVisibility(View.GONE);
            equipmentDetailCardView.setOnClickListener(view -> transferOverviewDetailFragment.editTransfer(getAdapterPosition(), title));
            optionsTextView.setOnClickListener(v -> {
                if (mActivity != null && NetworkService.isNetworkAvailable(mActivity)) {
                    equipmentsRecyclerView.scrollToPosition(getAdapterPosition());

                    LayoutInflater inflater = (LayoutInflater) optionsTextView.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
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
                    if (canEditTransfer != 1 /*|| transferData.getStatus() == 1*/) {
                        deleteMenuOption.setVisibility(View.GONE);
                        dividerMenu.setVisibility(View.GONE);
                        int _100sdp = (int) optionsTextView.getContext().getResources().getDimension(R.dimen._90sdp);
                        int _50sdp = (int) optionsTextView.getContext().getResources().getDimension(R.dimen._58sdp);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_100sdp, _50sdp);
                        popupView.setLayoutParams(layoutParams);
                        editMenuOption.setText("View");
                    }

                    if (transferData.getStatus() == 1 || transferData.getStatus() == 4 || (transferData.getStatus() == 2 && (transferData.getPickupIsVendor() == 1 || transferData.getShippedFrom() != projectID)) || (transferData.getStatus() == 3 && (transferData.getDropoffIsVendor() == 1 || transferData.getShippedTo() != projectID))) {
                        deleteMenuOption.setVisibility(View.GONE);
                        dividerMenu.setVisibility(View.GONE);
                        int _100sdp = (int) optionsTextView.getContext().getResources().getDimension(R.dimen._90sdp);
                        int _50sdp = (int) optionsTextView.getContext().getResources().getDimension(R.dimen._58sdp);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_100sdp, _50sdp);
                        popupView.setLayoutParams(layoutParams);
                        editMenuOption.setText("View");
                    }
                    if ((transferData.getStatus() == 3 && !(transferData.getDropoffIsVendor() == 1 || transferData.getShippedTo() != projectID))) {
                        deleteMenuOption.setVisibility(View.GONE);
                        dividerMenu.setVisibility(View.GONE);
                        int _100sdp = (int) optionsTextView.getContext().getResources().getDimension(R.dimen._90sdp);
                        int _50sdp = (int) optionsTextView.getContext().getResources().getDimension(R.dimen._58sdp);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_100sdp, _50sdp);
                        popupView.setLayoutParams(layoutParams);
                    }
                    editMenuOption.setOnClickListener(v1 -> {
                        transferOverviewDetailFragment.editTransfer(getAdapterPosition(), title);
                        mPopupWindow.dismiss();

                    });

                    deleteMenuOption.setOnClickListener(v12 -> {
                        AlertDialog alertDialog = new AlertDialog.Builder(optionsTextView.getContext()).create();
                        alertDialog.setMessage("Are you sure you want to delete this request?");
                        if (transferData.getStatus() > 1) {
                            alertDialog.setMessage("Are you sure you want to delete this Transfer?");
                        }
                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, optionsTextView.getContext().getString(R.string.cancel), (dialog, which) -> {
                            dialog.dismiss();
                            mPopupWindow.dismiss();
                        });
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, optionsTextView.getContext().getString(R.string.ok), (dialog, which) -> {
                            alertDialog.dismiss();
                            transferOverviewDetailFragment.callTransferDelete(transferData.getEqTransferRequestsId());
                            mPopupWindow.dismiss();
                        });
                        alertDialog.show();
                        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        nbutton.setTextColor(ContextCompat.getColor(optionsTextView.getContext(), R.color.gray_948d8d));
                        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(ContextCompat.getColor(optionsTextView.getContext(), R.color.colorPrimary));

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
                } else if (mActivity != null) {
                    AlertDialog alertDialog = new AlertDialog.Builder(optionsTextView.getContext()).create();
                    alertDialog.setMessage(mActivity.getString(R.string.internet_connection_check_transfer_overview));
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, optionsTextView.getContext().getString(R.string.ok), (dialog, which) -> {
                        alertDialog.dismiss();
                    });
                    alertDialog.show();
                    Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(ContextCompat.getColor(optionsTextView.getContext(), R.color.gray_948d8d));
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(ContextCompat.getColor(optionsTextView.getContext(), R.color.colorPrimary));
                }
            });


        }
    }
}
