package com.pronovoscm.galleryimagepicker;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pronovoscm.R;
import com.pronovoscm.galleryimagepicker.datamodel.PhotosModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gwl on 14/08/15.
 */
public class FolderPickerAdapter extends RecyclerView.Adapter<FolderPickerAdapter.MyViewHolder> {


    //define source of MediaStore.Images.Media, internal or external storage
    public static final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final String[] projections = {MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DISPLAY_NAME};
    public static String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
    static List<PhotosModel> data = new ArrayList<>();
    ArrayList<ImageModel> captureedImages;
    private Context context;
    private LayoutInflater inflater;

    public FolderPickerAdapter(Context context) {
        this.context = context;
        this.captureedImages = ((Activity) context).getIntent().getParcelableArrayListExtra("captured_images");

        inflater = LayoutInflater.from(context);
    }


    public void setData(List<PhotosModel> data) {
     if(data.size()>0){
         FolderPickerAdapter.data = data;
     }
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View myView = inflater.inflate(R.layout.grid_item, parent, false);
        return new MyViewHolder(myView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Log.d("MyTag", "GalleryPickerAdapter");
        final PhotosModel model = data.get(position);
        File file = new File(model.getImagePath());
        Uri imageUri = Uri.fromFile(file);
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true);
        Glide.with(context)
                .load(imageUri)
                .apply(requestOptions)
                .into(holder.iv_grid);

        holder.selectedImageView.setVisibility(View.GONE);
        holder.unSelectedImageView.setVisibility(View.GONE);

        holder.tv_grid.setText(model.getImageName() == null ? model.getImageBucket() : model.getImageName());

        holder.bind(position, model);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        View row;
        ImageView iv_grid;
        ImageView selectedImageView;
        ImageView unSelectedImageView;
        TextView tv_grid;

        public MyViewHolder(View itemView) {
            super(itemView);
            row = itemView;
            iv_grid = (ImageView) row.findViewById(R.id.gv_image);
            tv_grid = (TextView) row.findViewById(R.id.gv_title);
            selectedImageView = row.findViewById(R.id.selectedImageView);
            unSelectedImageView= row.findViewById(R.id.unSelectedImageView);


        }

        private void bind(int position, PhotosModel model) {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("FolderPickerAdapter", "openBucketFragment: ");
                    openBucketFragment();

//                    openBucketFragment(model.getImageName() == null ? model.getImageBucket() : model.getImageName());

                }
            });
        }

        // Do something when clicked on a bucket
        private void openBucketFragment(/*String folderName*/) {

            Log.d("MyTag", "openBucketFragment()");

//            Toast.makeText(context, "Clicked on the bucket at :" + getLayoutPosition(), Toast.LENGTH_LONG).show();
            Log.d("MyTag", "Bucket clicked");

            ImagesGridFragment fg = ImagesGridFragment.newInstance(getLayoutPosition());

            FragmentManager fm = ((GalleryPickerActivity) context).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
        /*    Bundle bundle=new Bundle();
            bundle.putString("folderName",folderName);
            fg.setArguments(bundle);
        */    ft.add(R.id.view_holder, fg);
            //ft.add(R.id.view_holder, fg);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}
