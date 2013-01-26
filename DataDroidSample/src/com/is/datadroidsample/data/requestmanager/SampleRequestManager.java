package com.is.datadroidsample.data.requestmanager;

import android.content.Context;

import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.is.datadroidsample.data.service.SampleService;

/**
 * This class is used as a proxy to call the Service. It provides easy-to-use methods to call the
 * service and manages the Intent creation. It also assures that a request will not be sent again if
 * an exactly identical one is already in progress.
 *
 * @author Foxykeep
 */
public final class SampleRequestManager extends RequestManager {

    // Singleton management
    private static SampleRequestManager sInstance;

    public synchronized static SampleRequestManager from(Context context) {
        if (sInstance == null) {
            sInstance = new SampleRequestManager(context);
        }

        return sInstance;
    }

    private SampleRequestManager(Context context) {
        super(context, SampleService.class);
    }
}