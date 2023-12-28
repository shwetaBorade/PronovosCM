package com.pronovoscm.adapter;

import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.model.EquipmentStatusEnum;
import com.pronovoscm.model.response.transferlogdetails.Equipments;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransferLogEquipmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Equipments> transferDataList;

    public TransferLogEquipmentsAdapter(List<Equipments> transferDataList) {
        this.transferDataList = transferDataList;
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
        @BindView(R.id.equipmentDetailCardView)
        CardView equipmentDetailCardView;

        @BindView(R.id.errorView)
        ImageView errorView;

        public InventoryCategoryHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            Equipments equipment = transferDataList.get(getAdapterPosition());
            if (equipment.getEquipmentId() == 0) {
                equipmentDetailCardView.setCardBackgroundColor(ContextCompat.getColor(equipmentDetailCardView.getContext(), R.color.yellow_eaf2b4));
            } else {

                equipmentDetailCardView.setCardBackgroundColor(ContextCompat.getColor(equipmentDetailCardView.getContext(), R.color.white));
//                equipmentDetailCardView.setCardBackgroundColor(equipmentDetailCardView.getContext().getColor(R.color.white));
            }
            equipmentNameTextView.setText(equipment.getName());
            quantityTV.setText(String.valueOf(equipment.getQuantity()));
//            unitsTV.setText(String.valueOf(equipment.getUnits()));
            unitsTV.setText(equipment.getUnits().equals("0.0") ? "-" : String.valueOf(equipment.getUnits()));
            Float f = Float.parseFloat(TextUtils.isEmpty(equipment.getWeight()) ? "0" : equipment.getWeight());
            String s1 = String.format("%.2f", f);
            weightTV.setText(s1);
            totalWeightTV.setText(String.valueOf(equipment.getTotalWeight()));
            ownedRentedstatusTV.setText(equipment.getEquipmentStatus() == 1 ? "Owned" : "Rented");
            activeDamagestatusTV.setText(equipment.getStatus() == 1 ? EquipmentStatusEnum.ACTIVE.toString() : EquipmentStatusEnum.INACTIVE.toString());
            if (equipment.getStatus() == 1) {
                activeDamagestatusTV.setTextColor(ContextCompat.getColor(activeDamagestatusTV.getContext(), R.color.green_00aa4f));
            } else {
                activeDamagestatusTV.setTextColor(ContextCompat.getColor(activeDamagestatusTV.getContext(), R.color.red_d0021b));
            }
            errorView.setVisibility(View.INVISIBLE);
            optionsTextView.setVisibility(View.INVISIBLE);
        }
    }
}
