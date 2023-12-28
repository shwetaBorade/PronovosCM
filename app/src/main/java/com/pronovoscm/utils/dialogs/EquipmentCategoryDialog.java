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
import com.pronovoscm.adapter.CategoryAdapter;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMaster;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EquipmentCategoryDialog extends DialogFragment implements View.OnClickListener, CategoryAdapter.updateCategory {
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
    private ArrayList<EquipmentCategoriesMaster> equipmentCategoriesMasters;
    private ArrayList<EquipmentCategoriesMaster> searchEquipmenrArrayList;
    private int projectId;
    private LoginResponse loginResponse;
    private CategoryAdapter categoryAdapter;
    private EquipmentCategoriesMaster equipmentCategoriesMaster;

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
        titleTextView.setText("Select Category");
        searchView.setVisibility(View.VISIBLE);
        projectId = getArguments().getInt("project_id");
        equipmentCategoriesMaster = (EquipmentCategoriesMaster) getArguments().getSerializable("eqCategoriesMaster");
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        equipmentCategoriesMasters = new ArrayList<>();
        searchEquipmenrArrayList = new ArrayList<>();
        equipmentCategoriesMasters.addAll(mEquipementInventoryRepository.getCategoriesAccordingTenantId(loginResponse.getUserDetails().getTenantId(), loginResponse.getUserDetails().getUsers_id()));
        searchEquipmenrArrayList.addAll(mEquipementInventoryRepository.getCategoriesAccordingTenantId(loginResponse.getUserDetails().getTenantId(), loginResponse.getUserDetails().getUsers_id()));
//        searchEquipmenrArrayList.addAll(mEquipementInventoryRepository.getCategories(projectId, loginResponse.getUserDetails().getUsers_id()));

        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoryAdapter = new CategoryAdapter(this, searchEquipmenrArrayList, equipmentCategoriesMaster);
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
    public void onUpdateSelectCategory(EquipmentCategoriesMaster equipmentCategoriesMaster) {
        this.equipmentCategoriesMaster = equipmentCategoriesMaster;
    }
}

