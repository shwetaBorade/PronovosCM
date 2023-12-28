package com.pronovoscm.ui.punchlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.ui.punchlist.adapter.AssigneeNameAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AssigneeNameDialog extends DialogFragment {

    Button cancel;
    @BindView(R.id.recyclerView)
    RecyclerView rv;
    AssigneeNameAdapter adapter;
    List<String> assigneeName;
    private Context mActivity;

    public AssigneeNameDialog(List<String> assigneeName) {
        this.assigneeName = assigneeName;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
    }

      @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          View rootView = inflater.inflate(R.layout.layout_dialog_fragment_punchlist_assignee_name, container, false);
          ButterKnife.bind(this, rootView);
          setCancelable(true);
          return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        //ADAPTER
        adapter=new AssigneeNameAdapter(this.getActivity(),assigneeName);
        rv.setAdapter(adapter);
    }

    @OnClick(R.id.cancelId)
    public void onCancelClick(){
        this.dismiss();
    }

}
