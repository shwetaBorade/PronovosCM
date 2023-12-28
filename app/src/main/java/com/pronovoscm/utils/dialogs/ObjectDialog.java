package com.pronovoscm.utils.dialogs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.RecycleviewAdapter;
import com.pronovoscm.adapter.SelectAlbumAdapter;
import com.pronovoscm.model.ObjectEnum;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.CompanyList;
import com.pronovoscm.persistence.domain.PhotoFolder;
import com.pronovoscm.persistence.domain.PunchlistAssignee;
import com.pronovoscm.persistence.domain.Trades;
import com.pronovoscm.persistence.repository.FieldPaperWorkRepository;
import com.pronovoscm.utils.ObjectEvent;
import com.pronovoscm.utils.PhotoFolderEvent;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ObjectDialog extends DialogFragment implements View.OnClickListener, SelectAlbumAdapter.updatePhotoFolder {
    @Inject
    FieldPaperWorkRepository mFieldPaperWorkRepository;
    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.searchView)
    RelativeLayout searchView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.tagsRecyclerView)
    RecyclerView tagsRecyclerView;
    @BindView(R.id.searchAlbumEditText)
    EditText searchAlbumEditText;
    private RecycleviewAdapter mSelectAlbumAdapter;
    private Object selectedObject;
    private List<Object> objectList;
    private int pjProjectId;
    private LoginResponse loginResponse;
    private int objectType, userTenantId;
    private List<Object> allObjectList;


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
        cancelTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        searchView.setVisibility(View.VISIBLE);
        selectedObject = getArguments().getParcelable("object");
        pjProjectId = getArguments().getInt("pjProjectId");
        objectType = getArguments().getInt("obj_type");
        userTenantId = getArguments().getInt("tenantId");
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        searchAlbumEditText.setHint(getString(R.string.search_here));
        if (objectType == ObjectEnum.TRADES.ordinal()) {
            objectList = new ArrayList<>(mFieldPaperWorkRepository.getTrades(loginResponse));
        titleTextView.setText(getString(R.string.select_trade));
        } else if (objectType == ObjectEnum.COMPANY_LIST.ordinal()) {
            objectList = new ArrayList<>(mFieldPaperWorkRepository.getCompanyList(pjProjectId));
        titleTextView.setText(getString(R.string.select_company_list));
        } else if (objectType == ObjectEnum.ASSIGNEE.ordinal()) {
        titleTextView.setText(getString(R.string.title_select_assignee));
            objectList = new ArrayList<>(mFieldPaperWorkRepository.getAssignee(pjProjectId));
        }

        allObjectList = new ArrayList<>(objectList);
        searchAlbumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                objectList.clear();
                if (s.toString() != null && s.toString().length() > 0) {
                    for (int i = 0; i < allObjectList.size(); i++) {

                        if (objectType == ObjectEnum.TRADES.ordinal()) {
                            Trades trades = (Trades) allObjectList.get(i);
                            if (trades.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                objectList.add(trades);
                            }
                        } else if (objectType == ObjectEnum.COMPANY_LIST.ordinal()) {
                            CompanyList companyList = (CompanyList) allObjectList.get(i);
                            if (companyList.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                objectList.add(companyList);
                            }
                        }else if (objectType == ObjectEnum.ASSIGNEE.ordinal()) {
                            PunchlistAssignee punchlistAssignee = (PunchlistAssignee) allObjectList.get(i);
                            if (punchlistAssignee.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                objectList.add(punchlistAssignee);
                            }
                        }
                    }
                } else {
                    objectList.addAll(new ArrayList<>(allObjectList));
                }
                mSelectAlbumAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSelectAlbumAdapter = new RecycleviewAdapter(objectList, selectedObject, object -> {
            selectedObject = object;
        });
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tagsRecyclerView.setAdapter(mSelectAlbumAdapter);

    }

    @Override
    public void onClick(View v) {
        ObjectEvent photoFolderEvent = new ObjectEvent();
        switch (v.getId()) {
            case R.id.saveTextView:
                photoFolderEvent.setObject(selectedObject);
                photoFolderEvent.setObjectType(objectType);
                EventBus.getDefault().post(photoFolderEvent);
                dismiss();
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateSelectedPhototFolder(PhotoFolder photoFolder) {
        selectedObject = photoFolder;
    }
}

