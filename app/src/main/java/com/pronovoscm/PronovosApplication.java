package com.pronovoscm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.pronovoscm.broadcastreceivers.NetworkStateReceiver;
import com.pronovoscm.component.AppComponent;
import com.pronovoscm.component.DaggerAppComponent;
import com.pronovoscm.model.request.transferrequest.TransferRequest;
import com.pronovoscm.modules.ApplicationModule;
import com.pronovoscm.persistence.DataBaseHelper;
import com.pronovoscm.persistence.domain.DaoMaster;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.TransactionWorker;

import org.greenrobot.greendao.database.Database;


//import com.google.android.gms.common.util.SharedPreferencesUtils;
public class PronovosApplication extends MultiDexApplication implements LifecycleObserver {
    private static PronovosApplication pronovosApplication;
    private static PronovosApplication context;
    private static SharedPreferences mPrefrence;
    public NetworkStateReceiver networkStateReceiver;
    boolean onForegroundState = true;
    private String mUrl = Constants.BASE_API_URL;
    private AppComponent component;
    private DaoSession daoSession;
//    private ArrayList<PunchListEmailRequest> mPunchListEmailRequests = new ArrayList<>();
    private TransferRequest createTransfer;
//    private PDFViewCtrl mPDFViewCtrl;
//
//    public PDFViewCtrl getPDFViewCtrl() {
//        return mPDFViewCtrl;
//    }
//
//    public void setPDFViewCtrl(PDFViewCtrl PDFViewCtrl) {
//        mPDFViewCtrl = PDFViewCtrl;
//    }

    public static PronovosApplication getContext() {

        if (pronovosApplication == null) {
            pronovosApplication = (PronovosApplication) context.getApplicationContext();
        }
        return pronovosApplication;
    }

    public static SharedPreferences getSharedPreferences() {
        return mPrefrence;
    }

    private String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
    Database greenDaoDB;
    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        context = PronovosApplication.this;
        networkStateReceiver = new NetworkStateReceiver();
        registerReceiver(networkStateReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        mPrefrence = getApplicationContext().getSharedPreferences("pronovoscm", Context.MODE_PRIVATE);
        DataBaseHelper helper = new DataBaseHelper(this, "pronovos_cm");
        greenDaoDB = helper.getWritableDb();
        daoSession = new DaoMaster(greenDaoDB).newSession();

        setupGraph();
    }

    public Database getGreenDaoDB() {
        return greenDaoDB;
    }

    private void setupGraph() {
        component = DaggerAppComponent.builder().applicationModule(new ApplicationModule(PronovosApplication.this, getUrl())).build();
        component.inject(this);
    }

    public AppComponent getDaggerComponent() {
        return component;
    }

    /**
     * Gets the DaoSession for database operations.
     *
     * @return The DaoSession.
     */
    public DaoSession getDaoSession() {
        return daoSession;
    }




    public void setupAndStartWorkManager() {
        @SuppressLint("RestrictedApi")
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(TransactionWorker.class).setConstraints(constraints).build();
        Log.i("PronovosApplication", "setupAndStartWorkManager called");
        WorkManager.getInstance().enqueueUniqueWork("PRONOVOS", ExistingWorkPolicy.REPLACE, simpleRequest);
//        WorkManager.getInstance().enqueue(simpleRequest);

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {

        setupAndStartWorkManager();
    }
    public TransferRequest getCreateTransferInBaseActivity() {
        return createTransfer;
    }

    public void setCreateTransferInBaseActivity(TransferRequest createTransfer) {
        this.createTransfer = createTransfer;
    }

}
