package com.pronovoscm.utils;

import android.content.Context;

import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;

public abstract class PronovosWorker {
    public abstract void doTransaction();
}
