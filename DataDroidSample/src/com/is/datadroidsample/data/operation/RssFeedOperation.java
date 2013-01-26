package com.is.datadroidsample.data.operation;

import android.content.Context;
import android.os.Bundle;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.is.datadroidsample.data.factory.RssFactory;
import com.is.datadroidsample.data.model.RssFeed;
import com.is.datadroidsample.data.requestmanager.SampleRequestFactory;

public class RssFeedOperation implements Operation {

    public static final String PARAM_FEED_URL = "com.foxykeep.datadroidpoc.extra.feedUrl";

    @Override
    public Bundle execute(Context context, Request request) throws ConnectionException,
            DataException {
        ConnectionResult result = new NetworkConnection(context, request.getString(PARAM_FEED_URL))
                .execute();

        Bundle bundle = new Bundle();
        RssFeed rssFeed = RssFactory.parseResult(result.body);
        bundle.putParcelable(SampleRequestFactory.BUNDLE_EXTRA_RSS_FEED_DATA, rssFeed);
        return bundle;
    }
}
