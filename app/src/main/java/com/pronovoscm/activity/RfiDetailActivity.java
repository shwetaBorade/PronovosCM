package com.pronovoscm.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.adapter.RfiDetailsAttachmentAdapter;
import com.pronovoscm.adapter.RfiRepliesAdapter;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.RFIStatusEnum;
import com.pronovoscm.model.response.rfi.attachment.RfiAttachmentResponse;
import com.pronovoscm.model.response.rfi.replies.RfiRepliesResponse;
import com.pronovoscm.persistence.domain.PjRfi;
import com.pronovoscm.persistence.domain.PjRfiAttachments;
import com.pronovoscm.persistence.domain.PjRfiContactList;
import com.pronovoscm.persistence.domain.PjRfiReplies;
import com.pronovoscm.persistence.repository.ProjectRfiRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.MyHtmlTagHandler;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RfiDetailActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;

    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.containerLayout)
    LinearLayout containerLayout;

    // rfi information
    @BindView(R.id.tvRfiTitleValue)
    TextView tvRfiTitleValue;
    @BindView(R.id.tvRfiStatusValue)
    TextView tvRfiStatusValue;
    @BindView(R.id.tvRfiReferenceValue)
    TextView tvRfiReferenceValue;
    @BindView(R.id.tvRfiReferenceSpecValue)
    TextView tvRfiReferenceSpecValue;
    @BindView(R.id.tvRfiScheduleImpactValue)
    TextView tvRfiScheduleImpactValue;
    @BindView(R.id.tvRfiCostImpactValue)
    TextView tvRfiCostImpactValue;
//rfi_responsible

    @BindView(R.id.tvRfiAuthorValue)
    TextView tvRfiAuthorValue;
    @BindView(R.id.tvRfiReceivedFromValue)
    TextView tvRfiReceivedFromValue;
    @BindView(R.id.tvRfiAssignToValue)
    TextView tvRfiAssignToValue;
    @BindView(R.id.tvRfiCcValue)
    TextView tvRfiCcValue;
    // rfi date requirement
    @BindView(R.id.tvRfiDateSubmittedValue)
    TextView tvRfiDateSubmittedValue;
    @BindView(R.id.tvRfiReceivedDateValue)
    TextView tvRfiReceivedDateValue;
    @BindView(R.id.tvRfiResponseReqValue)
    TextView tvRfiResponseReqValue;
    @BindView(R.id.tvRfiResponseDateDueValue)
    TextView tvRfiResponseDateDueValue;
    @BindView(R.id.tvRfiAttachmentLabel)
    TextView tvRfiAttachmentLabel;
    //rfi detail card
    @BindView(R.id.tvRfiQuestionValue)
    TextView tvRfiQuestionValue;
    @BindView(R.id.rfiDetailAttachmentsRecyclerView)
    RecyclerView rfiDetailAttachmentsRecyclerView;
    @BindView(R.id.rfiRepliesRecyclerView)
    RecyclerView rfiRepliesRecyclerView;

    @BindView(R.id.projectRfiDetailCardView)
    CardView projectRfiDetailCardView;
    @BindView(R.id.projectRfiReplyCardView)
    CardView projectRfiReplyCardView;

    @BindView(R.id.rfiDetailView)
    ScrollView rfiDetailView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @Inject
    ProjectsProvider projectsProvider;
    @Inject
    ProjectRfiRepository projectRfiRepository;
    private int projectId;
    private PjRfi pjRfi;
    private int rfiId;
    private boolean isOffline;
    private String titleNumber = "RFI # ";
    private PjRfiContactList pjRfiAssignToContact;
    private RfiDetailsAttachmentAdapter rfiDetailsAttachmentAdapter;
    private boolean isRfiAttachmentApiDone;
    private boolean isRfiReplyApiDone;

    @Override
    protected int doGetContentView() {
        return R.layout.activity_rfi_detail;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        rfiDetailView.setVisibility(View.GONE);
        projectId = getIntent().getIntExtra(Constants.INTENT_KEY_PROJECT_ID, 0);
        rfiId = getIntent().getIntExtra(Constants.INTENT_KEY_PROJECT_RFI_ID, 0);
        pjRfi = (PjRfi) getIntent().getSerializableExtra(Constants.INTENT_KEY_PROJECT_RFI);
        pjRfiAssignToContact = (PjRfiContactList) getIntent().getSerializableExtra(Constants.INTENT_KEY_PROJECT_RFI_CONTACT);
        backImageView.setOnClickListener(this);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        callRfiAttachmentApi(true);
        callRfiRepliesApi(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        doGetApplication().getDaggerComponent().inject(this);
        //projectName = getIntent().getStringExtra(Constants.INTENT_KEY_PROJECT_NAME);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateUi(boolean update) {
        if (update && pjRfi != null) {
            rfiDetailView.setVisibility(View.VISIBLE);
            titleNumber = "RFI # " + pjRfi.getRfiNumber();
            titleTextView.setText(titleNumber);
            updateRfiInfoDetails();
            updateRfiResponsiblePersonalDetail();
            updateDateReqCard();
            updateRfiDetailCard();
            updateReplyCard();
        }
    }

    private void updateDateReqCard() {
        if (pjRfi.getDateSubmitted() != null)
            tvRfiDateSubmittedValue.setText(DateFormatter.formatDateForPunchList(pjRfi.getDateSubmitted()));
        else tvRfiDateSubmittedValue.setText("-");

        if (pjRfi.getReceivedDate() != null) {
            tvRfiReceivedDateValue.setText(DateFormatter.formatDateForPunchList(pjRfi.getReceivedDate()));
        } else {
            tvRfiReceivedDateValue.setText("-");
        }
        tvRfiResponseReqValue.setText("" + pjRfi.responseDays);
        if (pjRfi.dueDate != null)
            tvRfiResponseDateDueValue.setText("" + DateFormatter.formatDateForPunchList(pjRfi.dueDate));
    }

    private void updateRfiResponsiblePersonalDetail() {

        if (TextUtils.isEmpty(pjRfi.getAuthorName()))
            tvRfiAuthorValue.setText("-");
        else
            tvRfiAuthorValue.setText(pjRfi.getAuthorName());

        if (TextUtils.isEmpty(pjRfi.getReceivedFrom()))
            tvRfiReceivedFromValue.setText("-");
        else {

            List<PjRfiContactList> receiverList = projectRfiRepository.getSearchPjRfiReceivedFromContact(pjRfi);
            if (receiverList.size() > 0) {
                String name = receiverList.get(0).getName();
                if (!TextUtils.isEmpty(receiverList.get(0).getEmail())) {
                    name = name + " (" + receiverList.get(0).getEmail() + ")";
                }
                tvRfiReceivedFromValue.setText(name);
            } else

                tvRfiReceivedFromValue.setText("-");
        }
        if (pjRfiAssignToContact != null) {
            String assigntovalue = pjRfiAssignToContact.getName();
            if (!TextUtils.isEmpty(pjRfiAssignToContact.getEmail())) {
                assigntovalue = assigntovalue + " (" + pjRfiAssignToContact.getEmail() + ") ";
            }
            tvRfiAssignToValue.setText(assigntovalue);
            if (TextUtils.isEmpty(pjRfiAssignToContact.getName())) {
                tvRfiAssignToValue.setText("-");
            }
        } else {
            tvRfiAssignToValue.setText("-");
        }


        String ccList = "";
        List<PjRfiContactList> pjRfiContactCcLists = projectRfiRepository.getSearchPjRfiCcContact(pjRfi);
        if (pjRfiContactCcLists != null)
            for (PjRfiContactList cc : pjRfiContactCcLists) {
                if (TextUtils.isEmpty(ccList)) {
                    ccList = cc.getName() + " (" + cc.getEmail() + ")";
                } else
                    ccList = ccList + "\n" + cc.getName() + " (" + cc.getEmail() + ")";
            }
        if (!TextUtils.isEmpty(ccList))
            tvRfiCcValue.setText(ccList);
        else {
            tvRfiCcValue.setText("-");
        }
    }

    public String customizeListTags(@Nullable String html) {
        if (html == null) {
            return null;
        }
        String UL = "CUSTOM_UL";
        String OL = "CUSTOM_OL";
        String LI = "CUSTOM_LI";
        String DD = "CUSTOM_DD";

        html = html.replace("<ul", "<" + UL);
        html = html.replace("</ul>", "</" + UL + ">");
        html = html.replace("<ol", "<" + OL);
        html = html.replace("</ol>", "</" + OL + ">");
        html = html.replace("<dd", "<" + DD);
        html = html.replace("</dd>", "</" + DD + ">");
        html = html.replace("<li", "<" + LI);
        html = html.replace("</li>", "</" + LI + ">");
        return html;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateRfiDetailCard() {
        if (!TextUtils.isEmpty(pjRfi.getQuestion())) {
            Log.d("DetailAct", "updateRfiDetailCard: " + pjRfi.getQuestion());
            //  tvRfiQuestionValue.setText(Html.fromHtml(pjRfi.getQuestion(), HtmlCompat.FROM_HTML_MODE_LEGACY ));
            tvRfiQuestionValue.setText(Html.fromHtml(customizeListTags(pjRfi.getQuestion()), HtmlCompat.FROM_HTML_MODE_LEGACY, null, new MyHtmlTagHandler()));
            // tvRfiQuestionValue.setText(Html.fromHtml(pjRfi.getQuestion(), null, new MyHtmlTagHandler()));
        } else {
            tvRfiQuestionValue.setText("-");
        }
        List<PjRfiAttachments> pjRfiAttachmentsList = projectRfiRepository.getRfiAttachmentsList(pjRfi);
        if (TextUtils.isEmpty(pjRfi.getQuestion()) && (pjRfiAttachmentsList == null || pjRfiAttachmentsList.size() == 0)) {
            projectRfiDetailCardView.setVisibility(View.GONE);
        }
        int myInteger = getResources().getInteger(R.integer.quantity_length);
        Log.i("PunchlistFragment", "onConfigurationChanged: " + myInteger);
        if (pjRfiAttachmentsList == null || pjRfiAttachmentsList.size() == 0) {
            tvRfiAttachmentLabel.setVisibility(View.GONE);
        } else {
            rfiDetailsAttachmentAdapter = new RfiDetailsAttachmentAdapter(RfiDetailActivity.this, pjRfiAttachmentsList, isOffline, projectRfiRepository);
            rfiDetailAttachmentsRecyclerView.setLayoutManager(new GridLayoutManager(rfiDetailAttachmentsRecyclerView.getContext(), myInteger));
            rfiDetailAttachmentsRecyclerView.setAdapter(rfiDetailsAttachmentAdapter);
        }
        // rfiDetailsAttachmentAdapter = new RfiDetailsAttachmentAdapter(this,)
    }

    private void updateReplyCard() {
        List<PjRfiReplies> pjRfiRepliesList = projectRfiRepository.getRfiReplyList(pjRfi.getPjRfiId());
        if (pjRfiRepliesList == null || pjRfiRepliesList.size() == 0)
            projectRfiReplyCardView.setVisibility(View.GONE);
        else {
            projectRfiReplyCardView.setVisibility(View.VISIBLE);
            RfiRepliesAdapter rfiRepliesAdapter = new RfiRepliesAdapter(RfiDetailActivity.this,
                    pjRfiRepliesList, isOffline, projectRfiRepository, pjRfi);
            rfiRepliesRecyclerView.setLayoutManager(new LinearLayoutManager(RfiDetailActivity.this));
            rfiRepliesRecyclerView.setAdapter(rfiRepliesAdapter);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void callRfiAttachmentApi(boolean showProgress) {
        if (NetworkService.isNetworkAvailable(RfiDetailActivity.this)) {
            if (showProgress)
                CustomProgressBar.showDialog(RfiDetailActivity.this);
            projectsProvider.getProjectRfiAttachments(rfiId, projectId, new ProviderResult<RfiAttachmentResponse>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void success(RfiAttachmentResponse result) {
                    Log.d("RfiAttachmentResponse", "callRfiAttachment  Api success: ");
                    if (isRfiReplyApiDone)
                        CustomProgressBar.dissMissDialog(RfiDetailActivity.this);
                    isRfiAttachmentApiDone = true;
                    updateUi(isRfiAttachmentApiDone && isRfiReplyApiDone);
                    //      loadRfiList(isRfiContactListApiDone&& isRfiApiDone);
                }

                @Override
                public void AccessTokenFailure(String message) {
                    CustomProgressBar.dissMissDialog(RfiDetailActivity.this);
                    noRecordTextView.setText("");
                    // isLoading = false;
                    handleAccessTokenFails();
                }

                @Override
                public void failure(String message) {
                    CustomProgressBar.dissMissDialog(RfiDetailActivity.this);
                    noRecordTextView.setText("");
                    updateUi(true);
                    // loadRfiList(true);
                }
            });

        } else {
            CustomProgressBar.dissMissDialog(RfiDetailActivity.this);
            updateUi(true);
            //  loadRfiList(true);
            //TODO show records form DB here
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void callRfiRepliesApi(boolean showProgress) {
        if (NetworkService.isNetworkAvailable(RfiDetailActivity.this)) {
            if (showProgress)
                CustomProgressBar.showDialog(RfiDetailActivity.this);
            projectsProvider.getProjectRfiReplies(rfiId, new ProviderResult<RfiRepliesResponse>() {
                @Override
                public void success(RfiRepliesResponse result) {
                    Log.d("callRfiListApi", "callRfiListApi success: ");
                    if (isRfiAttachmentApiDone)
                        CustomProgressBar.dissMissDialog(RfiDetailActivity.this);
                    isRfiReplyApiDone = true;
                    updateUi(isRfiAttachmentApiDone && isRfiReplyApiDone);
                }

                @Override
                public void AccessTokenFailure(String message) {
                    CustomProgressBar.dissMissDialog(RfiDetailActivity.this);
                    noRecordTextView.setText("");
                    // isLoading = false;
                    handleAccessTokenFails();
                }

                @Override
                public void failure(String message) {
                    CustomProgressBar.dissMissDialog(RfiDetailActivity.this);
                    noRecordTextView.setText("");
                    updateUi(true);
                    // loadRfiList(true);

                }
            });
        } else {
            CustomProgressBar.dissMissDialog(RfiDetailActivity.this);
            updateUi(true);
            //  loadRfiList(true);
            //TODO show records form DB here
        }
    }

    private void handleAccessTokenFails() {
        startActivity(new Intent(RfiDetailActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        SharedPref.getInstance(RfiDetailActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
        SharedPref.getInstance(RfiDetailActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
        finish();
    }

    private void updateRfiInfoDetails() {
        tvRfiTitleValue.setText(pjRfi.getRfiTitle());

        if (pjRfi.getStatus() == 0) {
            tvRfiStatusValue.setText(RFIStatusEnum.Draft.getStatusString());
        } else if (pjRfi.getStatus() == 1) {
            tvRfiStatusValue.setText(RFIStatusEnum.Open.getStatusString());
        } else {
            tvRfiStatusValue.setText(RFIStatusEnum.Closed.getStatusString());
        }
        /*
        Schedule Impact and Cost Impact
        {  "TBD":0,
	        "Yes":1,
        	"No":2     }
         */
        // tvRfiStatusValue.setText(pjRfi.getAuthorName());
        if (TextUtils.isEmpty(pjRfi.getRefDrawingNumber()))
            tvRfiReferenceValue.setText("-");
        else
            tvRfiReferenceValue.setText(String.format("%s", pjRfi.getRefDrawingNumber()));
        if (TextUtils.isEmpty(pjRfi.getRefSpecification()))
            tvRfiReferenceSpecValue.setText("-");
        else
            tvRfiReferenceSpecValue.setText(String.format("%s", pjRfi.getRefSpecification()));
        if (pjRfi.getScheduleImpactDays() != null && pjRfi.getScheduleImpactDays() == 0)
            tvRfiScheduleImpactValue.setText(R.string.tbd);
        else if (pjRfi.getScheduleImpactDays() != null && pjRfi.getScheduleImpactDays() == 1)
            tvRfiScheduleImpactValue.setText(R.string.yes);
        else if (pjRfi.getScheduleImpactDays() != null && pjRfi.getScheduleImpactDays() == 2)
            tvRfiScheduleImpactValue.setText(R.string.no);
        else {
            tvRfiScheduleImpactValue.setText(R.string.tbd);
        }
        if (pjRfi.getCostImpact() != null) {
            if (pjRfi.getCostImpact() == 1)
                tvRfiCostImpactValue.setText(R.string.yes);
            else if (pjRfi.getCostImpact() == 0)
                tvRfiCostImpactValue.setText(R.string.tbd);
            else if (pjRfi.getCostImpact() == 2)
                tvRfiCostImpactValue.setText(R.string.no);
        } else
            tvRfiCostImpactValue.setText(R.string.tbd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftImageView:
                onBackPressed();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            isOffline = true;
            offlineTextView.setVisibility(View.VISIBLE);
            noRecordTextView.setText(getString(R.string.rfi_detail_offline_message));
            noRecordTextView.setTextColor(ContextCompat.getColor(RfiDetailActivity.this, R.color.red_ff2424));
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            isOffline = false;
            offlineTextView.setVisibility(View.GONE);
            noRecordTextView.setVisibility(View.GONE);
            callRfiAttachmentApi(true);
            callRfiRepliesApi(true);
        }

    }
}
