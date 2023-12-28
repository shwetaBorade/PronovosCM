package com.pronovoscm.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pronovoscm.R;

import com.pronovoscm.persistence.domain.WeatherWidget;
import com.pronovoscm.utils.ui.LoadImage;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 29/11/18.
 *
 * @author GWL
 */
public class WeatherWidgetAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = WeatherWidgetAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<WeatherWidget> list;
    private OnItemClickListener onItemClickListener;
    private LoadImage mLoadImage;

    public WeatherWidgetAdapter(Context context, ArrayList<WeatherWidget> list,
                                OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
        mLoadImage = new LoadImage(context);
    }


    public  class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewTime)
        TextView textViewTime;
        @BindView(R.id.textViewSummary)
        TextView textViewSummary;
        @BindView(R.id.textViewTemperature)
        TextView textViewTemperature;
        @BindView(R.id.imageViewWeather)
        ImageView imageViewIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void bind(final WeatherWidget weatherWidget,
                         final OnItemClickListener listener) {

            textViewTime.setText(weatherWidget.getTime());
            textViewSummary.setText(weatherWidget.getSummary());
            textViewTemperature.setText(context.getResources().getString(R.string.temp, weatherWidget.getTemperature()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(getLayoutPosition());
                    }
                }
            });

            new android.os.Handler().postDelayed(() -> mLoadImage.LoadImagePath(weatherWidget.getIcon(),imageViewIcon), 0);
//            imageViewIcon.setImageResource(getWeatherDrawable(weatherWidget.getIcon()));
        }

//        private int getWeatherDrawable(String icon) {
//            switch (icon) {
//                case "clear-day":
//                    return R.drawable.sun;
//                case "clear-night":
//                    return R.drawable.moon;
//                case "rain":
//                    return R.drawable.rain;
//                case "snow":
//                    return R.drawable.snow;
//                case "sleet":
//                    return R.drawable.sleet;
//                case "wind":
//                    return R.drawable.wind;
//                case "fog":
//                    return R.drawable.fog;
//                case "cloudy":
//                    return R.drawable.overcast;
//                case "partly-cloudy-day":
//                    return R.drawable.partly_cloud_morning;
//                case "partly-cloudy-night":
//                    return R.drawable.partly_cloud_neight;
//                default:
//                    return R.drawable.sun;
//            }
//        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.adapter_dark_sky_item, parent, false);
        ButterKnife.bind(this, view);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WeatherWidget item = list.get(position);

        if (holder instanceof ViewHolder) {
            //Todo: Setup viewholder for item 
            ((ViewHolder) holder).bind(item, onItemClickListener);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}