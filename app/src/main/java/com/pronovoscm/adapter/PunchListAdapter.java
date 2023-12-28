package com.pronovoscm.adapter;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.activity.PunchListActivity;
import com.pronovoscm.model.PunchListStatus;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.punchlist.Attachment;
import com.pronovoscm.persistence.domain.PunchListAttachments;
import com.pronovoscm.persistence.domain.PunchlistDb;
import com.pronovoscm.persistence.repository.PunchListRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.ui.punchlist.AssigneeNameDialog;
import com.pronovoscm.ui.punchlist.RejectPunchListDialog;
import com.pronovoscm.ui.punchlist.RejectReasonOnFragmentCallback;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 5/11/18.
 *
 * @author GWL
 */
public class PunchListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> implements RejectReasonOnFragmentCallback {

    private static final String TAG = PunchListAdapter.class.getSimpleName();

    private final Context context;
    private final List<PunchlistDb> mPunchlists;
    private final RecyclerView punchlistRecyclerView;
    private LoginResponse loginResponse;
    private PopupWindow mPopupWindow;
    private int canDeletePunchList;

    private PunchListRepository punchListRepository;

    private boolean isPermission = false;
    private boolean viewPermission = false;
    private boolean createPermission = false;
    private boolean editPermission = false;

    public PunchListAdapter(Context context, List<PunchlistDb> list, boolean linkExisting, RecyclerView punchlistRecyclerView, PunchListRepository mPunchListRepository) {
        this.context = context;
        this.mPunchlists = list;
        this.punchListRepository = mPunchListRepository;
        this.punchlistRecyclerView = punchlistRecyclerView;
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        canDeletePunchList = loginResponse.getUserDetails().getPermissions().get(0).getDeletePunchList();
        isPermission = (loginResponse.getUserDetails().getPermissions().get(0).getCreatePunchList() == 1 &&
                loginResponse.getUserDetails().getPermissions().get(0).getEditPunchList() == 1
                && loginResponse.getUserDetails().getPermissions().get(0).getViewPunchList() == 1) ? true : false;
        viewPermission = (loginResponse.getUserDetails().getPermissions().get(0).getViewPunchList() == 1);
        createPermission = (loginResponse.getUserDetails().getPermissions().get(0).getCreatePunchList() == 1);
        editPermission = (loginResponse.getUserDetails().getPermissions().get(0).getEditPunchList() == 1);

    }

    @Override
    public void rejectReasonCallback() {

    }

    public void hidePopUp() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_view_punch_list, parent, false);
        ButterKnife.bind(this, view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PunchlistDb punchlist = mPunchlists.get(holder.getBindingAdapterPosition());

        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).bind(context, punchlist, position);
        }
    }

    @Override
    public int getItemCount() {
        return mPunchlists.size();
    }


    public interface OnItemClickListener {
        void onItemClick(int position);

        void markCompleteCallback(PunchlistDb punchlist);

        void approvedCallback(PunchlistDb punchlistDb);

        void reCompleteCallback(PunchlistDb punchlist);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements RejectReasonOnFragmentCallback {
        @BindView(R.id.textViewCreatedBy)
        TextView textViewCreatedBy;
        @BindView(R.id.textViewDate)
        TextView textViewDate;
        @BindView(R.id.textViewStatus)
        TextView textViewStatus;
        @BindView(R.id.textViewDescription)
        TextView textViewDescription;
        @BindView(R.id.textViewId)
        TextView textViewId;
        @BindView(R.id.syncingImageView)
        ImageView syncingImageView;
        @BindView(R.id.punchListOptions)
        TextView textViewPunchOptions;
        @BindView(R.id.assignedCountId)
        TextView assignedCount;

        @BindView(R.id.punchId)
        TextView punchId;

        @BindView(R.id.markCompleteId)
        AppCompatButton markComplete;
        @BindView(R.id.approvedId)
        AppCompatButton approvedBtn;
        @BindView(R.id.afterApprovedId)
        AppCompatButton afterApprovedBtn;
        @BindView(R.id.rejectReasonBtnId)
        AppCompatButton rejectReasonBtn;
        @BindView(R.id.recompleteId)
        AppCompatButton reCompleteBtn;
        @BindView(R.id.punchListLocationId)
        TextView punchListLocation;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        /**
         * Created -> 1
         * Completed -> 2
         * Approved -> 3
         * Rejected -> 4
         *
         * @param context
         * @param punchlist1
         * @param position
         */

        void bind(Context context, PunchlistDb punchlist1, int position) {
            try {
                PunchlistDb punchlist = mPunchlists.get(getBindingAdapterPosition());

                itemView.setOnClickListener(v -> {
                    Log.e("TAG", " PunchListAdapter 130 setOnClickListener: ");
                    ((PunchListActivity) context).onItemClick(getBindingAdapterPosition());
                });

                textViewPunchOptions.setVisibility(View.GONE);
           /* if (isLinkExisting) {
            } else {
                if (canEditPunchList != 1 && canDeletePunchList != 1) {
                    textViewPunchOptions.setVisibility(View.GONE);
                } else {
                    textViewPunchOptions.setVisibility(View.VISIBLE);
                }

            }
           */
                textViewCreatedBy.setText(punchlist.getAssigneeName().get(0)); //TODO: Nitin

                if (punchlist.getAssigneeName().size() <= 1) {
                    assignedCount.setVisibility(View.GONE);
                } else {
                    assignedCount.setVisibility(View.VISIBLE);
                    assignedCount.setText("+" + (punchlist.getAssigneeName().size() - 1));
                }

                assignedCount.setOnClickListener(v -> {
                    FragmentActivity activity = (FragmentActivity) (context);
                    FragmentManager fm = activity.getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    AssigneeNameDialog assigneeNameDialog = new AssigneeNameDialog(punchlist.getAssigneeName());
                    assigneeNameDialog.show(ft, "");

                });

                if (punchlist.getPunchlistId() == 0) {
                    punchId.setText("#New");
                } else {
                    punchId.setText("#" + punchlist.getItemNumber());
                }
                punchListLocation.setText(punchlist.getLocation());

                reCompleteBtn.setOnClickListener(v -> {
                    reCompleteBtn.setClickable(false);
                    disableButtons();
                    ((PunchListActivity) context).reCompleteCallback(punchlist);
                });

                List<PunchListAttachments> attachments = punchListRepository.getNonSyncedAttachmentsForSpecificPunchlist(punchlist.getPunchlistIdMobile());

                Log.d("King", "bind: "+ attachments.size());
                markComplete.setOnClickListener(v -> {
                    /*if(NetworkService.isNetworkAvailable(context)
                            && punchlist.getPunchlistId() == 0){
                        Log.d("King", "bind: IF ");
                        if(attachments.size() > 0) {
                            Log.d("King", "bind: atch ");
                            markComplete.setClickable(false);
                            disableButtons();
                            ((PunchListActivity) context).markCompleteCallback(punchlist);
                        }
                    }else {
                        Log.d("King", "bind: else ");
                        markComplete.setClickable(false);
                        disableButtons();
                        ((PunchListActivity) context).markCompleteCallback(punchlist);
                    }*/
                    markComplete.setClickable(false);
                    disableButtons();
                    ((PunchListActivity) context).markCompleteCallback(punchlist);
                });


                approvedBtn.setOnClickListener(v -> {
                    approvedBtn.setClickable(false);
                    disableButtons();
                    ((PunchListActivity) context).approvedCallback(punchlist);
                });

                rejectReasonBtn.setOnClickListener(v -> {
//                    disableButtons();
                    FragmentActivity activity = (FragmentActivity) (context);
                    FragmentManager fm = activity.getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    RejectPunchListDialog rejectPunchListDialog = new RejectPunchListDialog(false, this, punchlist, punchListRepository);
                    rejectPunchListDialog.show(ft, "");
                });

                if (punchlist.getStatus() == PunchListStatus.Open.getValue()) {

                    if (isPermission || createPermission || editPermission || viewPermission) {
                        markComplete.setVisibility(View.VISIBLE);
                        reCompleteBtn.setVisibility(View.GONE);
                        approvedBtn.setVisibility(View.GONE);
                        rejectReasonBtn.setVisibility(View.GONE);
                        afterApprovedBtn.setVisibility(View.GONE);
                        if (viewPermission && !isPermission) {
//                            markComplete.setClickable(false);
                            markComplete.setVisibility(View.GONE);
                        }
                    }
                } else if (punchlist.getStatus() == PunchListStatus.Complete.getValue()) {
                    if (isPermission || createPermission || editPermission || viewPermission) {
                        markComplete.setVisibility(View.GONE);
                        reCompleteBtn.setVisibility(View.GONE);
                        approvedBtn.setVisibility(View.VISIBLE);
                        rejectReasonBtn.setVisibility(View.VISIBLE);
                        afterApprovedBtn.setVisibility(View.GONE);
                        if (viewPermission && !isPermission) {
//                            approvedBtn.setClickable(false);
                            approvedBtn.setVisibility(View.GONE);
//                            rejectReasonBtn.setClickable(false);
                            rejectReasonBtn.setVisibility(View.GONE);
                        }
                    }
                } else if (punchlist.getStatus() == PunchListStatus.Rejected.getValue()) {
                    if (isPermission || createPermission || editPermission || viewPermission) {
                        approvedBtn.setVisibility(View.GONE);
                        rejectReasonBtn.setVisibility(View.GONE);
                        markComplete.setVisibility(View.GONE);
                        reCompleteBtn.setVisibility(View.VISIBLE);
                        afterApprovedBtn.setVisibility(View.GONE);
                        if (viewPermission && !isPermission) {
//                            reCompleteBtn.setClickable(false);
                            reCompleteBtn.setVisibility(View.GONE);
                        }
                    }
                } else if (punchlist.getStatus() == PunchListStatus.Approved.getValue()) {
                    if (isPermission || viewPermission || createPermission || editPermission) {
                        markComplete.setVisibility(View.GONE);
                        rejectReasonBtn.setVisibility(View.GONE);
                        reCompleteBtn.setVisibility(View.GONE);
                        approvedBtn.setVisibility(View.GONE);
                        afterApprovedBtn.setVisibility(View.VISIBLE);
                        if(viewPermission && !isPermission ) {
                            afterApprovedBtn.setVisibility(View.GONE);
                        }
                    }
                }


                textViewDate.setText(DateFormatter.formatDateForPunchListLandingPage(punchlist.getDateDue()));

                textViewStatus.setText(punchlist.getStatus() + "");
                textViewDescription.setText(punchlist.getDescriptions());
                textViewId.setText(String.valueOf(punchlist.getItemNumber()));
                showStatus(context, punchlist.getStatus());
                if (punchlist.getIsSync()) {
                    textViewId.setText(String.valueOf(punchlist.getItemNumber()));
                    textViewId.setVisibility(View.VISIBLE);
                    syncingImageView.setVisibility(View.INVISIBLE);
                } else {
                    syncingImageView.setVisibility(View.VISIBLE);
                    textViewId.setVisibility(View.GONE);

                }

                hidePopUp();


                textViewPunchOptions.setOnClickListener(v -> {
                    punchlistRecyclerView.scrollToPosition(position);
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View customView = inflater.inflate(R.layout.punchlist_custom_popup_menu, null);

                    mPopupWindow = new PopupWindow(
                            customView,
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );

                    TextView editMenuOption = customView.findViewById(R.id.editTextView);
                    TextView deleteMenuOption = customView.findViewById(R.id.deleteTextView);
                    View dividerMenu = customView.findViewById(R.id.dividerMenu);
                    RelativeLayout popupView = customView.findViewById(R.id.popupView);
                    if (canDeletePunchList != 1) {
                        deleteMenuOption.setVisibility(View.GONE);
                        dividerMenu.setVisibility(View.GONE);

                        int _100sdp = (int) textViewPunchOptions.getContext().getResources().getDimension(R.dimen._90sdp);
                        int _50sdp = (int) textViewPunchOptions.getContext().getResources().getDimension(R.dimen._58sdp);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_100sdp, _50sdp);
                        popupView.setLayoutParams(layoutParams);
                    }
                    editMenuOption.setOnClickListener(v1 -> {
                        mPopupWindow.dismiss();
                        ((PunchListActivity) context).onItemClick(getAdapterPosition());
                    });

                    deleteMenuOption.setOnClickListener(v12 -> {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setMessage(context.getString(R.string.are_you_sure_you_want_to_delete_this_entry));
                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), (dialog, which) -> {
                            dialog.dismiss();
                            mPopupWindow.dismiss();
                        });
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), (dialog, which) -> {
                            alertDialog.dismiss();
                            punchlist.setDeletedAt(new Date());
                            punchlist.setIsSync(false);
                            ((PunchListActivity) context).updatePunchListDb(punchlist);
                            mPopupWindow.dismiss();
                        });
                        alertDialog.show();
                        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        nbutton.setTextColor(ContextCompat.getColor(context, R.color.gray_948d8d));
                        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

                    });

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int[] loc_int = new int[2];

                            try {
                                textViewPunchOptions.getLocationOnScreen(loc_int);
                            } catch (NullPointerException npe) {
                                //Happens when the view doesn't exist on screen anymore.

                            }
                            Rect location = new Rect();
                            location.left = loc_int[0];
                            location.top = loc_int[1];
                            location.right = location.left + v.getWidth();
                            location.bottom = location.top + v.getHeight();


                            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                            mPopupWindow.setOutsideTouchable(true);
                            mPopupWindow.setFocusable(true);
                            mPopupWindow.showAtLocation(textViewPunchOptions, Gravity.TOP | Gravity.RIGHT, 0, location.top + v.getHeight());
                        }
                    }, 100);

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void disableButtons() {
            approvedBtn.setClickable(false);
            markComplete.setClickable(false);
            reCompleteBtn.setClickable(false);
            rejectReasonBtn.setClickable(false);
        }

        private void showStatus(Context context, Integer status) {
            if (PunchListStatus.getStatus(status) == PunchListStatus.Open) {
                textViewStatus.setText(context.getString(R.string.open));
                textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), R.color.red_ff2424));
            } else {
                textViewStatus.setText(context.getString(R.string.complete));
                textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), R.color.green_complete));
            }
        }

        @Override
        public void rejectReasonCallback() {

        }
    }

    private void showButtons() {

    }

}
