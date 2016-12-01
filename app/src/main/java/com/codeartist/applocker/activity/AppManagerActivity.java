
package com.codeartist.applocker.activity;

import java.util.ArrayList;
import java.util.List;

import com.codeartist.applocker.adapter.AppManagerAdapter;
import com.codeartist.applocker.db.DBManager;
import com.codeartist.applocker.model.AppManagerModel;
import com.codeartist.applocker.service.AppLockerService;
import com.codeartist.applocker.utility.Constants;
import com.codeartist.applocker.utility.Preferences;
import com.codeartist.applocker.utility.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import codeartist.applocker.R;

/**
 * Created by Wasim on 11/16/16.
 */

public final class AppManagerActivity extends BaseServiceBinderActivity {
    private Button mAllAppsButton, mLockAppsButton, mPaidAppsButton;
    private ListView mAppListView;
    private List<AppManagerModel> mAppList;
    private ProgressBar mProgressBar;
    private AppManagerAdapter mAdapter;
    private static final String SCHEME = "package";
    private DBManager mDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        mAllAppsButton = (Button) findViewById(R.id.button_allApp);
        mLockAppsButton = (Button) findViewById(R.id.button_lockApp);
        mPaidAppsButton = (Button) findViewById(R.id.button_paidApp);
        mAppListView = (ListView) findViewById(R.id.listView_appManager);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_AppList);
        mAdapter = new AppManagerAdapter(this, mClickListener);
        mAppListView.setAdapter(mAdapter);
        startService(new Intent(this, AppLockerService.class));
        mDb = DBManager.getInstance(getApplicationContext());
        mAppListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final AppManagerModel item = (AppManagerModel) adapterView
                        .getItemAtPosition(position);

                showInstalledAppDetails(item.getPackageName());

            }
        });
        bindService(new Intent(this, AppLockerService.class), mConnection,
                BIND_AUTO_CREATE);
        new GetApplication().execute();
    }

    private void appLocked(View view, String packageName) throws ClassCastException {
        if (view != null) {
            ImageView image = (ImageView) view;
            image.setImageDrawable(getResources().getDrawable(R.mipmap.locker));
        }
        sendMessageToService(packageName, AppLockerService.MSG_APP_LOCK);
        // Log.e("locker", "locked");
    }

    private void appUnlocked(View view, String packageName) throws ClassCastException {
        if (view != null) {
            ImageView image = (ImageView) view;
            image.setImageDrawable(getResources().getDrawable(R.mipmap.lock_open));
        }
        sendMessageToService(packageName, AppLockerService.MSG_APP_UNLOCK);
        // Log.e("locker", "unlocked");
    }

    @Override
    public void sendMessageToService(String packageName, int lockFlag) {
        // Log.e("locker", mBound + "");
        if (!mBound)
            return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, lockFlag, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_PKG_NAME, packageName);
        msg.setData(bundle);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    static final int REQUEST_CODE = 120;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Log.e("onActivity Result", resultCode + " " + requestCode);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            String packageName = data.getStringExtra(Constants.KEY_PKG_NAME);
            if (packageName != null) {
                for (int i = 0; i < mAppList.size(); i++) {
                    if (mAppList.get(i).getPackageName().equals(packageName)) {
                        mAppList.get(i).setLocked(true);
                        mAdapter.notifyDataSetChanged();
                        appLocked(null, packageName);
                    }
                }
            }
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
            AppManagerModel item = mAppList.get(position);
            String password = Preferences.loadString(getApplicationContext(),
                    Preferences.KEY_APP_LOCKER_PASSWORD, null);
            if (password == null) {
                Intent intent = new Intent(AppManagerActivity.this, PasswordSetterActivity.class);
                intent.putExtra(Constants.KEY_PKG_NAME, item.getPackageName());
                startActivityForResult(intent, REQUEST_CODE);
                return;
            }
            if (item.isLocked()) {
                mAppList.get(position).setLocked(false);
                appUnlocked(v, item.getPackageName());
            } else {
                mAppList.get(position).setLocked(true);
                appLocked(v, item.getPackageName());
            }
        }
    };

    private class GetApplication extends AsyncTask<Void, String, ArrayList<AppManagerModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<AppManagerModel> doInBackground(Void... voids) {
            return getInstalledApps();
        }

        @Override
        protected void onPostExecute(ArrayList<AppManagerModel> list) {
            super.onPostExecute(list);
            mAppList = list;
            if (getWindow().getDecorView() != null) {
                mAdapter.addAllItem(list);
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    public void showInstalledAppDetails(String packageName) {
        Intent intent = new Intent();
        // final int apiLevel = Build.VERSION.SDK_INT;
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts(SCHEME, packageName, null);
        intent.setData(uri);
        startActivity(intent);
    }

    public ArrayList<AppManagerModel> getInstalledApps() {
        ArrayList<AppManagerModel> applicationList = new ArrayList<>();
        List<ApplicationInfo> packages = getPackageManager()
                .getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<String> lockedApp = Utils.getLockedApp(mDb);

        for (ApplicationInfo appInfo : packages) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // // Third Party Applications
                AppManagerModel appManagerModel = new AppManagerModel();
                appManagerModel.setAppName(appInfo.loadLabel(getPackageManager()).toString());
                appManagerModel.setAppIcon(appInfo.loadIcon(getPackageManager()));
                appManagerModel.setPackageName(appInfo.packageName);
                if (lockedApp.contains(appInfo.packageName)) {
                    appManagerModel.setLocked(true);
                } else {
                    appManagerModel.setLocked(false);
                }

                applicationList.add(appManagerModel);
            }

        }
        return applicationList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
}
