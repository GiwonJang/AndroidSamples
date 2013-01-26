package com.is.datadroidsample.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.is.datadroidsample.R;
import com.is.datadroidsample.data.model.RssFeed;
import com.is.datadroidsample.data.model.RssItem;
import com.is.datadroidsample.data.requestmanager.SampleRequestFactory;
import com.is.datadroidsample.dialogs.ConnectionErrorDialogFragment;
import com.is.datadroidsample.dialogs.ConnectionErrorDialogFragment.ConnectionErrorDialogListener;

public class MainActivity extends DataDroidActivity
						implements RequestListener, OnClickListener, OnItemClickListener, ConnectionErrorDialogListener {

	private static final String SAVED_STATE_RSS_ITEM_LIST = "savedStateRssItemList";
	private LayoutInflater		_inflater;
	private String[]			_feedUrlArray;
	private RssItem_listAdapter	_listAdapter;
	private ListView			_listView;
	private Spinner				_spinnerFeedUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_main);
		bindViews();
		_feedUrlArray = getResources().getStringArray(R.array.rss_feed_url);
		_inflater = getLayoutInflater();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		for (int i = 0; i < mRequestList.size(); i++) {
			Request request = mRequestList.get(i);

			if (mRequestManager.isRequestInProgress(request)) {
				mRequestManager.addRequestListener(this, request);
				setProgressBarIndeterminateVisibility(true);
			} else {
				mRequestList.remove(request);
				i--;
				mRequestManager.callListenerWithCachedData(this, request);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!mRequestList.isEmpty()) {
			mRequestManager.removeRequestListener(this);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		ArrayList<RssItem> rssItemList = new ArrayList<RssItem>();
		for (int i = 0, n = _listAdapter.getCount(); i < n; i++) {
			rssItemList.add(_listAdapter.getItem(i));
		}

		outState.putParcelableArrayList(SAVED_STATE_RSS_ITEM_LIST, rssItemList);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		ArrayList<RssItem> rssItemList = savedInstanceState.getParcelableArrayList(SAVED_STATE_RSS_ITEM_LIST);
		_listAdapter.setNotifyOnChange(false);
		for (int i = 0, length = rssItemList.size(); i < length; i++) {
			_listAdapter.add(rssItemList.get(i));
		}
		_listAdapter.notifyDataSetChanged();
	}

	private void bindViews() {
		_spinnerFeedUrl = (Spinner) findViewById(R.id.sp_url);

        ((Button) findViewById(R.id.b_load)).setOnClickListener(this);
        ((Button) findViewById(R.id.b_clear_memory)).setOnClickListener(this);

        _listView = (ListView) findViewById(android.R.id.list);
        _listAdapter = new RssItem_listAdapter(this);
        _listView.setAdapter(_listAdapter);
        _listView.setOnItemClickListener(this);
        _listView.setEmptyView(findViewById(android.R.id.empty));
    }

    private void callRssFeedWS() {
        _listAdapter.clear();
        setProgressBarIndeterminateVisibility(true);

        Request request = SampleRequestFactory.getRssFeedRequest(_feedUrlArray[_spinnerFeedUrl.getSelectedItemPosition()]);
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_load:
                callRssFeedWS();
                break;
            case R.id.b_clear_memory:
                _listAdapter.clear();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        RssItem rssItem = ((RssItem_listAdapter) parent.getAdapter()).getItem(position);
        if (rssItem != null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rssItem.link)));
        }
    }

    @Override
    public void onRequestFinished(Request request, Bundle resultData) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            RssFeed rssFeed = resultData
                    .getParcelable(SampleRequestFactory.BUNDLE_EXTRA_RSS_FEED_DATA);

            _listAdapter.setNotifyOnChange(false);
            for (RssItem rssItem : rssFeed.rssItemList) {
                _listAdapter.add(rssItem);
            }
            _listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestConnectionError(Request request, int statusCode) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            ConnectionErrorDialogFragment.show(this, request, this);
        }
    }

    @Override
    public void onRequestDataError(Request request) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            showBadDataErrorDialog();
        }
    }

    @Override
    public void onRequestCustomError(Request request, Bundle resultData) {
        // Never called.
    }

    @Override
    public void connectionErrorDialogCancel(Request request) {}

    @Override
    public void connectionErrorDialogRetry(Request request) {
        callRssFeedWS();
    }

    class ViewHolder {
        private TextView mTextViewTitle;
        private TextView mTextViewDescription;

        public ViewHolder(View view) {
            mTextViewTitle = (TextView) view.findViewById(R.id.tv_title);
            mTextViewDescription = (TextView) view.findViewById(R.id.tv_description);
        }

        public void populateViews(RssItem rssItem) {
            mTextViewTitle.setText(rssItem.title);
            mTextViewDescription.setText(Html.fromHtml(rssItem.description));
        }
    }

    class RssItem_listAdapter extends ArrayAdapter<RssItem> {

        public RssItem_listAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = _inflater.inflate(R.layout.rss_feed_list_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.populateViews(getItem(position));

            return convertView;
        }
    }

}
