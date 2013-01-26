package com.is.datadroidsample.data.requestmanager;

import com.foxykeep.datadroid.requestmanager.Request;
import com.is.datadroidsample.data.operation.RssFeedOperation;

public class SampleRequestFactory {

	// Request types
    public static final int REQUEST_TYPE_RSS_FEED = 20;

    // Response data
    public static final String BUNDLE_EXTRA_RSS_FEED_DATA =
            "com.is.datadroidsample.extra.rssFeed";
    public static final String BUNDLE_EXTRA_ERROR_MESSAGE =
            "com.is.datadroidsample.extra.errorMessage";

    private SampleRequestFactory() {
        // no public constructor
    }

    

    /**
     * Create the request to get the RSS feed of the given URL and save it in the memory.
     *
     * @param feedUrl the URL of the RSS feed.
     * @return The request.
     */
    public static Request getRssFeedRequest(String feedUrl) {
        Request request = new Request(REQUEST_TYPE_RSS_FEED);
        request.setMemoryCacheEnabled(true);
        request.put(RssFeedOperation.PARAM_FEED_URL, feedUrl);
        return request;
    }
}
