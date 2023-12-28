package com.pronovoscm.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pronovoscm.R;
import com.pronovoscm.adapter.SubmittalsContactListAdapter;
import com.pronovoscm.adapter.SubmittalsDetailsAttachmentAdapter;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.model.SubmittalAssigneeEnum;
import com.pronovoscm.model.SubmittalStatusEnum;
import com.pronovoscm.persistence.domain.PjSubmittalAttachments;
import com.pronovoscm.persistence.domain.PjSubmittalContactList;
import com.pronovoscm.persistence.domain.PjSubmittals;
import com.pronovoscm.persistence.repository.ProjectSubmittalsRepository;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SubmittalDetailActivity extends BaseActivity implements View.OnClickListener {
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
    @BindView(R.id.tvSubmittalNumber)
    TextView tvSubmittalNumber;
    @BindView(R.id.tvRevisionNumber)
    TextView tvRevisionNumber;
    @BindView(R.id.tVTitle)
    TextView tVTitle;
    @BindView(R.id.tvSubmittalStatus)
    TextView tvSubmittalStatus;
    @BindView(R.id.tVBallInCourt)
    TextView tVBallInCourt;
    @BindView(R.id.tVSpecSection)
    TextView tVSpecSection;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvType)
    TextView tvType;
    @BindView(R.id.tvAuthor)
    TextView tvAuthor;
    @BindView(R.id.tvRecFrom)
    TextView tvRecFrom;


    //date req

    @BindView(R.id.tvReceived)
    TextView tvReceived;
    @BindView(R.id.tvDue)
    TextView tvDue;
    @BindView(R.id.tvOnsite)
    TextView tvOnsite;
    @BindView(R.id.tvLead)
    TextView tvLead;


    // current response
    @BindView(R.id.tvCurrentStatus)
    TextView tvCurrentStatus;
    @BindView(R.id.tvCurrentStatusValue)
    TextView tvCurrentStatusValue;
    @BindView(R.id.ivCurrentStatus)
    ImageView ivCurrentStatus;

    // sent card
    @BindView(R.id.ivSubmittalSentStatus)
    ImageView ivSubmittalSentStatus;
    @BindView(R.id.tvSubmittalSentStatus)
    TextView tvSubmittalSentStatus;
    @BindView(R.id.statusContainer)
    View statusContainer;

    // detail rv
    @BindView(R.id.submittalDetailAttachmentsRv)
    RecyclerView submittalDetailAttachmentsRv;
    @BindView(R.id.contactListRv)
    RecyclerView contactListRv;

    @BindView(R.id.tvDetailText)
    TextView tvDetailText;

    // distribution count
    @BindView(R.id.tvDistributionCount)
    TextView tvDistributionCount;

    //Cc
    @BindView(R.id.tvCc)
    TextView tvCc;

    @BindView(R.id.submittalDetailView)
    NestedScrollView submittalDetailView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @Inject
    ProjectsProvider projectsProvider;
    @Inject
    ProjectSubmittalsRepository projectSubmittalsRepository;
    int projectId;
    private PjSubmittals pjSubmittals;
    int submittalId;
    private boolean isOffline;
    String titleNumber = "Submittal # ";
    SubmittalsDetailsAttachmentAdapter submittalsDetailsAttachmentAdapter;
    SubmittalsContactListAdapter submittalsContactListAdapter;

    @Override
    protected int doGetContentView() {
        return R.layout.activity_submittal_detail;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        submittalDetailView.setVisibility(View.GONE);
        projectId = getIntent().getIntExtra(Constants.INTENT_KEY_PROJECT_ID, 0);
        submittalId = getIntent().getIntExtra(Constants.INTENT_KEY_PROJECT_SUBMITTALS_ID, 0);
        pjSubmittals = (PjSubmittals) getIntent().getSerializableExtra(Constants.INTENT_KEY_PROJECT_SUBMITTALS);
        backImageView.setOnClickListener(this);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        updateUi();

    }
    private void updateUi() {
        if (pjSubmittals != null) {
            submittalDetailView.setVisibility(View.VISIBLE);
            titleNumber = "Submittal #" + pjSubmittals.getSubmittalNumber();
            titleTextView.setText(titleNumber);
            updateDetail();
            updateDateReqCard();
            updateInfoCard();
            updateSentCardAndDistributionCount();
            updateAssigneeList();
            updateCurrentStatusCard();
            updateCcList();
        }
    }

    // update detail card
    private void updateDetail() {
        if (pjSubmittals.getDescription() != null && !pjSubmittals.getDescription().isEmpty()) {
            tvDetailText.setText(Html.fromHtml(Html.fromHtml(pjSubmittals.getDescription()).toString()));
        } else {
            tvDetailText.setText("-");
        }
        updateAttachmentList();
    }

    private void updateBallInCourt() {
        String ballInCourt = "";
        String[] ballInCourtArray = pjSubmittals.getBallInCourt().split("\\),");
        for (String text : ballInCourtArray) {
            if (TextUtils.isEmpty(ballInCourt)) {
                ballInCourt = text.trim();
            } else {
                ballInCourt = ballInCourt + "\n" + text.trim();
            }
        }
        if (!TextUtils.isEmpty(ballInCourt)) {
            tVBallInCourt.setText(ballInCourt);
        } else {
            tVBallInCourt.setText("-");
        }
    }

    private void updateCurrentStatusCard() {
        if (pjSubmittals.getCurrentResponseStatus() != null) {
            switch (pjSubmittals.getCurrentResponseStatus()) {
                case 0:
                    tvCurrentStatusValue.setText(SubmittalAssigneeEnum.NoResponse.getStatusString());
                    break;
                case 1:
                    tvCurrentStatusValue.setText(SubmittalAssigneeEnum.Approved.getStatusString());
                    break;
                case 2:
                    tvCurrentStatusValue.setText(SubmittalAssigneeEnum.Rejected.getStatusString());
                    break;
                case 3:
                    tvCurrentStatusValue.setText(SubmittalAssigneeEnum.ApprovedAsNoted.getStatusString());
                    break;
                case 4:
                    tvCurrentStatusValue.setText(SubmittalAssigneeEnum.Reviewed.getStatusString());
                    break;
                case 5:
                    tvCurrentStatusValue.setText(SubmittalAssigneeEnum.ReviewedAsNoted.getStatusString());
                    break;
                case 6:
                    tvCurrentStatusValue.setText(SubmittalAssigneeEnum.ReviseAndResubmit.getStatusString());
                    break;
                case 7:
                    tvCurrentStatusValue.setText(SubmittalAssigneeEnum.Other.getStatusString());
                    break;
            }

        }
    }

    private void updateSentCardAndDistributionCount() {
        if (pjSubmittals.getIsSubmittalSent() != null && pjSubmittals.getIsSubmittalSent() == 1 && pjSubmittals.getDateSent() != null) {
            ivSubmittalSentStatus.setImageResource(R.drawable.ic_digital_signature_valid);
            tvSubmittalSentStatus.setText(getString(R.string.sub_sent, DateFormatter.formatDateForSubmittals(pjSubmittals.getDateSent())));
        } else if (pjSubmittals.getIsSubmittalSent() != null && pjSubmittals.getIsSubmittalSent() == 1 && pjSubmittals.getDateSent() == null) {
            statusContainer.setVisibility(View.GONE);
        } else {
            ivSubmittalSentStatus.setImageResource(R.drawable.ic_email_not_sent_status);
            tvSubmittalSentStatus.setText(getString(R.string.sub_not_sent));
        }
        tvDistributionCount.setText(String.valueOf(projectSubmittalsRepository.getPjContactListSize(pjSubmittals)));
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{getExternalPermission()},
                    Constants.FILESTORAGE_REQUEST_CODE);
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults.length > 0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED && requestCode == Constants.FILESTORAGE_REQUEST_CODE) {
            init();
        }
    }
    private void updateDateReqCard() {
        if (pjSubmittals.getReceivedDate() != null) {
            tvReceived.setText(DateFormatter.formatDateForSubmittals(pjSubmittals.getReceivedDate()));
        } else {
            tvReceived.setText("-");
        }
        if (pjSubmittals.getDueDate() != null) {
            tvDue.setText(DateFormatter.formatDateForSubmittals(pjSubmittals.getDueDate()));
        } else {
            tvDue.setText("-");
        }
        if (pjSubmittals.getOnsiteDate() != null) {
            tvOnsite.setText(DateFormatter.formatDateForSubmittals(pjSubmittals.getOnsiteDate()));
        } else {
            tvOnsite.setText("-");
        }
        if (pjSubmittals.getLeadTime() != null) {
            tvLead.setText(pjSubmittals.getLeadTime());
        } else {
            tvLead.setText("-");
        }
    }

    public void updateCcList() {
        String ccList = "";
        List<PjSubmittalContactList> pjSubmittalContactLists = projectSubmittalsRepository.getSearchPjSubmittalCcContact(pjSubmittals);
        if (pjSubmittalContactLists != null)
            for (PjSubmittalContactList cc : pjSubmittalContactLists) {
                if (TextUtils.isEmpty(ccList)) {
                    ccList = cc.getContactName();
                } else
                    ccList = ccList + ", " + cc.getContactName();
            }
        if (!TextUtils.isEmpty(ccList))
            tvCc.setText(ccList);
        else {
            tvCc.setText("-");
        }
    }

    private void updateAttachmentList() {
        List<PjSubmittalAttachments> pjSubmittalAttachments = projectSubmittalsRepository.getPjSubmittalAttachments(pjSubmittals);
        int myInteger = getResources().getInteger(R.integer.quantity_length);
        if (pjSubmittalAttachments != null && pjSubmittalAttachments.size() > 0) {
            submittalDetailAttachmentsRv.setVisibility(View.VISIBLE);
            submittalsDetailsAttachmentAdapter = new SubmittalsDetailsAttachmentAdapter(SubmittalDetailActivity.this, pjSubmittalAttachments, isOffline, projectSubmittalsRepository);
            submittalDetailAttachmentsRv.setLayoutManager(new GridLayoutManager(submittalDetailAttachmentsRv.getContext(), myInteger));
            submittalDetailAttachmentsRv.setAdapter(submittalsDetailsAttachmentAdapter);
        }
    }

    private void updateAssigneeList() {
        List<PjSubmittalContactList> pjSubmittalContactLists = projectSubmittalsRepository.getPjSubmittalContactList(pjSubmittals);
        if (pjSubmittalContactLists == null || pjSubmittalContactLists.size() == 0) {
            contactListRv.setVisibility(View.GONE);
        } else {
            submittalsContactListAdapter = new SubmittalsContactListAdapter(SubmittalDetailActivity.this, pjSubmittalContactLists, projectSubmittalsRepository, isOffline);
            contactListRv.setLayoutManager(new LinearLayoutManager(contactListRv.getContext()));
            contactListRv.setAdapter(submittalsContactListAdapter);
        }
    }

    private void updateInfoCard() {
        if (pjSubmittals.getSubmittalStatus() == 0) {
            tvSubmittalStatus.setText(SubmittalStatusEnum.Draft.getStatusString());
        } else if (pjSubmittals.getSubmittalStatus() == 1) {
            tvSubmittalStatus.setText(SubmittalStatusEnum.Open.getStatusString());
        } else {
            tvSubmittalStatus.setText(SubmittalStatusEnum.Closed.getStatusString());
        }
        if (TextUtils.isEmpty(pjSubmittals.getSubmittalNumber())) {
            tvSubmittalNumber.setText("-");
        } else {
            tvSubmittalNumber.setText(pjSubmittals.getSubmittalNumber());
        }
        if (pjSubmittals.getRevision() == null) {
            tvRevisionNumber.setText("-");
        } else {
            tvRevisionNumber.setText(String.valueOf(pjSubmittals.getRevision()));
        }
        if (TextUtils.isEmpty(pjSubmittals.getSubmittalTitle())) {
            tVTitle.setText("-");
        } else {
            tVTitle.setText(pjSubmittals.getSubmittalTitle());
        }
        if (TextUtils.isEmpty(pjSubmittals.getBallInCourt())) {
            tVBallInCourt.setText("-");
        } else {
            if (pjSubmittals.getBallInCourt().contains(",")) {
                updateBallInCourt();
            } else {
                tVBallInCourt.setText(pjSubmittals.getBallInCourt());
            }
        }
        if (TextUtils.isEmpty(pjSubmittals.getSpecSection())) {
            tVSpecSection.setText("-");
        } else {
            tVSpecSection.setText(pjSubmittals.getSpecSection());
        }
        if (TextUtils.isEmpty(pjSubmittals.getLocation())) {
            tvLocation.setText("-");
        } else {
            tvLocation.setText(pjSubmittals.getLocation());
        }
        if (TextUtils.isEmpty(pjSubmittals.getSubmittalType())) {
            tvType.setText("-");
        } else {
            tvType.setText(pjSubmittals.getSubmittalType());
        }
        if (TextUtils.isEmpty(pjSubmittals.getSubmittalAuthorName())) {
            tvAuthor.setText("-");
        } else {
            tvAuthor.setText(pjSubmittals.getSubmittalAuthorName());
        }
        if (TextUtils.isEmpty(pjSubmittals.getReceivedFrom())) {
            tvRecFrom.setText("-");
        } else {
            tvRecFrom.setText(pjSubmittals.getReceivedFrom());
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.leftImageView) {
            onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            isOffline = true;
            offlineTextView.setVisibility(View.VISIBLE);
            noRecordTextView.setText(getString(R.string.submittal_detail_offline_message));
            noRecordTextView.setTextColor(ContextCompat.getColor(SubmittalDetailActivity.this, R.color.red_ff2424));
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            isOffline = false;
            offlineTextView.setVisibility(View.GONE);
            noRecordTextView.setVisibility(View.GONE);
        }
    }

}
