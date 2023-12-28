package com.pronovoscm.galleryimagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pronovoscm.R;
import com.pronovoscm.galleryimagepicker.datamodel.PhotosData;
import com.pronovoscm.galleryimagepicker.datamodel.PhotosModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gwl on 14/08/15.
 */
public class GalleryPickerAdapter extends RecyclerView.Adapter<GalleryPickerAdapter.MyViewHolder> {


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
    private int maxImageToCapture = 25;

    public GalleryPickerAdapter(Context context) {
        this.context = context;
        this.captureedImages = ((Activity) context).getIntent().getParcelableArrayListExtra("captured_images");

        inflater = LayoutInflater.from(context);
     //   Log.d("GallerYPickerAdapter", "GalleryPickerAdapter(): captureedImages " + captureedImages);
    }


    public void setData(List<PhotosModel> data) {
        if (data.size() > 0) {
            GalleryPickerAdapter.data = data;
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

        if (PhotosData.dir) {
            holder.tv_grid.setText(model.getImageName() == null ? model.getImageBucket() : model.getImageName());
        } else {
            ImageModel imageModel = new ImageModel("", false, model.getImagePath());
            if (captureedImages.contains(imageModel)) {
                holder.selectedImageView.setVisibility(View.VISIBLE);
            }
            holder.tv_grid.setVisibility(View.GONE);
        }
        holder.bind(position, model);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface imageClick {
        void onUpdateImageList(ArrayList<ImageModel> captureList);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        View row;
        ImageView iv_grid, selectedImageView;
        TextView tv_grid;

        public MyViewHolder(View itemView) {
            super(itemView);
            row = itemView;
            iv_grid = (ImageView) row.findViewById(R.id.gv_image);
            tv_grid = (TextView) row.findViewById(R.id.gv_title);
            selectedImageView = (ImageView) row.findViewById(R.id.selectedImageView);


        }

        private void bind(int position, PhotosModel model) {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ImageModel imageModel = new ImageModel("", false, model.getImagePath());
                    Bitmap mb = BitmapFactory.decodeFile(imageModel.getGalleryPath());
                    if (mb == null) {
                       Toast.makeText(context,"Please select valid image.",Toast.LENGTH_SHORT).show();
                        return;

                    }
                    if (!captureedImages.contains(imageModel) && captureedImages.size() < maxImageToCapture) {
                        captureedImages.add(new ImageModel("", false, model.getImagePath()));
                        selectedImageView.setVisibility(View.VISIBLE);
                    } else if (captureedImages.contains(imageModel)) {
                        selectedImageView.setVisibility(View.GONE);
                        captureedImages.remove(imageModel);
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
//                    alertDialog.setTitle(getContext().getString(R.string.message));
//                        alertDialog.setMessage("You cannot select more than " + maxImageToCapture + " images. Please deselect another image before trying to select again.");
                        alertDialog.setMessage("You can't select more than 25 photos. Please first deselect a photo in order to select any new photo.");
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), (dialog, which) -> {
                                    dialog.dismiss();
                                }
                        );
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                            }
                        });
                        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    }
                    ((GalleryPickerActivity) context).onUpdateImageList(captureedImages);
//                        notifyDataSetChanged();
//                        openImageFragment();

                }
            });
        }

        // Do something when clicked on a bucket
        private void openBucketFragment() {
            Toast.makeText(context, "Clicked on the bucket at :" + getLayoutPosition(), Toast.LENGTH_LONG).show();
            ImagesGridFragment fg = ImagesGridFragment.newInstance(getLayoutPosition());
            FragmentManager fm = ((GalleryPickerActivity) context).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.view_holder, fg);
            //ft.add(R.id.view_holder, fg);
            ft.addToBackStack(null);
            ft.commit();
        }

        // Do something when clicked on an image in bucket
//        private void openImageFragment() {
//
//            ArrayList<String> paths = new ArrayList<>();
//            for (int i = 0; i < data.size() - 1; i++) {
//                paths.add(data.get(i).getImagePath());
//                Log.d("Paths", paths.get(i));
//            }
//
//            Intent intent = new Intent(context, ImageViewActivity.class);
//            intent.putStringArrayListExtra("paths", paths);
//            context.startActivity(intent);
//            /*FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
//            ImageViewActivity im = ImageViewActivity.newInstance(context, path);
//            FragmentTransaction ft = fm.beginTransaction();
//            ft.replace(R.id.view_holder, im, "ImageViewActivity");
//            ft.addToBackStack("imageFragment");
//            ft.commit();*/
//        }
    }
}
