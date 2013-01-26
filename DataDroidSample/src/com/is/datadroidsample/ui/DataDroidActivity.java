package com.is.datadroidsample.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.foxykeep.datadroid.requestmanager.Request;
import com.is.datadroidsample.R;
import com.is.datadroidsample.data.requestmanager.SampleRequestManager;

public abstract class DataDroidActivity extends FragmentActivity {

	private static final String		SAVED_STATE_REQUEST_LIST	= "savedStateRequestList";

	protected SampleRequestManager	mRequestManager;
	protected ArrayList<Request>	mRequestList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mRequestManager = SampleRequestManager.from(this);

		if (savedInstanceState != null) {
			mRequestList = savedInstanceState.getParcelableArrayList(SAVED_STATE_REQUEST_LIST);
		} else {
			mRequestList = new ArrayList<Request>();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelableArrayList(SAVED_STATE_REQUEST_LIST, mRequestList);
	}

	protected void showBadDataErrorDialog() {
		// new
		// ErrorDialogFragmentBuilder(this).setTitle(R.string.dialog_error_data_error_title)
		// .setMessage(R.string.dialog_error_data_error_message).show();
	}
}