package com.pronovoscm.adapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pronovoscm.R;
import com.pronovoscm.model.ToolModel;
import com.pronovoscm.utils.photoeditor.ToolType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 3/10/18.
 *
 * @author Sanjay Kushwah
 */
public class EditingToolsAdapter extends RecyclerView.Adapter<EditingToolsAdapter.ViewHolder> {

    private List<ToolModel> mToolList = new ArrayList<>();
    private OnItemSelected mOnItemSelected;
    private ToolType selectedToolType;
    private int mColorCode;

    public EditingToolsAdapter(OnItemSelected onItemSelected, int black, List<ToolModel> toolList, ToolType selectedToolType) {
        mOnItemSelected = onItemSelected;
        mToolList = toolList;

        this.selectedToolType = selectedToolType;
        mColorCode = black;
    }

    public void setColorCode(int colorCode) {
        mColorCode = colorCode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_editing_tools, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        if (mToolList.get(position).getmToolType() != ToolType.COLOR_CHOOSER) {
            holder.imgToolIcon.setImageResource(item.getmToolIcon());

            holder.imgToolIcon.setVisibility(View.VISIBLE);
            holder.colorPickerIcon.setVisibility(View.GONE);
        } else {
//            holder.imgToolIcon.setImageDrawable(null);
            holder.imgToolIcon.setVisibility(View.GONE);
            holder.colorPickerIcon.setVisibility(View.VISIBLE);
//            ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
            holder.colorPickerIcon.setImageResource(R.drawable.white_black_rounded_view);
            holder.colorPickerIcon.setColorFilter(mColorCode);
//            holder.colorPickerIcon.setImageResource(R.drawable.white_black_rounded_view);
//            DrawableCompat.setTint(ContextCompat.getDrawable(holder.colorPickerIcon.getContext(), R.drawable.white_black_rounded_view), mColorCode);
//            holder.toolbg.setBackground(ContextCompat.getDrawable(holder.toolbg.getContext(), R.drawable.white_black_rounded_view));

//            holder.toolbg.setColorFilter(ContextCompat.getColor(context, mColorCode), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        if (selectedToolType != null && selectedToolType == item.getmToolType()) {
            holder.toolbg.setBackground(ContextCompat.getDrawable(holder.toolbg.getContext(), R.drawable.selected_rounded_tools_bg));
        } else {
            holder.toolbg.setBackground(ContextCompat.getDrawable(holder.toolbg.getContext(), R.drawable.rounded_tools_bg));
        }

    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }

    public interface OnItemSelected {
        void onToolSelected(ToolType toolType);
    }

//    public class ToolModel {
//        private String mToolName;
//        private int mToolIcon;
//        private ToolType mToolType;
//
//        ToolModel(String toolName, int toolIcon, ToolType toolType) {
//            mToolName = toolName;
//            mToolIcon = toolIcon;
//            mToolType = toolType;
//        }
//
//    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgToolIcon;
        ImageView colorPickerIcon;
        RelativeLayout toolbg;

        ViewHolder(View itemView) {
            super(itemView);
            imgToolIcon = itemView.findViewById(R.id.imgToolIcon);
            colorPickerIcon = itemView.findViewById(R.id.colorPickerIcon);
            toolbg = itemView.findViewById(R.id.toolbg);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).getmToolType());
                    if (mToolList.get(getLayoutPosition()).getmToolType() != ToolType.COLOR_CHOOSER) {
                        selectedToolType = mToolList.get(getLayoutPosition()).getmToolType();
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
