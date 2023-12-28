package com.pronovoscm.galleryimagepicker;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.galleryimagepicker.datamodel.PhotosData;
import com.pronovoscm.galleryimagepicker.datamodel.PhotosModel;

import java.util.ArrayList;

/**
 * Created by gwl on 18/08/15.
 */
public class ImagesBucketFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final int URL_LOADER = 0;
    TextView doneTextView;
    ArrayList<ImageModel> captureList;
    private RecyclerView rv;
    private GridLayoutManager glm;
    private FolderPickerAdapter adapter;
    private ProgressBar pbMain;
private ConstraintLayout bottomView;
    public static ImagesBucketFragment newInstance() {
        ImagesBucketFragment fb = new ImagesBucketFragment();

        return fb;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((GalleryPickerActivity)getActivity()).setTitleText("Photos");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        pbMain = view.findViewById(R.id.pb_main);
        bottomView =  view.findViewById(R.id.bottomView);
        bottomView.setVisibility(View.GONE);
        pbMain.setVisibility(View.VISIBLE);
        doneTextView = view.findViewById(R.id.doneTextView);
//        doneTextView.setOnClickListener(this);
        view.findViewById(R.id.toolbar).setVisibility(View.GONE);
        getLoaderManager().initLoader(URL_LOADER, null, this);

        Log.d("MyTag", "ImagesBucketFragment");
        pbMain.setVisibility(View.GONE);
        loadBucket(view);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        ((GalleryPickerActivity) getActivity()).setTitleText("Photos");
        Log.d("ImagesBucketFragment", "Bucket onResume()");
        LoaderManager.getInstance(this).initLoader(URL_LOADER, null, this);
        adapter = new FolderPickerAdapter(getActivity());
        glm = new GridLayoutManager(getActivity(), 2);
        rv.setLayoutManager(glm);
        rv.setAdapter(adapter);
    }

    private void loadBucket(View view) {

        rv = (RecyclerView) view.findViewById(R.id.rv_main_grid);
        if (adapter == null) {
            adapter = new FolderPickerAdapter(getActivity());
        }
        glm = new GridLayoutManager(getActivity(), 2);
        rv.setLayoutManager(glm);
        rv.setAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("ImagesBucketFragment", "onCreateLoader:id " + id);
        return new CursorLoader(getActivity(),
                GalleryPickerAdapter.uri,
                GalleryPickerAdapter.projections,
                null,
                null,
                GalleryPickerAdapter.sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.setData(PhotosData.getData(true, cursor));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == doneTextView.getId()) {
            Intent intent = getActivity().getIntent();
            intent.putParcelableArrayListExtra("captured_images", captureList);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }
}
