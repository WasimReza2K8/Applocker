
package com.codeartist.applocker.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.codeartist.applocker.R;
import com.codeartist.applocker.db.DBManager;
import com.codeartist.applocker.interfaces.OnHomePressedListener;
import com.codeartist.applocker.utility.Constants;
import com.codeartist.applocker.utility.HomeWatcher;
import com.codeartist.applocker.utility.Preferences;
import com.codeartist.applocker.utility.Utils;
import com.takwolf.android.lock9.Lock9View;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by bjit-16 on 11/14/16.
 */

public class AppLockerService extends Service {

    // String CURRENT_PACKAGE_NAME;
    private Timer mTimer;
    private View widget;
    private static List<String> activityList;
    private Dialog checkerDialog;

    public static final int MSG_APP_UNLOCK = 1;
    public static final int MSG_APP_LOCK = 2;
    public static final int MSG_APP_PERMITTED = 3;
    private DBManager mDb;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // Toast.makeText(getApplicationContext(), "hello!", Toast.LENGTH_SHORT).show();
            String packageName = msg.getData().getString(Constants.KEY_PKG_NAME);
            switch (msg.what) {
                case MSG_APP_UNLOCK:
                    activityList.remove(packageName);
                    Utils.deleteFromAppLockerTable(packageName, mDb);
                    // Toast.makeText(getApplicationContext(), "MSG_APP_UNLOCK! "+ packageName,
                    // Toast.LENGTH_SHORT).show();
                    break;
                case MSG_APP_LOCK:
                    activityList.add(packageName);
                    Utils.insertInToAppLockerTable(packageName, mDb);
                    // Toast.makeText(getApplicationContext(), "MSG_APP_LOCK! "+ packageName,
                    // Toast.LENGTH_SHORT).show();
                    break;
                case MSG_APP_PERMITTED:
                    activityList.remove(packageName);
                    // Toast.makeText(getApplicationContext(), "MSG_APP_PERMITTED! "+ packageName,
                    // Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * When binding to the service, we return an interface to our messenger for sending messages to
     * the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private HomeWatcher mHomeWatcher = new HomeWatcher(this);

    public void destroyDialog() {
        if (checkerDialog != null && checkerDialog.isShowing()) {
            checkerDialog.dismiss();
            checkerDialog = null;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDb = DBManager.getInstance(getApplicationContext());
        activityList = Utils.getLockedApp(mDb);
        scheduleMethod();
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                destroyDialog();
                scheduleMethod();
            }

            @Override
            public void onHomeLongPressed() {
                destroyDialog();
                scheduleMethod();
            }
        });
        mHomeWatcher.startWatch();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        startForeground(0, createNotification());
        restartService(60 * 60 * 1000, true);
        // Log.e("activity on TOp", "" + "onCreate");
        // Toast.makeText(getApplicationContext(), "service onCreate! ", Toast.LENGTH_SHORT).show();
    }

    private Notification createNotification() {
        Intent nextIntent = new Intent(this, AppLockerService.class);
        // nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);
        NotificationCompat.Builder notificationI = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setTicker(getString(R.string.app_name))
                .setContentText(getString(R.string.app_name))
                .setAutoCancel(true)
                .setContentIntent(pnextIntent);

        /*
         * Notification notification = notificationI.build(); notification.priority =
         * Notification.PRIORITY_MIN;
         */
        // notification.visibility = Notification.VISIBILITY_SECRET;
        // notification.setLatestEventInfo( this, title, text, contentIntent );
        Notification notification = notificationI.build();
        notification.priority = Notification.PRIORITY_MIN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int smallIconViewId = getResources().getIdentifier("right_icon", "id",
                    android.R.class.getPackage().getName());

            if (smallIconViewId != 0) {
                if (notification.contentIntent != null)
                    notification.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                if (notification.headsUpContentView != null)
                    notification.headsUpContentView.setViewVisibility(smallIconViewId,
                            View.INVISIBLE);

                if (notification.bigContentView != null)
                    notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
            }
        }
        return notification;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Log.e("activity on TOp", "" + "onStartCommand");
        return START_STICKY;
    }

    private BroadcastReceiver mScreenOnOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.i("[BroadcastReceiver]", "MyReceiver");

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                // Log.i("[BroadcastReceiver]", "Onnn");
                activityList = Utils.getLockedApp(mDb);
                scheduleMethod();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Log.i("[BroadcastReceiver]", "OFF");
                removeScheduleTask();
            }
        }
    };

    private void removeScheduleTask() {
        // Log.e("remove schdule", mTimer + "");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        restartService(1000, false);
        super.onTaskRemoved(rootIntent);
    }

    private void restartService(int interval, boolean isRepeating) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        if (isRepeating) {
            alarmService.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                    interval, restartServicePendingIntent);
        } else {
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + interval,
                    restartServicePendingIntent);
        }

    }

    private void scheduleMethod() {
        // Log.e("schdule task", mTimer + "");
        removeScheduleTask();
        mTimer = new Timer();
        // Log.e("activity on TOp", "" + "scheduleMethod");

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // This method will check for the Running apps after every 500ms
                checkRunningApps();
            }
        }, 0, 150);

    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            String packageName = (String) msg.obj;
            // Log.e("mHandler", packageName + "");
            showCheckerDialog(packageName);
            // showPatternDialog(packageName);
            removeScheduleTask();
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    public void checkRunningApps() {
        if (activityList == null || activityList.isEmpty()) {
            return;
        }
        ActivityManager mActivityManager = (ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE);

        String activityOnTop;

        if (Build.VERSION.SDK_INT > 20) {
            activityOnTop = mActivityManager.getRunningAppProcesses().get(0).processName;
        } else {
            activityOnTop = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        }
        Log.e("activity on TOp", "" + activityOnTop);

        // Provide the package name(s) of apps here, you want to show password activity
        if (activityList.contains(activityOnTop)) {
            Message msg = Message.obtain(); // Creates an new Message instance
            msg.obj = activityOnTop; // Put the string into Message, into "obj" field.
            msg.setTarget(mHandler); // Set the Handler
            msg.sendToTarget(); // Send the message
            // mHandler.sendToTarget();
        }
    }

    private void showCheckerDialog(final String packageName) {
        if (checkerDialog == null || !checkerDialog.isShowing()) {

            final Context context = getApplicationContext();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            widget = layoutInflater.inflate(R.layout.number_lock_view, null);
            final ImageButton check = (ImageButton) widget.findViewById(R.id.button_check);
            final EditText password = (EditText) widget
                    .findViewById(R.id.edt1);

            Button button0 = (Button) widget.findViewById(R.id.button0);
            Button button1 = (Button) widget.findViewById(R.id.button1);
            Button button2 = (Button) widget.findViewById(R.id.button2);
            Button button3 = (Button) widget.findViewById(R.id.button3);
            Button button4 = (Button) widget.findViewById(R.id.button4);
            Button button5 = (Button) widget.findViewById(R.id.button5);
            Button button6 = (Button) widget.findViewById(R.id.button6);
            Button button7 = (Button) widget.findViewById(R.id.button7);
            Button button8 = (Button) widget.findViewById(R.id.button8);
            Button button9 = (Button) widget.findViewById(R.id.button9);
            Button buttonC = (Button) widget.findViewById(R.id.buttonC);

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password.append("1");
                }
            });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password.append("2");
                }
            });

            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password.append("3");
                }
            });

            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password.append("4");
                }
            });

            button5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password.append("5");
                }
            });

            button6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password.append("6");
                }
            });

            button7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password.append("7");
                }
            });

            button8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password.append("8");
                }
            });

            button9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password.append("9");
                }
            });

            button0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password.append("0");
                }
            });

            buttonC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int length = password.getText().length();
                    if (length > 0) {
                        password.getText().delete(length - 1, length);
                    }
                }
            });
            password.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                    return true;
                }

            });
            checkerDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            checkerDialog.setCanceledOnTouchOutside(false);
            checkerDialog.setCancelable(false);
            checkerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
            checkerDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            checkerDialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);
            checkerDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            checkerDialog.setContentView(widget);
            checkerDialog.getWindow().setGravity(Gravity.CENTER);
            checkerDialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                        KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        destroyDialog();
                        scheduleMethod();
                        return true;

                    }
                    return false;
                }
            });

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String savedPassword = Preferences.loadString(getApplicationContext(),
                            Preferences.KEY_APP_LOCKER_PASSWORD, null);
                    if (savedPassword != null && password.getText().toString() != null
                            && password.getText().toString().equals(savedPassword)) {
                        destroyDialog();
                        activityList.remove(packageName);
                        scheduleMethod();
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong Password",
                                Toast.LENGTH_LONG).show();
                    }

                }
            });

            checkerDialog.show();
        }

    }

    private void showPatternDialog(final String packageName) {
        if (checkerDialog == null || !checkerDialog.isShowing()) {

            final Context context = getApplicationContext();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            widget = layoutInflater.inflate(R.layout.pattern_lock_layout, null);
            final Lock9View pattern = (Lock9View) widget.findViewById(R.id.lock_9_view);
            checkerDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            checkerDialog.setCanceledOnTouchOutside(false);
            checkerDialog.setCancelable(false);
            checkerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
            checkerDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            checkerDialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);
            checkerDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            checkerDialog.setContentView(widget);
            checkerDialog.getWindow().setGravity(Gravity.CENTER);
            checkerDialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                        KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        destroyDialog();
                        scheduleMethod();
                        return true;

                    }
                    return false;
                }
            });

            /*
             * check.setOnClickListener(new View.OnClickListener() {
             * @Override public void onClick(View view) { String savedPassword =
             * Preferences.loadString(getApplicationContext(), Preferences.KEY_APP_LOCKER_PASSWORD,
             * null); if (savedPassword != null && password.getText().toString() != null &&
             * password.getText().toString().equals(savedPassword)) { destroyDialog();
             * activityList.remove(packageName); scheduleMethod(); } else {
             * Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_LONG).show();
             * } } });
             */

            /*
             * pattern.setOnPatternDetectedListener(new PatternView.OnPatternDetectedListener() {
             * @Override public void onPatternDetected() { String savedPassword =
             * Preferences.loadString(getApplicationContext(), Preferences.KEY_APP_LOCKER_PASSWORD,
             * null); if (savedPassword != null && pattern.getPatternString() != null &&
             * pattern.getPatternString().equals(savedPassword)) { destroyDialog();
             * activityList.remove(packageName); scheduleMethod(); } else {
             * Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_LONG).show();
             * } } });
             */

            pattern.setCallBack(new Lock9View.CallBack() {
                @Override
                public void onFinish(String password) {
                    String savedPassword = Preferences.loadString(getApplicationContext(),
                            Preferences.KEY_APP_LOCKER_PASSWORD, null);
                    if (savedPassword != null && password != null
                            && password.equals(savedPassword)) {
                        destroyDialog();
                        activityList.remove(packageName);
                        scheduleMethod();
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong Password",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            checkerDialog.show();
        }

    }

    @Override
    public void onDestroy() {

        // Log.e("activity on TOp", "" + "onDestroy");
        removeScheduleTask();
        if (mScreenOnOffReceiver != null) {
            unregisterReceiver(mScreenOnOffReceiver);
        }
        if (mHomeWatcher != null) {
            mHomeWatcher.stopWatch();
        }
        stopForeground(true);
        destroyDialog();
        restartService(1000, false);
        super.onDestroy();

    }

}
