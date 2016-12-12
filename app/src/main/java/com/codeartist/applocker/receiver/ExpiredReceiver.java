
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
        Log.d("ExpiredReceiver", "Expired Receiver fired");
        if (!isProcessRunning(context)) {
            context.stopService(new Intent(context, AppLockerService.class));
            // context.startService(new Intent(context, AppLockerService.class));
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

}
