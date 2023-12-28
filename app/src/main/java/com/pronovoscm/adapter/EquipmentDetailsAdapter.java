package com.pronovoscm.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.EquipmentRegion;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;

import java.text.DecimalFormat;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EquipmentDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;
    private int projectId;
    private int userId;
    private List<EquipmentRegion> equipmentCategoriesDetails;
    private Activity mActivity;

    public EquipmentDetailsAdapter(Activity mActivity, List<EquipmentRegion> equipmentCategoriesDetails, int projectId, int users_id) {
        this.equipmentCategoriesDetails = equipmentCategoriesDetails;
        this.mActivity = mActivity;
        ((PronovosApplication) mActivity.getApplication()).getDaggerComponent().inject(this);
        this.projectId = projectId;
        this.userId = users_id;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.equipment_detail_list_item, parent, false);
        return new InventoryCategoryHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((InventoryCategoryHolder) holder).bind();
    }

    @Override
    public int getItemCount() {
        if (equipmentCategoriesDetails != null) {
            return equipmentCategoriesDetails.size();
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
        @BindView(R.id.totalValueTextView)
        TextView totalValueTextView;
        @BindView(R.id.activeOwnedTextView)
        TextView activeOwnedTextView;
        @BindView(R.id.inActiveOwnedTextView)
        TextView inActiveOwnedTextView;
        @BindView(R.id.inActiveRentedTextView)
        TextView inActiveRentedTextView;
        @BindView(R.id.activeRentedTextView)
        TextView activeRentedTextView;

        public InventoryCategoryHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            equipmentNameTextView.setText(equipmentCategoriesDetails.get(getAdapterPosition()).getName());

            int ownActive = mEquipementInventoryRepository.getOwnedActive(equipmentCategoriesDetails.get(getAdapterPosition()), userId, projectId);
            int ownDamage = mEquipementInventoryRepository.getOwnedDamaged(equipmentCategoriesDetails.get(getAdapterPosition()), userId, projectId);
            int rentActive = mEquipementInventoryRepository.getRentedActive(equipmentCategoriesDetails.get(getAdapterPosition()), userId, projectId);
            int rentDamage = mEquipementInventoryRepository.getRentedDamaged(equipmentCategoriesDetails.get(getAdapterPosition()), userId, projectId);
            DecimalFormat formatter = new DecimalFormat("##,###,###");

            activeOwnedTextView.setText(ownActive != 0 ? formatter.format(ownActive) : "-");
            inActiveOwnedTextView.setText(ownDamage != 0 ? formatter.format(ownDamage) : "-");
            activeRentedTextView.setText(rentActive != 0 ? formatter.format(rentActive) : "-");
            inActiveRentedTextView.setText(rentDamage != 0 ? formatter.format(rentDamage) : "-");
            totalValueTextView.setText(formatter.format(ownActive + ownDamage + rentActive + rentDamage));
        }
    }
}
