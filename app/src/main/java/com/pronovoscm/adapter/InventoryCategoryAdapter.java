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
import com.pronovoscm.activity.InventorySubcategoryActivity;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMaster;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int projectId;
    private int userId;
    private List<EquipmentCategoriesMaster> categoriesList;
    private Activity mActivity;
    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;


    public InventoryCategoryAdapter(Activity mActivity, List<EquipmentCategoriesMaster> categoriesList, int users_id, int projectId) {
        ((PronovosApplication)mActivity.getApplication()).getDaggerComponent().inject(this);
        this.categoriesList = categoriesList;
        this.mActivity = mActivity;
        this.projectId = projectId;
        this.userId = users_id;
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
//            getCategoriesCount
            categoryCountTextView.setText(String.valueOf(mEquipementInventoryRepository.getCategoriesCount(categoriesList.get(getAdapterPosition()).getEq_categories_id(),userId,projectId)));
//            transferOverviewCountTextView.setText("");
            categoryCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.startActivity(new Intent(mActivity, InventorySubcategoryActivity.class).putExtra("category_id",categoriesList.get(getAdapterPosition()).getId()).putExtra("project_id",projectId));
                }
            });
        }
    }
}
