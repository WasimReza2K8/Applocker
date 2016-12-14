
package com.codeartist.applocker.receiver;

import com.codeartist.applocker.service.AppLockerService;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Wasim on 10/17/2015.
 */
public class ExpiredReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("activity on TOp", "Expired Receiver fired");
        if (!isProcessRunning(context) || !isMyServiceRunning(AppLockerService.class, context)) {
            context.stopService(new Intent(context, AppLockerService.class));
            context.startService(new Intent(context, AppLockerService.class));
        }

    }

    private boolean isProcessRunning(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return true;
            }
        }

        return false;

    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
