package com.pronovoscm.utils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.pronovoscm.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoadImage {
    private Context context;

    public LoadImage(Context context) {
        this.context = context;
    }

    public LoadImage() {
    }

    public void LoadImagePath(String url, String thumburl, String imageName, ImageView imageView, ProgressBar albumImageProgressBar, boolean isThumbImage, ImageView backImage) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true);

        if ((!isThumbImage || thumburl == null) && isFileExist(imageName)) {
//            String completePath = Environment.getExternalStorageDirectory() + "/Pronovos/" + imageName;
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/" + imageName;

            File file = new File(completePath);
//            Bitmap bmImg = BitmapFactory.decodeFile(file.getAbsolutePath());
//            imageView.setImageBitmap(bmImg);

            Uri imageUri = Uri.fromFile(file);
            Glide.with(context)
                    .load(imageUri)
                    .apply(requestOptions)
                    .into(imageView);
            albumImageProgressBar.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
        } else if (isThumbImage && thumburl != null && isThumbFileExist(imageName)) {
//            String completePath = Environment.getExternalStorageDirectory() + "/Pronovos/ThumbImage/" + imageName;
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/" + imageName;

            File file = new File(completePath);

            Uri imageUri = Uri.fromFile(file);
            Glide.with(context)
                    .load(imageUri)
                    .into(imageView);
            albumImageProgressBar.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);

        } else {
            File file = new File(imageName);
            Uri imageUri = Uri.fromFile(file);
            Glide.with(context)
                    .load(imageUri)
                    .apply(requestOptions)
                    .into(imageView);
            albumImageProgressBar.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
        }
    }

    public void LoadImagePathRounded(String url, String thumburl, String imageName, ImageView imageView, ProgressBar albumImageProgressBar, boolean isThumbImage, ImageView backImage) {
        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .transform(new RoundedCorners(20))
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true);

        if ((!isThumbImage || thumburl == null) && isFileExist(imageName)) {
//            String completePath = Environment.getExternalStorageDirectory() + "/Pronovos/" + imageName;
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/" + imageName;

            File file = new File(completePath);
//            Bitmap bmImg = BitmapFactory.decodeFile(file.getAbsolutePath());
//            imageView.setImageBitmap(bmImg);

            Uri imageUri = Uri.fromFile(file);
            Glide.with(context)
                    .load(imageUri)
                    .apply(requestOptions)
//                    .apply(new RequestOptions().transform(new RoundedCorners(20)))
                    .into(imageView);
            albumImageProgressBar.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
        } else if (isThumbImage && thumburl != null && isThumbFileExist(imageName)) {
//            String completePath = Environment.getExternalStorageDirectory() + "/Pronovos/ThumbImage/" + imageName;
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/" + imageName;

            File file = new File(completePath);

            Uri imageUri = Uri.fromFile(file);
            Glide.with(context)
                    .load(imageUri)
                    .apply(new RequestOptions().transform(new RoundedCorners(20)))
                    .into(imageView);
            albumImageProgressBar.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);

        } else {
            File file = new File(imageName);
            Uri imageUri = Uri.fromFile(file);
            Glide.with(context)
                    .load(imageUri)
                    .apply(requestOptions)
                    .apply(new RequestOptions().transform(new RoundedCorners(20)))
                    .into(imageView);
            albumImageProgressBar.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);
        }
    }

    public void getRoundedImagePath(String url, String thumburl,
                                    String imageName,
                                    final ImageView imageView,
                                    ProgressBar albumImageProgressBar,
                                    boolean isThumbImage, ImageView backImage) {
        int dp = (int) (context.getResources().getDimension(R.dimen.album_photo_radius) / context.getResources().getDisplayMetrics().density);

        if ((!isThumbImage || thumburl == null) && isFileExist(imageName)) {
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/" + imageName;
            File file = new File(completePath);
            try {
                Uri imageUri = Uri.fromFile(file);
                Glide.with(context)
                        .load(imageUri)
                        .apply(new RequestOptions().centerCrop().transform(new RoundedCorners(dp))
//                                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                                /*.skipMemoryCache(true)*/).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        albumImageProgressBar.setVisibility(View.GONE);
                        backImage.setVisibility(View.GONE);
                        return false;
                    }
                })
                        .into(imageView);
//                albumImageProgressBar.setVisibility(View.GONE);
//                backImage.setVisibility(View.GONE);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        } // TODO: 29/10/18 Need to optimise this code
        else if (isThumbImage && thumburl != null && isThumbFileExist(imageName)) {
//            String completePath = Environment.getExternalStorageDirectory() + "/Pronovos/ThumbImage/" + imageName;
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/" + imageName;

            File file = new File(completePath);
            Uri imageUri = Uri.fromFile(file);
            try {
                Glide.with(context)
                        .load(imageUri)
                        .apply(new RequestOptions().centerCrop().transform(new RoundedCorners(dp))
//                                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                                /*    .skipMemoryCache(true)*/).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        albumImageProgressBar.setVisibility(View.GONE);
                        backImage.setVisibility(View.GONE);
                        return false;
                    }
                })
                        .into(imageView);

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        } else if (isThumbImage && thumburl != null && isFileExist(imageName)) {
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/" + imageName;

            File file = new File(completePath);
            Uri imageUri = Uri.fromFile(file);
            Glide.with(context)
                    .load(imageUri)
                    .apply(new RequestOptions().centerCrop().transform(new RoundedCorners(dp))
                            /*.diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                            .skipMemoryCache(true)*/).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    albumImageProgressBar.setVisibility(View.GONE);
                    backImage.setVisibility(View.GONE);
                    return false;
                }
            })
                    .into(imageView);
//            albumImageProgressBar.setVisibility(View.GONE);
//            backImage.setVisibility(View.GONE);

        } else {
            try {
                Glide.with(context)
                        .asBitmap().load(isThumbImage && thumburl != null ? thumburl : url)/*.apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                        .skipMemoryCache(true))*/
//                        .apply(new RequestOptions().transform(new RoundedCorners(10)))
                        .listener(new RequestListener<Bitmap>() {
                                      @Override
                                      public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                          albumImageProgressBar.setVisibility(View.GONE);
                                          imageView.setImageBitmap(null);
                                          backImage.setVisibility(View.VISIBLE);
                                          return false;
                                      }

                                      @Override
                                      public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                          backImage.setVisibility(View.GONE);
                                          albumImageProgressBar.setVisibility(View.GONE);
                                          if (isThumbImage && thumburl != null) {
                                              SaveImageInStorage(bitmap, imageName, imageView, albumImageProgressBar, "/Pronovos/ThumbImage/", backImage, true);
                                          } else {
                                              SaveImageInStorage(bitmap, imageName, imageView, albumImageProgressBar, "/Pronovos/", backImage, true);
                                          }
                                          return false;
                                      }
                                  }
                        ).into(imageView)/*.submit()*/;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }


        }
    }

    public void getImagePathNew(String url, String thumburl, String imageName, final ImageView imageView, ProgressBar albumImageProgressBar, boolean isThumbImage, ImageView backImage) {
        if ((!isThumbImage || thumburl == null) && isFileExist(imageName)) {
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/" + imageName;
            File file = new File(completePath);
            try {
                Uri imageUri = Uri.fromFile(file);
                Glide.with(context)
                        .load(imageUri)
                        .apply(new RequestOptions().transform(new RoundedCorners(1)))
                        .into(imageView);
                albumImageProgressBar.setVisibility(View.GONE);
                backImage.setVisibility(View.GONE);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        } // TODO: 29/10/18 Need to optimise this code
        else if (isThumbImage && thumburl != null && isThumbFileExist(imageName)) {
//            String completePath = Environment.getExternalStorageDirectory() + "/Pronovos/ThumbImage/" + imageName;
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/" + imageName;

            File file = new File(completePath);
            Uri imageUri = Uri.fromFile(file);
            try {
                Glide.with(context)
                        .load(imageUri)
                        .apply(new RequestOptions().transform(new RoundedCorners(1)))
                        .into(imageView);
                albumImageProgressBar.setVisibility(View.GONE);
                backImage.setVisibility(View.GONE);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        } else if (isThumbImage && thumburl != null && isFileExist(imageName)) {
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/" + imageName;

            File file = new File(completePath);
            Uri imageUri = Uri.fromFile(file);
            Glide.with(context)
                    .load(imageUri)
                    .apply(new RequestOptions().transform(new RoundedCorners(1)))
                    .into(imageView);
            albumImageProgressBar.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);

        } else {
            try {
                Glide.with(context)
                        .asBitmap().load(isThumbImage && thumburl != null ? thumburl : url)
//                        .apply(new RequestOptions().transform(new RoundedCorners(10)))
                        .listener(new RequestListener<Bitmap>() {
                                      @Override
                                      public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                          albumImageProgressBar.setVisibility(View.GONE);
                                          imageView.setImageBitmap(null);
                                          backImage.setVisibility(View.VISIBLE);
                                          return false;
                                      }

                                      @Override
                                      public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                          if (isThumbImage && thumburl != null) {
                                              SaveImageInStorage(bitmap, imageName, imageView, albumImageProgressBar, "/Pronovos/ThumbImage/", backImage, false);
                                          } else {
                                              SaveImageInStorage(bitmap, imageName, imageView, albumImageProgressBar, "/Pronovos/", backImage, false);
                                          }
                                          return false;
                                      }
                                  }
                        ).submit();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }


        }
    }

    public void getImagePath(String url, String thumburl, String imageName, final ImageView imageView, ProgressBar albumImageProgressBar, boolean isThumbImage, ImageView backImage) {
        if ((!isThumbImage || thumburl == null) && isFileExist(imageName)) {
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/" + imageName;
            File file = new File(completePath);
            try {
                Uri imageUri = Uri.fromFile(file);
                Glide.with(context)
                        .load(imageUri)
                        .into(imageView);
                albumImageProgressBar.setVisibility(View.GONE);
                backImage.setVisibility(View.GONE);
            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
            }

        } // TODO: 29/10/18 Need to optimise this code
        else if (isThumbImage && thumburl != null && isThumbFileExist(imageName)) {
//            String completePath = Environment.getExternalStorageDirectory() + "/Pronovos/ThumbImage/" + imageName;
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/" + imageName;

            File file = new File(completePath);
            Uri imageUri = Uri.fromFile(file);
            try {
                Glide.with(context)
                        .load(imageUri)
                        .into(imageView);
                albumImageProgressBar.setVisibility(View.GONE);
                backImage.setVisibility(View.GONE);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        } else if (isThumbImage && thumburl != null && isFileExist(imageName)) {
            String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/" + imageName;

            File file = new File(completePath);
            Uri imageUri = Uri.fromFile(file);
            Glide.with(context)
                    .load(imageUri)
                    .into(imageView);
            albumImageProgressBar.setVisibility(View.GONE);
            backImage.setVisibility(View.GONE);

        } else {
            try {
                Glide.with(context)
                        .asBitmap().load(isThumbImage && thumburl != null ? thumburl : url)
//                        .apply(new RequestOptions().transform(new RoundedCorners(10)))
                        .listener(new RequestListener<Bitmap>() {
                                      @Override
                                      public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                          albumImageProgressBar.setVisibility(View.GONE);
                                          imageView.setImageBitmap(null);
                                          backImage.setVisibility(View.VISIBLE);
                                          return false;
                                      }

                                      @Override
                                      public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                          if (isThumbImage && thumburl != null) {
                                              SaveImageInStorage(bitmap, imageName, imageView, albumImageProgressBar, "/Pronovos/ThumbImage/", backImage, false);
                                          } else {
                                              SaveImageInStorage(bitmap, imageName, imageView, albumImageProgressBar, "/Pronovos/", backImage, false);
                                          }
                                          return false;
                                      }
                                  }
                        ).submit();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * Check that File is exist in storage or not.
     *
     * @param fileName
     * @return
     */
    public boolean isFileExist(String fileName) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = fileName;
        File file = new File(myDir, fname);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Check that File is exist in storage or not.
     *
     * @param fileName
     * @return
     */
    private boolean isThumbFileExist(String fileName) {
        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/Pronovos/ThumbImage/");
        File myDir = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = fileName;
        File file = new File(myDir, fname);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Save Image in storage and show it on the screen
     *
     * @param finalBitmap
     * @param fileName
     * @param imageView
     * @param albumImageProgressBar
     * @param backImage
     * @param isRounded
     * @return
     */
    private String SaveImageInStorage(Bitmap finalBitmap, String fileName, ImageView imageView, ProgressBar albumImageProgressBar, String path, ImageView backImage, boolean isRounded) {

        int dp = (int) (context.getResources().getDimension(R.dimen.album_photo_radius) / context.getResources().getDisplayMetrics().density);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                  Bitmap bmImg = BitmapFactory.decodeFile(file.getAbsolutePath());
//                  imageView.setImageBitmap(bmImg);
                String root = Environment.getExternalStorageDirectory().toString();
//                File myDir = new File(root + path);//"/Pronovos"
                File myDir = new File(context.getFilesDir().getAbsolutePath() + path);//"/PronovosPronovos"
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                String fname = fileName;
                File file = new File(myDir, fname);
                if (file.exists()) {
                    file.delete();
                }

                try {
                    FileOutputStream out = new FileOutputStream(file);
                    finalBitmap.compress(Bitmap.CompressFormat.WEBP, 90, out);
                    out.flush();
                    out.close();

                    try {
//                        Uri imageUri = Uri.fromFile(file);
                       /* if (isRounded) {
                            Glide.with(context)
                                    .load(imageUri)
                                    .apply(new RequestOptions().centerCrop().transform(new RoundedCorners(dp))*//*.diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                                            .skipMemoryCache(true)*//*)
                                    .into(imageView);
                            albumImageProgressBar.setVisibility(View.GONE);
                            backImage.setVisibility(View.GONE);
                        } else {
                            Glide.with(context)
                                    .load(imageUri)
                                    .apply(new RequestOptions().transform(new RoundedCorners(1)))
                                    .into(imageView);
                        }*/
                    } catch (Exception e) {
                        imageView.setImageBitmap(null);
                        e.printStackTrace();
                    }

                } catch (IllegalArgumentException e) {
                    albumImageProgressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    albumImageProgressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                } catch (IOException e) {
                    albumImageProgressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }, 0);

        return "";
    }


    public void LoadImagePath(String url, ImageView imageView) {
        try {
            Glide.with(context)
                    .asBitmap().load(url)//.apply(roundedCorners(new RequestOptions(), context, 10))
//                    .apply(new RequestOptions().transform(new RoundedCorners(50)))
                    .listener(new RequestListener<Bitmap>() {
                                  @Override
                                  public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                      imageView.setImageBitmap(null);
                                      return false;
                                  }

                                  @Override
                                  public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                      imageView.setImageBitmap(bitmap);

                                      return false;
                                  }
                              }
                    ).into(imageView);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
