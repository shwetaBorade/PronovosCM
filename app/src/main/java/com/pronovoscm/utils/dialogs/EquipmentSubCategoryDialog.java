package com.pronovoscm.utils.dialogs;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.SubCategoryAdapter;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMaster;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMaster;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EquipmentSubCategoryDialog extends DialogFragment implements View.OnClickListener, SubCategoryAdapter.updateSubCategory {
    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;

    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.tagsRecyclerView)
    RecyclerView tagsRecyclerView;
    @BindView(R.id.searchView)
    RelativeLayout searchView;
    @BindView(R.id.searchAlbumEditText)
    EditText searchAlbumEditText;
    private ArrayList<EquipmentSubCategoriesMaster> equipmentCategoriesMasters;
    private ArrayList<EquipmentSubCategoriesMaster> searchEquipmenrArrayList;
    private int projectId;
    private LoginResponse loginResponse;
    private SubCategoryAdapter categoryAdapter;
    private EquipmentSubCategoriesMaster equipmentCategoriesMaster;
    private int eqCategoryId;

    public static ArrayList<EquipmentCategoriesMaster> cloneList(ArrayList<EquipmentCategoriesMaster> imageTags) {
        ArrayList<EquipmentCategoriesMaster> clonedList = new ArrayList<>(imageTags.size());
        for (EquipmentCategoriesMaster imageTag : imageTags) {
            clonedList.add(imageTag);
        }
        return clonedList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tags_dialog_view, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        cancelTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        titleTextView.setText("Select Subcategory");
        searchView.setVisibility(View.VISIBLE);
        projectId = getArguments().getInt("project_id");
        equipmentCategoriesMaster = (EquipmentSubCategoriesMaster) getArguments().getSerializable("eqSubCategoriesMaster");
        eqCategoryId = getArguments().getInt("eq_category_id");
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        equipmentCategoriesMasters = new ArrayList<>();
        searchEquipmenrArrayList = new ArrayList<>();
        equipmentCategoriesMasters.addAll(mEquipementInventoryRepository.getSubCategoriesAccordingTenantId(eqCategoryId , loginResponse.getUserDetails().getUsers_id()));
        searchEquipmenrArrayList.addAll(mEquipementInventoryRepository.getSubCategoriesAccordingTenantId(eqCategoryId ,loginResponse.getUserDetails().getUsers_id()));

        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoryAdapter = new SubCategoryAdapter(this, searchEquipmenrArrayList, equipmentCategoriesMaster);
        tagsRecyclerView.setAdapter(categoryAdapter);
        saveTextView.setVisibility(View.VISIBLE);
        saveTextView.setText("Select");
        searchAlbumEditText.setHint(getString(R.string.search_here));
        searchAlbumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String string = charSequence.toString();
                searchEquipmenrArrayList.clear();
                if (string.length() > 0) {
                    for (int j = 0; j < equipmentCategoriesMasters.size(); j++) {
                        if (equipmentCategoriesMasters.get(j).getName().toLowerCase().contains(string.toLowerCase())) {
                            searchEquipmenrArrayList.add(equipmentCategoriesMasters.get(j));
                        }
                    }
                } else {
                    searchEquipmenrArrayList.addAll(equipmentCategoriesMasters);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()) {
            case R.id.saveTextView:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (equipmentCategoriesMaster != null) {
                    EventBus.getDefault().post(equipmentCategoriesMaster);
                    dismiss();
                }
                break;
            case R.id.cancelTextView:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateSelectSubCategory(EquipmentSubCategoriesMaster equipmentSubCategoriesMaster) {
        equipmentCategoriesMaster = equipmentSubCategoriesMaster;
    }
}

