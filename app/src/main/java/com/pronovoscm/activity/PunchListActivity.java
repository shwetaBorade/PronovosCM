package com.pronovoscm.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovos.pdf.utils.AddPunchList;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.PunchListAdapter;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.PunchListProvider;
import com.pronovoscm.fragments.PunchlistFragment;
import com.pronovoscm.model.PunchListStatus;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.punchlist.PunchListHistoryRequest;
import com.pronovoscm.model.request.punchlist.PunchListRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.login.UserPermissions;
import com.pronovoscm.persistence.domain.PunchListAttachments;
import com.pronovoscm.persistence.domain.PunchlistAssignee;
import com.pronovoscm.persistence.domain.PunchlistDb;
import com.pronovoscm.persistence.domain.punchlist.PunchListHistoryDb;
import com.pronovoscm.persistence.repository.PunchListRepository;
import com.pronovoscm.ui.punchlist.PunchListViewModel;
import com.pronovoscm.utils.IntentExtra;
import com.pronovoscm.utils.PunchListFilterEvent;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.PunchListFilterDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity to display punch list
 * </>
 * Developer note* : Start this activity by using {@link #startActivity(Context, String, int)}
 *
 * @author GWL
 */
public class PunchListActivity extends BaseActivity implements PunchListAdapter.OnItemClickListener,
        DialogInterface.OnDismissListener {

    public final String TAG = PunchListActivity.class.getSimpleName();

    @Inject
    PunchListProvider mPunchListProvider;
    @Inject
    PunchListRepository mPunchListRepository;

    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.punchlistRecyclerView)
    RecyclerView punchlistRecyclerView;
    @BindView(R.id.searchDrawingEditText)
    EditText searchDrawingEditText;
    @BindView(R.id.seachClearImageView)
    ImageView seachClearImageView;
    @BindView(R.id.filterTextView)
    TextView filterTextView;
    @BindView(R.id.addImageView)
    ImageView addImageView;
    //    @BindView(R.id.listTextView)
//    TextView listTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;

    private int mProjectId = 0;
    private Context context;
    private PunchListAdapter mPunchListAdapter;
    private List<PunchlistDb> mPunchlistDbs;

    private PunchListStatus mPunchListStatus;
    private PunchlistAssignee mPunchlistAssignee;
    private PunchlistFragment punchlistFragment;
    private LoginResponse loginResponse;
    private boolean linkExisting;
    private AddPunchList mAddPunchList;

    private List<PunchListHistoryDb> punchListHistoryDbs;

    PunchListViewModel model;

    /**
     * Start punch list activity
     *
     * @param context current activity context
     */
    public static void startActivity(Context context, String projectName, int projectId) {
        Intent intent = new Intent(context, PunchListActivity.class);
        intent.putExtra(IntentExtra.PROJECT_NAME.name(), projectName);
        intent.putExtra(IntentExtra.PROJECT_ID.name(), projectId);
        context.startActivity(intent);

    }

    @Override
    protected int doGetContentView() {
        return R.layout.activity_punch_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        model = new ViewModelProvider(this).get(PunchListViewModel.class);
        ButterKnife.bind(this);
        this.context = this;
        mProjectId = getIntent().getIntExtra(IntentExtra.PROJECT_ID.name(), 0);
        linkExisting = getIntent().getBooleanExtra("linkExisting", false);


        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        initializeFilterValue();
        initUI();


        List<PunchListAttachments> nonSyncedAttachments = mPunchListRepository.getNonSyncedAttachments();

        // Upload attachments if there is any in work detail
        if (nonSyncedAttachments.size() > 0) {
       /*     mPunchListProvider.syncPunchListToServer(mProjectId, new ProviderResult<List<PunchlistDb>>() {
                @Override
                public void success(List<PunchlistDb> result) {
                    mPunchlistDbs.clear();
                    mPunchlistDbs.addAll(result);
                    mPunchListAdapter.notifyDataSetChanged();
                    if (mPunchlistDbs == null || mPunchlistDbs.size() <= 0) {
                    noRecordTextView.setText("No punch list items available.");
                        noRecordTextView.setVisibility(View.VISIBLE);
                    } else {
                        noRecordTextView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void AccessTokenFailure(String message) {
                    startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    SharedPref.getInstance(context).writePrefs(SharedPref.SESSION_DETAILS, null);
                    SharedPref.getInstance(context).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                    finish();
                }

                @Override
                public void failure(String message) {

                }
            });*/
        }
        callPunchListAPI(mProjectId);
//        rightImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_filter));
    }

    private void initializeFilterValue() {
        mPunchListStatus = PunchListStatus.Open;
        mPunchlistAssignee = new PunchlistAssignee(-1, -1, "All", true, mProjectId, false, false);
        int filterCount = 0;
        if (mPunchListStatus != PunchListStatus.All) {
            filterCount = filterCount + 1;
        }
        if (mPunchlistAssignee.getUsersId() != -1) {
            filterCount = filterCount + 1;
        }
        if (filterCount > 0) {
            filterTextView.setText(String.valueOf(filterCount));
            filterTextView.setVisibility(View.VISIBLE);
            rightImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_filter_regions));
        } else {
            filterTextView.setVisibility(View.GONE);
            rightImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_filter_regions));
        }
    }

    /**
     * Initialise ui
     */
    private void initUI() {
        titleTextView.setText(R.string.punch_list);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        punchlistRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        searchDrawingEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    seachClearImageView.setVisibility(View.VISIBLE);

                } else {
                    seachClearImageView.setVisibility(View.GONE);
                }
                List<PunchlistDb> punchlistDbs = mPunchListRepository.getFilterPunchList(mProjectId,
                        mPunchListStatus, mPunchlistAssignee, s.toString(), linkExisting);
                if (mPunchlistDbs != null) {
                    mPunchlistDbs.clear();
                    mPunchlistDbs.addAll(punchlistDbs);
                    mPunchListAdapter.notifyDataSetChanged();
                } else {
                    mPunchlistDbs = new ArrayList<>();
                    mPunchlistDbs = punchlistDbs;
                    mPunchListAdapter = new PunchListAdapter(context, mPunchlistDbs, linkExisting, punchlistRecyclerView,
                            mPunchListRepository);
//                    DividerItemDecoration itemDecor = new DividerItemDecoration(getApplicationContext(), VERTICAL);
//                    punchlistRecyclerView.addItemDecoration(itemDecor);
                    punchlistRecyclerView.setAdapter(mPunchListAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        UserPermissions userPermissions = loginResponse.getUserDetails().getPermissions().get(0);
        if (userPermissions.getCreatePunchList() == 1 && !linkExisting) {
//            addTextView.setVisibility(View.VISIBLE);
            addImageView.setVisibility(View.VISIBLE);
        } else {
//            addTextView.setVisibility(View.GONE);
            addImageView.setVisibility(View.GONE);
        }
        //            listTextView.setText("Choose from List");
    }


    /**
     * Get all the punch list from the server with respect to the folder id
     *
     * @param projectId id of the project
     */
    private void callPunchListAPI(int projectId) {
        PunchListRequest punchListRequest = new PunchListRequest();
        punchListRequest.setProjectId(projectId);
//        punchListRequest.setPunchlists(mPunchListRepository.getNonSyncPunchListSyncAttachmentList(projectId));
        punchListRequest.setPunchlists(new ArrayList<>());

        mPunchListProvider.getPunchList(punchListRequest, new ProviderResult<List<PunchlistDb>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void success(List<PunchlistDb> result) {
                List<PunchlistDb> punchlistDbs = mPunchListRepository.getFilterPunchList(mProjectId, mPunchListStatus, mPunchlistAssignee, searchDrawingEditText.getText().toString().trim(),linkExisting);
                if (mPunchlistDbs != null) {
                    mPunchlistDbs.clear();
                } else {
                    mPunchlistDbs = new ArrayList<>();
                }
                mPunchlistDbs.addAll(punchlistDbs);
                mPunchListAdapter = new PunchListAdapter(context, mPunchlistDbs, linkExisting, punchlistRecyclerView, mPunchListRepository);
//                DividerItemDecoration itemDecor = new DividerItemDecoration(getApplicationContext(), VERTICAL);
//                punchlistRecyclerView.addItemDecoration(itemDecor);
                punchlistRecyclerView.setAdapter(mPunchListAdapter);

                if (mPunchlistDbs == null || mPunchlistDbs.size() <= 0) {
                    if (mPunchListStatus == PunchListStatus.Open){

                        noRecordTextView.setText(R.string.message_no_open_punch_list_items_are_available);
                    }else if (mPunchListStatus == PunchListStatus.Complete || (mPunchlistAssignee!=null && mPunchlistAssignee.getUsersId() != -1)){
                        noRecordTextView.setText("There are no punch list items for this filter criteria.");
                    }
                    else{
                        noRecordTextView.setText("No punch list items available.");
                    }
                    noRecordTextView.setVisibility(View.VISIBLE);
                } else {
                    noRecordTextView.setVisibility(View.GONE);
                }
                ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
                callPunchListHistoryAPI(projectId);
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(context).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(context).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {

            }
        });

    }

    private void callPunchListHistoryAPI(int projectId) {
        PunchListHistoryRequest punchListHistoryRequest = new PunchListHistoryRequest();
        punchListHistoryRequest.setProjectId(projectId);
        punchListHistoryRequest.setPunchListHistories(new ArrayList<>());

        mPunchListProvider.getPunchListHistories(punchListHistoryRequest, new ProviderResult<List<PunchListHistoryDb>>() {
            @Override
            public void success(List<PunchListHistoryDb> result) {

                if(result != null)
                    punchListHistoryDbs.addAll(result);
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(context).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(context).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {

            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPunchListAdapter.notifyDataSetChanged();
            }
        },500);*/
        if (mPunchListAdapter != null) {
            mPunchListAdapter.hidePopUp();
            mPunchListAdapter.notifyDataSetChanged();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Subscribe
    public void onAddPunchList(PunchlistDb punchlistDb) {
        updatePunchListDb(punchlistDb);
    }

    public void updatePunchListDb(PunchlistDb details) {

        if (details.getDeletedAt() != null) {
            mPunchListRepository.updatePunchListDbForDelete(details);
        }
        updatePunchList();
    }

    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (!linkExisting) {
            super.onBackPressed();
        } else {
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @OnClick(R.id.seachClearImageView)
    public void onSeachClearClick() {
        searchDrawingEditText.setText("");
    }

    @OnClick(R.id.addImageView)
    public void onAddClick() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        punchlistFragment = new PunchlistFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", mProjectId);
        Log.d(TAG, "onAddClick: "+ mProjectId);
        punchlistFragment.setArguments(bundle);
        ft.replace(R.id.punchlistContainer, punchlistFragment, punchlistFragment.getClass().getSimpleName()).addToBackStack(PunchlistFragment.class.getName());
        ft.commit();
    }

    @OnClick(R.id.rightImageView)
    public void onFilterClick() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        PunchListFilterDialog filterDialog = new PunchListFilterDialog(mPunchListStatus, mPunchlistAssignee,linkExisting);
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", mProjectId);
        filterDialog.setCancelable(false);
        filterDialog.setArguments(bundle);
        filterDialog.show(ft, "");
    }

    @Subscribe
    public void filterPunchList(PunchListFilterEvent punchListFilterEvent) {
        mPunchListStatus = punchListFilterEvent.getPunchListStatus();
        mPunchlistAssignee = punchListFilterEvent.getPunchlistAssignee();
        int filterCount = 0;
        if (mPunchListStatus != PunchListStatus.All) {
            filterCount = filterCount + 1;
        }
        if (mPunchlistAssignee.getUsersId() != -1) {
            filterCount = filterCount + 1;
        }
        if (filterCount > 0) {
            filterTextView.setText(String.valueOf(filterCount));
            filterTextView.setVisibility(View.VISIBLE);
            rightImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_filter_regions));
        } else {
            filterTextView.setVisibility(View.GONE);
            rightImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_filter_regions));
        }

        List<PunchlistDb> punchList = mPunchListRepository.getFilterPunchList(mProjectId, mPunchListStatus, mPunchlistAssignee, searchDrawingEditText.getText().toString().trim(),linkExisting);
        mPunchlistDbs.clear();
        mPunchlistDbs.addAll(punchList);
//        mPunchListAdapter = new PunchListAdapter(PunchListActivity.this, punchList);
//        punchlistRecyclerView.setLayoutManager(new LinearLayoutManager(PunchListActivity.this));
//        punchlistRecyclerView.setAdapter(mPunchListAdapter);
        mPunchListAdapter.notifyDataSetChanged();
        if (punchList.size() <= 0) {
            if (mPunchListStatus == PunchListStatus.Open){

                noRecordTextView.setText("No open punch list items are available.");
            }else if (mPunchListStatus == PunchListStatus.Complete || (mPunchlistAssignee!=null && mPunchlistAssignee.getUsersId() != -1)){
                noRecordTextView.setText("There are no punch list items for this filter criteria.");

            }
            else{
                noRecordTextView.setText("No punch list items available.");
            }
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            noRecordTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "punchlist onResume: ");
        mPunchlistDbs = mPunchListRepository.getFilterPunchList(mProjectId, mPunchListStatus, mPunchlistAssignee, searchDrawingEditText.getText().toString().trim(),linkExisting);
        mPunchListAdapter = new PunchListAdapter(PunchListActivity.this, mPunchlistDbs, linkExisting, punchlistRecyclerView, mPunchListRepository);
        punchlistRecyclerView.setLayoutManager(new LinearLayoutManager(PunchListActivity.this));
        punchlistRecyclerView.setAdapter(mPunchListAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && punchlistFragment != null) {
            punchlistFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onItemClick(int position) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        int canEditPunchList = loginResponse.getUserDetails().getPermissions().get(0).getEditPunchList();
        if (!linkExisting && position >= 0 /*&& canEditPunchList == 1*/) {
            PunchlistDb punchlist = mPunchlistDbs.get(position);
            FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            punchlistFragment = new PunchlistFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("projectId", punchlist.getPjProjectsId());
            bundle.putLong("punchListMobileId", punchlist.getPunchlistIdMobile());
//            punchListDialog.setCancelable(false);
            punchlistFragment.setArguments(bundle);
//            punchListDialog.show(ft, "");
            ft.replace(R.id.punchlistContainer, punchlistFragment, punchlistFragment.getClass().getSimpleName())
                    .addToBackStack(PunchlistFragment.class.getName());
            ft.commit();
        } else if (position >= 0 && linkExisting) {
            mAddPunchList = new AddPunchList();
            mPunchListRepository.addDrawingPunchlist(mPunchlistDbs.get(position).getPunchlistId(),mPunchlistDbs.get(position).getPunchlistIdMobile(),0l,mProjectId,loginResponse.getUserDetails().getUsers_id());

            String str = "punch_id = " + mPunchlistDbs.get(position).getPunchlistId() +
                    ", punch_id_mobile = " + mPunchlistDbs.get(position).getPunchlistIdMobile() +
                    ", punch_status = " + mPunchlistDbs.get(position).getStatus() +
                    ", punch_number = " + mPunchlistDbs.get(position).getItemNumber() +
//                    ", title = " + mPunchlistDbs.get(position).getCreatedBy();
                    ", title = " + loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname();
            mAddPunchList.setPunchNumber( mPunchlistDbs.get(position).getItemNumber());
            mAddPunchList.setStatus( mPunchlistDbs.get(position).getStatus());
            mAddPunchList.setContent(str);
            mPunchListRepository.addPunchlistDrawing(mPunchlistDbs.get(position),getIntent().getParcelableExtra("drawing_details"));

            EventBus.getDefault().post(mAddPunchList);
            this.finish();

        } else {
            mPunchListAdapter.notifyDataSetChanged();
        }

//        punchListDialog.isOffline(offlineTextView.getVisibility() == VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (punchlistFragment != null) {
            punchlistFragment.isOffline(event);
        }
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TransactionLogUpdate transactionLogUpdate) {
        Log.d("Manya", "onEvent: conebar: "+ transactionLogUpdate.getTransactionModuleEnum());
        if (transactionLogUpdate.getTransactionModuleEnum() != null
                && (transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.PUNCHLIST)
                || transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.PUNCHLIST_REJECT_REASON_ATTACHMENT))) {
            Log.d("MANya", "onEvent: if: "+ transactionLogUpdate.getTransactionModuleEnum());
            updatePunchList();
        }else if(transactionLogUpdate.getTransactionModuleEnum() != null && transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.PUNCHLIST_HISTORY) ){
            Log.d("Manya", "onEvent: conebar else: "+ transactionLogUpdate.getTransactionModuleEnum());
            updatePunchList(); //TODO: Need to check punch list history update
        } else if (transactionLogUpdate.getTransactionModuleEnum() != null
                && transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.PUNCHLIST_ATTACHMENT)) {
            ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
        }
    }

    private void updatePunchList() {
        List<PunchlistDb> punchList = mPunchListRepository.getFilterPunchList(mProjectId, mPunchListStatus, mPunchlistAssignee, searchDrawingEditText.getText().toString().trim(),linkExisting);
        Log.d("Manya", "updatePunchList: "+ punchList.size());
        mPunchlistDbs.clear();
        mPunchlistDbs.addAll(punchList);

        if (punchList.size() <= 0) {
            if (mPunchListStatus == PunchListStatus.Open){

                noRecordTextView.setText("No open punch list items are available.");
            }else if (mPunchListStatus == PunchListStatus.Complete || (mPunchlistAssignee!=null && mPunchlistAssignee.getUsersId() != -1)){
                noRecordTextView.setText("There are no punch list items for this filter criteria.");

            }
            else{
                noRecordTextView.setText("No punch list items available.");
            }            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            noRecordTextView.setVisibility(View.GONE);
        }
        mPunchListAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
//                if (punchlistFragment == null) {
//                    hideKeyboard(this);
//                } else if (!punchlistFragment.locationEditText.hasFocus() && !punchlistFragment.punchListItemDescriptionEditText.hasFocus()) {
                hideKeyboard(this);
//                }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void reCompleteCallback(PunchlistDb punchlist) {
        Log.d(TAG, "reCompleteCallback: ");
        PunchlistDb  punchlistDb  = punchlist;
        punchlistDb.setStatus(PunchListStatus.Complete.getValue());
        punchlistDb.setIsSync(false);
        if(punchlistDb.getComments() == null) {
            punchlistDb.setComments("");
        }
        mPunchListProvider.mPunchListRepository.updatePunchListDb(punchlistDb, "", new ArrayList<>());
        updatePunchList();
    }

    @Override
    public void markCompleteCallback(PunchlistDb punchlist) {
        Log.d(TAG, "markCompleteCallback: ");
        PunchlistDb  punchlistDb  = punchlist;
        punchlistDb.setStatus(PunchListStatus.Complete.getValue());
        punchlistDb.setIsSync(false);
        if(punchlistDb.getComments() == null) {
            punchlistDb.setComments("");
        }
        mPunchListProvider.mPunchListRepository.updatePunchListDb(punchlistDb, "", new ArrayList<>());
        updatePunchList();
    }

    @Override
    public void approvedCallback(PunchlistDb punchlistDb) {
        Log.d(TAG, "approvedCallback: ");
        PunchlistDb  punchlist  = punchlistDb;
        punchlist.setStatus(PunchListStatus.Approved.getValue());
        punchlist.setIsSync(false);
        if(punchlist.getComments() == null) {
            punchlist.setComments("");
        }
        mPunchListProvider.mPunchListRepository.updatePunchListDb(punchlist, "", new ArrayList<>());
        updatePunchList();
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        updatePunchList();
    }
}
