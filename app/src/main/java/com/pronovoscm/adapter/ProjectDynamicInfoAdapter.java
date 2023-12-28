package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.model.response.projectinfo.ProjectOverviewInfoData;
import com.pronovoscm.model.response.projectinfo.Section;
import com.pronovoscm.model.response.projectinfo.SectionData;
import com.pronovoscm.model.response.projectinfo.SectionTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

public class ProjectDynamicInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Section> sections;
    private List<Integer> mInfoType;
    private Activity mActivity;
    private RecyclerView infoRV;
    private ProjectOverviewInfoData info;
    private HashMap<String, Boolean> viewMap = new HashMap<>();

    public ProjectDynamicInfoAdapter(Activity mActivity, ArrayList<Integer> mInfoType, ProjectOverviewInfoData info, RecyclerView infoRV) {
        this.mInfoType = mInfoType;
        this.mActivity = mActivity;
        this.info = info;
        this.infoRV = infoRV;
        setHasStableIds(true);
        if (info != null)
            sections = info.getSections();


    }

    public void setrefreshInfo(ProjectOverviewInfoData result) {
        info = result;
        if (info != null) {
            sections = info.getSections();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // if (viewType == -1) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.project_info_dynamic_container, parent, false);

        return new ProjectInfoDynamicViewHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProjectInfoDynamicViewHolder) {

            Log.d("onBindView", position + " = position  onBindViewHolder: " + sections.get(position).getName() + "  inside  map " + viewMap.get(sections.get(position).getName()));
            if (viewMap.containsKey(sections.get(position).getName()) && viewMap.get(sections.get(position).getName()) == true) {
                //   Log.e("bind", "**********onBindViewHolder: " + sections.get(position).getName()+"    "+viewMap.get(sections.get(position).getName()));
            } else {
                String mapItem = sections.get(position).getName();
                viewMap.put(mapItem, true);
                ((ProjectInfoDynamicViewHolder) holder).bind(sections.get(position));

            }
            //  holder.setIsRecyclable(false);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        ((ProjectInfoDynamicViewHolder) holder).onViewRecycled();

    }


    @Override
    public int getItemCount() {
        if (mInfoType != null) {
            return mInfoType.size();
        } else {
            return 0;
        }
    }


    private CardView createCardView() {
        CardView cardview;
        cardview = new CardView(mActivity);
        LinearLayout.LayoutParams layoutparams;
        layoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutparams.leftMargin = (int) mActivity.getResources().getDimension(R.dimen._12sdp);
        layoutparams.rightMargin = (int) mActivity.getResources().getDimension(R.dimen._12sdp);
        layoutparams.topMargin = (int) mActivity.getResources().getDimension(R.dimen._10sdp);
        layoutparams.bottomMargin = (int) mActivity.getResources().getDimension(R.dimen._10sdp);

        cardview.setMaxCardElevation(mActivity.getResources().getDimension(R.dimen._12sdp));
        cardview.setLayoutParams(layoutparams);
        cardview.setCardElevation(mActivity.getResources().getDimension(R.dimen._7sdp));
        cardview.setRadius(mActivity.getResources().getDimension(R.dimen._7sdp));
        cardview.setCardBackgroundColor(Color.WHITE);

        cardview.setPadding(10, 20, 10, 10);

        cardview.setVisibility(View.VISIBLE);
        return cardview;
    }

    public void clearViewMap() {

        for (String s : viewMap.keySet()) {
            viewMap.put(s, false);
        }
    }

    public class ProjectInfoDynamicViewHolder extends RecyclerView.ViewHolder {
        String sectionName;
        LinearLayout itemView;
        LinearLayout infocard;
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        public ProjectInfoDynamicViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = (LinearLayout) itemView;
            this.infocard = itemView.findViewById(R.id.dynamicrowsContainerll);
        }

        private void setAddressClickListner(View v, String address, String address2TextValue, String cityTextValue,
                                            String stateTextValue, String zipTextValue) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mapAddress;
                    if (!address.startsWith("-")) {
                        mapAddress = address;
                        if (address2TextValue!=null && !address2TextValue.startsWith("-")) {
                            mapAddress = mapAddress + "," + address2TextValue;
                        }
                        if (cityTextValue!=null && !cityTextValue.startsWith("-")) {
                            mapAddress = mapAddress + "," + cityTextValue;
                        }
                        if (stateTextValue!=null && !stateTextValue.startsWith("-")) {
                            mapAddress = mapAddress + "," + stateTextValue;
                        }
                        if (zipTextValue!=null && !zipTextValue.startsWith("-")) {
                            mapAddress = mapAddress + "," + zipTextValue.toString();
                        }

                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("geo:0,0?q=" + mapAddress));
                        mActivity.startActivity(intent);
                    }
                }
            });


        }

        private void bind(Section section) {
            sectionName = section.getName();
            //  Log.d("Siddesh", "bind: "+getAdapterPosition());
            section = sections.get(getAdapterPosition());
            String address = "", address2TextValue = "", cityTextValue = "";
            String stateTextValue = "", zipTextValue = "";
            TextView addressTextView = null;
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            LinearLayout containerll = (LinearLayout) itemView.findViewById(R.id.dynamicrowsContainerll);
            /* Create the Table’s Row */
            CardView overviewCard = createCardView();
            LinearLayout cardLinerLayout = new LinearLayout(mActivity);
            LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardLinerLayout.setLayoutParams(layoutparams);
            cardLinerLayout.setOrientation(LinearLayout.VERTICAL);
            cardLinerLayout.setPadding(10, 10, 10, 10);
            LinearLayout rowView = null;
            for (SectionData data : section.getData()) {
                rowView = (LinearLayout) inflater.inflate(R.layout.project_info_dynamic_row, null);
                TextView lable = ((TextView) rowView.findViewById(R.id.dynamicInfoLableTV));
                TextView valueTV = ((TextView) rowView.findViewById(R.id.dynamicInfoValue));
                lable.setText(data.getName());
                valueTV.setText(TextUtils.isEmpty(data.getValue()) ? "-" : data.getValue());
                rowView.setWeightSum(2);

                if (!TextUtils.isEmpty(data.getName()) && data.getName().equalsIgnoreCase("Address")) {
                    address = data.getValue();
                    addressTextView = valueTV;
                } else if (!TextUtils.isEmpty(data.getName()) && data.getName().equalsIgnoreCase("Address 2")) {
                    address2TextValue = data.getValue();
                } else if (!TextUtils.isEmpty(data.getName()) && data.getName().equalsIgnoreCase("City")) {
                    cityTextValue = data.getValue();
                } else if (!TextUtils.isEmpty(data.getName()) && data.getName().equalsIgnoreCase("State")) {
                    stateTextValue = data.getValue();
                } else if (!TextUtils.isEmpty(data.getName()) && data.getName().equalsIgnoreCase("Zip")) {
                    zipTextValue = data.getValue();
                }
                if (data.getType() == SectionTypeEnum.PHONE.getValue()) {
                    Linkify.addLinks(valueTV, Linkify.PHONE_NUMBERS);
                    valueTV.setLinksClickable(true);
                    valueTV.setTextColor(mActivity.getResources().getColor(R.color.color_accent));
                } else if (data.getType() == SectionTypeEnum.ADDRESS.getValue()) {
                    Linkify.addLinks(valueTV, Linkify.MAP_ADDRESSES);
                    valueTV.setLinksClickable(true);
                } else if (data.getType() == SectionTypeEnum.CAMERA_LINK.getValue()) {
                    Linkify.addLinks(valueTV, Linkify.WEB_URLS);
                    valueTV.setLinksClickable(true);
                }
                cardLinerLayout.addView(rowView);

            }
            if (addressTextView != null) {
                addressTextView.setTextColor(mActivity.getResources().getColor(R.color.color_accent));
                setAddressClickListner(addressTextView, address, address2TextValue, cityTextValue, stateTextValue, zipTextValue);
            }
            overviewCard.addView(cardLinerLayout);
            containerll.addView(overviewCard);
        }

        public void onViewRecycled() {
            Log.d("ADAPTER", "onViewRecycled: sectionName " + sectionName);
            viewMap.put(sectionName, false);
        }
    }

}