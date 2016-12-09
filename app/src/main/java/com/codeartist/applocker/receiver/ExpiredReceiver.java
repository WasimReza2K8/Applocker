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

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null) {
            return;
        }

        mContext = context;

        Log.d("ExpiredReceiver", "Expired Receiver fired");
        mContext.startService(new Intent(mContext, AppLockerService.class));
    }

}
