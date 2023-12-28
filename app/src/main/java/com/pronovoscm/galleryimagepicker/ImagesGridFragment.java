package com.pronovoscm.galleryimagepicker;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.galleryimagepicker.datamodel.PhotosData;


/**
 * Created by gwl on 18/08/15.
 */
public class ImagesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URL_LOADER = 1;
    private static int position;
    private RecyclerView rv;
    private GridLayoutManager glm;
    private GalleryPickerAdapter adapter;
    private ProgressBar pbFrag;
    private ConstraintLayout bottomView;
    TextView photoTV;

    private String folderName;

    public static ImagesGridFragment newInstance(int pos) {
        ImagesGridFragment fg = new ImagesGridFragment();
        Bundle args = new Bundle();
        args.putInt("pos", pos);
        fg.setArguments(args);

        return fg;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments() != null ? getArguments().getInt("pos") : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);

        bottomView = view.findViewById(R.id.bottomView);
        bottomView.setVisibility(View.GONE);
        pbFrag = (ProgressBar) view.findViewById(R.id.pb_main);
        pbFrag.setVisibility(View.VISIBLE);
        LoaderManager.getInstance(this).initLoader(URL_LOADER, null, this);
        photoTV = view.findViewById(R.id.photoTV);
        view.findViewById(R.id.toolbar).setVisibility(View.GONE);

        showImages(view);
        pbFrag.setVisibility(View.GONE);
        folderName = getArguments().getString("folderName");
       /* if (folderName!=null){
            ((GalleryPickerActivity)getActivity()).setTitleText(folderName);
        }*/
        Log.e("ImagesGridFragment", "ImagesGridFragment onCreateView");
        return view;
    }

    private void showImages(View view) {
        rv = (RecyclerView) view.findViewById(R.id.rv_main_grid);
        if (adapter == null) {
            adapter = new GalleryPickerAdapter(getActivity());
        }
        glm = new GridLayoutManager(getActivity(), 2);
        rv.setLayoutManager(glm);
        rv.setAdapter(adapter);
        Log.e("ImagesGridFragment", "  showImages ");
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] selectionArgs = {FolderPickerAdapter.data.get(position).getBucketId()};
        Log.e("ImagesGridFragment", "onCreateLoader: id = " + id + "    GalleryPickerAdapter.uri " + GalleryPickerAdapter.uri + " projections  " + GalleryPickerAdapter.projections);
        if (id == URL_LOADER) {
            String title = FolderPickerAdapter.data.get(position).getImageName() == null ? FolderPickerAdapter.data.get(position).getImageBucket() : FolderPickerAdapter.data.get(position).getImageName();
            ((GalleryPickerActivity) getActivity()).setTitleText(title);
            CursorLoader cursorLoader = new CursorLoader(getActivity(),
                    GalleryPickerAdapter.uri,
                    GalleryPickerAdapter.projections,
                    FolderPickerAdapter.projections[3] + " = ?"/*+ " = \"" + FolderPickerAdapter.data.get(position).getBucketId() + "\""*/,
                    selectionArgs,
                    GalleryPickerAdapter.sortOrder);
            Log.d("ImagesGridFragment", "onCreateLoader:  cursorLoader :" + cursorLoader);
            return cursorLoader;

        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.setData(PhotosData.getData(false, cursor));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
