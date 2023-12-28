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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.CreateTransfersActivity;
import com.pronovoscm.model.EquipmentStatusEnum;
import com.pronovoscm.model.request.transferrequest.Equipment;
import com.pronovoscm.model.request.transferrequest.TransferRequest;
import com.pronovoscm.persistence.domain.EquipmentInventory;
import com.pronovoscm.persistence.domain.EquipmentRegion;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.services.NetworkService;

import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class TransferEquipmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final RecyclerView crewRecyclerView;
    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;
    private List<Equipment> transferDataList;
    private Activity mActivity;
    private PopupWindow mPopupWindow;
    private TransferRequest createTransfer;

    public TransferEquipmentsAdapter(Activity mActivity, List<Equipment> transferDataList, RecyclerView equipmentRV, TransferRequest createTransfer) {
        this.transferDataList = transferDataList;
        this.mActivity = mActivity;
        ((PronovosApplication) mActivity.getApplication()).getDaggerComponent().inject(this);
        this.createTransfer = createTransfer;
        crewRecyclerView = equipmentRV;
    }

    public void hidePopup() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.transfer_equipment_list_item, parent, false);
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
        @BindView(R.id.equipmentNameTextView)
        TextView equipmentNameTextView;
        @BindView(R.id.statusTV)
        TextView activeDamagestatusTV;
        @BindView(R.id.statusViewTV)
        TextView ownedRentedstatusTV;
        @BindView(R.id.textViewOptions)
        TextView optionsTextView;
        @BindView(R.id.quantityTV)
        TextView quantityTV;
        @BindView(R.id.unitsTV)
        TextView unitsTV;
        @BindView(R.id.weightTV)
        TextView weightTV;
        @BindView(R.id.totalWeightTV)
        TextView totalWeightTV;
        @BindView(R.id.trackingNumberTV)
        TextView trackingNumberTV;
        @BindView(R.id.trackingNumberTextView)
        TextView trackingNumberTextView;

        @BindView(R.id.equipmentDetailCardView)
        CardView equipmentDetailCardView;

        @BindView(R.id.errorView)
        ImageView errorView;

        public InventoryCategoryHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            Equipment equipment = transferDataList.get(getAdapterPosition());
            if (equipment.getEquipmentId() == 0) {
                equipmentDetailCardView.setCardBackgroundColor(ContextCompat.getColor(equipmentDetailCardView.getContext(), R.color.yellow_eaf2b4));
            } else {

                equipmentDetailCardView.setCardBackgroundColor(ContextCompat.getColor(equipmentDetailCardView.getContext(), R.color.white));
//                equipmentDetailCardView.setCardBackgroundColor(equipmentDetailCardView.getContext().getColor(R.color.white));
            }
            equipmentNameTextView.setText(equipment.getName());
            quantityTV.setText(String.valueOf(equipment.getQuantity()));
            unitsTV.setText(equipment.getUnit() == 0.0 ? "-" : String.valueOf(equipment.getUnit()));
//            weightTV.setText(equipment.getWeight());
            Float f = Float.parseFloat(TextUtils.isEmpty(equipment.getWeight()) ? "0" : equipment.getWeight());
            String s1 = String.format("%.2f", f);
            weightTV.setText(s1);

            totalWeightTV.setText(equipment.getTotalWeight());
            if (!TextUtils.isEmpty(equipment.getTrackingNumber())) {
                trackingNumberTextView.setVisibility(View.VISIBLE);
                trackingNumberTV.setVisibility(View.VISIBLE);
                trackingNumberTV.setText(String.valueOf(equipment.getTrackingNumber()));
            } else {
                trackingNumberTextView.setVisibility(View.GONE);
                trackingNumberTV.setVisibility(View.GONE);
            }
//            totalWeightTV.setText(equipment.getStatus());
            ownedRentedstatusTV.setText(equipment.getEquipmentStatus() == 1 ? "Owned" : "Rented");
            activeDamagestatusTV.setText(equipment.getStatus() == 1 ? EquipmentStatusEnum.ACTIVE.toString() : EquipmentStatusEnum.INACTIVE.toString());
            if (equipment.getStatus() == 1) {
                activeDamagestatusTV.setTextColor(ContextCompat.getColor(activeDamagestatusTV.getContext(), R.color.green_00aa4f));
            } else {
                activeDamagestatusTV.setTextColor(ContextCompat.getColor(activeDamagestatusTV.getContext(), R.color.red_d0021b));
            }
            errorView.setVisibility(View.INVISIBLE);
            if ((createTransfer.getStatus() == 2 && (createTransfer.getPickupVendorStatus() != 1 && createTransfer.getPickupLocation() == mActivity.getIntent().getIntExtra("project_id", 0)))) {
                EquipmentRegion equipmentCategoriesDetail = null;
                if (equipment.getEquipmentId() != 0) {
                    equipmentCategoriesDetail = mEquipementInventoryRepository.getEquipmentRegion(equipment.getEquipmentId());
                }
                List<EquipmentInventory> equipmentCategories = mEquipementInventoryRepository.checkgetEquipmentInventory(equipment.getEquipmentId(), 0, createTransfer.getPickupLocation());
                if (equipmentCategoriesDetail != null && equipmentCategoriesDetail.getType().equals("Unique") && ((createTransfer.getPickupVendorStatus() == 1 || (equipmentCategories == null || equipmentCategories.size() == 0)) || TextUtils.isEmpty(equipment.getTrackingNumber()))) {
                    errorView.setVisibility(View.VISIBLE);
                }
            }
            optionsTextView.setVisibility(View.INVISIBLE);
            optionsTextView.setClickable(false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (createTransfer.getStatus() >= 1 && (createTransfer.getStatus() != 2 || (createTransfer.getDropoffVendorStatus() != 1 && createTransfer.getPickupLocation() != mActivity.getIntent().getIntExtra("project_id", 0)))) {
                        return;
                    }
                    ((CreateTransfersActivity) optionsTextView.getContext()).addEquipment(getAdapterPosition());

                }
            });
            optionsTextView.setOnClickListener(v -> {
                if (NetworkService.isNetworkAvailable(mActivity)) {
                    crewRecyclerView.scrollToPosition(getAdapterPosition());

                    LayoutInflater inflater = (LayoutInflater) optionsTextView.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View customView = inflater.inflate(R.layout.custom_popup_menu, null);

                    mPopupWindow = new PopupWindow(
                            customView,
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );

                    TextView editMenuOption = customView.findViewById(R.id.editTextView);
                    TextView deleteMenuOption = customView.findViewById(R.id.deleteTextView);
                    editMenuOption.setOnClickListener(v1 -> {
                        ((CreateTransfersActivity) optionsTextView.getContext()).addEquipment(getAdapterPosition());
                        mPopupWindow.dismiss();

                    });

                    deleteMenuOption.setOnClickListener(v12 -> {
                        AlertDialog alertDialog = new AlertDialog.Builder(optionsTextView.getContext()).create();
                        alertDialog.setMessage(optionsTextView.getContext().getString(R.string.are_you_sure_you_want_to_delete));
                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, optionsTextView.getContext().getString(R.string.cancel), (dialog, which) -> {
                            dialog.dismiss();
                            mPopupWindow.dismiss();
                        });
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, optionsTextView.getContext().getString(R.string.ok), (dialog, which) -> {
                            alertDialog.dismiss();
                            ((CreateTransfersActivity) optionsTextView.getContext()).deleteEquipment(getAdapterPosition());
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

                } else {
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
