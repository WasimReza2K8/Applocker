package com.codeartist.applocker.receiver;

import com.codeartist.applocker.service.AppLockerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by Wasim on 10/17/2015.
 */
public class ExpiredReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ExpiredReceiver", "Expired Receiver fired");
        context.startService(new Intent(context, AppLockerService.class));
    }

}
