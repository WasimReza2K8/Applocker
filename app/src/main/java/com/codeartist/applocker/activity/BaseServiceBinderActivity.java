
package com.codeartist.applocker.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by bjit-16 on 11/24/16.
 */

public abstract class BaseServiceBinderActivity extends AppCompatActivity {
    /** Messenger for communicating with the service. */
    protected Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    protected boolean mBound;

    /**
     * Class for interacting with the main interface of the service.
     */
    protected ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service. We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };

    abstract void sendMessageToService(String packageName, int lockFlag);

}
