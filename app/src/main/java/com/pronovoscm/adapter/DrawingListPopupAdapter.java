package com.pronovoscm.adapter;

import android.app.Activity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.DrawingList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrawingListPopupAdapter extends RecyclerView.Adapter<DrawingListPopupAdapter.ProjectHolder> {
    private List<DrawingList> mRegionsList;
    private Activity mActivity;
    private DrawingList drawingList;

    public DrawingListPopupAdapter(Activity mActivity, List<DrawingList> regionsList, DrawingList drawingList) {
        this.mRegionsList = regionsList;
        this.mActivity = mActivity;
        this.drawingList = drawingList;
    }


    @Override
    public ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.drawing_popup_item_list, parent, false);

        return new ProjectHolder(view);
    }


    @Override
    public void onBindViewHolder(ProjectHolder holder, int position) {
        holder.bind(mRegionsList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mRegionsList != null) {
            return mRegionsList.size();
        } else {
            return 0;
        }
    }


    public interface selectDrawingList{
        void onSelectDrawingList(DrawingList drawingList);
    }

    public class ProjectHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.drawingNameTextView)
        TextView drawingNameTextView;
        @BindView(R.id.drawingView)
        ConstraintLayout drawingView;
        @BindView(R.id.bottom_view)
        View bottom_view;

        public ProjectHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(final DrawingList drawingObj) {
            if (drawingObj.getOriginalDrwId() == drawingList.getOriginalDrwId()) {
                drawingNameTextView.setTextColor(ContextCompat.getColor(drawingView.getContext(), R.color.white));
                drawingView.setBackgroundColor(ContextCompat.getColor(drawingView.getContext(), R.color.colorPrimary));
                bottom_view.setVisibility(View.GONE);
            } else {
                drawingNameTextView.setTextColor(ContextCompat.getColor(drawingView.getContext(), R.color.colorPrimary));
                drawingView.setBackgroundColor(ContextCompat.getColor(drawingView.getContext(), R.color.gray_fafafa));
                bottom_view.setVisibility(View.VISIBLE);
            }
            drawingNameTextView.setText(drawingObj.getDrawingName()+" - "+drawingObj.getDescriptions());
            drawingNameTextView.setOnClickListener(v -> {
                drawingList = drawingObj;
                notifyDataSetChanged();
                ((selectDrawingList) mActivity).onSelectDrawingList(drawingObj);
            });
            if (getAdapterPosition() == (mRegionsList.size() - 1)) {
                bottom_view.setVisibility(View.GONE);
            }
        }
    }

}
