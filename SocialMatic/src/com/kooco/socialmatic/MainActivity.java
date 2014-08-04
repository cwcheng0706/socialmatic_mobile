package com.kooco.socialmatic;

import java.util.HashMap;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.auly.control.wslClass;
import com.kooco.socialmatic.camera.MainView;
import com.kooco.socialmatic.configuration.LoginView;
import com.kooco.socialmatic.flow.FlowView;
import com.kooco.socialmatic.follow.FollowingView;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.message.MessageView;
import com.kooco.socialmatic.offer.OfferView;
import com.kooco.socialmatic.profile.SelectUserProfileView;
import com.kooco.socialmatic.qrcode.QRCodeReaderView;
import com.kooco.socialmatic.search.SearchView;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class MainActivity extends SherlockFragmentActivity{

	TabHost mTabHost;
	TabManager mTabManager;

	public static Activity mActivity;
	private ImageView mProfileImgView;
	private Button mCameraBtn;
	private Button mBarCodeBtn;

	private ImageLoader imageLoader;

	public static final int WMT_SPI_LCD_STATE_HAPPY = 0;
	public static final int WMT_SPI_LCD_STATE_SUN = 1;
	public static final int WMT_SPI_LCD_STATE_CLOUDY = 2;
	public static final int WMT_SPI_LCD_STATE_SAD = 3;
	public static final int WMT_SPI_LCD_STATE_PRINT = 4;
	public static final int WMT_SPI_LCD_STATE_MESSAGE = 5;

	static wslClass mwslClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_tabs);

		getSupportActionBar().hide();

		// hide status bar
		// ---------------------------------------------
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		if (UserDataAccess.mGCMRegId.equals(""))
			UserDataAccess.requestGCMRegId(this);

		imageLoader = new ImageLoader(getApplicationContext());

		mProfileImgView = (ImageView) findViewById(R.id.profile_imgView);
		mCameraBtn = (Button) findViewById(R.id.camear_btn);
		mBarCodeBtn = (Button) findViewById(R.id.barcode_btn);

		mProfileImgView.setOnClickListener(new ProfileBtnListener());
		mCameraBtn.setOnClickListener(new CameraBtnListener());
		mBarCodeBtn.setOnClickListener(new BarCodeBtnListener());

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mTabManager = new TabManager(this, mTabHost, android.R.id.tabcontent);
		mTabManager.mContext = this;

		// MainView.MainFragment.mActivity = this;
		mActivity = this;

		TabWidget tw = mTabHost.getTabWidget();
		tw.setOrientation(LinearLayout.VERTICAL);

		mTabManager.addTab(
				mTabHost.newTabSpec("Discovery").setIndicator(
						createIndicatorView(mTabHost, "Discovery")),
				SearchView.MainFragment.class, null);

		mTabManager.addTab(
				mTabHost.newTabSpec("Follow").setIndicator(
						createIndicatorView(mTabHost, "Follow")),
				FollowingView.MainFragment.class, null);

		mTabManager.addTab(
				mTabHost.newTabSpec("Flow").setIndicator(
						createIndicatorView(mTabHost, "Flow")),
				FlowView.MainFragment.class, null);
		/*
		 * mTabManager.addTab( mTabHost.newTabSpec("Profile").setIndicator(
		 * createIndicatorView(mTabHost, "Profile")),
		 * ProfileView.MainFragment.class, null);
		 */
		mTabManager.addTab(
				mTabHost.newTabSpec("Message").setIndicator(
						createIndicatorView(mTabHost, "Message")),
				MessageView.MainFragment.class, null);
		/*
		 * mTabManager.addTab( mTabHost.newTabSpec("Camera").setIndicator(
		 * createIndicatorView(mTabHost, "Camera")),
		 * MainView.MainFragment.class, null);
		 */
		mTabManager.addTab(
				mTabHost.newTabSpec("Offer").setIndicator(
						createIndicatorView(mTabHost, "Offer")),
				OfferView.MainFragment.class, null);

		mTabManager.addTab(
				mTabHost.newTabSpec("Setting").setIndicator(
						createIndicatorView(mTabHost, "Setting")),
				LoginView.MainFragment.class, null);

		String isLogin = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.isLoginKey);

		if (isLogin.equals("y"))
			mTabHost.setCurrentTab(0);
		else
			mTabHost.setCurrentTab(5);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		mwslClass = new wslClass();
		mwslClass.Init();
		mwslClass.IOCTLVIB(WMT_SPI_LCD_STATE_HAPPY);
	}

	private View createIndicatorView(TabHost tabHost, CharSequence label) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View tabIndicator = inflater.inflate(R.layout.discovery_tab_indicator,
				tabHost.getTabWidget(), // tab widget is the parent
				false); // no inflate params

		if (label.equals("Flow"))
			tabIndicator = inflater.inflate(R.layout.flow_tab_indicator,
					tabHost.getTabWidget(), // tab widget is the parent
					false);
		else if (label.equals("Discovery"))
			tabIndicator = inflater.inflate(R.layout.discovery_tab_indicator,
					tabHost.getTabWidget(), // tab widget is the parent
					false);
		else if (label.equals("Message"))
			tabIndicator = inflater.inflate(R.layout.message_tab_indicator,
					tabHost.getTabWidget(), // tab widget is the parent
					false);
		/*
		 * else if (label.equals("Profile")) tabIndicator =
		 * inflater.inflate(R.layout.profile_tab_indicator,
		 * tabHost.getTabWidget(), // tab widget is the parent false);
		 */
		else if (label.equals("Offer"))
			tabIndicator = inflater.inflate(R.layout.offer_tab_indicator,
					tabHost.getTabWidget(), // tab widget is the parent
					false);
		/*
		 * else if (label.equals("Camera")) tabIndicator =
		 * inflater.inflate(R.layout.camera_tab_indicator,
		 * tabHost.getTabWidget(), // tab widget is the parent false);
		 */
		else if (label.equals("Follow"))
			tabIndicator = inflater.inflate(R.layout.follow_tab_indicator,
					tabHost.getTabWidget(), // tab widget is the parent
					false);
		else if (label.equals("Setting"))
			tabIndicator = inflater.inflate(R.layout.setting_tab_indicator,
					tabHost.getTabWidget(), // tab widget is the parent
					false);

		return tabIndicator;
	}

	public void onStart() {
		super.onStart();

		/*
		 * PrinterAcceptService.mActivity = MainActivity.this; Intent i=new
		 * Intent(MainActivity.this, PrinterAcceptService.class); //设置新Task的方式
		 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //以startService 方法启动
		 * Intent startService(i);
		 */
		createProfileBtn();
	}

	public void setImageFromBluetooth(Bitmap bitmap) {
		mProfileImgView.setImageBitmap(bitmap);
	}

	public void createProfileBtn() {

		String photoUrl = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.photoKey);
		String gender = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.genderKey);
		String isLogin = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.isLoginKey);

		if (isLogin.equals("y")) {

			Resources r = getResources();
			float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					30, r.getDisplayMetrics());
			float mergLeft = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 16, r.getDisplayMetrics());
			float mergTop = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 7.5f, r.getDisplayMetrics());
			float mergBottom = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 7.5f, r.getDisplayMetrics());

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					(int) size, (int) size);
			layoutParams.setMargins((int) mergLeft, (int) mergTop, 0,
					(int) mergBottom);
			mProfileImgView.setLayoutParams(layoutParams);

			if (photoUrl.equals("")) {
				Drawable res;

				if (gender.equals("M"))
					res = getResources()
							.getDrawable(R.drawable.account_photo_3);
				else if (gender.equals("W"))
					res = getResources()
							.getDrawable(R.drawable.account_photo_1);
				else
					res = getResources()
							.getDrawable(R.drawable.account_photo_2);

				mProfileImgView.setBackground(res);

			} else {
				mProfileImgView.setTag(photoUrl);

				imageLoader.DisplayImage(photoUrl, mProfileImgView, 40);
			}

		} else {
			Resources r = getResources();
			float size_w = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
			float size_h = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 45, r.getDisplayMetrics());
			float mergLeft = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					(int) size_w, (int) size_h);
			layoutParams.setMargins((int) mergLeft, 0, 0, 0);
			mProfileImgView.setLayoutParams(layoutParams);

			Drawable res;
			res = getResources().getDrawable(
					R.drawable.profile_tab_indicator_selector);

			/*
			 * int height = (int)
			 * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
			 * getResources().getDisplayMetrics()); int width = (int)
			 * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
			 * getResources().getDisplayMetrics()); LinearLayout.LayoutParams
			 * layoutParams = new LinearLayout.LayoutParams(width, height);
			 * mProfileImgView.setLayoutParams(layoutParams);
			 */
			mProfileImgView.setBackground(res);
		}
	}

	private class ProfileBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			String isLogin = UserDataAccess.getDataWithKeyAndEncrypt(
					MainActivity.this, Config.isLoginKey);

			if (!isLogin.equals("y")) {
				mTabHost.setCurrentTab(5);
				return;
			}

			String userID = UserDataAccess.getDataWithKeyAndEncrypt(
					MainActivity.this, Config.userIdKey);

			SelectUserProfileView.mQueryUserID = userID;

			Intent i = new Intent(mActivity, SelectUserProfileView.class);
			mActivity.startActivity(i);
		}
	}

	private class CameraBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(mActivity, MainView.class);
			mActivity.startActivity(i);
		}
	}

	private class BarCodeBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(mActivity, QRCodeReaderView.class);
			// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// Intent i = new Intent(mActivity, SendingdataActivity.class);
			mActivity.startActivity(i);
		}
	}

	private View createIndicatorView(TabHost tabHost, CharSequence label,
			CharSequence icon) {

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View tabIndicator = inflater.inflate(R.layout.tab_indicator,
				tabHost.getTabWidget(), // tab widget is the parent
				false); // no inflate params

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/FontAwesome.ttf");

		/*
		 * final TextView tv = (TextView) tabIndicator.findViewById(R.id.title);
		 * tv.setText(label);
		 */

		final TextView iconView = (TextView) tabIndicator
				.findViewById(R.id.icon);
		iconView.setTypeface(tf);
		iconView.setText(icon);
		// iconView.setImageDrawable(icon);

		return tabIndicator;
	}

	private void setTitleView() {
		View actionBarView;

		actionBarView = getLayoutInflater().inflate(
				R.layout.actionbar_title_withlogo_layout, null);

		Typeface tf = Typeface
				.createFromAsset(getAssets(), "fonts/Rockwel.ttf");
		TextView txt = (TextView) actionBarView.findViewById(R.id.bar_title);
		txt.setTypeface(tf);

		txt.setText(getResources().getString(R.string.app_name) + " ");

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(actionBarView);

		getSupportActionBar().setNavigationMode(ActionBar.DISPLAY_SHOW_CUSTOM);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static Point getDisplaySize(final Display display) {
		final Point point = new Point();
		try {
			display.getSize(point);
		} catch (java.lang.NoSuchMethodError ignore) { // Older device
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		return point;
	}

	/**
	 * This is a helper class that implements a generic mechanism for
	 * associating fragments with the tabs in a tab host. It relies on a trick.
	 * Normally a tab host has a simple API for supplying a View or Intent that
	 * each tab will show. This is not sufficient for switching between
	 * fragments. So instead we make the content part of the tab host 0dp high
	 * (it is not shown) and the TabManager supplies its own dummy view to show
	 * as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct fragment shown in a separate content area whenever
	 * the selected tab changes.
	 */
	public static class TabManager implements TabHost.OnTabChangeListener {
		private final FragmentActivity mActivity;
		private final TabHost mTabHost;
		private final int mContainerId;
		@SuppressWarnings("unused")
		private Context mContext;
		private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
		TabInfo mLastTab;
		int mLastIndex = 0;
		String mLastTabId = "";

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;
			private Fragment fragment;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabManager(FragmentActivity activity, TabHost tabHost,
				int containerId) {
			mActivity = activity;
			mTabHost = tabHost;
			mContainerId = containerId;
			mTabHost.setOnTabChangedListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mActivity));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			info.fragment = mActivity.getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (info.fragment != null && !info.fragment.isDetached()) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager()
						.beginTransaction();
				ft.detach(info.fragment);
				ft.commit();
			}

			mTabs.put(tag, info);
			mTabHost.addTab(tabSpec);
		}

		@Override
		public void onTabChanged(String tabId) {
			TabInfo newTab = mTabs.get(tabId);

			/*
			 * String isLogin =
			 * UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
			 * Config.isLoginKey);
			 * 
			 * if (!isLogin.equals("y")) { mTabHost.setCurrentTab(5); mLastIndex
			 * = 5; return; }
			 */

			if (!mLastTabId.equals("")) {
				if (mLastTabId.equals("Discovery")) {
					mLastIndex = 0;
				} else if (mLastTabId.equals("Follow")) {
					mLastIndex = 1;
				} else if (mLastTabId.equals("Flow")) {
					mLastIndex = 2;

				} /*
				 * else if (mLastTabId.equals("Profile")) { mLastIndex = 3; }
				 */else if (mLastTabId.equals("Message")) {
					mLastIndex = 3;
				}/*
				 * else if (mLastTabId.equals("Camera")) { mLastIndex = 5; }
				 */else if (mLastTabId.equals("Offer")) {
					mLastIndex = 4;
				} else if (mLastTabId.equals("Setting")) {
					mLastIndex = 5;
				}
			}
			mwslClass.IOCTLVIB(mLastIndex);

			mLastTabId = tabId;
			/*
			 * if (newTab.clss.getName() ==
			 * MyCollectMainView.MainFragment.class.getName()) { String strTitle
			 * = (String) mContext.getResources().getText(
			 * R.string.application_name); String msg; if
			 * (!MemberDataDeal.isLogin(mContext)) { msg = "請先登入會員";
			 * showLoginAlertView(strTitle, msg, true); return; } }
			 */

			if (mLastTab != newTab) {

				if (tabId.equals("Camera")) {
					Intent i = new Intent(mActivity, MainView.class);
					mActivity.startActivity(i);

					mTabHost.setCurrentTab(mLastIndex);
				} else {
					FragmentTransaction ft = mActivity
							.getSupportFragmentManager().beginTransaction();
					if (mLastTab != null) {
						if (mLastTab.fragment != null) {
							ft.detach(mLastTab.fragment);
						}

						mLastTab = newTab;
					}
					if (newTab != null) {

						WebManager webManager = new WebManager();
						
						if (!webManager.isNetworkAvailable())
						{
							UserDataAccess.setDataWithKey(mActivity, Config.isLoginKey, "n");
							UserDataAccess.setDataWithKeyAndEncrypt(mContext, Config.nameKey,
									"");
							UserDataAccess.setDataWithKeyAndEncrypt(mContext, Config.genderKey,
									"");
							UserDataAccess.setDataWithKeyAndEncrypt(mContext, Config.photoKey,
									"");
							UserDataAccess.setDataWithKeyAndEncrypt(mContext, Config.GCMRegIdKey,
									"");
							UserDataAccess.mGCMRegId = "";
						}
						
						String isLogin = UserDataAccess
								.getDataWithKeyAndEncrypt(mActivity,
										Config.isLoginKey);

						if (isLogin.equals("y")) {
							if (newTab.fragment == null) {
								newTab.fragment = Fragment.instantiate(
										mActivity, newTab.clss.getName(),
										newTab.args);
								ft.add(mContainerId, newTab.fragment,
										newTab.tag);
							} else {
								ft.attach(newTab.fragment);
							}

							mLastTab = newTab;
						} else {
							
							TabInfo loginTab = mTabs.get("Setting");

							if (loginTab != null) {
								if (loginTab.fragment == null) {
									loginTab.fragment = Fragment.instantiate(
											mActivity, loginTab.clss.getName(),
											loginTab.args);
									ft.add(mContainerId, loginTab.fragment,
											loginTab.tag);
								} else {
									ft.attach(loginTab.fragment);
								}
							}
							mTabHost.setCurrentTab(5);

							mLastTab = loginTab;
						}
					}

					ft.commit();

					mActivity.getSupportFragmentManager()
							.executePendingTransactions();
				}
			}
		}
	}

	/*
	 * @Override public void onClick(DialogInterface dialog, int index) { //
	 * TODO Auto-generated method stub // Log.d("index---", "index = " + index);
	 * 
	 * if (index == -1) { Intent i = new Intent(this, MoreSignInView.class);
	 * startActivity(i); } else if (index == -2) {
	 * 
	 * } }
	 */
}
