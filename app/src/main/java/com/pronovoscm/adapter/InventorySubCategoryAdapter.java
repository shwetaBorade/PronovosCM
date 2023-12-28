package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.EquipmentDetailsActivity;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMaster;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pronovoscm.activity.InventorySubcategoryActivity.INVENTORY_UPDATE;

public class InventorySubCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;
    private List<EquipmentSubCategoriesMaster> categoriesList;
    private Activity mActivity;
    private int projectId;
    private int userId;

    public InventorySubCategoryAdapter(Activity mActivity, List<EquipmentSubCategoriesMaster> categoriesList, int projectId, int users_id) {
        ((PronovosApplication) mActivity.getApplication()).getDaggerComponent().inject(this);
        this.categoriesList = categoriesList;
        this.mActivity = mActivity;
        this.projectId = projectId;
        this.userId= users_id;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.inventory_category_list_item, parent, false);
        return new InventoryCategoryHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((InventoryCategoryHolder) holder).bind();
    }

    @Override
    public int getItemCount() {
        if (categoriesList != null) {
            return categoriesList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;

    }

    public class InventoryCategoryHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.categoryNameTextView)
        TextView categoryNameTextView;
        @BindView(R.id.categoryCountTextView)
        TextView categoryCountTextView;
        @BindView(R.id.categoryCardView)
        CardView categoryCardView;

        public InventoryCategoryHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            categoryNameTextView.setText(categoriesList.get(getAdapterPosition()).getName());
            categoryCountTextView.setText(String.valueOf(mEquipementInventoryRepository.getSubCategoriesCount(categoriesList.get(getAdapterPosition()).getEqSubCategoryId(),userId,projectId)));
            categoryCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.startActivityForResult(new Intent(mActivity, EquipmentDetailsActivity.class)
                            .putExtra("sub_category_id", categoriesList.get(getAdapterPosition()).getEqSubCategoryId())
                            .putExtra("category_id", categoriesList.get(getAdapterPosition()).getEqCategoryId())
                            .putExtra("project_id", projectId),INVENTORY_UPDATE);
                }
            });
        }
    }
}
