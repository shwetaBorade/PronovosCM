package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.activity.ProjectOptionsActivity;
import com.pronovoscm.persistence.domain.PjProjects;
import com.pronovoscm.utils.CircleOutlineProvider;
import com.pronovoscm.utils.ui.LoadImageInBackground;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectHolder> {
    private final int cornerRadius;
    private List<PjProjects> mProjectsArrayList;
    private Activity mActivity;
    private long mLastClickTime=0;

    public ProjectAdapter(Activity mActivity, List<PjProjects> projectsArrayList) {
        this.mProjectsArrayList = projectsArrayList;
        this.mActivity = mActivity;
        cornerRadius = (int) (mActivity.getResources().getDimension(R.dimen.album_photo_radius) / mActivity.getResources().getDisplayMetrics().density);
    }


    @Override
    public ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.project_item_list, parent, false);

        return new ProjectHolder(view);
    }


    @Override
    public void onBindViewHolder(ProjectHolder holder, int position) {
        holder.bind(mProjectsArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mProjectsArrayList != null) {
            return mProjectsArrayList.size();
        } else {
            return 0;
        }
    }


    public class ProjectHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.projectNameTextView)
        TextView projectNameTextView;
        @BindView(R.id.projetcNoTextView)
        TextView projetcNoTextView;
        @BindView(R.id.projetcAddressTextView)
        TextView projetcAddressTextView;
        @BindView(R.id.projectImageView)
        ImageView projectImageView;
        @BindView(R.id.projectImageProgressBar)
        ProgressBar projectImageProgressBar;
        @BindView(R.id.projectBackgroundImageView)
        ImageView projectBackgroundImageView;
        @BindView(R.id.projectCardView)
        CardView projectCardView;

        public ProjectHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(final PjProjects projects) {
            String addressStr = projects.getAddress() + (!TextUtils.isEmpty(projects.getCity()) ? ", " + projects.getCity() : "") + (!TextUtils.isEmpty(projects.getState()) ? ", " + projects.getState() : "") + (!TextUtils.isEmpty(projects.getZip()) ? " " + projects.getZip() : "");

            projectNameTextView.setText(projects.getName());
            projetcNoTextView.setText("Project # " + projects.getProjectNumber());
            projetcAddressTextView.setText(addressStr);
            projectImageView.setImageResource(R.drawable.ic_blank);

//            new LoadImageAsync().execute(projects.getShowcasePhoto());
            URI uri = null;
            try {
                uri = new URI(projects.getShowcasePhoto());
                String[] segments = uri.getPath().split("/");
                String imageName = segments[segments.length - 1];
                String filePath = projectImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/";
                String[] params = new String[]{projects.getShowcasePhoto(), filePath};
//                Object[] params = new Object[]{projects.getShowcasePhoto(), filePath,projectImageView.getContext(),projectImageView};
                File imgFile = new File(filePath + "/" + imageName);
                if (!imgFile.exists()) {
                    try {
                        new LoadImageInBackground(new LoadImageInBackground.Listener() {
                            @Override
                            public void onImageDownloaded(Bitmap bitmap) {
//                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(projectImageView.getContext().getResources(), bitmap);
//                            roundedBitmapDrawable.setCornerRadius(cornerRadius);
//                            projectImageView.setImageDrawable(roundedBitmapDrawable);

                                projectImageView.setImageBitmap(bitmap);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    projectImageView.setOutlineProvider(new CircleOutlineProvider());
                                }
                                projectBackgroundImageView.setVisibility(View.GONE);
                                projectImageProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onImageDownloadError() {
                                projectImageProgressBar.setVisibility(View.GONE);
                            }
                        }).executeOnExecutor(THREAD_POOL_EXECUTOR, params);
                    } catch (RejectedExecutionException e) {
                        e.printStackTrace();
                    }

                } else {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(projectImageView.getContext().getResources(), myBitmap);
//                    roundedBitmapDrawable.setCornerRadius(cornerRadius);
//                    projectImageView.setImageDrawable(roundedBitmapDrawable);
                    projectImageView.setImageBitmap(myBitmap);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        projectImageView.setOutlineProvider(new CircleOutlineProvider());
                    }
                    projectImageProgressBar.setVisibility(View.GONE);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            projectCardView.setOnClickListener(v -> {
                // Preventing multiple clicks, using threshold of 1 second
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                mActivity.startActivity(new Intent(mActivity, ProjectOptionsActivity.class).putExtra("project_name", projects.getName()).putExtra("project_id", projects.getPjProjectsId()));
            });
        }

    }


}
