package com.is.gcmtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends FragmentActivity {

	public static final String	TAG	= "MainActivity";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter		mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager					mViewPager;
	DummySectionFragment		mCurrentFragment;
	AsyncTask<Void, Void, Void>	mRegisterTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		registerToGcmServer();
		
		final float density = getResources().getDisplayMetrics().density;
		Log.i(TAG, density + "");
	}
	
	@Override
	public void onDestroy() {
		if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void registerToGcmServer() {
		//checkNotNull(CommonUtilities.SERVER_URL, "SERVER_URL");
		checkNotNull(CommonUtilities.SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		} else {
			Log.i(TAG, "GCM Registred Device Id : " + regId);
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				mCurrentFragment.addMessage(getString(R.string.already_registered) + "\n");
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						//boolean registered = ServerUtilities.register(context, regId);
						// At this point all attempts to register with the app
						// server failed, so we need to unregister the device
						// from GCM - the app will try to register again when
						// it is restarted. Note that GCM will send an
						// unregistered callback upon completion, but
						// GCMIntentService.onUnregistered() will ignore it.
						//if (!registered) {
						//	GCMRegistrar.unregister(context);
						//}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config, name));
		}
	}

	private final BroadcastReceiver	mHandleMessageReceiver	= new BroadcastReceiver() {
																@Override
																public void onReceive(Context context, Intent intent) {
																	String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
																	mCurrentFragment.addMessage(newMessage + "\n");
																}
															};

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			mCurrentFragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			mCurrentFragment.setArguments(args);
			return mCurrentFragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return getString(R.string.title_section1).toUpperCase();
				case 1:
					return getString(R.string.title_section2).toUpperCase();
				case 2:
					return getString(R.string.title_section3).toUpperCase();
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String	ARG_SECTION_NUMBER	= "section_number";
		private TextView			mTextView;

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			mTextView = new TextView(getActivity());
			mTextView.setGravity(Gravity.CENTER);
			mTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return mTextView;
		}

		public void addMessage(String msg) {
			mTextView.append(msg + "\n");
		}
	}

}
