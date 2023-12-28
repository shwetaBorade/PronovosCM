package com.pronovoscm.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.PhotoFolder;
import com.pronovoscm.utils.dialogs.AlbumsDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectAlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PhotoFolder> mPhotoFolders;
    private AlbumsDialog mAlbumsDialog;
    private PhotoFolder mPhotoFolder;

    public SelectAlbumAdapter(AlbumsDialog albumsDialog, List<PhotoFolder> photoFolders, PhotoFolder photoFolder) {
        this.mPhotoFolders = photoFolders;
        this.mPhotoFolder = photoFolder;
        this.mAlbumsDialog = albumsDialog;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.select_album_item_list, parent, false);

        return new AlbumListViewHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((AlbumListViewHolder) holder).bind(mPhotoFolders.get(position));
    }

    @Override
    public int getItemCount() {
        if (mPhotoFolders != null) {
            return mPhotoFolders.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface updatePhotoFolder {
        void onUpdateSelectedPhototFolder(PhotoFolder photoFolder);
    }

    public class AlbumListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.albumTextView)
        TextView albumTextView;
        @BindView(R.id.albumRadioButton)
        RadioButton albumRadioButton;
        @BindView(R.id.tagsView)
        ConstraintLayout tagsView;

        public AlbumListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(final PhotoFolder photoFolder) {
            if (photoFolder != null) {

                albumTextView.setText(photoFolder.getName());
                if (mPhotoFolder != null && photoFolder.equals(mPhotoFolder)) {
                    albumRadioButton.setChecked(true);
                } else {
                    albumRadioButton.setChecked(false);
                }
                albumRadioButton.setClickable(false);
                tagsView.setOnClickListener(v -> {
                            albumRadioButton.setChecked(!albumRadioButton.isChecked());
                            mPhotoFolder = photoFolder;
                            notifyDataSetChanged();
                            (mAlbumsDialog).onUpdateSelectedPhototFolder(mPhotoFolder);
                        }
                );

            }
        }

    }
}